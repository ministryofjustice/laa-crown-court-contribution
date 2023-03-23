package uk.gov.justice.laa.crime.contribution.staticdata.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ContributionRulesTest {

    @Test
    void givenValidContributionRulesId_whenGetFromIsInvoked_thenCorrectEnumIsReturned() {
        assertThat(ContributionRules.getFrom(11010055)).isEqualTo(ContributionRules.INDICTABLE_11010055);
    }

    @Test
    void givenBlankString_whenGetFromIsInvoked_thenNullIsReturned() {
        assertThat(ContributionRules.getFrom(null)).isNull();
    }

    @Test
    void givenInvalidResultString_whenGetFromIsInvoked_thenExceptionIsThrown() {
        assertThatThrownBy(() -> ContributionRules.getFrom(100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Contribution Rules Id with value: 100 does not exist.");
    }

    @Test
    void givenValidInput_ValidateEnumValues() {
        assertThat(11010057).isEqualTo(ContributionRules.INDICTABLE_11010057.getId());
        assertThat("INDICTABLE").isEqualTo(ContributionRules.INDICTABLE_11010057.getCaseType());
        assertThat("COMMITTED FOR TRIAL").isEqualTo(ContributionRules.INDICTABLE_11010057.getMagistratesCourtOutcome());
        assertThat("AQUITTED").isEqualTo(ContributionRules.INDICTABLE_11010057.getCrownCourtOutcome());
        assertThat("SOL COSTS").isEqualTo(ContributionRules.EITHER_WAY_11010322.getVariation());
        assertThat("+").isEqualTo(ContributionRules.EITHER_WAY_11010322.getVariationRule());
    }

}