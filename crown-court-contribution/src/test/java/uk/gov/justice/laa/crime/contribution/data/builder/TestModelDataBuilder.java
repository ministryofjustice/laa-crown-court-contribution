package uk.gov.justice.laa.crime.contribution.data.builder;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.common.Constants;
import uk.gov.justice.laa.crime.contribution.dto.*;
import uk.gov.justice.laa.crime.contribution.model.*;
import uk.gov.justice.laa.crime.contribution.model.common.ApiAssessment;
import uk.gov.justice.laa.crime.contribution.model.maat_api.ApiCrownCourtOutcome;
import uk.gov.justice.laa.crime.contribution.model.maat_api.ApiCrownCourtSummary;
import uk.gov.justice.laa.crime.contribution.model.maat_api.*;
import uk.gov.justice.laa.crime.contribution.projection.CorrespondenceRuleAndTemplateInfo;
import uk.gov.justice.laa.crime.contribution.staticdata.entity.ContributionRulesEntity;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static uk.gov.justice.laa.crime.contribution.common.Constants.FULL;
import static uk.gov.justice.laa.crime.contribution.common.Constants.PASS;

@Component
public class TestModelDataBuilder {

    public static final String PASSPORT_RESULT_FAIL_CONTINUE = "FAIL CONTINUE";
    public static final Integer REP_ID = 1234;
    public static final Integer CORRESPONDENCE_ID = 1;
    public static final LocalDateTime TEST_DATE = LocalDateTime.of(2022, 1, 1, 0, 0);

    public static final LocalDate CALC_DATE = LocalDate.of(2023, 8, 28);
    public static final LocalDate UPLIFT_APPLIED_DATE = LocalDate.of(2023, 7, 20);
    public static final LocalDate UPLIFT_REMOVED_DATE = LocalDate.of(2023, 8, 20);
    public static final String LAA_TRANSACTION_ID = "7c49ebfe-fe3a-4f2f-8dad-f7b8f03b8327";
    public static final LocalDate COMMITTAL_DATE = LocalDate.of(2023, 8, 8);
    public static final Integer CONTRIBUTION_ID = 999;

    public static final String TEST_USER = "TEST_USER";

    public static ContributionRequestDTO getContributionRequestDTO() {
        return ContributionRequestDTO.builder()
                .iojResult(PASS)
                .decisionResult(PASS)
                .initResult(PASS)
                .fullResult(FULL)
                .hardshipResult(PASS)
                .passportResult(PASS).build();
    }

    public static CreateContributionRequest getCreateContributionRequest(TransferStatus transferStatus, LocalDateTime dateTime){
        return new CreateContributionRequest()
                .withRepId(REP_ID)
                .withApplId(123)
                .withUserCreated(TEST_USER)
                .withContributionFileId(123)
                .withEffectiveDate(dateTime)
                .withCalcDate(dateTime)
                .withContributionCap(BigDecimal.valueOf(250))
                .withMonthlyContributions(BigDecimal.valueOf(250))
                .withUpfrontContributions(BigDecimal.valueOf(250))
                .withUpliftApplied("N")
                .withBasedOn("N")
                .withTransferStatus(transferStatus)
                .withDateUpliftApplied(dateTime)
                .withDateUpliftRemoved(dateTime)
                .withCreateContributionOrder("N")
                .withCorrespondenceId(123);
        }

    public static CorrespondenceRuleAndTemplateInfo getCorrespondenceRuleAndTemplateInfo() {
        return new CorrespondenceRuleAndTemplateInfo() {
            @Override
            public String getCalcContribs() {
                return "N";
            }

            @Override
            public Integer getUpliftCoteId() {
                return CORRESPONDENCE_ID;
            }

            @Override
            public Integer getReassessmentCoteId() {
                return CORRESPONDENCE_ID;
            }

            @Override
            public Integer getId() {
                return CORRESPONDENCE_ID;
            }

            @Override
            public String getCotyCorrespondenceType() {
                return "CONTRIBUTION_NOTICE";
            }

            @Override
            public String getDescription() {
                return "No contributions required";
            }
        };
    }

    public static CorrespondenceRuleAndTemplateInfo getEmptyCorrespondenceRuleAndTemplateInfo() {
        return new CorrespondenceRuleAndTemplateInfo() {
            @Override
            public String getCalcContribs() {
                return "N";
            }

            @Override
            public Integer getUpliftCoteId() {
                return CORRESPONDENCE_ID;
            }

            @Override
            public Integer getReassessmentCoteId() {
                return CORRESPONDENCE_ID;
            }

            @Override
            public Integer getId() {
                return CORRESPONDENCE_ID;
            }

            @Override
            public String getCotyCorrespondenceType() {
                return "";
            }

            @Override
            public String getDescription() {
                return "";
            }
        };
    }

