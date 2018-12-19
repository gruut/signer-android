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

# ver. Luigi
> 메세지 서명과 검증을 위한 데이터 처리는 GE5. Data Structure 문서를 기준으로 구현하였습니다.

1. Verifying
    - 메세지 헤더에 MacType이 HMAC_SHA256(0xF1)으로 설정되어 있을 경우, HMAC을 검증함
    - 메세지 헤더와 메세지 내용이 일치하는지 검증함
    - MSG_CHALLENGE 수신 시, 메세지 내의 Timestamp를 검증함(10s 이내)
    - MSG_RESPONSE2 수신 시, 메세지 내의 Signature를 메세지 내의 Certificate로 검증함
    - MSG_REQ_SSIG 수신 시, 블록의 시간 유효성을 검증함(10m 이내)
    - MSG_REQ_SSIG 수신 시, 이미 서명한 블록인지 검증함

2. Signing
    - MSG_RESPONSE1 송신 시, RSA키로 서명하여 Signature를 메세지에 포함시켜 보냄
    - 지지서명 생성 시, RSA키로 서명하여 보냄

