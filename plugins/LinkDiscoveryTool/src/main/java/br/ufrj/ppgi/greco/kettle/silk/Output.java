package br.ufrj.ppgi.greco.kettle.silk;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAttribute;

@XmlRootElement
public class Output {
	
	@XmlAttribute
	private String id;
	
	public Output(){}
	
	public Output(String id){
		this.id = id;
	}
}