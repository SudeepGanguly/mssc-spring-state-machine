package guru.springframework.msscspringstatemachine.services;

import guru.springframework.msscspringstatemachine.domain.Payment;
import guru.springframework.msscspringstatemachine.domain.PaymentEvent;
import guru.springframework.msscspringstatemachine.domain.PaymentState;
import org.springframework.statemachine.StateMachine;

public interface PaymentService {

    //Create a new Payment
    Payment newPayment(Payment payment);
    //PreAuth
    StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId);
    //Authorize Payment
    StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId);
    //Decline Auth request
    StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId);
}
