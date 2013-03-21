package de.fuberlin.wiwiss.pubby;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * A {@link DataSource} that provides access to an index of the resources in one
 * or more other data sources.
 */
public class IndexDataSource implements DataSource {
	private final static String INDEX_TITLE = "Index of Resources";
	private final static Model EMPTY_MODEL = ModelFactory.createDefaultModel();

	private final String indexURL;
	private final Collection<Dataset> datasets;
	private final Configuration config;

	public IndexDataSource(String indexURL, Collection<Dataset> datasets,
			Configuration config) {
		this.indexURL = indexURL;
		this.datasets = datasets;
		this.config = config;
	}

	public String getEndpointURL() {
		return null;
	}

	public Model getResourceDescription(String resourceURI) {
		if (!indexURL.equals(resourceURI)) {
			return EMPTY_MODEL;
		}
		List<HypermediaResource> resources = new ArrayList<HypermediaResource>();
		for (Dataset dataset : datasets) {
			resources.addAll(dataset.getIndex(config));
		}
		return describe(resources);
	}

	public Model getAnonymousPropertyValues(String resourceURI,
			Property property, boolean isInverse) {
		return EMPTY_MODEL;
	}

	public List<Resource> getIndex() {
		return Collections.singletonList(ResourceFactory
				.createResource(indexURL));
	}

	private Model describe(List<HypermediaResource> resources) {
		Model model = ModelFactory.createDefaultModel();
		model.setNsPrefix("sioc", SIOC_NS);
		model.setNsPrefix("rdfs", RDFS.getURI());
		model.setNsPrefixes(config.getPrefixes());
		Resource index = model
				.createResource(config.getWebApplicationBaseURI());
		for (HypermediaResource resource : resources) {
			Resource r = model.createResource(resource.getAbsoluteIRI());
			if (index.equals(r))
				continue;
			index.addProperty(siocContainerOf, r);
		}
		index.addProperty(RDFS.label, INDEX_TITLE);
		return model;
	}

	private final static String SIOC_NS = "http://rdfs.org/sioc/ns#";
	private final static Property siocContainerOf = ResourceFactory
			.createProperty(SIOC_NS + "container_of");
}
