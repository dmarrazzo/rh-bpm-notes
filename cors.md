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

## EAP Configuration

https://github.com/jesuino/dashbuilder-docker/blob/master/demos/custom_images/jbpm-server/conf/standalone.xml#L543-L567

## Interesting articles

[https://access.redhat.com/solutions/2839031]()

[http://toptenstartups.com/question/access-control-allow-origin-to-wildfly-8-1-0-configuration/]()