    public static ContributionVariationDTO getContributionVariationDTO() {
        return ContributionVariationDTO.builder()
                .variation("SQL COSTS")
                .variationRule("+")
                .build();
    }

    public static ContributionRulesEntity getContributionRules() {
        return ContributionRulesEntity.builder()
                .variation("SQL COSTS")
                .variationRule("+")
                .build();
    }

    public static ApiCrownCourtOutcome getApiCrownCourtOutcome(CrownCourtOutcome crownCourtOutcome) {
        return new ApiCrownCourtOutcome()
                .withOutcome(crownCourtOutcome);
    }

    public static ApiCrownCourtSummary getApiCrownCourtSummary() {
        List<ApiCrownCourtOutcome> apiCrownCourtOutcomeList = new ArrayList<>();
        apiCrownCourtOutcomeList.add(getApiCrownCourtOutcome(CrownCourtOutcome.ABANDONED));
        apiCrownCourtOutcomeList.add(getApiCrownCourtOutcome(CrownCourtOutcome.PART_CONVICTED));
        apiCrownCourtOutcomeList.add(getApiCrownCourtOutcome(CrownCourtOutcome.SUCCESSFUL));
        return new ApiCrownCourtSummary()
                .withCrownCourtOutcome(apiCrownCourtOutcomeList);
    }

    public static RepOrderDTO getRepOrderDTO() {
        return getRepOrderDTO(TestModelDataBuilder.REP_ID);
    }

    public static RepOrderDTO getRepOrderDTO(Integer id) {
        return RepOrderDTO.builder()
                .id(id)
                .catyCaseType("case-type")
                .magsOutcome("outcome")
                .magsOutcomeDate(TEST_DATE.toString())
                .magsOutcomeDateSet(TEST_DATE)
                .committalDate(TEST_DATE.toLocalDate())
                .decisionReasonCode("rder-code")
                .crownRepOrderDecision("cc-rep-doc")
                .crownRepOrderType("cc-rep-type")
                .rorsStatus("rors-status")
                .financialAssessments(Collections.singletonList(getFinancialAssessmentDTO()))
                .passportAssessments(Collections.singletonList(getPassportAssessmentDTO()))
                .build();
    }

    public static FinancialAssessmentDTO getFinancialAssessmentDTO() {
        return FinancialAssessmentDTO.builder()
                .repId(5678)
                .initialAscrId(1)
                .assessmentType("INIT")
                .dateCreated(LocalDateTime.parse("2023-07-09T15:01:25"))
                .userCreated("test-f")
                .cmuId(30)
                .fassInitStatus("COMPLETE")
                .initialAssessmentDate(LocalDateTime.parse("2021-10-09T15:02:25"))
                .initTotAggregatedIncome(BigDecimal.valueOf(15600.00))
                .initAdjustedIncomeValue(BigDecimal.valueOf(15600.00))
                .initResult("FULL")
                .initApplicationEmploymentStatus("NONPASS")
                .replaced("Y")
                .build();
    }

    public static PassportAssessmentDTO getPassportAssessmentDTO() {
        return PassportAssessmentDTO.builder()
                .repId(REP_ID)
                .nworCode("FMA")
                .dateCreated(LocalDateTime.parse("2023-07-08T15:01:25"))
                .userCreated("test-f")
                .cmuId(30)
                .assessmentDate(LocalDateTime.parse("2021-10-09T15:01:25"))
                .partnerBenefitClaimed("Y")
                .partnerFirstName("Test")
                .partnerSurname("Partner")
                .partnerNiNumber("AB123456C")
                .partnerDob(LocalDateTime.parse("1978-10-09T06:00:00"))
                .incomeSupport("Y")
                .jobSeekers("Y")
                .statePensionCredit("N")
                .under18FullEducation("N")
                .under16("N")
                .pcobConfirmation("DWP")
                .result("PASS")
                .dateModified(LocalDateTime.parse("2021-10-09T15:01:25"))
                .userModified("test-f")
                .dwpResult("Yes")
                .between16And17("N")
                .under18HeardInYouthCourt("N")
                .under18HeardInMagsCourt("N")
                .lastSignOnDate(LocalDateTime.parse("2021-08-09T12:12:48"))
                .esa("N")
                .pastStatus("COMPLETE")
                .replaced("N")
                .valid("Y")
                .dateCompleted(LocalDateTime.parse("2021-10-09T15:01:25"))
                .usn(1234)
                .whoDWPChecked("ABC")
                .rtCode("DEF")
                .replaced("Y")
                .build();
    }

