package uk.gov.justice.laa.crime.contribution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.crime.contribution.entity.CorrespondenceRule;
import uk.gov.justice.laa.crime.contribution.projection.CorrespondenceRuleAndTemplateInfo;

@Repository
public interface CorrespondenceRuleRepository extends JpaRepository<CorrespondenceRule, Integer> {

    @Query(value = "SELECT R.CALC_CONTRIBS, R.UPLIFT_COTE_ID, R.REASSESSMENT_COTE_ID, T.ID, T.COTY_CORRESPONDENCE_TYPE, T.DESCRIPTION " +
            "FROM crown_court_contribution.CORRESPONDENCE_RULES R JOIN crown_court_contribution.CORRESPONDENCE_TEMPLATES T on (T.ID = R.COTE_ID) " +
            "WHERE   MEANS_RESULT = :meansResult " +
            "AND (R.IOJ_RESULT = :iojResult or  IOJ_RESULT = 'ANY' ) " +
            "AND (R.MCOO_OUTCOME = :magsOutcome OR MCOO_OUTCOME = 'ANY' OR MCOO_OUTCOME = 'NONE' ) " +
            "AND (R.CCOO_OUTCOME = :ccSummaryOutcome OR    R.CCOO_OUTCOME = 'ANY' OR    R.CCOO_OUTCOME = 'NONE' ) " +
            "AND (INIT_RESULT  = 'ANY' OR INIT_RESULT  = :initResult );", nativeQuery = true)
    CorrespondenceRuleAndTemplateInfo getCoteInfo(String meansResult,
                                                  String iojResult,
                                                  String magsOutcome,
                                                  String ccSummaryOutcome,
                                                  String initResult
    );

}