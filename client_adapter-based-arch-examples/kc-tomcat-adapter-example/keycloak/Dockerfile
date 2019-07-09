# Dockerイメージの指定
FROM jboss/keycloak:4.8.3.Final

# demo用初期設定ファイルの配置
ADD ./json/*.json /opt/jboss/import/

# p3pPolicy の日本語プロパティの修正
RUN sed -i -e 's/p3pPolicy=.*/p3pPolicy="This is not a P3P policy!"/g' keycloak/themes/base/login/messages/messages_ja.properties

# コンテナ実行時のリッスンポート
EXPOSE 8080

# ENTRYPOINTに初期起動シェルパスを設定
ENTRYPOINT [ "/opt/jboss/tools/docker-entrypoint.sh" ]

# ENTRYPOINT引数の指定
# Export
#CMD ["-b", "0.0.0.0", "-Dkeycloak.migration.action=export", "-Dkeycloak.migration.provider=dir", "-Dkeycloak.migration.dir=/tmp/"]
# Import
CMD ["-b", "0.0.0.0", "-Dkeycloak.migration.action=import", "-Dkeycloak.migration.provider=dir", "-Dkeycloak.migration.dir=/opt/jboss/import/", "-Dkeycloak.migration.strategy=OVERWRITE_EXISTING" ]
# Normal
#CMD ["-b", "0.0.0.0"]

# KEYCLOAK-10660、KEYCLOAK-10783、KEYCLOAK-10784への対応
ADD ./patch/themes /opt/jboss/keycloak/themes
ADD ./patch/modules /opt/jboss/keycloak/modules
