/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.formModeler.core.fieldTypes.auto;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Template;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.jbpm.formModeler.core.fieldTypes.CustomFieldType;
import org.jbpm.formModeler.service.bb.mvc.components.URLMarkupGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * First parameter: the URL of the rest service
 * Second parameter: the property to lookup in the rest response
 */
public class AutocompleteCustomType implements CustomFieldType {

    private Logger log = LoggerFactory.getLogger(AutocompleteCustomType.class);


    @Inject
    private URLMarkupGenerator urlMarkupGenerator;

    protected String dropIcon;
    protected String iconFolder;
    protected String defaultFileIcon;
    protected Map<String, String> icons;

    @PostConstruct
    public void init() {
    }

    @Override
    public String getDescription(Locale locale) {
    	log.debug(">>> getDescription");
        ResourceBundle bundle = ResourceBundle.getBundle("org.jbpm.formModeler.core.fieldTypes.auto.messages", locale);
        return bundle.getString("description");
    }

    @Override
    public Object getValue(Map requestParameters, Map requestFiles, String fieldName, String namespace, Object previousValue, boolean required, boolean readonly, String... params) {
        log.debug(">>> getValue");
    	for (int i = 0; i < params.length; i++) {
			log.debug(">>>> param: "+params[i]);
		}
    	return previousValue;
    }

    @Override
    public String getShowHTML(Object value, String fieldName, String namespace, boolean required, boolean readonly, String... params) {
    	log.debug(">>> getShowHTML");
    	for (int i = 0; i < params.length; i++) {
			log.debug(">>>> param: "+params[i]);
		}
    	return renderField(fieldName, (String) value, namespace, false, params[0], params[1]);
    }

    @Override
    public String getInputHTML(Object value, String fieldName, String namespace, boolean required, boolean readonly, String... params) {
    	log.debug(">>> getInputHTML()");
    	for (int i = 0; i < params.length; i++) {
			log.debug(">>>> param: "+params[i]);
		}
    	return renderField(fieldName, (String) value, namespace, true && !readonly, params[0], params[1]);
    }

    public String renderField(String fieldName, String path, String namespace, boolean showInput, String restURL, String property) {
        /*
         * We are using a .ftl template to generate the HTML to show on screen, as it is a sample you can use any other way to do that.
         * To see the template format look at input.ftl on the resources folder.
         */
        String str = null;
        try {

            Map<String, Object> context = new HashMap<String, Object>();

            // if there is a file in the specified path, the input will show a link to download it.
            context.put("inputId", namespace + "_autocomplete_" + fieldName);
            // If the field is readonly or we are just showing the field value we will hide the input file.
            context.put("showInput", showInput);
            
            //REST URL
            context.put("restURL", restURL);
            //property
            context.put("property", property);
            

            InputStream src = this.getClass().getResourceAsStream("input.ftl");
            freemarker.template.Configuration cfg = new freemarker.template.Configuration();
            BeansWrapper defaultInstance = new BeansWrapper();
            defaultInstance.setSimpleMapWrapper(true);
            cfg.setObjectWrapper(defaultInstance);
            cfg.setTemplateUpdateDelay(0);
            Template temp = new Template(fieldName, new InputStreamReader(src), cfg);
            StringWriter out = new StringWriter();
            temp.process(context, out);
            out.flush();
            str = out.getBuffer().toString();
        } catch (Exception e) {
            log.warn("Failed to process template for field '{}'", fieldName, e);
        }
        return str;
    }
}
