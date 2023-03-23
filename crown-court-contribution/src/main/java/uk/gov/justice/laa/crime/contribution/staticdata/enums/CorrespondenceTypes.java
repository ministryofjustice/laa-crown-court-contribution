package uk.gov.justice.laa.crime.contribution.staticdata.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
@Getter
public enum CorrespondenceTypes {

    CONTRIBUTION_NOTICE("CONTRIBUTION_NOTICE", "Contribution Notice", "Rhybudd Cyfrannu"),
    INCOME_EVIDENCE_1("INCOME EVIDENCE 1", "Income Evidence Request", "Cais am dystiolaeth o Incwm"),
    INCOME_EVIDENCE_2("INCOME EVIDENCE 2", "Income Evidence Reminder", "Tystiolaeth o Incwm -  Llythyr Atgoffa"),
    CAPITAL_EVIDENCE("CAPITAL EVIDENCE", "Capital Evidence Request", "Cais am dystiolaeth o Gyfalaf"),
    INCOME_SANCTION("INCOME_SANCTION", "Income Sanction letter", "Sancsiwn Incwm"),
    CAPITAL_SANCTION("CAPITAL_SANCTION", "Capital Sanction Letter", "Capital Sanction Letter"),
    CONTRIBUTION_ORDER("CONTRIBUTION_ORDER", "Contribution Order", "Gorchymyn Cyfrannu"),
    REFUSAL_NOTICE("REFUSAL_NOTICE", "Notice", "Cyfrannu");

    private final String code;
    private final String description;
    private final String welshDescription;

    public static CorrespondenceTypes getFrom(String code) {
        if (StringUtils.isBlank(code)) return null;

        return Stream.of(CorrespondenceTypes.values())
                .filter(correspondenceTypes -> correspondenceTypes.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Correspondence Types with value: %s does not exist.", code)));
    }

}
