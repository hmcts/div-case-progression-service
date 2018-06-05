#!/bin/bash
## Usage: ./idam-user-token.sh [role] [user_id]
##
## Options:
##    - role: Role assigned to user in generated token. Default to `ccd-import`.
##    - user_id: ID assigned to user in generated token. Default to `1`.
##
## Returns a valid IDAM user token for the given role and user_id.

IDAM_URL=http://localhost:4501
IDAM_REDIRECT_URL=https://localhost:4003/authenticated
CLIENT_ID=divorce
CLIENT_SECRET=idam_test_secret
SUFFIX=$RANDOM
USER_EMAIL=test$SUFFIX@mailinator.com
USER_PASSWORD=test$SUFFIX
AUTHORIZATION_HEADER=`echo -n "$USER_EMAIL:$USER_PASSWORD" | openssl base64 -base64`


echo "Creating account with username [$USER_EMAIL] and password [$USER_PASSWORD]"
curl --silent -X POST \
  $IDAM_URL/testing-support/accounts \
  -H 'Content-Type: application/json' \
  -d "{\"email\":\"$USER_EMAIL\", \"forename\":\"Test\",\"surname\":\"User\",\"password\":\"$USER_PASSWORD\"}"

AUTHORIZATION_CODE=`curl --silent -X POST \
  "$IDAM_URL/oauth2/authorize?response_type=code&client_id=divorce&redirect_uri=$IDAM_REDIRECT_URL" \
  -H "Authorization:Basic $AUTHORIZATION_HEADER" | jq -r '.code'`

echo "Generated authorization code $AUTHORIZATION_CODE"

ACCESS_TOKEN=`curl --silent -X POST \
  "$IDAM_URL/oauth2/token?code=$AUTHORIZATION_CODE&client_id=$CLIENT_ID&client_secret=$CLIENT_SECRET&redirect_uri=$IDAM_REDIRECT_URL&grant_type=authorization_code" \
  -H 'Content-Type: application/x-www-form-urlencoded' | jq -r '.access_token'`

echo "Access token $ACCESS_TOKEN"

