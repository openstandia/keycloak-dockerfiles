`keycloak & mod_auth_openidc` example
======================

What is it?
-----------

This is an example using mod_auth_openidc in reverce_proxy based architecture. 


Requirements
-----------

You need to configure the host of the client.

   ````
   XXX.XXX.XXX.XXX	sso.example.com app1.example.com app2.example.com
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

|name|url|userid/pass|
|:--|:--|:--|
|Keycloak Admin Console|https://sso.example/auth/admin/|admin/password|
|Securing Application1|https://app1.example.com/|user001/password|
|Securing Application2|https://app2.example.com/|user002/password|


Example Stop
-----------

To stop the sample, execute the following docker-compose command:

   ````
   docker-comporse down
   ````
