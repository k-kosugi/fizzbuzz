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
# Microsoft 公式の JDK イメージには、アプリが使わない Go 製ツール (/usr/bin/jaz) が同梱されており、
# その Go 標準ライブラリの脆弱性を Trivy が検出する。そのため、JDK 本体だけを distroless/base にコピーして使う。
FROM mcr.microsoft.com/openjdk/jdk:25-azurelinux AS jdk

FROM mcr.microsoft.com/azurelinux/distroless/base:3.0
WORKDIR /app

ENV JAVA_HOME=/usr/lib/jvm/msopenjdk-25
COPY --from=jdk /usr/lib/jvm/msopenjdk-25 ${JAVA_HOME}
# JDK が必要とする zlib は distroless/base に含まれないため、JDK イメージからコピーする
COPY --from=jdk /usr/lib/libz.so.1.3.2 /usr/lib/libz.so.1.3.2
COPY --from=jdk /usr/lib/libz.so.1 /usr/lib/libz.so.1

# 変更頻度の低い層から順にコピーし、イメージのキャッシュを効かせる
COPY --from=build /workspace/extracted/dependencies/ ./
COPY --from=build /workspace/extracted/spring-boot-loader/ ./
COPY --from=build /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build /workspace/extracted/application/ ./

# Kubernetes の runAsNonRoot で検証できるよう、ユーザーは数値で指定する。
# distroless/base に app ユーザーは無いが、UID を指定すれば非 root で実行できる
USER 101
EXPOSE 8080

ENTRYPOINT ["/usr/lib/jvm/msopenjdk-25/bin/java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
