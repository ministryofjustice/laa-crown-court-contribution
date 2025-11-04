package uk.gov.justice.laa.crime.contribution.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.crime.common.model.common.ApiCrownCourtOutcome;
import uk.gov.justice.laa.crime.common.model.contribution.ApiAssessment;
import uk.gov.justice.laa.crime.enums.AppealType;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;
import uk.gov.justice.laa.crime.enums.contribution.TransferStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalculateContributionDTO {

    private Integer contributionId;
    private Integer applicantId;
    private Integer repId;
    private Integer contributionFileId;
    private LocalDate effectiveDate;
    private LocalDate calcDate;
    private BigDecimal contributionCap;
    private BigDecimal monthlyContributions;
    private BigDecimal upfrontContributions;
    private String upliftApplied;
    private String basedOn;
    private TransferStatus transferStatus;
    private LocalDate dateUpliftApplied;
    private LocalDate dateUpliftRemoved;
    private String userCreated;
    private String createContributionOrder;
    private CaseType caseType;
    private List<ApiAssessment> assessments;
    private AppealType appealType;
    private MagCourtOutcome magCourtOutcome;
    private RepOrderDTO repOrderDTO;
    private String removeContribs;
    private LocalDate committalDate;
    private String applicationStatus;
    private Integer totalMonths;
    private List<ApiCrownCourtOutcome> crownCourtOutcomeList;
    private BigDecimal disposableIncomeAfterCrownHardship;
    private BigDecimal disposableIncomeAfterMagHardship;
    private BigDecimal totalAnnualDisposableIncome;
}
