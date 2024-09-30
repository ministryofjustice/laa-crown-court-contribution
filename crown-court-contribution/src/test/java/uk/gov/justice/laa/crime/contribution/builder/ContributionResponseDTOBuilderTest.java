package uk.gov.justice.laa.crime.contribution.builder;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.ContributionResponseDTO;
import uk.gov.justice.laa.crime.contribution.projection.CorrespondenceRuleAndTemplateInfo;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CorrespondenceType;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SoftAssertionsExtension.class)
class ContributionResponseDTOBuilderTest {

    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void givenAValidCorrespondenceRuleAndTemplateInfo_whenMapIsInvoked_thenContributionResponseDTOIsMappedCorrectly() {

        CorrespondenceRuleAndTemplateInfo correspondence = TestModelDataBuilder.getCorrespondenceRuleAndTemplateInfo();
        ContributionResponseDTO contributionResponse = ContributionResponseDTO.builder().build();
        ContributionResponseDTOMapper contributionResponseDTOMapper = new ContributionResponseDTOMapper();
        contributionResponseDTOMapper.map(correspondence, contributionResponse);
        softly.assertThat(contributionResponse.getId()).isEqualTo(correspondence.getId());
        softly.assertThat(contributionResponse.getCalcContribs()).isEqualTo(correspondence.getCalcContribs());
        softly.assertThat(contributionResponse.getTemplateDesc()).isEqualTo(correspondence.getDescription());
        softly.assertThat(contributionResponse.getTemplate()).isEqualTo(correspondence.getId());
        softly.assertThat(contributionResponse.getCorrespondenceType()).isEqualTo(correspondence.getCotyCorrespondenceType());
        softly.assertThat(contributionResponse.getUpliftCote()).isEqualTo(correspondence.getUpliftCoteId());
        softly.assertThat(contributionResponse.getReassessmentCoteId()).isEqualTo(correspondence.getReassessmentCoteId());
        softly.assertThat(contributionResponse.getCorrespondenceTypeDesc()).isEqualTo(
                CorrespondenceType.getFrom(correspondence.getCotyCorrespondenceType()).getDescription());

        softly.assertAll();
    }

    @Test
    void givenEmptyCorrespondence_whenMapIsInvoked_thenContributionResponseDTOIsMappedCorrectly() {
        CorrespondenceRuleAndTemplateInfo correspondence = TestModelDataBuilder.getEmptyCorrespondenceRuleAndTemplateInfo();
        ContributionResponseDTO contributionResponse = ContributionResponseDTO.builder().build();
        ContributionResponseDTOMapper contributionResponseDTOMapper = new ContributionResponseDTOMapper();
        contributionResponseDTOMapper.map(correspondence, contributionResponse);
        assertThat(contributionResponse.getCorrespondenceTypeDesc()).isNull();
    }
}