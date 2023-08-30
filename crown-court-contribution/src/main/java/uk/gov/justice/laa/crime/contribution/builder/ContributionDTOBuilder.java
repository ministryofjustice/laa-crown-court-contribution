package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.dto.ContributionDTO;
import uk.gov.justice.laa.crime.contribution.model.CalculateContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.util.DateUtil;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContributionDTOBuilder {

    public static ContributionDTO build(final CalculateContributionRequest request) {
        ContributionDTO.ContributionDTOBuilder builder = ContributionDTO.builder()
                .id(request.getRepId())
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
                .dateCreated(request.getDateCreated())
                .userCreated(request.getUserCreated())
                .dateModified(request.getDateModified())
                .userModified(request.getUserModified())
                .createContributionOrder(request.getCreateContributionOrder())
                .correspondenceId(request.getCorrespondenceId())
                .active(request.getActive())
                .replacedDate(DateUtil.parse(request.getReplacedDate()))
                .latest(request.getLatest())
                .ccOutcomeCount(request.getCcOutcomeCount())
                .seHistoryId(request.getSeHistoryId())
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

    public static ContributionDTO build(Contribution contribution) {
        ContributionDTO.ContributionDTOBuilder builder = ContributionDTO.builder()
                .id(contribution.getId())
                .applId(contribution.getApplId())
                .repId(contribution.getRepId())
                .contributionFileId(contribution.getContributionFileId())
                .effectiveDate(contribution.getEffectiveDate())
                .calcDate(contribution.getCalcDate())
                .contributionCap(contribution.getContributionCap())
                .monthlyContributions(contribution.getMonthlyContributions())
                .upfrontContributions(contribution.getUpfrontContributions())
                .upliftApplied(contribution.getUpliftApplied())
                .basedOn(contribution.getBasedOn())
                .transferStatus(contribution.getTransferStatus())
                .dateUpliftApplied(contribution.getDateUpliftApplied())
                .dateUpliftRemoved(contribution.getDateUpliftRemoved())
                .dateCreated(contribution.getDateCreated())
                .userCreated(contribution.getUserCreated())
                .dateModified(contribution.getDateModified())
                .userModified(contribution.getUserModified())
                .createContributionOrder(contribution.getCreateContributionOrder())
                .correspondenceId(contribution.getCorrespondenceId())
                .active(contribution.getActive())
                .replacedDate(contribution.getReplacedDate())
                .latest(contribution.getLatest())
                .ccOutcomeCount(contribution.getCcOutcomeCount())
                .seHistoryId(contribution.getSeHistoryId());

        return builder.build();

    }
}
