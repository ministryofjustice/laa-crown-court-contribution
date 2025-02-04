package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.contribution.util.DateUtil;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContributionDTOBuilder {

    public static CalculateContributionDTO build(final ApiMaatCalculateContributionRequest request) {
        return CalculateContributionDTO.builder()
                .contributionId(request.getContributionId())
                .applicantId(request.getApplicantId())
                .repId(request.getRepId())
                .effectiveDate(DateUtil.parseLocalDate(request.getEffectiveDate()))
                .contributionCap(request.getContributionCap())

                // Do not default to zero if getMonthlyContributions() returns null, as the nullness
                // of this field is used to determine if the CalculateContributionDTO instance
                // represents a real contribution (where monthly contributions is not null) or an
                // initialised but empty object (where monthly contributions is null and therefore
                // represents no actual contribution).
                .monthlyContributions(request.getMonthlyContributions())

                .upfrontContributions(request.getUpfrontContributions() != null ? request.getUpfrontContributions() : BigDecimal.ZERO)
                .dateUpliftApplied(DateUtil.parseLocalDate(request.getDateUpliftApplied()))
                .dateUpliftRemoved(DateUtil.parseLocalDate(request.getDateUpliftRemoved()))
                .caseType(request.getCaseType())
                .assessments(request.getAssessments())
                .appealType(request.getAppealType())
                .removeContribs(request.getRemoveContributions())
                .committalDate(DateUtil.parseLocalDate(request.getCommittalDate()))
                .magCourtOutcome(request.getMagCourtOutcome())
                .crownCourtOutcomeList(request.getCrownCourtOutcome())
                .disposableIncomeAfterCrownHardship(request.getDisposableIncomeAfterCrownHardship())
                .disposableIncomeAfterMagHardship(request.getDisposableIncomeAfterMagHardship())
                .totalAnnualDisposableIncome(request.getTotalAnnualDisposableIncome())
                .userCreated(request.getUserCreated())
                .calcDate(LocalDate.now())
                .build();
    }

    public static void build(final CalculateContributionDTO contributionDTO,
                             final ApiMaatCalculateContributionResponse response) {

        contributionDTO.setContributionCap(response.getContributionCap());
        contributionDTO.setEffectiveDate(DateUtil.parseLocalDate(response.getEffectiveDate()));
        contributionDTO.setMonthlyContributions(response.getMonthlyContributions());
        contributionDTO.setUpfrontContributions(response.getUpfrontContributions());
        contributionDTO.setUpliftApplied(response.getUpliftApplied());
        contributionDTO.setBasedOn(response.getBasedOn());
    }
}
