package uk.gov.justice.laa.crime.contribution.config;

public class MockServicesConfiguration {

    public static ServicesConfiguration getConfiguration(int port) {
        String host = String.format("http://localhost:%s", port);
        ServicesConfiguration servicesConfiguration = new ServicesConfiguration();
        ServicesConfiguration.MaatApi maatApiConfiguration = new ServicesConfiguration.MaatApi();
        ServicesConfiguration.HardshipApi hardshipApiConfiguration = new ServicesConfiguration.HardshipApi();

        ServicesConfiguration.MaatApi.ContributionEndpoints contributionEndpoints =
                new ServicesConfiguration.MaatApi.ContributionEndpoints(
                        "/contributions",
                        "/contributions/{repId}",
                        "/contributions/{repId}/summary",
                        "/rep-orders/{repId}",
                        "/contribution-calc-params/{effectiveDate}"
                );

        ServicesConfiguration.MaatApi.CorrespondenceStateEndpoints correspondenceStateEndpoints =
                new ServicesConfiguration.MaatApi.CorrespondenceStateEndpoints(
                        "/correspondence-state", "/correspondence-state/{repId}"
                );

        ServicesConfiguration.MaatApi.RepOrderEndpoints repOrderEndpoints =
                new ServicesConfiguration.MaatApi.RepOrderEndpoints("/rep-orders/cc-outcome/reporder/{repId}");

        ServicesConfiguration.HardshipApi.HardshipEndpoints hardshipEndpoints =
                new ServicesConfiguration.HardshipApi.HardshipEndpoints("/api/internal/v1/hardship/calculate-hardship-for-detail");

        maatApiConfiguration.setBaseUrl(host);
        servicesConfiguration.setMaatApi(maatApiConfiguration);
        maatApiConfiguration.setContributionEndpoints(contributionEndpoints);
        maatApiConfiguration.setCorrespondenceStateEndpoints(correspondenceStateEndpoints);
        maatApiConfiguration.setRepOrderEndpoints(repOrderEndpoints);

        hardshipApiConfiguration.setBaseUrl(host);
        servicesConfiguration.setHardshipApi(hardshipApiConfiguration);
        hardshipApiConfiguration.setHardshipEndpoints(hardshipEndpoints);
        return servicesConfiguration;
    }
}
