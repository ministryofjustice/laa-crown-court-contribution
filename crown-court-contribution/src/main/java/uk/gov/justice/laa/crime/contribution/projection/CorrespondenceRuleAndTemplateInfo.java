package uk.gov.justice.laa.crime.contribution.projection;

/**
 * A Projection for the {@link uk.gov.justice.laa.crime.contribution.entity.CorrespondenceRule and CorrespondenceTemplate} entity
 */
public interface CorrespondenceRuleAndTemplateInfo {
    String getCalcContribs();

    Integer getUpliftCoteId();

    Integer getReassessmentCoteId();

    Integer getId();

    String getCotyCorrespondenceType();

    String getDescription();
}
