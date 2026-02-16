# 1단계: 빌드 환경
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Gradle 래퍼와 소스 코드 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src

# gradlew 실행 권한 부여 및 jar 빌드
RUN chmod +x ./gradlew
RUN ./gradlew bootJar -x test

# 2단계: 실행 환경
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# 시간대 설정
ENV TZ=Asia/Seoul
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone && \
    apk del tzdata

# 언어 설정
ENV LANG=C.UTF-8 \
    LC_ALL=C.UTF-8

# 1단계(builder)에서 생성된 jar 파일만 현재 스테이지로 복사
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} -jar app.jar"]