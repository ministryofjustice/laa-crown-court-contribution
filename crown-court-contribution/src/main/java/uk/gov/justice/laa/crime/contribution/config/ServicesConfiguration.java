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

    @NotNull
    private HardshipApi hardshipApi;

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

        @NotNull
        private RepOrderEndpoints repOrderEndpoints;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ContributionEndpoints {

            @NotNull
            private String baseUrl;

            @NotNull
            private String findUrl;

            @NotNull
            private String summaryUrl;

            @NotNull
            private String getAppealAmountUrl;

            @NotNull
            private String getContributionCountUrl;

            @NotNull
            private String getRepOrderUrl;

            @NotNull
            private String contribsParametersUrl;

            @NotNull
            private String findLatestSentContributionUrl;
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

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class RepOrderEndpoints {
            @NotNull
            private String findOutcomeUrl;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HardshipApi {
        @NotNull
        private String baseUrl;

        @NotNull
        private HardshipEndpoints hardshipEndpoints;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class HardshipEndpoints {
            @NotNull
            private String calculateHardshipForDetailUrl;
        }
    }
}