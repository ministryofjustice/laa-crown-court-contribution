package uk.gov.justice.laa.crime.contribution.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorrespondenceState {
    private Integer repId;
    private String status;
}
