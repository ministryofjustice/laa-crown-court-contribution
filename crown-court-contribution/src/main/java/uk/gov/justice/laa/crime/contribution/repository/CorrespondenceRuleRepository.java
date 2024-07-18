package uk.gov.justice.laa.crime.contribution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.crime.contribution.entity.CorrespondenceRule;
import uk.gov.justice.laa.crime.contribution.projection.CorrespondenceRuleAndTemplateInfo;

import java.util.Optional;

@Repository
public interface CorrespondenceRuleRepository extends JpaRepository<CorrespondenceRule, Integer> {

    @Query(value = "SELECT R.CALC_CONTRIBS AS calcContribs, R.UPLIFT_COTE_ID AS upliftCoteId, " +
            " R.REASSESSMENT_COTE_ID AS reassessmentCoteId, T.ID, T.COTY_CORRESPONDENCE_TYPE AS cotyCorrespondenceType, " +
            " T.DESCRIPTION AS description " +
            "FROM crown_court_contribution.CORRESPONDENCE_RULES R JOIN crown_court_contribution.CORRESPONDENCE_TEMPLATES T on (T.ID = R.COTE_ID) " +
            "WHERE   MEANS_RESULT = :meansResult " +
            "AND (R.IOJ_RESULT = :iojResult or  IOJ_RESULT = 'ANY' ) " +
            "AND (R.MCOO_OUTCOME = :magsOutcome OR MCOO_OUTCOME = 'ANY' OR (MCOO_OUTCOME = 'NONE' and :magsOutcome is null )) " +
            "AND (R.CCOO_OUTCOME = :ccOutcome OR    R.CCOO_OUTCOME = 'ANY' OR    (R.CCOO_OUTCOME = 'NONE' and :ccOutcome is null)) " +
            "AND (INIT_RESULT  = 'ANY' OR INIT_RESULT  = :initResult );", nativeQuery = true)
    Optional<CorrespondenceRuleAndTemplateInfo> getCoteInfo(String meansResult,
                                                            String iojResult,
                                                            String magsOutcome,
                                                            String ccOutcome,
                                                            String initResult
    );

}