package uk.gov.justice.laa.crime.contribution.client;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.ApiCalculateHardshipByDetailRequest;
import uk.gov.justice.laa.crime.common.model.contribution.maat_api.ApiCalculateHardshipByDetailResponse;

@HttpExchange()
public interface HardshipApiClient {

    @PostExchange("/calculate-hardship-for-detail")
    ApiCalculateHardshipByDetailResponse calculateHardshipForDetail(@RequestBody ApiCalculateHardshipByDetailRequest request);
}
