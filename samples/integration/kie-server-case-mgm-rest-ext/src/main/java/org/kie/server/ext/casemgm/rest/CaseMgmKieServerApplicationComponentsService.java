package org.kie.server.ext.casemgm.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.kie.server.services.api.KieServerApplicationComponentsService;
import org.kie.server.services.api.KieServerRegistry;
import org.kie.server.services.api.SupportedTransports;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CaseMgmKieServerApplicationComponentsService implements KieServerApplicationComponentsService {
    private static final Logger logger = LoggerFactory.getLogger(CaseMgmKieServerApplicationComponentsService.class);

    private static final String OWNER_EXTENSION = "jBPM";
    
    public Collection<Object> getAppComponents(String extension, SupportedTransports type, Object... services) {
    	logger.info("begin - ext: {}", extension);
    	
        // skip calls from other than owning extension
        if ( !OWNER_EXTENSION.equals(extension) ) {
            return Collections.emptyList();
        }
        
        CustomResource customResource = new CustomResource();
        
        for( Object object : services ) { 
            if( ProcessService.class.isAssignableFrom(object.getClass()) ) {
            	customResource.setProcessService((ProcessService) object);
            	continue;
            } else if (DeploymentService.class.isAssignableFrom(object.getClass())) {
            	customResource.setDeploymentService((DeploymentService) object);
            	continue;
            } else if( KieServerRegistry.class.isAssignableFrom(object.getClass()) ) {
            	customResource.setRegistry((KieServerRegistry) object);
            	continue;
            } else if ( RuntimeDataService.class.isAssignableFrom(object.getClass())) {
				customResource.setRuntimeDataService((RuntimeDataService) object);
            	continue;
			}
        }

        ArrayList<Object> components = new ArrayList<>(1);
		if( SupportedTransports.REST.equals(type) ) {
			components.add(customResource);
        }
        
    	logger.info("end");
        return components;

    }

}
