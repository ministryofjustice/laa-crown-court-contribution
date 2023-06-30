package uk.gov.justice.laa.crime.contribution.staticdata.enums;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum CorrespondenceStatus {
    APPEAL_CC("appealCC"),
    CDS15("cds15"),
    REASS("re-ass"),
    NONE("none");

    @NotNull
    @JsonValue
    @JsonPropertyDescription("Specifies the case type")
    private final String status;

}