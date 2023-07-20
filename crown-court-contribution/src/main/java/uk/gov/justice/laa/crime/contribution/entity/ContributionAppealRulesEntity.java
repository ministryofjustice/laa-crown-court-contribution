package uk.gov.justice.laa.crime.contribution.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "CONTRIB_APPEAL_RULES")
public class ContributionAppealRulesEntity {

    @Id
    @Column(name = "id")
    private Integer id;

    @NotNull
    @Column(name = "CATY_CASE_TYPE")
    private String caseType;

    @NotNull
    @Column(name = "APTY_CODE")
    private String appealTypeCode;

    @NotNull
    @Column(name = "CCOO_OUTCOME")
    private String outcome;

    @Column(name = "CONTRIB_AMOUNT")
    private BigDecimal contributionAmount;

    @NotNull
    @Column(name = "ASSESSMENT_RESULT")
    private String assessmentResult;

}