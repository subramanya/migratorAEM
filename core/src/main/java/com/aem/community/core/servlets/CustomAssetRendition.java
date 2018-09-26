package com.aem.community.core.servlets;
import java.io.IOException;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.dam.api.renditions.RenditionMaker;
import com.day.cq.dam.api.renditions.RenditionTemplate;
import com.day.cq.dam.api.thumbnail.ThumbnailConfig;
import com.day.cq.dam.commons.thumbnail.ThumbnailConfigImpl;

//@SlingServlet(paths = "/bin/asset/customrenditions",
//methods = "GET", extensions = {"html"})
@Component(service=Servlet.class,
           property={
                   Constants.SERVICE_DESCRIPTION + "=Simple Demo Servlet",
                   "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                   "sling.servlet.paths="+ "/bin/asset/customrenditions",
                   "sling.servlet.extensions=" + "html"
           })

public class CustomAssetRendition extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomAssetRendition.class);

	@Reference
	private RenditionMaker renditionMaker;

	public void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		ResourceResolver resolver = request.getResourceResolver();
		String damPath = request.getParameter("damPath");
		if (StringUtils.isNotBlank(damPath)) {
			try {
				Asset asset = resolver.getResource(damPath).adaptTo(Asset.class);
				RenditionTemplate[] templates = createRenditionTemplates(asset);

				List<Rendition> renditionList = renditionMaker.generateRenditions(asset, templates);
				for (Rendition rendition : renditionList) {
					response.getWriter().println(rendition.getPath());
				}

				resolver.commit();
			} catch (Exception ex) {
				LOGGER.error("Exception {}", ex);
				response.getWriter().print(ex);
			} finally {
				if (resolver != null) {
					resolver.close();
				}
			}
		} else {
			response.getOutputStream().print("Dam Path is Empty!!!!!!!!!!!");			
		}
	}

	private RenditionTemplate[] createRenditionTemplates(Asset asset) {
		ThumbnailConfig[] thumbnails = new ThumbnailConfig[3];
		thumbnails[0] = new ThumbnailConfigImpl(458, 369, false);
		thumbnails[1] = new ThumbnailConfigImpl(375, 210, false);
		thumbnails[2] = new ThumbnailConfigImpl(110, 180, false);

		RenditionTemplate[] templates = new RenditionTemplate[thumbnails.length];
		
		for (int i = 0; i < thumbnails.length; i++) {
			ThumbnailConfig thumb = thumbnails[i];
			templates[i] = renditionMaker.createThumbnailTemplate(asset, thumb.getWidth(), thumb.getHeight(),
					thumb.doCenter());
		}
		return templates;
	}

}
