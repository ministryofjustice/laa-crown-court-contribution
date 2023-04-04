package uk.gov.justice.laa.crime.contribution.staticdata.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "contribution_rules", schema = "crown_court_contribution")
public class ContributionRulesEntity {
    @Id
    @Column(name = "ID")
    private Integer id;

    @Column(name = "CASE_TYPE")
    private String caseType;

    @Column(name = "MCOO_OUTCOME")
    private String magistratesCourtOutcome;

    @Column(name = "CCOO_OUTCOME")
    private String crownCourtOutcome;

    @Column(name = "VARIATION")
    private String variation;

    @Column(name = "VARIATION_RULE")
    private String variationRule;

}