package uk.gov.justice.laa.crime.contribution.staticdata.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum AssessmentStatus {
    COMPLETE("COMPLETE"),
    IN_PROGRESS("IN PROGRESS");

    public final String value;

    public static AssessmentStatus getFrom(String value) {
        if (StringUtils.isBlank(value)) return null;

        return Stream.of(AssessmentStatus.values())
                .filter(assessmentStatus -> assessmentStatus.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Assessment status with value: %s does not exist.", value)));
    }
}
