package guru.springframework.msscspringstatemachine.services;

import guru.springframework.msscspringstatemachine.domain.Payment;
import guru.springframework.msscspringstatemachine.domain.PaymentEvent;
import guru.springframework.msscspringstatemachine.domain.PaymentState;
import guru.springframework.msscspringstatemachine.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateContext;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final StateMachineFactory<PaymentState,PaymentEvent> stateMachineFactory;
    public static final String PAYMENT_ID_HEADER="payment_id";
    private final PaymentStateChangeInterceptor paymentStateChangeInterceptor;

    //Create an initial State and save it in the DB
    @Override
    public Payment newPayment(Payment payment) {
        System.out.println(" Print : newPayment Method;");
        payment.setState(PaymentState.NEW);
        return paymentRepository.save(payment);
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId) {
        System.out.println(" Print  : preAuth Method;");
        StateMachine<PaymentState,PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId,sm,PaymentEvent.PRE_AUTH_APPROVED);
        return sm;
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId) {
        System.out.println(" Print  : authorizePayment Method;");
        StateMachine<PaymentState,PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId,sm,PaymentEvent.AUTH_APPROVED);
        return sm;
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId) {
        System.out.println(" Print  : declineAuth Method;");
        StateMachine<PaymentState,PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId,sm,PaymentEvent.AUTH_DECLINED);
        return sm;
    }

    private void sendEvent(Long paymentId,StateMachine<PaymentState,PaymentEvent> sm , PaymentEvent event){
        Message msg = MessageBuilder.withPayload(event)
                            .setHeader(PAYMENT_ID_HEADER, paymentId)
                            .build();

        sm.sendEvent(msg);
    }

    private StateMachine<PaymentState,PaymentEvent> build(Long paymentId){
        System.out.println(" Print  : build Method Line 1");
        Payment payment = paymentRepository.getOne(paymentId);
        StateMachine<PaymentState,PaymentEvent> sm = stateMachineFactory
                                                        .getStateMachine(Long.toString(payment.getId()));
        sm.stop();
        System.out.println(" Print  : build Method Line 2");

        sm.getStateMachineAccessor()
                .doWithAllRegions(sma ->{
                    sma.addStateMachineInterceptor(paymentStateChangeInterceptor);
                    sma.resetStateMachine(new DefaultStateMachineContext<>(payment.getState(),
                                                                                 null ,
                                                                            null,
                                                                            null));
                });

        System.out.println(" Print  : build Method Line 3");

        sm.start();
        return sm;
    }
}
