# Dockerイメージの指定
FROM jboss/keycloak:4.8.3.Final

# demo用初期設定ファイルの配置
COPY *.json /opt/jboss/import/

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
