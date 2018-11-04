package br.ufrj.ppgi.greco.kettle.silk;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAttribute;

@XmlRootElement
public class DataSource {
	
	@XmlAttribute
	private String dataSource;
	
	@XmlAttribute
	private String var;
	
	@XmlElement(name = "RestrictTo")
	private String restrictTo;
	
	public DataSource(){}
	
	public DataSource(String dataSource, String var, String restrictTo){
		this.dataSource = dataSource;
		this.var = var;
		this.restrictTo = restrictTo;
	}
}