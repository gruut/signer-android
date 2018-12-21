export DEPLOY_BRANCH="$TRAVIS_BRANCH"

NEWLINE="%0A"
SLACK_KEY="$SLACK_BOT_TOKEN"
SLACK_TEXT=":socks: 테스트 실행 파일을 가져왔어요[\`$DEPLOY_BRANCH\`] :socks:
\`https://github.com/${TRAVIS_REPO_SLUG}tree/$DEPLOY_BRANCH\`
${TRAVIS_COMMIT_MESSAGE:-none}"

curl https://slack.com/api/files.upload \
  -F "token=$SLACK_KEY" \
  -F "channels=#dev_thevaulters" \
  -F "initial_comment=$SLACK_TEXT" \
  -F "file=@app/build/outputs/apk/debug/app-debug.apk"
