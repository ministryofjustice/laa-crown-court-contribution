package uk.gov.justice.laa.crime.contribution.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.TransferStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contribution {

    private Integer id;
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
    private LocalDateTime dateCreated;
    private String userCreated;
    private LocalDateTime dateModified;
    private String userModified;
    private String createContributionOrder;
    private Integer correspondenceId;
    private String active;
    private LocalDate replacedDate;
    private Boolean latest;
    private Integer ccOutcomeCount;
    private Integer seHistoryId;
    private String calculationRan;
}
