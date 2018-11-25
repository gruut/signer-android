export DEPLOY_BRANCH="$TRAVIS_BRANCH"
export DEPLOY_COMMIT="$TRAVIS_COMMIT"

SLACK_KEY="$SLACK_BOT_TOKEN"
SLACK_TEXT="[ \`$DEPLOY_BRANCH\` | \`$DEPLOY_COMMIT\` ] ${TRAVIS_COMMIT_MESSAGE:-none} "

curl \
  -F "token=$SLACK_KEY" \
  -F "channels=playground" \
  -F "initial_comment=$SLACK_TEXT" \
  -F "file=@app/build/outputs/apk/app-debug.apk" \
  https://slack.com/api/files.upload
