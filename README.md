# 기능 명세서
### 실행

바로 `mvn clean package`시에 build + test 실행 후 
```shell
java -jar target/was-1.0.jar
```
으로 실행할 수 있습니다.

### 외부 Jar 사용
1. src와 같은 level의 경로에 app 생성
2. was-1.0.jar 또한 target에서 꺼내 같은 level로 옮김
3. service 프로젝트의 빌드 결과물 service-1.0-SNAPSHOT.jar를 1번에서 만든 app 경로로 옮김
```shell
jara -jar was-1.0.jar
```
실행 후 클래스로딩 되고 실행 가능합니다

**Example**
```shell
curl http://localhost:8080/externalJarService
```

## 서버 부팅
VirtualHost 기능
1. 가상 호스트 설정
```shell
sudo vi /etc/hosts
```
```
127.0.0.1       localhost
127.0.0.1       client1.com
127.0.0.1       client2.com
```
**해당 설정 필요**
1. 위에서 설정한 host 이름을 resources 밑에 ServerConfig.json에 배치
2. virtualHosts내에 host가 있다면 하위 httpRoot, errorPage는 필수값
3. errorPage의 상태코드 키값은 Integer

### exe 파일 에러 테스트
```shell
curl http://client1.com:8080/Hello.exe
```
### 상위경로 접근 에러 테스트
```shell
curl -v --path-as-is http://client1.com:8080/../Hello/h2
```
curl 명령어에 기본적으로 normalize 기능이 있어 `--path-as-is` 옵션으로 요청해야함

### 현재 시각을 출력하는 서비스 테스트
```shell
curl -v --path-as-is http://localhost:8080/KSTTime
```

### 403 응답코드에 대한 규칙 확장
validator 내의 URIValidatorChain.defaultChain에 추가 확장

### 설정파일로 URL 클래스파일 매핑
Request와 URL을 매핑한 RequestMapping 클래스를 분리해두었고 ServerConfig 내에 추가하여 확장 가능합니다