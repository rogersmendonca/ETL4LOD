package br.ufrj.ppgi.greco.kettle.silk;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAttribute;

@XmlRootElement
public class Param {
	
	@XmlAttribute
	private String name;
	
	@XmlAttribute
	private String value; //TODO: needs to be required!
	
	public Param(){}
	
	public Param(String name, String value){
		this.name = name;
		this.value = value;
	}
}