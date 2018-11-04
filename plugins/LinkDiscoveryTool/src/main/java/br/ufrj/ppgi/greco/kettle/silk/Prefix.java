package br.ufrj.ppgi.greco.kettle.silk;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAttribute;

@XmlRootElement
public class Prefix {
	
	@XmlAttribute
	private String id;
	
	@XmlAttribute
	private String namespace;
	
	public Prefix(){}
	
	public Prefix(String id, String namespace){
		this.id = id;
		this.namespace = namespace;
	}
}