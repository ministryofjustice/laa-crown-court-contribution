package uk.gov.justice.laa.crime.contribution.config;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "services")
public class ServicesConfiguration {

    @NotNull
    private MaatApi maatApi;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MaatApi {

        @NotNull
        private String baseUrl;

        @NotNull
        private ContributionEndpoints contributionEndpoints;

        @NotNull
        private CorrespondenceStateEndpoints correspondenceStateEndpoints;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ContributionEndpoints {

            @NotNull
            private String baseUrl;

            @NotNull
            private String findUrl;

            @NotNull
            private String getAppealAmountUrl;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class CorrespondenceStateEndpoints {

            @NotNull
            private String baseUrl;

            @NotNull
            private String findUrl;

        }
    }
}