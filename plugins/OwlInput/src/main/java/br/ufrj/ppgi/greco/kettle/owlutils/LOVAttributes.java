package br.ufrj.ppgi.greco.kettle.owlutils;

import org.json.JSONObject;

public class LOVAttributes {
	private String URI;
	private String description;
	private String title;
	private String prefix;
	
	public LOVAttributes(){
		
	}
	
	public LOVAttributes(JSONObject _source){
		setURI(_source.getString("uri"));
		setDescription(_source.getString("http://purl.org/dc/terms/description@en"));
		setTitle(_source.getString("http://purl.org/dc/terms/title@en"));
		setPrefix(_source.getString("prefix"));
	}

	public String getURI() {
		return URI;
	}

	public void setURI(String URI) {
		this.URI = !URI.endsWith("/") && !URI.endsWith("#") ? URI + "#" : URI;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public boolean isEmpty(){
		return URI.equals("") || URI == null;
	}
	
}
