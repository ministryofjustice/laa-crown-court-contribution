package uk.gov.justice.laa.crime.contribution.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(
        name = "correspondence_rules",
        schema = "crown_court_contribution",
        indexes = {
            @Index(
                    name = "unique_corr_rule",
                    columnList = "means_result, mcoo_outcome, ccoo_outcome, ioj_result",
                    unique = true)
        })
public class CorrespondenceRule {
    @Id
    @Column(name = "id")
    private Integer id;

    @Size(max = 10)
    @Column(name = "means_result", length = 10)
    private String meansResult;

    @Size(max = 50)
    @Column(name = "mcoo_outcome", length = 50)
    private String mcooOutcome;

    @NotNull
    @Column(name = "date_from", nullable = false)
    private LocalDateTime dateFrom;

    @Column(name = "date_to")
    private LocalDateTime dateTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cote_id")
    private CorrespondenceTemplate cote;

    @Size(max = 1)
    @NotNull
    @Column(name = "calc_contribs", nullable = false, length = 1)
    private String calcContribs;

    @Size(max = 4)
    @Column(name = "ioj_result", length = 4)
    private String iojResult;

    @Size(max = 50)
    @Column(name = "ccoo_outcome", length = 50)
    private String ccooOutcome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uplift_cote_id")
    private CorrespondenceTemplate upliftCote;

    @Size(max = 10)
    @Column(name = "init_result", length = 10)
    private String initResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reassessment_cote_id")
    private CorrespondenceTemplate reassessmentCote;
}
