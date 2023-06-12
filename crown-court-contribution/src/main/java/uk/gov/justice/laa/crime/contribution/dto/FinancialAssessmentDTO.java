package uk.gov.justice.laa.crime.contribution.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FinancialAssessmentDTO {

    private Integer id;
    private Integer repId;
    private Integer initialAscrId;
    private String assessmentType;
    private LocalDateTime dateCreated;
    private String userCreated;
    private Integer cmuId;
    private String fassInitStatus;
    private LocalDateTime initialAssessmentDate;
    private String initOtherBenefitNote;
    private String initOtherIncomeNote;
    private BigDecimal initTotAggregatedIncome;
    private BigDecimal initAdjustedIncomeValue;
    private String initNotes;
    private String initResult;
    private String initResultReason;
    private LocalDateTime incomeEvidenceDueDate;
    private LocalDateTime incomeUpliftRemoveDate;
    private LocalDateTime incomeUpliftApplyDate;
    private String incomeEvidenceNotes;
    private String initApplicationEmploymentStatus;
    private String fassFullStatus;
    private LocalDateTime fullAssessmentDate;
    private String fullResultReason;
    private String fullAssessmentNotes;
    private String fullResult;
    private BigDecimal fullAdjustedLivingAllowance;
    private BigDecimal fullTotalAnnualDisposableIncome;
    private String fullOtherHousingNote;
    private BigDecimal fullTotalAggregatedExpenses;
    private Integer fullAscrId;
    private LocalDateTime dateCompleted;
    private LocalDateTime updated;
    private String userModified;
    private Integer usn;
    private String rtCode;
    @Builder.Default
    private String replaced = "N";
    private LocalDateTime firstReminderDate;
    private LocalDateTime secondReminderDate;
    private LocalDateTime evidenceReceivedDate;
}
