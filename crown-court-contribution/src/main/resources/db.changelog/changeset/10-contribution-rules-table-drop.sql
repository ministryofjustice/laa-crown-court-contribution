--liquibase formatted sql
--changeset venkatv:10-contribution-rules-table-drop
DROP TABLE IF EXISTS crown_court_contribution.contribution_rules;