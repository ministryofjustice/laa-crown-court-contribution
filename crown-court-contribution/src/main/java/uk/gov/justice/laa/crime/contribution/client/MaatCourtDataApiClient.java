package uk.gov.justice.laa.crime.contribution.client;

import uk.gov.justice.laa.crime.common.model.contribution.maat_api.CreateContributionRequest;
import uk.gov.justice.laa.crime.contribution.dto.ContributionCalcParametersDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionsSummaryDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderCCOutcomeDTO;
import uk.gov.justice.laa.crime.contribution.dto.RepOrderDTO;
import uk.gov.justice.laa.crime.contribution.model.Contribution;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange()
public interface MaatCourtDataApiClient {

    @GetExchange("/contribution-calc-params/{effectiveDate}")
    ContributionCalcParametersDTO getContributionCalcParameters(@PathVariable String effectiveDate);

    @GetExchange("/contributions/{repId}/summary")
    List<ContributionsSummaryDTO> getContributionsSummary(@PathVariable Integer repId);

    @GetExchange("/rep-orders/{repId}")
    RepOrderDTO getRepOrderByRepId(@PathVariable Integer repId);

    @GetExchange("/rep-orders/cc-outcome/reporder/{repId}")
    List<RepOrderCCOutcomeDTO> getRepOrderCCOutcomeByRepId(@PathVariable Integer repId);

    @PostExchange("/contributions")
    Contribution create(@RequestBody CreateContributionRequest request);

    @GetExchange("/contributions/{repId}")
    List<Contribution> find(@PathVariable Integer repId, @RequestParam Boolean findLatestContribution);
}
