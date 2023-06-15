package uk.gov.justice.laa.crime.contribution.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContributionResponseDTO {
    private Integer id;
    private String calcContribution;
    private String correspondenceType;
    private String correspondenceTypeDesc;
    private Integer upliftCote;
    private Integer reassessmentCoteId;
    private String templateDesc;
    private Integer template;
    private String header;
    private char doContribs;
    private char calcContribs;
}
