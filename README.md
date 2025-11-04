# laa-crown-court-contribution

This is a Java based Spring Boot application hosted on [MOJ Cloud Platform](https://user-guide.cloud-platform.service.justice.gov.uk/documentation/concepts/about-the-cloud-platform.html).

[![CircleCI](https://circleci.com/gh/ministryofjustice/laa-crown-court-contribution/tree/main.svg?style=shield)](https://circleci.com/gh/ministryofjustice/laa-crown-court-contribution/tree/main)
[![MIT license](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## Contents

- [Getting started](#getting-started)
  - [Developer setup](#developer-setup)
  - [Decrypting docker-compose.override.yml](#decrypting-docker-composeoverrideyml)
- [Running locally](#running-locally)
- [Database](#database)
- [CI/CD](#cicd)

## Getting started

### Developer setup

1. Go through with this [Java Developer On-boarding Check List](https://dsdmoj.atlassian.net/wiki/spaces/ASLST/pages/3738468667/Java+Developer+Onboarding+Check+List/) and complete all tasks.
2. Request a team member to be added to the repository.
3. Create a GPG (more detail further down on the page) key and create a PR. Someone from the team will approve the PR.
4. This is a document to outline the general guideline [Developer Guidelines](https://dsdmoj.atlassian.net/wiki/spaces/ASLST/pages/3896049821/Developer+Guidelines).
5. This project have its own dedicated Jira Scrum board, and you can access [from here](https://dsdmoj.atlassian.net/jira/software/projects/LCAM/boards/881) and [project backlog](https://dsdmoj.atlassian.net/jira/software/projects/LCAM/boards/881/backlog)

### Obtaining environment variables for running locally

To run the app locally, you will need to download the appropriate environment variables from the team
vault in 1Password. These environment variables are stored as a .env file, which docker-compose uses
when starting up the service. If you don't see the team vault, speak to your tech lead to get access.

To begin with, make sure that you have the 1Password CLI installed:

```sh
op version
```

If the command is not found, [follow the steps on the 1Password developer docs to get the CLI set-up](https://developer.1password.com/docs/cli/get-started/).

Once you're ready to run the application:

```sh
./startup-local.sh
```

### Decrypting values files

The values YAML files are encrypted using [git-crypt](https://github.com/AGWA/git-crypt).

To be able to view and/or edit these files, you will need to decrypt them first.

You will first need to create a GPG key. See [Create a GPG Key](https://docs.publishing.service.gov.uk/manual/create-a-gpg-key.html) for details on how to do this with `GPGTools` (GUI) or `gpg` (command line).
You can install either from a terminal or just download the UI version.

```
brew update
brew install gpg
brew install git-crypt
```

Once you have done this, a team member who already has access can add your key by running `git-crypt add-gpg-user USER_ID`\* and creating a pull request to this repo.

Once this has been merged you can decrypt your local copy of the repository by running `git-crypt unlock`.

\*`USER_ID` can be your key ID, a full fingerprint, an email address, or anything else that uniquely identifies a public key to GPG (see "HOW TO SPECIFY A USER ID" in the gpg man page).

## Running locally

Clone Repository

```sh
git clone git@github.com:ministryofjustice/laa-crown-court-contribution.git

cd crown-court-contribution
```

The project is build using [Gradle](https://gradle.org/). This also includes plugins for generating IntelliJ configuration.

Make sure tests all testes are passed by running following ‘gradle’ Command

```sh
./gradlew clean test
```

You will need to build the artifacts for the source code, using `gradle`.

```sh
./gradlew clean build
```

The apps should then startup cleanly if you run

```sh
./startup-local.sh
```

laa-crown-court-contribution application will be running on http://localhost:8080

## Database

This application is run with PostgresSQL using docker compose. PostgresSQL is used solely for static data.
For database changes, we are using [liquibase]() and all the sql scripts stored in the directory (resources/db/changelog/).

All CRUD operations in the MAATDB are run via the [MAAT-API](https://github.com/ministryofjustice/laa-maat-court-data-api)

## CI/CD

We have configured a CircleCI code pipelines. You can [log in](https://app.circleci.com/pipelines/github/ministryofjustice/laa-crown-court-contribution) from here to access the pipeline.

To make any changes,create a branch and submit the PR. Once the PR is submitted the branch deployment is kicked off under the new branch name.
On successful build, the image is pushed to AWS ECR and requires approval to deploy to dev.

Once the PR is merged with main, the build is automatically deployed to DEV. Deployment to higher environments requires approval.

## 🧹 Code formatting with Spotless

This project uses [**Spotless**](https://github.com/diffplug/spotless) to enforce consistent Java code style across all modules.

### Why

Consistent formatting ensures cleaner diffs, easier reviews, and fewer style conflicts between IDEs.  
Spotless runs automatically in CI and will fail the build if any files don’t conform to the configured format.

### How it works

Spotless is configured in [`build.gradle`](./crown-court-proceeding/build.gradle) to:

- Format Java source files under `src/*/java/**`
- Use the [**Palantir Java Format**](https://github.com/palantir/palantir-java-format/) (a Google-style formatter with 4-space indentation and 120-character line width)
- Clean up imports (`removeUnusedImports`, `forbidWildcardImports`, `importOrder`)
- Trim trailing whitespace and ensure files end with a newline
- Exclude generated or build directories
- Fail the build if wildcard imports or standard JUnit 5 assertions are used; AssertJ is the required assertion framework

In CI, the Gradle `build` task automatically depends on `spotlessCheck`, so any formatting issues will cause the build to fail before tests or SonarQube analysis run.

### Local usage

You can run Spotless locally in two ways:

| Command                   | Description                                    |
| ------------------------- | ---------------------------------------------- |
| `./gradlew spotlessCheck` | Checks formatting and reports any violations   |
| `./gradlew spotlessApply` | Automatically fixes formatting issues in place |

If you see a failure such as:

```bash
> Task :spotlessCheck FAILED
The following files had format violations:
  src/main/java/uk/gov/laa/.../MyClass.java
Run './gradlew spotlessApply' to fix these violations.
```

...simply run `./gradlew spotlessApply`, commit the changes, and re-push.

### Developer Tips:

- You don’t need to install any IDE plugin — Spotless ensures consistent results across environments.
- You can safely ignore `.git/hooks/pre-push` — formatting is enforced in CI.
- It’s good practice to run `./gradlew spotlessApply` before opening a PR to avoid unnecessary CI failures.
