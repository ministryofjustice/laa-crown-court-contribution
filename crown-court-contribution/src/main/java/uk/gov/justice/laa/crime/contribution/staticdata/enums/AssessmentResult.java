package uk.gov.justice.laa.crime.contribution.staticdata.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum AssessmentResult {
    PASS("PASS"),
    FAIL("FAIL"),
    INEL("INEL"),
    TEMP ("TEMP"),
    FAIL_CONTINUE("FAIL CONTINUE"),
    FULL("FULL"),
    HARDSHIP_APPLICATION("HARDSHIP_APPLICATION");

    public final String value;

    public static AssessmentResult getFrom(String value) {
        if (StringUtils.isBlank(value)) return null;

        return Stream.of(AssessmentResult.values())
                .filter(assessmentResult -> assessmentResult.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Assessment result with value: %s does not exist.", value)));
    }
}
