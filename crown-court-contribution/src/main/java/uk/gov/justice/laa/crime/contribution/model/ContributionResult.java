package uk.gov.justice.laa.crime.contribution.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record ContributionResult(
        BigDecimal totalAnnualDisposableIncome,
        BigDecimal monthlyAmount,
        BigDecimal upfrontAmount,
        BigDecimal contributionCap,
        int totalMonths,
        boolean isUplift,
        String basedOn,
        LocalDate effectiveDate) {}
