# laa-crown-court-contribution
This is a Java based Spring Boot application hosted on [MOJ Cloud Platform](https://user-guide.cloud-platform.service.justice.gov.uk/documentation/concepts/about-the-cloud-platform.html).

[![CircleCI](https://dl.circleci.com/status-badge/img/gh/ministryofjustice/laa-crown-court-contribution/tree/main.svg?style=shield)](https://dl.circleci.com/status-badge/redirect/gh/ministryofjustice/laa-crown-court-contribution/tree/main)
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

### Decrypting docker-compose.override.yml

The `docker-compose.override.yml` is encrypted using [git-crypt](https://github.com/AGWA/git-crypt).

To run the app locally you need to be able to decrypt this file.

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
docker-compose build
docker-compose up
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