    public static Contribution buildContribution() {
        return Contribution.builder()
                .id(9)
                .applId(9)
                .repId(9)
                .contributionFileId(1)
                .effectiveDate(LocalDate.now())
                .calcDate(LocalDate.now())
                .contributionCap(BigDecimal.valueOf(250))
                .monthlyContributions(BigDecimal.valueOf(250))
                .upfrontContributions(BigDecimal.valueOf(250))
                .upliftApplied("NS")
                .basedOn("Means")
                .transferStatus(TransferStatus.MANUAL)
                .dateUpliftApplied(LocalDate.now())
                .dateUpliftRemoved(LocalDate.now())
                .dateCreated(LocalDateTime.now())
                .userCreated("test")
                .dateModified(LocalDateTime.now())
                .userModified("test")
                .createContributionOrder("N")
                .correspondenceId(1)
                .active("Y")
                .replacedDate(LocalDate.now())
                .latest(true)
                .ccOutcomeCount(1)
                .seHistoryId(1)
                .build();
    }

    public static Contribution buildContributionForCompareContributionService() {
        return Contribution.builder()
                .id(123)
                .applId(123)
                .repId(123)
                .replacedDate(null)
                .calcDate(LocalDate.now())
                .contributionCap(BigDecimal.valueOf(250))
                .monthlyContributions(BigDecimal.valueOf(250))
                .upfrontContributions(BigDecimal.valueOf(250))
                .effectiveDate(LocalDate.now())
                .userCreated("test")
                .active("Y")
                .build();
    }

    public static ApiMaatCalculateContributionRequest buildAppealContributionRequest() {
        return new ApiMaatCalculateContributionRequest()
                .withApplId(999)
                .withRepId(999)
                .withCaseType(CaseType.APPEAL_CC)
                .withAppealType(AppealType.ACS)
                .withUserCreated("TEST")
                .withLastOutcome(buildLastOutcome())
                .withAssessments(List.of(buildAssessment()));
    }

    public static ApiMaatCalculateContributionRequest buildCalculateContributionRequest() {
        return new ApiMaatCalculateContributionRequest()
                .withApplId(999)
                .withRepId(999)
                .withCaseType(CaseType.EITHER_WAY)
                .withAppealType(AppealType.ACS)
                .withUserCreated("TEST")
                .withLastOutcome(buildLastOutcome())
                .withAssessments(List.of(buildAssessment()));
    }

    public static ApiCalculateContributionRequest buildApiCalculateContributionRequest() {
        return new ApiCalculateContributionRequest()
                .withAnnualDisposableIncome(new BigDecimal(1000))
                .withDisposableIncomePercent(BigDecimal.TEN)
                .withMinimumMonthlyAmount(new BigDecimal(100))
                .withContributionCap(new BigDecimal(50))
                .withUpliftApplied(false)
                .withUpfrontTotalMonths(12);
    }

    public static ApiCalculateContributionRequest buildInvalidApiCalculateContributionRequest() {
        return new ApiCalculateContributionRequest()
                .withAnnualDisposableIncome(new BigDecimal(1000))
                .withDisposableIncomePercent(BigDecimal.TEN)
                .withMinimumMonthlyAmount(new BigDecimal(100))
                .withContributionCap(new BigDecimal(50))
                .withUpliftApplied(false);
    }

    public static LastOutcome buildLastOutcome() {
        return new LastOutcome()
                .withOutcome(CrownCourtAppealOutcome.SUCCESSFUL)
                .withDateSet(TEST_DATE);
    }

    public static ApiAssessment buildAssessment() {
        return new ApiAssessment()
                .withAssessmentType(AssessmentType.INIT)
                .withStatus(AssessmentStatus.COMPLETE)
                .withResult(AssessmentResult.PASS);
    }

    public static RepOrderCCOutcomeDTO getRepOrderCCOutcomeDTO(Integer outcomeId, String outcome) {
        return RepOrderCCOutcomeDTO.builder()
                .id(outcomeId)
                .outcome(outcome)
                .outcomeDate(LocalDateTime.now())
                .build();
    }

    public static CalculateContributionDTO getContributionDTOForCompareContributionService(String caseType,
                                                                                           BigDecimal contributionCap,
                                                                                           BigDecimal upfrontContributions,
                                                                                           BigDecimal monthlyContributions,
                                                                                           LocalDate effectiveDate,
                                                                                           String isActive,
                                                                                           MagCourtOutcome magCourtOutcome) {
        return CalculateContributionDTO.builder()
                .repId(123)
                .applId(123)
                .laaTransactionId("123456")
                .contributionCap(contributionCap)
                .upfrontContributions(upfrontContributions)
                .monthlyContributions(monthlyContributions)
                .replacedDate(null)
                .effectiveDate(effectiveDate)
                .active(isActive).magCourtOutcome(magCourtOutcome)
                .assessments(List.of(buildAssessment()))
                .lastOutcome(buildLastOutcome())
                .userCreated("TEST")
                .repOrderDTO(getRepOrderDTOForCaseType(caseType))
                .calcDate(CALC_DATE)
                .dateUpliftApplied(UPLIFT_APPLIED_DATE)
                .dateUpliftRemoved(UPLIFT_REMOVED_DATE)
                .transferStatus(TransferStatus.REQUESTED)
                .build();

    }

