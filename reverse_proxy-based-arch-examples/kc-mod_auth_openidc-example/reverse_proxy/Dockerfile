# Dockerイメージの指定
FROM centos:centos7

# 各種バージョンの定義
ENV HTTPD_VERSION=2.4.6-80.el7.centos \
    MOD_AUTH_OPENIDC_VERSION=2.3.6-1.el7.x86_64 \
    MOD_AUTH_OPENIDC_SHORT_VERSION=2.3.6 \
    HIREDIS_VERSION=0.12.1-1.el7.x86_64 \
    CJOSE_VERSION=0.5.1-1.el7.centos.x86_64

# iproute と Apache HTTPD Serverインストール
RUN set -x \
  && yum install -y iproute \
  && yum install -y httpd-${HTTPD_VERSION} \
  && yum clean all 

# mod_auth_openidcのインストール
RUN mkdir -p /tmp/work \
  && cd /tmp/work \
  && curl -k -L -O https://dl.fedoraproject.org/pub/epel/7/x86_64/Packages/h/hiredis-${HIREDIS_VERSION}.rpm \
  && curl -k -L -O https://github.com/zmartzone/mod_auth_openidc/releases/download/v2.3.0/cjose-${CJOSE_VERSION}.rpm \
  && curl -k -L -O https://github.com/zmartzone/mod_auth_openidc/releases/download/v${MOD_AUTH_OPENIDC_SHORT_VERSION}/mod_auth_openidc-${MOD_AUTH_OPENIDC_VERSION}.rpm \
  && yum install -y *.rpm \
  && yum clean all \
  && rm -rf /tmp/work

# シンボリックリンクで出力先を標準出力(dev/stdout)、標準エラー(/dev/stderr)に設定
RUN ln -sf /dev/stdout /var/log/httpd/access_log && ln -sf /dev/stderr /var/log/httpd/error_log

# リバプロの設定、mod_auth_openidcの設定情報の配置
COPY proxy.conf /etc/httpd/conf.d/

# コンテナ実行時のリッスンポート
EXPOSE 80

# docker run時の初期起動シェルの配置
COPY docker-entrypoint.sh /opt/
RUN chmod -v +x /opt/docker-entrypoint.sh

# ENTRYPOINTに初期起動シェルパスを設定
ENTRYPOINT [ "/opt/docker-entrypoint.sh" ]
