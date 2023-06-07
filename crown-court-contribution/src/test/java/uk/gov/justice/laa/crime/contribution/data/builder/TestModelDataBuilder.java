package uk.gov.justice.laa.crime.contribution.data.builder;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.common.Constants;
import uk.gov.justice.laa.crime.contribution.dto.*;
import uk.gov.justice.laa.crime.contribution.model.ApiCrownCourtOutcome;
import uk.gov.justice.laa.crime.contribution.model.ApiCrownCourtSummary;
import uk.gov.justice.laa.crime.contribution.staticdata.entity.ContributionRulesEntity;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.CrownCourtOutcome;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import uk.gov.justice.laa.crime.contribution.model.AppealContributionRequest;
import uk.gov.justice.laa.crime.contribution.model.Assessment;
import uk.gov.justice.laa.crime.contribution.model.LastOutcome;
import uk.gov.justice.laa.crime.contribution.model.*;
import uk.gov.justice.laa.crime.contribution.staticdata.enums.*;
import java.util.List;

@Component
public class TestModelDataBuilder {

    public static final String PASSPORT_RESULT_FAIL_CONTINUE = "FAIL CONTINUE";
    public static final Integer REP_ID = 1234;
    public static final LocalDateTime TEST_DATE = LocalDateTime.of(2022, 1, 1, 0, 0);

    public static AssessmentRequestDTO getAssessmentRequestDTO() {

        return AssessmentRequestDTO.builder()
                .iojResult(Constants.PASS)
                .decisionResult(Constants.PASS)
                .passportResult(Constants.PASS)
                .initResult(Constants.PASS)
                .fullResult(Constants.FULL)
                .hardshipResult(Constants.PASS)
                .build();
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
                .financialAssessments(Arrays.asList(getFinancialAssessmentDTO()))
                .passportAssessments(Arrays.asList(getPassportAssessmentDTO()))
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
                .effectiveDate(LocalDate.now())
                .calcDate(LocalDate.now())
                .contributionCap(BigDecimal.valueOf(250))
                .monthlyContributions(BigDecimal.valueOf(250))
                .upfrontContributions(BigDecimal.valueOf(250))
                .dateCreated(LocalDateTime.now())
                .userCreated("test")
                .build();
    }

    public static AppealContributionRequest buildAppealContributionRequest() {
        return new AppealContributionRequest()
                .withApplId(999)
                .withRepId(999)
                .withCaseType(CaseType.EITHER_WAY)
                .withAppealType(AppealType.ACS)
                .withUserCreated("TEST")
                .withLastOutcome(buildLastOutcome())
                .withAssessments(List.of(buildAssessment()));
    }

    public static AppealContributionResponse buildAppealContributionResponse() {
        return new AppealContributionResponse()
                .withId(9)
                .withApplId(9)
                .withRepId(9)
                .withContributionFileId(9)
                .withEffectiveDate(LocalDateTime.now())
                .withCalcDate(LocalDateTime.now())
                .withContributionCap(BigDecimal.valueOf(250))
                .withMonthlyContributions(BigDecimal.valueOf(50))
                .withUpfrontContributions(BigDecimal.ZERO)
                .withUpliftApplied("N")
                .withBasedOn("test")
                .withTransferStatus(TransferStatus.SENT)
                .withDateUpliftApplied(null)
                .withDateUpliftRemoved(null)
                .withDateCreated(LocalDateTime.now())
                .withUserCreated("test")
                .withDateModified(null)
                .withUserModified(null)
                .withCreateContributionOrder(null)
                .withCorrespondenceId(9)
                .withActive("Y")
                .withReplacedDate(null)
                .withLatest(true)
                .withCcOutcomeCount(9)
                .withSeHistoryId(9);
    }

    public static LastOutcome buildLastOutcome() {
        return new LastOutcome()
                .withOutcome(CrownCourtAppealOutcome.SUCCESSFUL)
                .withDateSet(LocalDateTime.now().minusDays(1));
    }

    public static Assessment buildAssessment() {
        return new Assessment()
                .withAssessmentType(AssessmentType.INIT)
                .withStatus(AssessmentStatus.COMPLETE)
                .withResult(AssessmentResult.PASS);
    }
}
