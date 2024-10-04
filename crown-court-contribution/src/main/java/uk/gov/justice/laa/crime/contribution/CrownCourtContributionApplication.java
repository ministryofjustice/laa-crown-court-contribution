package uk.gov.justice.laa.crime.contribution;

import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import reactor.core.publisher.Hooks;

@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan
public class CrownCourtContributionApplication {

	public static void main(String[] args) {
		log.info("********** CrownCourtContributionApplication start **************");
		Hooks.enableAutomaticContextPropagation();
		SpringApplication.run(CrownCourtContributionApplication.class, args);
		Sentry.captureMessage("This sentry connection");
		try {
			throw new RuntimeException("Test error");
		} catch (Exception e) {
			Sentry.captureException(e);
		}
		Sentry.close();
	}

}
