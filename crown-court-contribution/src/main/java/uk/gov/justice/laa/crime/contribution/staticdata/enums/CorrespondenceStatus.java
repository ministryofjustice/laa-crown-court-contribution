package uk.gov.justice.laa.crime.contribution.staticdata.enums;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CorrespondenceStatus {
    APPEAL_CC("appealCC"),
    CDS15("cds15"),
    REASS("re-ass"),
    NONE("none");

    @NotNull
    private final String status;

}