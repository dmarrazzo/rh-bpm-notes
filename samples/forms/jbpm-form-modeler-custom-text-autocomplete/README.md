# Autocomplete Input Field

## Installation

build it and place in the business central lib folder

## Allow access for the remote REST service

In order to allow the remote REST service to be consumed by the browser you have to add the following header information in your REST service response:

    Access-Control-Allow-Origin: *

If your REST service is hosted by a Tomcat WAR, a simple solution is to add the following configuration to your web.xml:

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
 

Further information on the topic:
[Mozilla Cors](https://developer.mozilla.org/en-US/docs/Web/HTTP/Access_control_CORS "CORS")  

## Usage

1. Insert a field in the form that you want to handle with the autocomplete
2. Open the Properties pane (Click edit in the field tooltip)  
	![properties pane](./imgs/properties.png "properties pane")
3. In `Field type` list, select `Custom field`
4. As `Custom field` select `Autocomplete Input`
5. As `First Parameter` insert the Rest Service URL
6. The `Second Parameter` is optional, you can use it if the Rest Service returns a list of object, here you can place the object field name that you want to use to populate the suggestion list.
 
**Note:** if you fill the `Second Parameter` the list is filtered using the typed term. E.g. Typed term: 'aa', Rest response 'abb', 'aab', 'aac', the suggestion list is narrowed to 'aab', 'aab'

##Information about the REST service implementation
The ideal rest service should replies a pre filtered list of suggested options using the query parameter `term`, if it's not the case the filtering will be done on client side. 

The filtering logic is in the javascript code of the `input.flt` file, and specifically:

		  var listOfValues =[];
		  for( var i in data ) {
			if (data[i].hasOwnProperty("${property}")) {
			  value = data[i].${property};
			  if (value.indexOf(term) > -1)
			    listOfValues.push(value);
			}
		  }

You can change this logic to adapt to specific needs.