<%@ page import="org.keycloak.KeycloakSecurityContext" %>

<%
	response.setHeader("Expires", "-1");
	response.setHeader("Pragma","no-cache");
	response.setHeader("Cache-Control","no-cache");
	KeycloakSecurityContext keycloakContext = (KeycloakSecurityContext)request.getAttribute(KeycloakSecurityContext.class.getName());
%>

<%=keycloakContext.getTokenString()%>