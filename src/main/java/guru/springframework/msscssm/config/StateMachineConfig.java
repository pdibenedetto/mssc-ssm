package guru.springframework.msscssm.config;

import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import java.util.EnumSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigBuilder;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

@Slf4j
@EnableStateMachineFactory
@Configuration
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentState, PaymentEvent>
{
    @Override
    public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception
    {
        states.withStates()
              .initial(PaymentState.NEW)
              .states(EnumSet.allOf(PaymentState.class))
              .end(PaymentState.AUTH)
              .end(PaymentState.PRE_AUTH_ERROR)
              .end(PaymentState.AUTH_ERROR);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception
    {
        transitions.withExternal()
                   .source(PaymentState.NEW)
                   .target(PaymentState.NEW)
                   .event(PaymentEvent.PRE_AUTHORIZE)       // start off at New and stay at New when the Pre Authorize event happens
                   .and()
                   .withExternal()
                   .source(PaymentState.NEW)
                   .target(PaymentState.PRE_AUTH)
                   .event(PaymentEvent.PRE_AUTH_APPROVED)   // start off at New and goto Pre Auth and Pre Auth is Approved
                   .and()
                   .withExternal()
                   .source(PaymentState.NEW)
                   .target(PaymentState.PRE_AUTH_ERROR)
                   .event(PaymentEvent.PRE_AUTH_DECLINED);  // start off at New and goto Pre Auth Error when Pre Auth is Declined

    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config) throws Exception
    {
        StateMachineListener<PaymentState, PaymentEvent> adapter = new StateMachineListenerAdapter<>()
        {
            @Override
            public void stateChanged(State<PaymentState, PaymentEvent> from,State<PaymentState, PaymentEvent> to)
            {
                log.info(String.format("stateChanged(from: %s, to: %s)", from, to));
            }
        };

        config.withConfiguration()
              .listener(adapter);
    }
}
