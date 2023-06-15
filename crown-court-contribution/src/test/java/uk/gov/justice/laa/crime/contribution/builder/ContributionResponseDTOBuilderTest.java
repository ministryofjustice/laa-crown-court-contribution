package uk.gov.justice.laa.crime.contribution.builder;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.ContributionResponseDTO;
import uk.gov.justice.laa.crime.contribution.projection.CorrespondenceRuleAndTemplateInfo;

@ExtendWith(SoftAssertionsExtension.class)
class ContributionResponseDTOBuilderTest {

    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void givenAValidCorrespondenceRuleAndTemplateInfo_whenBuildIsInvoked_thenReturnContributionResponseDTO() {

        CorrespondenceRuleAndTemplateInfo correspondence = TestModelDataBuilder.getCorrespondenceRuleAndTemplateInfo();
        ContributionResponseDTO contributionResponse = ContributionResponseDTOBuilder.build(correspondence);
        softly.assertThat(contributionResponse.getId()).isEqualTo(TestModelDataBuilder.CORRESPONDENCE_ID);
        softly.assertThat(contributionResponse.getTemplateDesc()).isEqualTo("No contributions required");
        softly.assertThat(contributionResponse.getCorrespondenceType()).isEqualTo("CONTRIBUTION_NOTICE");
        softly.assertThat(contributionResponse.getUpliftCote()).isEqualTo(TestModelDataBuilder.CORRESPONDENCE_ID);
        softly.assertThat(contributionResponse.getReassessmentCoteId()).isEqualTo(TestModelDataBuilder.CORRESPONDENCE_ID);
        softly.assertAll();
    }

}