package uk.gov.justice.laa.crime.contribution.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class RequestBuilderUtils {

    private static final String MOCK_TOKEN = "token";

    public static MockHttpServletRequestBuilder buildRequestGivenContent(HttpMethod method, String content, String endpointUrl) {
        return buildRequestGivenContent(method, content, endpointUrl, true);
    }

    public static MockHttpServletRequestBuilder buildRequestGivenContent(HttpMethod method, String content, String endpointUrl, boolean withAuth) {
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.request(method, endpointUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Laa-Transaction-Id", "7c49ebfe-fe3a-4f2f-8dad-f7b8f03b8327")
                        .content(content);
        if (withAuth) {
            requestBuilder.header(HttpHeaders.AUTHORIZATION, "Bearer " + MOCK_TOKEN);
        }
        return requestBuilder;
    }
}
