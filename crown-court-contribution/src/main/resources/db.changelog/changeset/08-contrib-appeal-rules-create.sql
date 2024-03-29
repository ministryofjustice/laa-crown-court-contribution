
CREATE TABLE IF NOT EXISTS crown_court_contribution.CONTRIB_APPEAL_RULES
(
    ID INTEGER PRIMARY KEY,
    CATY_CASE_TYPE VARCHAR(20) NOT NULL,
    APTY_CODE VARCHAR(20) NOT NULL,
    CCOO_OUTCOME VARCHAR(20) NOT NULL,
    CONTRIB_AMOUNT	DECIMAL(10,0),
    ASSESSMENT_RESULT	VARCHAR(4) NOT NULL
);