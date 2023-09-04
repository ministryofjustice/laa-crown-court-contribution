package uk.gov.justice.laa.crime.contribution.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.contribution.common.Constants;
import uk.gov.justice.laa.crime.contribution.model.ApiCalculateContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.ApiCalculateContributionResponse;
import uk.gov.justice.laa.crime.contribution.util.CalculateContributionUtil;

import java.math.BigDecimal;


@Slf4j
@Service
@RequiredArgsConstructor
public class CalculateContributionService {
    public ApiCalculateContributionResponse calcualteContibution(ApiCalculateContributionRequest request) {
        ApiCalculateContributionResponse response = new ApiCalculateContributionResponse();
        if(request.getUpliftApplied()) {
            BigDecimal monthlyContributions = CalculateContributionUtil.calculateUpliftedMonthlyAmount(request.getAnnualDisposableIncome(),
                    request.getUpliftedIncomePercent(), request.getMinUpliftedMonthlyAmount());
            response.setMonthlyContributions(monthlyContributions);
            response.setUpliftApplied(Constants.Y);
        } else {
            BigDecimal monthlyContributions = CalculateContributionUtil.calculateMonthlyContribution(request.getAnnualDisposableIncome(),
                    request.getDisposableIncomePercent(),
                    request.getMinimumMonthlyAmount());
            if (monthlyContributions.compareTo(request.getContributionCap()) > 0) {
                monthlyContributions = request.getContributionCap();
                response.setMonthlyContributions(monthlyContributions);
                response.setBasedOn(Constants.OFFENCE_TYPE);
            }
            response.setUpliftApplied(Constants.N);
            response.setMonthlyContributions(monthlyContributions);
            response.setBasedOn(Constants.MEANS);
            response.setUpfrontContributions(CalculateContributionUtil.calculateUpfrontContributions(monthlyContributions,
                    request.getContributionCap(), request.getUpfrontTotalMonths()));
        }
        return response;
    }

}