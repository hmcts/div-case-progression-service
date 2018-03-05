#!/usr/bin/env bash

curl -H 'Content-Type: application/json' -d '{ "email":"simulate-delivered@notifications.service.gov.uk", "forename":"simulate-delivered@notifications.service.gov.uk","surname":"simulate-delivered@notifications.service.gov.uk","password":"123"}' http://localhost:4501/testing-support/accounts

eval echo $(curl --silent -X POST -H 'Authorization: Basic dGVzdEBURVNULkNPTToxMjM=' http://localhost:4501/oauth2/authorize | jq '.["access-token"]')
