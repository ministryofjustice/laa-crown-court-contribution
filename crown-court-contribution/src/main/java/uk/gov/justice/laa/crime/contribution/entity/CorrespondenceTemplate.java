package uk.gov.justice.laa.crime.contribution.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "correspondence_templates", schema = "crown_court_contribution", indexes = {
        @Index(name = "cote_uk", columnList = "id, effective_date, template, coty_correspondence_type", unique = true)
})
public class CorrespondenceTemplate {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 20)
    @Column(name = "coty_correspondence_type", length = 20)
    private String cotyCorrespondenceType;

    @Size(max = 20)
    @Column(name = "template", length = 20)
    private String template;

    @Size(max = 250)
    @Column(name = "description", length = 250)
    private String description;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @OneToMany(mappedBy = "cote")
    private Set<CorrespondenceRule> correspondenceRules = new LinkedHashSet<>();

    @OneToMany(mappedBy = "upliftCote")
    private Set<CorrespondenceRule> correspondenceRulesUC = new LinkedHashSet<>();

    @OneToMany(mappedBy = "reassessmentCote")
    private Set<CorrespondenceRule> correspondenceRulesRC = new LinkedHashSet<>();

}