# Maven + OpenJDK 17をベースイメージとして使用
FROM maven:3.9.11-eclipse-temurin-17 AS build

# 作業ディレクトリを設定
WORKDIR /app

# pom.xmlをコピー
COPY pom.xml .

# 依存関係をダウンロード
RUN mvn dependency:go-offline

# ソースコードをコピー
COPY src ./src

# アプリケーションをビルド
RUN mvn clean package -DskipTests

# 実行用イメージ
FROM eclipse-temurin:17-jre-alpine

# 作業ディレクトリを設定
WORKDIR /app

# ビルドステージからJARファイルをコピー
COPY --from=build /app/target/*.jar app.jar

# ポート8080を公開
EXPOSE 8080

# アプリケーションを起動
ENTRYPOINT ["java", "-jar", "app.jar"]
