# syntax=docker/dockerfile:1

# ---- build ----
FROM mcr.microsoft.com/openjdk/jdk:25-azurelinux AS build
WORKDIR /workspace

# Maven Wrapper が Maven の配布物を展開するために tar が必要
RUN tdnf install -y tar && tdnf clean all

# 依存関係の取得を先に行い、ソースが変わってもキャッシュを再利用する
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw -B -q dependency:go-offline

COPY src src
# テストは CI の ./mvnw verify で実行済みのため、イメージのビルドでは省略する
RUN ./mvnw -B -q package -DskipTests \
    && mv target/*.jar target/app.jar \
    && java -Djarmode=tools -jar target/app.jar extract --layers --destination extracted

# ---- runtime ----
# distroless: シェルやパッケージマネージャーを含まない
FROM mcr.microsoft.com/openjdk/jdk:25-distroless
WORKDIR /app

# 変更頻度の低い層から順にコピーし、イメージのキャッシュを効かせる
COPY --from=build /workspace/extracted/dependencies/ ./
COPY --from=build /workspace/extracted/spring-boot-loader/ ./
COPY --from=build /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build /workspace/extracted/application/ ./

# Kubernetes の runAsNonRoot で検証できるよう、ユーザーは数値で指定する (ベースイメージの app ユーザー)
USER 101
EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
