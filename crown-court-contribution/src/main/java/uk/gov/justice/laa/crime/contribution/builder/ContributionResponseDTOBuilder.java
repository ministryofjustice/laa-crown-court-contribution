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

        log.info("id--"+ request.getId());
        log.info("Calc contribs--"+ request.getCalcContribs());
        log.info(" Description--"+ request.getDescription());
        log.info("CotyCorrespondenceType--"+ request.getCotyCorrespondenceType());
        log.info("UpliftCoteId--"+ request.getUpliftCoteId());
        log.info("ReassessmentCoteId--"+ request.getReassessmentCoteId());
        ContributionResponseDTO.ContributionResponseDTOBuilder builder = ContributionResponseDTO.builder()
                .id(request.getId())
                .calcContribution(request.getCalcContribs())
                .templateDesc(request.getDescription())
                .correspondenceType(request.getCotyCorrespondenceType())
                .upliftCote(request.getUpliftCoteId())
                .reassessmentCoteId(request.getReassessmentCoteId());

        return builder.build();

    }
}
