`Keycloak & client adapter (Tomcat)` example
======================

What is it?
-----------

This is an example using `Tomcat Adapter` in client adapter based architecture.


Requirements
-----------

You need to edit the hosts file on your local machine to access to this Docker host:

   ````
   XXX.XXX.XXX.XXX	sso.example.com authz.example.com uma.example.com
   ````


Build and Run
-----------

To build and run the example, execute the following mvn and docker-compose command:

   ````
   # build war application
   mvn clean package -f tomcat1/authz-app/pom.xml
   mvn clean package -f tomcat2/authz-uma-api/pom.xml
   mvn clean package -f tomcat2/authz-uma-client/pom.xml

   # invoke docker container
   docker-compose up -d --build
   ````

After the startup, you can access the following applications:

|name|URL|userid/passsword|
|:--|:--|:--|
|Keycloak Admin Console|http://sso.example.com/auth/admin/|admin/password|
|Authz Application|http://authz.example.com/authz-app/|user001/password<br>user002/password<br>user003/password<br>admin001/password<br>admin002/password<br>admin003/password<br>|
|UMA API|http://uma.example.com/authz-uma-api/|bearer access only|
|UMA Client|http://uma.example.com/authz-uma-client/|user001/password<br>user002/password<br>user003/password|


Stop
-----------

To stop the example, execute the following docker-compose command:

   ````
   docker-compose down
   ````
