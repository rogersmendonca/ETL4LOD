package br.ufrj.ppgi.greco.kettle.silk;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Prefixes {
	
	@XmlElement(name = "Prefix")
	private List<Prefix> prefixes = new ArrayList<>();
	
	public void add(Prefix prefix){
		this.prefixes.add(prefix);
	}
}