    public static RepOrderDTO getRepOrderDTOForCaseType(String caseType) {
        return RepOrderDTO.builder()
                .id(123)
                .catyCaseType(caseType).build();
    }

    public static Contribution getContribution() {
        return Contribution.builder()
                .id(123)
                .applId(123)
                .repId(123)
                .replacedDate(null)
                .calcDate(LocalDate.now())
                .contributionCap(BigDecimal.valueOf(250))
                .monthlyContributions(BigDecimal.valueOf(250))
                .upfrontContributions(BigDecimal.valueOf(250))
                .effectiveDate(LocalDate.now())
                .userCreated("test")
                .active("Y")
                .build();
    }

    public static ApiCalculateHardshipByDetailRequest getApiCalculateHardshipByDetailRequest() {
        return new ApiCalculateHardshipByDetailRequest()
                .withDetailType("TEST")
                .withRepId(REP_ID)
                .withLaaTransactionId(LAA_TRANSACTION_ID);
    }

    public static ApiMaatCalculateContributionResponse getApiMaatCalculateContributionResponse() {
        return new ApiMaatCalculateContributionResponse()
                .withMonthlyContributions(BigDecimal.ZERO)
                .withUpliftApplied(Constants.N)
                .withEffectiveDate(COMMITTAL_DATE.toString())
                .withUpfrontContributions(BigDecimal.ZERO)
                .withTotalMonths(0);
    }

    public static ApiCalculateContributionResponse getCalculateContributionResponse() {
        return new ApiCalculateContributionResponse()
                .withMonthlyContributions(BigDecimal.ZERO)
                .withUpliftApplied(Constants.N)
                .withUpfrontContributions(BigDecimal.ZERO);
    }

    public static CalculateContributionDTO getContributionDTOForCalcContribs() {
        return CalculateContributionDTO.builder()
                .committalDate(COMMITTAL_DATE)
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(AssessmentType.PASSPORT)
                        .withAssessmentDate(TEST_DATE)))
                .caseType(CaseType.INDICTABLE)
                .magCourtOutcome(MagCourtOutcome.COMMITTED)
                .build();
    }

    public static ContributionsSummaryDTO getContributionSummaryDTO() {
        return ContributionsSummaryDTO.builder()
                .id(CONTRIBUTION_ID)
                .monthlyContributions(BigDecimal.TEN)
                .upfrontContributions(BigDecimal.ONE)
                .basedOn("Means")
                .upliftApplied(Constants.Y)
                .effectiveDate(TEST_DATE.toLocalDate())
                .calcDate(CALC_DATE)
                .fileName("TEST")
                .dateSent(LocalDate.of(2023, 1, 1))
                .dateReceived(LocalDate.of(2023, 2, 2))
                .build();
    }

    public static ContributionCalcParametersDTO getContributionCalcParametersDTO() {
        return ContributionCalcParametersDTO.builder()
                .disposableIncomePercent(BigDecimal.TEN)
                .minUpliftedMonthlyAmount(new BigDecimal(50))
                .minimumMonthlyAmount(new BigDecimal(100))
                .upfrontTotalMonths(12)
                .upliftedIncomePercent(BigDecimal.ONE)
                .build();
    }

    public static ApiCalculateContributionResponse getApiCalculateContributionResponse() {
        return new ApiCalculateContributionResponse()
                .withBasedOn(Constants.MEANS)
                .withMonthlyContributions(BigDecimal.TEN)
                .withUpfrontContributions(BigDecimal.ZERO)
                .withUpliftApplied(Constants.Y);
    }

    public static CalculateContributionDTO getCalculateContributionDTO() {
        return CalculateContributionDTO.builder()
                .repId(TestModelDataBuilder.REP_ID)
                .assessments(List.of(new ApiAssessment()
                        .withAssessmentType(AssessmentType.INIT)
                        .withResult(AssessmentResult.PASS)
                        .withAssessmentDate(TestModelDataBuilder.TEST_DATE)))
                .effectiveDate(LocalDate.now())
                .monthlyContributions(BigDecimal.ZERO)
                .build();
    }

}
