# IOT클라우드 플랫폼 프로젝트
# 스마트 미세먼지 fan                           
### 두 개의 다른 공간에 미세먼지 농도를 각각 측정하여 두 개의 농도를 비교하고 fan을 작동 시켜 좋은 공기상태를 유지하는 IOT서비스이다.

#### -작동 순서 및 기능 설명
1) Arduino MKR WiFi 1010에 연결된 2개의 미세먼지 센서를 통해 실시간으로 A공간 / B공간 미세먼지 농도 측정을 한다. 
2) 농도에 따른 상태 구분 : 좋음/보통/나쁨/매우나쁨
3) A공간 / B공간 상태 비교하여 단계 나누어 단계에 따른 fan의 속도 적용과 fan의 방향 적용이 가능 하다. 

![image](https://user-images.githubusercontent.com/95115814/205662500-59886d54-b99e-46b4-ab89-ecedd52d7878.png)

4) Arduino MKR WiFi 1010을 통해 측정된 두 공간의 미세먼지 값/ 미세먼지 상태 값/ fan의 속도 및 방향 dynamoDB에 저장을 한다. 
5) Arduino 앱을 통해 실시간 미세먼지 농도 값과 상태, fan의 세기, 방향을 조회 및 상태 변경이 가능하다.
6) Arduino 앱을 통해 날짜/시간 별 dynamoDB에 저장된 로그값 조회 가능하다.

#### -구조 설명

![image](https://user-images.githubusercontent.com/95115814/205663115-e4a0e7c6-eb3a-4934-908a-7f941fc2dd06.png)
1) IoT 백엔드는 AWS의 AWS IoT Core, AWS Lambda, Amazon DynamoDB, Amazon API Gateway를 이용해 구축된 IoT 클라우드 플랫폼이다.
2) AWS IoT Device gateway를 통해 연결된 MKRWiFi1010으로부터 주기적으로 미세먼지 정보를 수신하고, MQTT 프로토콜을 이용하여 Device shadow, IoT rule 컴포넌트와 상호작용한다.
3) Device shadow는 Device gateway를 통해 게시된 주제에 따라 디바이스 상태정보를 업데이트하거나 현재 상태정보를 게시한다.
4) IoT rule은 등록된 주제(update/documents)의 메시지가 수신될 때마다 AWS Lambda 함수를 통해서 수신된 메시지를 Amazon DynamoDB에 저장한다.
5) Arduino 앱은 Amazon API Gateway를 통해서 게시된 REST API를 활용하여 IoT 백엔드와 상호작용한다.
