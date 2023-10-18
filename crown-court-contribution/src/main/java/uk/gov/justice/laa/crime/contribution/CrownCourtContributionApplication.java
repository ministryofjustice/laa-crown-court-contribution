package uk.gov.justice.laa.crime.contribution;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.tracing.zipkin.ZipkinAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@Slf4j
@SpringBootApplication (exclude = ZipkinAutoConfiguration.class)
@ConfigurationPropertiesScan
public class CrownCourtContributionApplication {

	public static void main(String[] args) {
		log.info("********** CrownCourtContributionApplication start **************");
		SpringApplication.run(CrownCourtContributionApplication.class, args);
	}

}
