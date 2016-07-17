# CORS

## Spec

https://developer.mozilla.org/en-US/docs/Web/HTTP/Access_control_CORS

    Access-Control-Allow-Origin: *


## Tomcat not working

	<filter>
	  <filter-name>CorsFilter</filter-name>
	  <filter-class>org.apache.catalina.filters.CorsFilter</filter-class>
	  <init-param>
	    <param-name>cors.allowed.origins</param-name>
	    <param-value>*</param-value>
	  </init-param>
	</filter>
	<filter-mapping>
	  <filter-name>CorsFilter</filter-name>
	  <url-pattern>/*</url-pattern>
	</filter-mapping>
