package com.aem.community.core;
import java.util.HashMap;
import java.util.Map;
import javax.jcr.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate=true)
public class WriteServiceImpl implements WriteService {
	

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Activate
	public void doAWriteOperation() throws LoginException {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(ResourceResolverFactory.SUBSERVICE, "writeService");
		ResourceResolver resolver = null;
		try {
			resolver = resolverFactory.getServiceResourceResolver(param);
			log.info(resolver.getUserID());
			Resource res = resolver.getResource("/content/mydata/jcr:content");
			ValueMap readMap = res.getValueMap();
			log.info(readMap.get("jcr:primaryType", ""));
			ModifiableValueMap modMap = res.adaptTo(ModifiableValueMap.class);
			if(modMap != null){
				modMap.put("myKey", "myValue");
				resolver.commit();
				log.info("Successfully saved");
			}
		} catch (PersistenceException e) {
			log.error("LoginException",e);
		} catch (org.apache.sling.api.resource.LoginException e) {
			e.printStackTrace();
		}finally{
			if(resolver != null && resolver.isLive()){
				resolver.close();
			}
		}
	}
}
