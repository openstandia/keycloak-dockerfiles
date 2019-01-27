`keycloak & mod_auth_openidc` example
======================

What is it?
-----------

This is an example using mod_auth_openidc in reverce_proxy based architecture. 


Requirements
-----------

You need to configure the host of the client.

   ````
   XXX.XXX.XXX.XXX	sso.example.com smtp.example.com app1.example.com app2.example.com
   ````


Example Build and Run
-----------

To build and run the sample, run the following docker-compose command:

   ````
   docker-comporse up -d --build
   ````


Example Confirm
-----------

To confirm the sample, start the browser and use the following URL, userid and password to access it.

|name|URL|userid/passsword|
|:--|:--|:--|
|Keycloak Admin Console|https://sso.example.com/auth/admin/|admin/password|
|Mailbox (Fake SMTP)|https://smtp.example.com/|
|Securing Application1|https://app1.example.com/|user001/password|
|Securing Application2|https://app2.example.com/|user002/password|


To confirm the logout, start the browser and use the following URL.

|name|URL|
|:--|:--|
|Keycloak Logput|https://sso.example.com/auth/realms/demo/protocol/openid-connect/logout?redirect_uri=https://sso.example.com/|
|Application1 Logout|https://app1.example.com/oidc-redirect/?logout=https://app1.example.com/|
|Application2 Logout|https://app2.example.com/oidc-redirect/?logout=https://app2.example.com/|


Example Stop
-----------

To stop the sample, execute the following docker-compose command:

   ````
   docker-comporse down
   ````
