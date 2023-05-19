package uk.gov.justice.laa.crime.contribution.service;

import uk.gov.justice.laa.crime.contribution.model.AppealContributionResponse;

public class AppealContributionService {

    public AppealContributionResponse calculateContribution() {
        // Check if there is a cc_outcome and appeal in request data

            // Go through the assessment results in request data to determine if result is PASS or FAIL
            // This can be a call off to a method checkAssessmentResult() or something to handle the logic

            // Get the latest crown court outcome details from request data

            // Call off to MAAT API to get contribution amount from appeal rules
            // Use the service class getContributionAppealAmount() from Matts PR to handle the call and response

            // Call off to MAAT API again to get the latest contribution data
            // Use the service class findContribution() from Matts PR to handle the call and response

            // Check if latest contrib amount is different to amount retrieved from appeal rules

                // Set defaults for certain data before calling off to create new contribution row

                // Call off to MAAT API to handle inserting the new contribution row with updated amount etc
                // Use the service class createContribution() from Matts PR to handle the call and response
        return null;
    }
}
