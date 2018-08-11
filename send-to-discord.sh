#!/bin/bash

# This is a simplified variant of https://github.com/k3rn31p4nic/travis-ci-discord-webhook/blob/master/send.sh
# Adapted to send simple text message instead of embedded content
# This allows message to be accessed by users that have compact mode enabled

if [ -z "$2" ]; then
    echo -e "WARNING!!\nYou need to pass the WEBHOOK_URL environment variable as the second argument to this script.\nFor details & guide, visit: https://github.com/k3rn31p4nic/travis-ci-discord-webhook" && exit
fi

echo -e "[Webhook]: Sending webhook to Discord...\\n";

case $1 in
  "success" )
    STATUS_MESSAGE="Passed"
    AVATAR="https://travis-ci.org/images/logos/TravisCI-Mascot-blue.png"
    ;;

  "failure" )
    STATUS_MESSAGE="Failed"
    AVATAR="https://travis-ci.org/images/logos/TravisCI-Mascot-red.png"
    ;;

  * )
    STATUS_MESSAGE="Status Unknown"
    AVATAR="https://travis-ci.org/images/logos/TravisCI-Mascot-1.png"
    ;;
esac

AUTHOR_NAME="$(git log -1 "$TRAVIS_COMMIT" --pretty="%aN")"
COMMITTER_NAME="$(git log -1 "$TRAVIS_COMMIT" --pretty="%cN")"
if [ "$AUTHOR_NAME" == "$COMMITTER_NAME" ]; then
    CREDITS="$AUTHOR_NAME authored & committed"
else
    CREDITS="$AUTHOR_NAME authored & $COMMITTER_NAME committed"
fi

TRAVIS_URL="https://travis-ci.org/$TRAVIS_REPO_SLUG/builds/$TRAVIS_BUILD_ID"

if [[ $TRAVIS_PULL_REQUEST != false ]]; then
    GITHUB_URL="https://github.com/$TRAVIS_REPO_SLUG/pull/$TRAVIS_PULL_REQUEST"

    # GitHub commit subjects for PRs seem to be useless so we just rely on the pull request number, source repository and branch
    WEBHOOK_DATA='{
      "username": "Travis CI",
      "avatar_url": "'"$AVATAR"'",
      "content" : "'"$STATUS_MESSAGE"'" Travis CI build on pull request '"$TRAVIS_PULL_REQUEST"' from '"$TRAVIS_PULL_REQUEST_SLUG"', branch '"$TRAVIS_PULL_REQUEST_BRANCH"' ('"$CREDITS"', see '"$GITHUB_URL"' / '"$TRAVIS_URL"')"
    }'
else
    GITHUB_URL="https://github.com/$TRAVIS_REPO_SLUG/commit/$TRAVIS_COMMIT"
    COMMIT_SUBJECT="$(git log -1 "$TRAVIS_COMMIT" --pretty="%s")"

    WEBHOOK_DATA='{
      "username": "Travis CI",
      "avatar_url": "'"$AVATAR"'",
      "content" : "'"$STATUS_MESSAGE"' Travis CI build on commit to '"$TRAVIS_BRANCH"': '"$COMMIT_SUBJECT"' ('"$CREDITS"', see '"$GITHUB_URL"' / '"$TRAVIS_URL"')"
    }'
fi

(curl --fail --progress-bar -A "TravisCI-Webhook" -H Content-Type:application/json -d "$WEBHOOK_DATA" "$2" \
&& echo -e "\\n[Webhook]: Successfully sent the webhook.") || echo -e "\\n[Webhook]: Unable to send webhook."