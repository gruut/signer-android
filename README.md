# signer-android
develop [![Build Status](https://travis-ci.com/gruut/signer-android.svg?branch=develop)](https://travis-ci.com/gruut/signer-android)

# ver. Mario
1. Sign UP
    - Gruut Authority(G.A)로부터 인증서를 발급받는 절차
    - User의 핸드폰 번호를 G.A에게 보내고, 인증서를 받는다
    - Network Error가 날 경우, G.A 서버가 운영 중인지 확인해야한다

2. JOIN
    - Setting 버튼으로 참여할 Merger의 IP주소와 Port를 설정한다
    - Merger에게 네트워크 참여를 요청한다
    - 참여가 성공적으로 이루어 지면, Merger로부터 서명 요청 메세지를 수신할 수 있다
