package uk.gov.justice.laa.crime.contribution;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Hooks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan
public class CrownCourtContributionApplication {

    public static void main(String[] args) {
        log.info("********** CrownCourtContributionApplication start **************");
        Hooks.enableAutomaticContextPropagation();
        SpringApplication.run(CrownCourtContributionApplication.class, args);
    }
}
