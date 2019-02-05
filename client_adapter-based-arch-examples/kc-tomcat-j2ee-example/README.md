`keycloak & client adapter (tomcat)` example
======================

What is it?
-----------

This is an example using `J2EE Tomcat Adapter` in client adapter based architecture. 


Requirements
-----------

You need to configure the host of the client.

   ````
   XXX.XXX.XXX.XXX	sso.example.com authz.example.com uma.example.com
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
|Securing Application1|https://authz.example.com/authz-app/|user001/password|
|Securing Application2|https://uma.example.com/authz-uma-client/|user002/password|


Example Stop
-----------

To stop the sample, execute the following docker-compose command:

   ````
   docker-comporse down
   ````
