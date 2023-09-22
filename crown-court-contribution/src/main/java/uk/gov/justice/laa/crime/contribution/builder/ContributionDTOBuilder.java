package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.contribution.model.maat_api.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.model.maat_api.CreateContributionRequest;
import uk.gov.justice.laa.crime.contribution.util.DateUtil;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContributionDTOBuilder {

    public static CalculateContributionDTO build(final ApiMaatCalculateContributionRequest request) {
        CalculateContributionDTO.CalculateContributionDTOBuilder builder = CalculateContributionDTO.builder()
                .contributionId(request.getContributionId())
                .applId(request.getApplId())
                .repId(request.getRepId())
                .contributionFileId(request.getContributionFileId())
                .effectiveDate(DateUtil.parseLocalDate(request.getEffectiveDate()))
                .calcDate(DateUtil.parseLocalDate(request.getCalcDate()))
                .contributionCap(request.getContributionCap())
                .monthlyContributions(request.getMonthlyContributions())
                .upfrontContributions(request.getUpfrontContributions())
                .upliftApplied(request.getUpliftApplied())
                .basedOn(request.getBasedOn())
                .transferStatus(request.getTransferStatus() != null ? request.getTransferStatus() : null)
                .dateUpliftApplied(DateUtil.parseLocalDate(request.getDateUpliftApplied()))
                .dateUpliftRemoved(DateUtil.parseLocalDate(request.getDateUpliftRemoved()))
                .createContributionOrder(request.getCreateContributionOrder())
                .correspondenceId(request.getCorrespondenceId())
                .active(request.getActive())
                .replacedDate(DateUtil.parse(request.getReplacedDate()))
                .latest(request.getLatest())
                .ccOutcomeCount(request.getCcOutcomeCount())
                .caseType(request.getCaseType())
                .assessments(request.getAssessments())
                .appealType(request.getAppealType())
                .lastOutcome(request.getLastOutcome())
                .removeContribs(request.getRemoveContributions())
                .committalDate(DateUtil.parseLocalDate(request.getCommittalDate()))
                .magCourtOutcome(request.getMagCourtOutcome())
                .crownCourtSummary(request.getCrownCourtSummary())
                .disposableIncomeAfterCrownHardship(request.getDisposableIncomeAfterCrownHardship())
                .disposableIncomeAfterMagHardship(request.getDisposableIncomeAfterMagHardship())
                .totalAnnualDisposableIncome(request.getTotalAnnualDisposableIncome());

        return builder.build();

    }
}
