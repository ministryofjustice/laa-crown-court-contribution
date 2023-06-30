package uk.gov.justice.laa.crime.contribution.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CompareContributionServiceTest {

    @InjectMocks
    private CompareContributionService compareContributionService;
    @Mock
    private MaatCourtDataService maatCourtDataService;
    @Mock
    private ContributionService contributionService;


}
