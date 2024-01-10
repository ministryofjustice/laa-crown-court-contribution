package uk.gov.justice.laa.crime.contribution.builder;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.model.maat_api.UpdateContributionRequest;

import static uk.gov.justice.laa.crime.contribution.util.DateUtil.convertDateToDateTime;

@ExtendWith(SoftAssertionsExtension.class)
class UpdateContributionRequestMapperTest {

    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void givenAValidContribution_whenMapIsInvoked_thenReturnUpdateContributionRequest() {

        Contribution contribution = TestModelDataBuilder.getContribution();
        UpdateContributionRequestMapper mapper = new UpdateContributionRequestMapper();

        UpdateContributionRequest request = mapper.map(contribution);

        softly.assertThat(request.getId()).isEqualTo(contribution.getId());
        softly.assertThat(request.getUserModified()).isEqualTo(contribution.getUserModified());
        softly.assertThat(request.getCorrespondenceId()).isEqualTo(contribution.getCorrespondenceId());
        softly.assertThat(request.getContributionCap()).isEqualTo(contribution.getContributionCap());
        softly.assertThat(request.getBasedOn()).isEqualTo(contribution.getBasedOn());
        softly.assertThat(request.getCalcDate()).isEqualTo(convertDateToDateTime(contribution.getCalcDate()));
        softly.assertThat(request.getMonthlyContributions()).isEqualTo(contribution.getMonthlyContributions());
        softly.assertThat(request.getCreateContributionOrder()).isEqualTo(contribution.getCreateContributionOrder());
        softly.assertThat(request.getUpfrontContributions()).isEqualTo(contribution.getUpfrontContributions());
        softly.assertThat(request.getEffectiveDate()).isEqualTo(convertDateToDateTime(contribution.getEffectiveDate()));
        softly.assertThat(request.getUpliftApplied()).isEqualTo(contribution.getUpliftApplied());
        softly.assertThat(request.getDateUpliftApplied()).isEqualTo(convertDateToDateTime(contribution.getDateUpliftApplied()));
        softly.assertThat(request.getDateUpliftRemoved()).isEqualTo(convertDateToDateTime(contribution.getDateUpliftRemoved()));
        softly.assertThat(request.getTransferStatus()).isEqualTo(contribution.getTransferStatus());
        softly.assertThat(request.getContributionFileId()).isEqualTo(contribution.getContributionFileId());
        softly.assertAll();
    }

}