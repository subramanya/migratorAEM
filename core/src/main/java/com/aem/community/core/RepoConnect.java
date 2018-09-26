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
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


@Component(configurationPid = "example")

@Service

public class RepoConnect {
	protected static final String CONTENT_NODE_COMPANY = "company";
	protected static final String RESOURCE_TYPE_OVERVIEW="weretail/components/structure/page";
	protected static final String TEMPLATE_OVERVIEW = "/conf/we-retail/settings/wcm/templates/hero-page";
	protected static final String CONTENT_NODE_COMPANY_TITLE = "Company";
	protected static final String RESOURCE_TYPE_IMAGE = "foundation/components/image";
	protected static final String RESOURCE_TYPE_ARTICLE = "company-www/fdc/components/page/article";
	protected static final String RESOURCE_TYPE_TEXT = "company-www/fdc/components/text/text";
	protected static final String RESOURCE_TYPE_CONTENT_PAR = "foundation/components/parsys";
	protected static final String NT_UNSTRUCTURED = "nt:unstructured";
	
	//@Reference
	//final static ResourceResolverFactory resolverFactory;

	public static void main(String[] args) throws Exception {
		
		

		try {
			// readFile("input.txt");
			
			System.out.println("Rading file");
			List<String> list = new ArrayList<>();
			try (BufferedReader br = Files.newBufferedReader(Paths.get("InputFile.csv"))) {

				//br returns as stream and convert it into a List
				list = br.lines().collect(Collectors.toList());

			} catch (IOException e) {
				e.printStackTrace();
			}
		
			list.forEach(System.out::println);



			// Create a connection to the CQ repository running on local host
			Repository repository = JcrUtils.getRepository("http://localhost:4502/crx/server");

			// Create a Session
			javax.jcr.Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
			


			// Create a node that represents the root node
			Node root = session.getRootNode();

			long currentMillis = System.currentTimeMillis();
			Node homeNode = session.getNode("/content");
			Node pathNode;
			Node contentNode;
			Node rootNode;
			Node imageNode;
			Node responsiveGridNode;
			Node teaserCategoryNode;
			Node titleNode;
			Node respNode;
			Node defaultNode;
			

			contentNode = createPage(session, currentMillis, homeNode);
			
			rootNode = contentNode.addNode("root",NT_UNSTRUCTURED);
			rootNode.setProperty("sling:resourceType", "wcm/foundation/components/responsivegrid");
			session.save();
			
			imageNode = rootNode.addNode("hero_image",NT_UNSTRUCTURED);
			imageNode.setProperty("sling:resourceType", "weretail/components/content/heroimage");
			imageNode.setProperty("useFullWidth", true);
			imageNode.setProperty("fileReference", "/content/dam/we-retail/en/activities/biking/forest-trail.jpg");
			session.save();
			
			responsiveGridNode = rootNode.addNode("responsivegrid",NT_UNSTRUCTURED);
			responsiveGridNode.setProperty("sling:resourceType", "wcm/foundation/components/responsivegrid");
			session.save();
			
			titleNode=responsiveGridNode.addNode("title",NT_UNSTRUCTURED);
			titleNode.setProperty("sling:resourceType", "weretail/components/content/title");
			titleNode.setProperty("type", "h2");
			titleNode.setProperty("jcr:title", "Welcome our finest equipment");
			session.save();
			
			teaserCategoryNode = responsiveGridNode.addNode("category_teaser",NT_UNSTRUCTURED);
			teaserCategoryNode.setProperty("sling:resourceType", "weretail/components/content/categoryteaser");
			teaserCategoryNode.setProperty("fileRefernce", "/content/dam/we-retail/en/activities/hiking-camping/hiker-anapurna.jpg");
			teaserCategoryNode.setProperty("buttonLinkTo", "/content/we-retail/us/en/products/equipment/hiking" );
			session.save();
			
			respNode = teaserCategoryNode.addNode("cq:responsive",NT_UNSTRUCTURED);
			session.save();
			defaultNode = respNode.addNode("default",NT_UNSTRUCTURED);
			defaultNode.setProperty("width", 6);
			session.save();
			
			System.out.println("------done---------");
			session.logout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Node createPage(javax.jcr.Session session, long currentMillis, Node homeNode)
			throws ItemExistsException, PathNotFoundException, NoSuchNodeTypeException, LockException, VersionException,
			ConstraintViolationException, RepositoryException, AccessDeniedException, ReferentialIntegrityException,
			InvalidItemStateException, ValueFormatException {
		Node pathNode;
		Node contentNode;
		pathNode = homeNode.addNode(CONTENT_NODE_COMPANY
				+currentMillis, "cq:Page");
		session.save();
		//if ()
		contentNode = pathNode.addNode("jcr:content", "cq:PageContent");
		contentNode.setProperty("sling:resourceType", RESOURCE_TYPE_OVERVIEW);
		contentNode.setProperty("cq:template", TEMPLATE_OVERVIEW);
		contentNode.setProperty("jcr:title", CONTENT_NODE_COMPANY_TITLE);
		contentNode.setProperty("jcr:description", CONTENT_NODE_COMPANY_TITLE);
		session.save();
		return contentNode;
	}
	
	public static Map readFile(String filename) {
		
		return null;
	}
}
