package uk.gov.justice.laa.crime.contribution.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Hooks;
@Order(2)
@Component
@Slf4j
public class ContextPropagationInitialiserConfiguration implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments applicationArguments) {
        log.info("********** Context Propagation Initializer **************");
        Hooks.enableAutomaticContextPropagation();
    }
}