export DEPLOY_BRANCH="$TRAVIS_BRANCH"
export DEPLOY_COMMIT="$TRAVIS_COMMIT"

SLACK_KEY="c9nHzeCbTW+AkBL21qZy//atHhuiY5IYx32oGuULc+cQQRBJ8wDQsjAJ1ME4BU7PoNHqYALn/Wm+7WGRk4BttU6FKDG6GMou7b8II8bRUzaiQ5tT6mv+qkjdZM6YRNKQP2ur5oSHeP6ukACP8KwBXAQhdOL5osbKPtYs2CsAEJLFMOjqk4TQxzOPb+BPrvGsO7EJ2M03fVQVxQubC9ZR5iLuilVtBikdic95Yv5ptxNa2ijfWJdhTTN3+iLfAv/4QXjm5EoWQ2/3s8aG8q/iYUKvk6MLLo4QKM3b5Yu1rHJDQTrOVry335Q+WnKmE97LJiI4r1Aa6zCAWCVtxr9hZAQR1yqWR4diREFlwWgM1p35wpkQztUh9z4nuJSsGnrXMXOTj2qR+M++EyicVch59OhHsXFDSQ3GydRo61BKUkYeBHHQGOUSXFDP9uciVnHmQZXzS47sn8pCIpYUWtwByS8OLEkkYxmGBzfq0p+fHRDaac6igbmW7EOAVLVNG6lhzCfsL8qlrbtpeilkxOIImle/uvpVxdm9S7RcSPHLZPr3nif9SQgesgCpAra3QoMiVET5iM/iUwOZS3H6v/KF6EBNkvaxLa8tbAnl+m9+az2IQW+nZTS3aapTrukY23BGfuWzGN4bjWgS/lDWbG0GxxaQJZHfU7c/fYdl9X29hVE="
SLACK_TEXT="[ \`$DEPLOY_BRANCH\` | \`$DEPLOY_COMMIT\` ] ${TRAVIS_COMMIT_MESSAGE:-none} "

curl \
  -F "token=$SLACK_KEY" \
  -F "channels=playground" \
  -F "initial_comment=$SLACK_TEXT" \
  -F "file=@app/build/outputs/apk/app-debug.apk" \
  https://slack.com/api/files.upload
