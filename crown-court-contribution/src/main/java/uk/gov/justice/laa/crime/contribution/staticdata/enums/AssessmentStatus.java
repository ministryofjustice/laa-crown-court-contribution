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

    private static final String EXCEPTION_MESSAGE = "Assessment status with value: %s does not exist";

    private final String value;

    public static AssessmentStatus getFrom(String value) {
        if (StringUtils.isBlank(value)) return null;

        return Stream.of(AssessmentStatus.values())
                .filter(assessmentStatus -> assessmentStatus.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format(EXCEPTION_MESSAGE, value)));
    }
}
