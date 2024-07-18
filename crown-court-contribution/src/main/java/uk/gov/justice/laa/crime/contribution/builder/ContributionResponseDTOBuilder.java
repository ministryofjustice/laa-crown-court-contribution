package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.dto.ContributionResponseDTO;
import uk.gov.justice.laa.crime.contribution.projection.CorrespondenceRuleAndTemplateInfo;

@Slf4j
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContributionResponseDTOBuilder {

    public static ContributionResponseDTO build(final CorrespondenceRuleAndTemplateInfo request) {

        ContributionResponseDTO.ContributionResponseDTOBuilder builder = ContributionResponseDTO.builder()
                .id(request.getId())
                .calcContribs(request.getCalcContribs())
                .templateDesc(request.getDescription())
                .correspondenceType(request.getCotyCorrespondenceType())
                .upliftCote(request.getUpliftCoteId())
                .reassessmentCoteId(request.getReassessmentCoteId());

        return builder.build();

    }
}
