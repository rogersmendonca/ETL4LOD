package br.ufrj.ppgi.greco.kettle.silk;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Interlinks {
	
	@XmlElement(name = "Interlink")
	private List<Interlink> interlinks = new ArrayList<>();
	
	public Interlinks(){}
	
	public void add(Interlink interlink){
		this.interlinks.add(interlink);
	}
}