package uk.gov.justice.laa.crime.contribution.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.crime.contribution.model.maat_api.ApiCrownCourtSummary;
import uk.gov.justice.laa.crime.contribution.model.common.ApiAssessment;
import uk.gov.justice.laa.crime.contribution.model.maat_api.LastOutcome;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.AppealType;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CaseType;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.MagCourtOutcome;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.TransferStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalculateContributionDTO {

    private String laaTransactionId;
    private Integer contributionId;
    private Integer applId;
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
//    private LocalDateTime dateCreated;
    private String userCreated;
//    private LocalDateTime dateModified;
//    private String userModified;
    private String createContributionOrder;
    private Integer correspondenceId;
    private String active;
    private LocalDate replacedDate;
    private Boolean latest;
    private Integer ccOutcomeCount;
    private CaseType caseType;
    private List<ApiAssessment> assessments;
    private AppealType appealType;
    private LastOutcome lastOutcome;
    private MagCourtOutcome magCourtOutcome;
    private RepOrderDTO repOrderDTO;
    private String removeContribs;
    private LocalDate committalDate;
    private String applicationStatus;
    private ApiCrownCourtSummary crownCourtSummary;
    private BigDecimal disposableIncomeAfterCrownHardship;
    private BigDecimal disposableIncomeAfterMagHardship;
    private BigDecimal totalAnnualDisposableIncome;
}
