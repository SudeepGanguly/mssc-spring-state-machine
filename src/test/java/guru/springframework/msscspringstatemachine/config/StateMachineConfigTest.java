package guru.springframework.msscspringstatemachine.config;

import guru.springframework.msscspringstatemachine.domain.PaymentEvent;
import guru.springframework.msscspringstatemachine.domain.PaymentState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StateMachineConfigTest {

    @Autowired
    StateMachineFactory<PaymentState, PaymentEvent> factory;

    @Test
    void testNewStatemachine(){
        StateMachine<PaymentState,PaymentEvent> sm = factory.getStateMachine(UUID.randomUUID());

        //Start teh statemachine
        sm.start();

        //Get the State
        System.out.println("Initial State : "+sm.getState().toString());

        //Send An Event
        sm.sendEvent(PaymentEvent.PRE_AUTHORIZE);

        //Get the State
        System.out.println("State Change 1 : "+ sm.getState().toString());

        //Send second Event
        sm.sendEvent(PaymentEvent.PRE_AUTH_APPROVED);

        //Get the State
        System.out.println("State Change 2 : "+sm.getState().toString());
    }
}