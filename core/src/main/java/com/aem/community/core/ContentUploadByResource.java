package com.aem.community.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

public class ContentUploadByResource {
	@Reference
	private ResourceResolverFactory resolverFactory;

	public static void main(String[] args) throws RepositoryException {
		new ContentUploadByResource().contentUplaoder();
		
	}
	public void contentUplaoder() throws RepositoryException {
		Repository repository = JcrUtils.getRepository("http://localhost:4502/crx/server");
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(ResourceResolverFactory.SUBSERVICE, "writeService");
		ResourceResolver resolver = null;
		try {
			resolver = resolverFactory.getServiceResourceResolver(param);
		}catch(Exception ex){
			
		}
	}

}
