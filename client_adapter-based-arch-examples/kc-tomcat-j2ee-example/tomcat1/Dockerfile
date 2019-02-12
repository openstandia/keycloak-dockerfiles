# ==================================================================
## ビルド用Dockerイメージ
#FROM maven:3.6.0-jdk-8-slim AS build-env
#ADD authz-app /build
#WORKDIR /build
#RUN mvn -e package
# ==================================================================

# Dockerイメージの指定
FROM library/tomcat:8.5.37-jre8

# 各種バージョンの定義
ENV TOMCAT_HOME=/usr/local/tomcat \
     KEYCLOAK_VERSION=4.8.3.Final

# Client Adapter(TOMCAT)のインストール
RUN cd ${TOMCAT_HOME}/lib/ \
  && curl -k -L -O https://downloads.jboss.org/keycloak/${KEYCLOAK_VERSION}/adapters/keycloak-oidc/keycloak-tomcat8-adapter-dist-${KEYCLOAK_VERSION}.tar.gz \
  && tar zxvf keycloak-tomcat8-adapter-dist-${KEYCLOAK_VERSION}.tar.gz

# WARの配置
#COPY --from=build-env /build/target/*.war ${TOMCAT_HOME}/webapps/
RUN rm -rf ${TOMCAT_HOME}/webapps/*
ADD ./authz-app/target/*.war ${TOMCAT_HOME}/webapps/

# HTTPS 対応
COPY server.xml ${TOMCAT_HOME}/conf/

# Keycloak ssl 接続チェック対応
COPY wait-for-kc.sh /wait-for-kc.sh

# エントリーポイントの実行
#CMD ["catalina.sh", "run"]
