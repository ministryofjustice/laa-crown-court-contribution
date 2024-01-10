package uk.gov.justice.laa.crime.contribution.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContributionCalcParametersDTO {
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private BigDecimal disposableIncomePercent;
    private BigDecimal upliftedIncomePercent;
    private Integer totalMonths;
    private Integer upfrontTotalMonths;
    private BigDecimal minUpliftedMonthlyAmount;
    private BigDecimal interestRate;
    private Integer firstReminderDaysDue;
    private Integer secondReminderDaysDue;
    private BigDecimal minimumMonthlyAmount;
}
