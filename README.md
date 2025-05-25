# 기능 명세서
바로 `mvn clean package`시에 


## 서버 부팅
1. 가상 호스트 설정
```shell
sudo vi /etc/hosts
```
```
127.0.0.1       localhost
127.0.0.1       client1.com
127.0.0.1       client2.com
```
해당 설정 필요

```shell
curl -v --path-as-is http://client1.com:8080/../Hello/h2
```
