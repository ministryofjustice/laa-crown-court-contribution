package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.contribution.model.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.contribution.util.DateUtil;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContributionDTOBuilder {

    public static CalculateContributionDTO build(final ApiMaatCalculateContributionRequest request) {
        return CalculateContributionDTO.builder()
                .contributionId(request.getContributionId())
                .applId(request.getApplId())
                .repId(request.getRepId())
                .effectiveDate(DateUtil.parseLocalDate(request.getEffectiveDate()))
                .contributionCap(request.getContributionCap())
                .monthlyContributions(request.getMonthlyContributions())
                .upfrontContributions(request.getUpfrontContributions())
                .dateUpliftApplied(DateUtil.parseLocalDate(request.getDateUpliftApplied()))
                .dateUpliftRemoved(DateUtil.parseLocalDate(request.getDateUpliftRemoved()))
                .caseType(request.getCaseType())
                .assessments(request.getAssessments())
                .appealType(request.getAppealType())
                .lastOutcome(request.getLastOutcome())
                .removeContribs(request.getRemoveContributions())
                .committalDate(DateUtil.parseLocalDate(request.getCommittalDate()))
                .magCourtOutcome(request.getMagCourtOutcome())
                .crownCourtOutcomeList(request.getCrownCourtOutcome())
                .disposableIncomeAfterCrownHardship(request.getDisposableIncomeAfterCrownHardship())
                .disposableIncomeAfterMagHardship(request.getDisposableIncomeAfterMagHardship())
                .totalAnnualDisposableIncome(request.getTotalAnnualDisposableIncome())
                .build();
    }
}
