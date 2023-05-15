--liquibase formatted sql
--changeset muthus:06-contribution-rules-table-create
CREATE TABLE IF NOT EXISTS crown_court_contribution.contribution_rules
(
    ID INTEGER PRIMARY KEY,
    CASE_TYPE VARCHAR(20),
    MCOO_OUTCOME VARCHAR(20),
    CCOO_OUTCOME VARCHAR(20),
    VARIATION	VARCHAR(20),
    VARIATION_RULE	VARCHAR(20),
    CONSTRAINT CON_RULE_UK1 UNIQUE (CASE_TYPE, MCOO_OUTCOME, CCOO_OUTCOME)
    );