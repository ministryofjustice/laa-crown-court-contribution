package uk.gov.justice.laa.crime.contribution.builder;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.laa.crime.contribution.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.contribution.dto.CalculateContributionDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionResponseDTO;
import uk.gov.justice.laa.crime.contribution.model.Contribution;
import uk.gov.justice.laa.crime.contribution.model.CreateContributionRequest;
import uk.gov.justice.laa.crime.contribution.projection.CorrespondenceRuleAndTemplateInfo;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.TransferStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ExtendWith(SoftAssertionsExtension.class)
class ContributionDTOBuilderTest {

    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void givenAValidContribution_whenBuildIsInvoked_thenReturnCalculateContributionDTO() {

        Contribution contribution = TestModelDataBuilder.getContribution();
        CalculateContributionDTO calculateContributionDTO = ContributionDTOBuilder.build(contribution);

        softly.assertThat(calculateContributionDTO.getId()).isEqualTo(contribution.getId());
        softly.assertThat(calculateContributionDTO.getContributionCap()).isEqualTo(contribution.getContributionCap());
        softly.assertThat(calculateContributionDTO.getMonthlyContributions()).isEqualTo(contribution.getMonthlyContributions());
        softly.assertThat(calculateContributionDTO.getContributionFileId()).isEqualTo(contribution.getContributionFileId());
        softly.assertThat(calculateContributionDTO.getUpfrontContributions()).isEqualTo(contribution.getUpfrontContributions());
        softly.assertThat(calculateContributionDTO.getActive()).isEqualTo(contribution.getActive());
        softly.assertThat(calculateContributionDTO.getCreateContributionOrder()).isEqualTo(contribution.getCreateContributionOrder());
        softly.assertThat(calculateContributionDTO.getBasedOn()).isEqualTo(contribution.getBasedOn());
        softly.assertThat(calculateContributionDTO.getReplacedDate()).isEqualTo(contribution.getReplacedDate());
        softly.assertThat(calculateContributionDTO.getRepId()).isEqualTo(contribution.getRepId());
        softly.assertThat(calculateContributionDTO.getApplId()).isEqualTo(contribution.getApplId());
        softly.assertThat(calculateContributionDTO.getCalcDate()).isEqualTo(contribution.getCalcDate());
        softly.assertThat(calculateContributionDTO.getCcOutcomeCount()).isEqualTo(contribution.getCcOutcomeCount());
        softly.assertThat(calculateContributionDTO.getDateCreated()).isEqualTo(contribution.getDateCreated());
        softly.assertThat(calculateContributionDTO.getDateModified()).isEqualTo(contribution.getDateModified());
        softly.assertThat(calculateContributionDTO.getDateUpliftApplied()).isEqualTo(contribution.getDateUpliftApplied());
        softly.assertThat(calculateContributionDTO.getDateUpliftRemoved()).isEqualTo(contribution.getDateUpliftRemoved());
        softly.assertThat(calculateContributionDTO.getEffectiveDate()).isEqualTo(contribution.getEffectiveDate());
        softly.assertThat(calculateContributionDTO.getContributionCap()).isEqualTo(contribution.getContributionCap());
        softly.assertThat(calculateContributionDTO.getUpliftApplied()).isEqualTo(contribution.getUpliftApplied());
        softly.assertThat(calculateContributionDTO.getTransferStatus()).isEqualTo(contribution.getTransferStatus());
        softly.assertThat(calculateContributionDTO.getUserCreated()).isEqualTo(contribution.getUserCreated());
        softly.assertThat(calculateContributionDTO.getDateModified()).isEqualTo(contribution.getDateModified());
        softly.assertThat(calculateContributionDTO.getUserModified()).isEqualTo(contribution.getUserModified());
        softly.assertThat(calculateContributionDTO.getCreateContributionOrder()).isEqualTo(contribution.getCreateContributionOrder());
        softly.assertThat(calculateContributionDTO.getCorrespondenceId()).isEqualTo(contribution.getCorrespondenceId());
        softly.assertThat(calculateContributionDTO.getActive()).isEqualTo(contribution.getActive());
        softly.assertThat(calculateContributionDTO.getLatest()).isEqualTo(contribution.getLatest());
        softly.assertThat(calculateContributionDTO.getSeHistoryId()).isEqualTo(contribution.getSeHistoryId());

        softly.assertAll();
    }


    @Test
    void givenAValidCreateContributionRequest_whenBuildIsInvoked_thenReturnCalculateContributionDTO() {
        CreateContributionRequest createContributionRequest = TestModelDataBuilder.getCreateContributionRequest();
        CalculateContributionDTO calculateContributionDTO = ContributionDTOBuilder.build(createContributionRequest);
        softly.assertThat(calculateContributionDTO.getContributionCap()).isEqualTo(createContributionRequest.getContributionCap());
        softly.assertThat(calculateContributionDTO.getMonthlyContributions()).isEqualTo(createContributionRequest.getMonthlyContributions());
        softly.assertThat(calculateContributionDTO.getContributionFileId()).isEqualTo(createContributionRequest.getContributionFileId());
        softly.assertThat(calculateContributionDTO.getUpfrontContributions()).isEqualTo(createContributionRequest.getUpfrontContributions());
        softly.assertThat(calculateContributionDTO.getCreateContributionOrder()).isEqualTo(createContributionRequest.getCreateContributionOrder());
        softly.assertThat(calculateContributionDTO.getBasedOn()).isEqualTo(createContributionRequest.getBasedOn());
        softly.assertThat(calculateContributionDTO.getRepId()).isEqualTo(createContributionRequest.getRepId());
        softly.assertThat(calculateContributionDTO.getApplId()).isEqualTo(createContributionRequest.getApplId());
        softly.assertThat(calculateContributionDTO.getCalcDate()).isEqualTo(createContributionRequest.getCalcDate().toLocalDate());
        softly.assertThat(calculateContributionDTO.getDateUpliftApplied()).isEqualTo(createContributionRequest.getDateUpliftApplied().toLocalDate());
        softly.assertThat(calculateContributionDTO.getDateUpliftRemoved()).isEqualTo(createContributionRequest.getDateUpliftRemoved().toLocalDate());
        softly.assertThat(calculateContributionDTO.getEffectiveDate()).isEqualTo(createContributionRequest.getEffectiveDate().toLocalDate());
        softly.assertThat(calculateContributionDTO.getContributionCap()).isEqualTo(createContributionRequest.getContributionCap());
        softly.assertThat(calculateContributionDTO.getUpliftApplied()).isEqualTo(createContributionRequest.getUpliftApplied());
        softly.assertThat(calculateContributionDTO.getTransferStatus()).isEqualTo(createContributionRequest.getTransferStatus());
        softly.assertThat(calculateContributionDTO.getUserCreated()).isEqualTo(createContributionRequest.getUserCreated());
        softly.assertThat(calculateContributionDTO.getCreateContributionOrder()).isEqualTo(createContributionRequest.getCreateContributionOrder());
        softly.assertThat(calculateContributionDTO.getCorrespondenceId()).isEqualTo(createContributionRequest.getCorrespondenceId());
        softly.assertAll();
    }


}