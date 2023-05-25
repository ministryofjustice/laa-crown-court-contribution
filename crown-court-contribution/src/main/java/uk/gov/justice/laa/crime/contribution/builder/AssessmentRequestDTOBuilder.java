package uk.gov.justice.laa.crime.contribution.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.dto.AssessmentRequestDTO;
import uk.gov.justice.laa.crime.contribution.dto.ContributionRequestDTO;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssessmentRequestDTOBuilder {

    public static AssessmentRequestDTO build(final ContributionRequestDTO request) {
        AssessmentRequestDTO.AssessmentRequestDTOBuilder builder = AssessmentRequestDTO.builder()
                .iojResult(request.getIojResult())
                .decisionResult(request.getDecisionResult())
                .initResult(request.getInitResult())
                .fullResult(request.getFullResult())
                .hardshipResult(request.getHardshipResult())
                .passportResult(request.getFullResult());

        return builder.build();

    }
}
