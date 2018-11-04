package br.ufrj.ppgi.greco.kettle.silk;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAttribute;

@XmlRootElement
public class Dataset {

	public static final String SPARQL = "sparqlEndpoint";
	public static final String CSV    = "csv";
	public static final String RDF    = "file";
	public static final String XML    = "xml";

	@XmlAttribute
	private String id;

	@XmlAttribute
	private String type;

	@XmlElement(name = "Param")
	protected List<Param> params;

	public Dataset() {}

	public Dataset(String id, String type) {
		params = new ArrayList<>();
		this.id = id;
		this.type = type;
		
		switch (this.type) {
		case SPARQL:
			this.setDefaultSparqlParams();
			break;
		case CSV:
			this.setDefaultCsvParams();
			break;
		case RDF:
			this.setDefaultRdfParams();
			break;
		case XML:
			this.setDefaultXmlParams();
		default:
			break;
		}
	}

	public void add(Param param) {
		this.params.add(param);
	}

	/**
	 * Set the default Silk SLS parameters that the user
	 * will not be able to set on the view
	 */
	public void setDefaultSparqlParams() {
		this.params.add(new Param("pageSize", "1000"));
		this.params.add(new Param("pauseTime", "0"));
		this.params.add(new Param("retryCount", "3"));
		this.params.add(new Param("retryPause", "1000"));
		this.params.add(new Param("queryParameters", "1000"));
		this.params.add(new Param("login", ""));
		this.params.add(new Param("useOrderBy", "true"));
		this.params.add(new Param("entityList", ""));
		this.params.add(new Param("parallel", "true"));
		this.params.add(new Param("password", ""));
	}
	
	public void setDefaultCsvParams() {
		this.params.add(new Param("arraySeparator", "1000"));
		this.params.add(new Param("separator", ","));
		this.params.add(new Param("prefix", ""));
		this.params.add(new Param("uri", ""));
		this.params.add(new Param("quote", "\""));
		this.params.add(new Param("properties", ""));
		this.params.add(new Param("regexFilter", ""));
		this.params.add(new Param("charset", "UTF-8"));
		this.params.add(new Param("linesToSkip", "0"));
	}
	
	public void setDefaultRdfParams(){
		
	}
	
	public void setDefaultXmlParams(){
		this.params.add(new Param("basePath", ""));
		this.params.add(new Param("uriPattern", ""));
	}

}