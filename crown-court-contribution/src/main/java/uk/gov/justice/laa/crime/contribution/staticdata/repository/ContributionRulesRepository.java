package uk.gov.justice.laa.crime.contribution.staticdata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.crime.contribution.staticdata.entity.ContributionRulesEntity;


@Repository
public interface ContributionRulesRepository extends JpaRepository<ContributionRulesEntity, Integer> {

    ContributionRulesEntity findContributionRulesEntitiesByCaseTypeAndVariationNotNullAndMagistratesCourtOutcomeAndCrownCourtOutcome(
            String caseType, String magsCourtOutcome, String crownCourtOutcome);

}