package com.aem.community.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AEMMigrationUtil {
	private static final Logger logger = LoggerFactory.getLogger(AEMMigrationUtil.class);
	@Reference
	private static ResourceResolverFactory resolverFactory;

	public static void main(String[] args) throws Exception {
		System.out.println("============= AEMMigrationUtil tool=========");
		Repository repository = JcrUtils.getRepository("http://localhost:4502/crx/server");
		javax.jcr.Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
		long currentMillis = System.currentTimeMillis();
		Node contentNode = null;
		Node homeNode = session.getNode("/content");
		Node rootNode = null;
		Node imageNode = null;
		Node responsiveGridNode = null;
		Node teaserCategoryNode = null;
		Node titleNode = null;
		Node respNode = null;
		Node textNode = null;

		contentNode = createPage(session, currentMillis, homeNode);

		System.out.println("=================Reading file=====================");

		try (BufferedReader br = new BufferedReader(new FileReader("InputFile.csv"))) {
			String line;
			while ((line = br.readLine()) != null) {
				logger.debug(line);
				String[] elements = line.split(",");
				if (elements[0].startsWith("rootParsys") && elements[1].trim().equalsIgnoreCase("Node")) {
					rootNode = contentNode.addNode("root", "nt:unstructured");
				} else if (elements[0].startsWith("rootParsys") && elements[1].trim().equalsIgnoreCase("Property")) {
					rootNode.setProperty(elements[2].trim(), elements[3].trim());
					session.save();
				}

				if (elements[0].startsWith("image") && elements[1].equalsIgnoreCase("Node")) {
					imageNode = rootNode.addNode("hero_image", elements[3].trim());
				} else if (elements[0].startsWith("image") && elements[1].equalsIgnoreCase("Property")) {
					imageNode.setProperty(elements[2].trim(), elements[3].trim());
					session.save();
				}
				if (elements[0].startsWith("responsiveGrid") && elements[1].equalsIgnoreCase("Node")) {
					responsiveGridNode = rootNode.addNode("responsivegrid", elements[3].trim());
				} else if (elements[0].startsWith("responsiveGrid") && elements[1].equalsIgnoreCase("Property")) {
					responsiveGridNode.setProperty(elements[2].trim(), elements[3].trim());
					session.save();
				}

				if (elements[0].startsWith("titleResposivegrid") && elements[1].equalsIgnoreCase("Node")) {
					titleNode = responsiveGridNode.addNode("title", elements[3].trim());
				} else if (elements[0].startsWith("titleResposivegrid") && elements[1].equalsIgnoreCase("Property")) {
					titleNode.setProperty(elements[2].trim(), elements[3].trim());
					session.save();
				}

				if (elements[0].startsWith("category")) {
					buildTeaserNode(session, responsiveGridNode, br, elements);
				}
				if (elements[0].startsWith("text") && elements[1].equalsIgnoreCase("Node")) {
					textNode = responsiveGridNode.addNode("text", elements[3].trim());
				} else if (elements[0].startsWith("text") && elements[1].equalsIgnoreCase("Property")) {
					textNode.setProperty(elements[2].trim(), elements[3]);
					session.save();
				}
			}

			System.out.println("--------Migration completed--------------");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			session.logout();
		}
		System.out.println("================================");
		System.out.println("============done================");
		System.out.println("================================");
	}

	private static void buildTeaserNode(javax.jcr.Session session, Node responsiveGridNode, BufferedReader br,
			String[] elements) throws ItemExistsException, PathNotFoundException, NoSuchNodeTypeException,
			LockException, VersionException, ConstraintViolationException, RepositoryException, IOException,
			ValueFormatException, AccessDeniedException, ReferentialIntegrityException, InvalidItemStateException {
		int linecounter = 3;
		Node teaserCategory = null;
		if (elements[0].startsWith("category") && elements[1].equalsIgnoreCase("Node")) {
			logger.debug("--adding node---");
			teaserCategory = responsiveGridNode.addNode("category_teasor_" + System.currentTimeMillis(),
					elements[3].trim());
		}
		while (linecounter > 0 && elements[0].startsWith("category")) {
			logger.debug("----inside while----" + linecounter);
			String nextLine = br.readLine();
			logger.debug("nextLine--" + nextLine);
			String[] categoryItems = nextLine.split(",");
			if (categoryItems[0].startsWith("category") && categoryItems[1].equalsIgnoreCase("Property")) {
				logger.debug("adding property");
				String text = categoryItems[3].replaceAll(";", ",");
				teaserCategory.setProperty(categoryItems[2].trim(), text);
			}
			linecounter--;
		}
		session.save();

		Node cqResonsivenode = teaserCategory.addNode("cq:responsive", "nt:unstructured");
		Node defaultNode_ = cqResonsivenode.addNode("default", "nt:unstructured");
		defaultNode_.setProperty("width", "6");
		session.save();
	}

	private static Node createPage(javax.jcr.Session session, long currentMillis, Node homeNode)
			throws ItemExistsException, PathNotFoundException, NoSuchNodeTypeException, LockException, VersionException,
			ConstraintViolationException, RepositoryException, AccessDeniedException, ReferentialIntegrityException,
			InvalidItemStateException, ValueFormatException {
		Node pathNode;
		Node contentNode = null;
		try {
			Node companyNode = session.getNode("/content/company");
			if (companyNode != null) {
				logger.debug("---removing the company page-----");
				companyNode.remove();
			}
		} catch (Exception ex) {
			logger.debug("page /content/company does not exist for deletion");
		}

		logger.debug("---Creating company the page-----");
		pathNode = homeNode.addNode("company", "cq:Page");
		session.save();
		String fileName = "InputFile.csv";
		List<String> list = new ArrayList<>();
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
			list = stream.filter(line -> line.startsWith("page")).collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}

		// list.forEach(System.out::println);
		Iterator<String> it = list.iterator();
		while (it.hasNext()) {
			String[] elements = it.next().split(",");
			if (elements[1].trim().equalsIgnoreCase("Node")) {
				contentNode = pathNode.addNode("jcr:content", elements[3].trim());
			}
			if (contentNode != null && elements[1].trim().equalsIgnoreCase("Property")) {
				contentNode.setProperty(elements[2], elements[3].trim());
			}
		}
		session.save();
		return contentNode;
	}

}
