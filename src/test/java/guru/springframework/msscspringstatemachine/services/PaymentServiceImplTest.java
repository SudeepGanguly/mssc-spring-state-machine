package guru.springframework.msscspringstatemachine.services;

import guru.springframework.msscspringstatemachine.domain.Payment;
import guru.springframework.msscspringstatemachine.domain.PaymentEvent;
import guru.springframework.msscspringstatemachine.domain.PaymentState;
import guru.springframework.msscspringstatemachine.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;

import javax.transaction.Transactional;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentServiceImplTest {

    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentRepository paymentRepository;

    Payment payment;

    @BeforeEach
    void setUp(){
        payment = Payment.builder()
                            .amount(new BigDecimal("12.99")).build();
    }

    @Transactional
    @Test
    void preAuth() {
        //Step 1 : Invoke the new Payment method
        Payment savedPayment = paymentService.newPayment(payment);

        //Step 2 : Invoke the preAuth method
        StateMachine<PaymentState, PaymentEvent> sm = paymentService.preAuth(savedPayment.getId());

        //Step 3 : Check in database with the same paymentId
        Payment preAuthPayment = paymentRepository.getOne(savedPayment.getId());
        System.out.println("Pre Auth Payment :"+preAuthPayment);
    }
}