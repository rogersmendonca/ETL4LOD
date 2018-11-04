package br.ufrj.ppgi.greco.kettle.silk;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAttribute;

@XmlRootElement
public class Input {
	
	@XmlAttribute
	private String id;
	
	@XmlAttribute
	private String path;
	
	public Input(){}
	
	public Input(String id, String path){
		this.id = id;
		this.path = path;
	}
}