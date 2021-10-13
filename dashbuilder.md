Dashbuilder
============================================================================

When moving to a Kie Server different from where you created the datasets the queries won't be created, so an error is expected. The solution is to configure dashbuilder runtime to create the queries for you:

dashbuilder.kieserver.serverTemplate.{SERVER_TEMPLATE_NAME}.replace_query=true

Just use the same serverTemplate you used to set the credentials.

See: https://github.com/kiegroup/kie-docs/blob/master/doc-content/jbpm-docs/src/main/asciidoc/BAM/DashbuilderRuntime-section.adoc