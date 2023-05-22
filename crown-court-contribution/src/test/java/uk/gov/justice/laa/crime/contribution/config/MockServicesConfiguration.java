package uk.gov.justice.laa.crime.contribution.config;

public class MockServicesConfiguration {

    public static ServicesConfiguration getConfiguration(int port) {

        String host = String.format("http://localhost:%s", port);

        ServicesConfiguration servicesConfiguration = new ServicesConfiguration();
        ServicesConfiguration.MaatApi maatApiConfiguration = new ServicesConfiguration.MaatApi();

        ServicesConfiguration.MaatApi.ContributionEndpoints contributionEndpoints =
                new ServicesConfiguration.MaatApi.ContributionEndpoints(
                        "/contributions",
                        "/contributions/{repId}",
                        "/contribution-appeal"
                );

        ServicesConfiguration.MaatApi.CorrespondenceStateEndpoints correspondenceStateEndpoints =
                new ServicesConfiguration.MaatApi.CorrespondenceStateEndpoints(
                        "/correspondence-state", "/correspondence-state/{repId}"
                );

        maatApiConfiguration.setBaseUrl(host);
        servicesConfiguration.setMaatApi(maatApiConfiguration);
        maatApiConfiguration.setContributionEndpoints(contributionEndpoints);
        maatApiConfiguration.setCorrespondenceStateEndpoints(correspondenceStateEndpoints);

        return servicesConfiguration;
    }
}