# 1단계: 빌드 스테이지
# 그레이들 빌드를 위해 gradle 이미지를 사용합니다.
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# 현재 폴더의 모든 파일을 도커 컨테이너의 /app 폴더로 복사합니다.
COPY . .

# gradlew 파일에 실행 권한을 부여하고 빌드를 진행합니다. (테스트는 제외)
RUN chmod +x gradlew
RUN ./gradlew clean build -x test

# 2단계: 실행 스테이지
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# 빌드 스테이지에서 생성된 jar 파일을 복사합니다.
# 그레이들은 결과물이 build/libs 폴더에 생성됩니다.
COPY --from=build /app/build/libs/*.jar app.jar

# 서버 실행
ENTRYPOINT ["java", "-jar", "app.jar"]