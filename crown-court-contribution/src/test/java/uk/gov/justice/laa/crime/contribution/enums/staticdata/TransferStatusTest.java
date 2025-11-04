package uk.gov.justice.laa.crime.contribution.enums.staticdata;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import uk.gov.justice.laa.crime.enums.contribution.TransferStatus;

import org.junit.jupiter.api.Test;

class TransferStatusTest {
    @Test
    void givenValidValue_whenGetFromIsInvoked_thenValidTransferStatusIsReturned() {
        assertThat(TransferStatus.getFrom("SENT")).isEqualTo(TransferStatus.SENT);
    }

    @Test
    void givenBlankString_whenGetFromIsInvoked_thenNullIsReturned() {
        assertThat(TransferStatus.getFrom("")).isNull();
    }

    @Test
    void givenInvalidValue_whenGetFromIsInvoked_thenExceptionIsRaised() {
        assertThatThrownBy(() -> TransferStatus.getFrom("FLIBBLE")).isInstanceOf(IllegalArgumentException.class);
    }
}
