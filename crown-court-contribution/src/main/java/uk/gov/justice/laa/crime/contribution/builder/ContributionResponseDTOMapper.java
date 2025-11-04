package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.contribution.dto.ContributionResponseDTO;
import uk.gov.justice.laa.crime.contribution.projection.CorrespondenceRuleAndTemplateInfo;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CorrespondenceType;

import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ContributionResponseDTOMapper {
    public void map(CorrespondenceRuleAndTemplateInfo processedCases, ContributionResponseDTO contributionResponse) {
        contributionResponse.setId(processedCases.getId());
        contributionResponse.setCalcContribs(processedCases.getCalcContribs());
        contributionResponse.setTemplateDesc(processedCases.getDescription());
        contributionResponse.setTemplate(processedCases.getId());
        contributionResponse.setCorrespondenceType(processedCases.getCotyCorrespondenceType());
        contributionResponse.setUpliftCote(processedCases.getUpliftCoteId());
        contributionResponse.setReassessmentCoteId(processedCases.getReassessmentCoteId());
        CorrespondenceType correspondenceType = CorrespondenceType.getFrom(processedCases.getCotyCorrespondenceType());
        if (correspondenceType != null) {
            contributionResponse.setCorrespondenceTypeDesc(correspondenceType.getDescription());
        }
    }
}
