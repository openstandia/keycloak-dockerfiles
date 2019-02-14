`keycloak & client adapter (tomcat)` example
======================

What is it?
-----------

This is an example using `Tomcat Adapter` in client adapter based architecture.


Requirements
-----------

You need to configure the host of the client.

   ````
   XXX.XXX.XXX.XXX	sso.example.com authz.example.com uma.example.com
   ````


Example Build and Run
-----------

To build and run the sample, run the following mvn and docker-compose command:

   ````
   # build war application
   mvn clean package -f tomcat1/authz-app/pom.xml
   mvn clean package -f tomcat2/authz-uma-api/pom.xml
   mvn clean package -f tomcat2/authz-uma-client/pom.xml

   # invoke docker container
   docker-compose up -d --build
   ````


Example Confirm
-----------

To confirm the sample, start the browser and use the following URL, userid and password to access it.

|name|URL|userid/passsword|
|:--|:--|:--|
|Keycloak Admin Console|https://sso.example.com/auth/admin/|admin/password|
|Authz Application|https://authz.example.com/authz-app/|user001/password<br>user002/password<br>user003/password<br>admin001/password<br>admin002/password<br>admin003/password<br>|
|UMA Application|https://uma.example.com/authz-uma-client/|user001/password<br>user002/password<br>user003/password|


Example Stop
-----------

To stop the sample, execute the following docker-compose command:

   ````
   docker-compose down
   ````
