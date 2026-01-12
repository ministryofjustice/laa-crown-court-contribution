package uk.gov.justice.laa.crime.contribution.staticdata.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MeansAssessmentResult {
    INEL("INEL"),
    NONE("NONE"),
    FAIL("FAIL"),
    PASS("PASS"),
    PASSPORT("PASSPORT"),
    FAILPORT("FAILPORT"),
    INIT_FAIL("INITFAIL"),
    INIT_PASS("INITPASS");

    private final String result;
}
