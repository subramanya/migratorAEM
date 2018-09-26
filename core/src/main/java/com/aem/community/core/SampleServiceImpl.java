package com.aem.community.core;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings("deprecation")
@Component(immediate = true, service = SampleService.class, configurationPid = "com.mindtree.commerce.connector.service.impl.MagentoCommerceConnectorImpl")
public class SampleServiceImpl implements SampleService{

    private static final Logger log = LoggerFactory.getLogger(SampleServiceImpl.class);
    @Reference
    private static ResourceResolverFactory resolverFactory;

    @Activate
    @Modified
    protected final void activate() throws Exception {

        

    }
    
    public static void main(String[] args) throws LoginException {
    	ResourceResolver resourceResolver = resolverFactory.getAdministrativeResourceResolver(null);
    	Resource res = resourceResolver.getResource("/content");
	}
}
