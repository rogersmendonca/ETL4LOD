package br.ufrj.ppgi.greco.kettle.silk;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAttribute;

@XmlRootElement
public class Interlink {
	
	@XmlAttribute
	private String id;
	
	@XmlElement(name="SourceDataset")
	protected DataSource source;
	
	@XmlElement(name="TargetDataset")
	protected DataSource target;
	
	@XmlElement(name="LinkageRule")
	protected LinkageRule link;
	
	@XmlElement(name="Outputs")
	protected Outputs output;
	
	//protected List<Transform> transform;
	
	public Interlink(){}
	
	public Interlink(String id, DataSource source, DataSource target, LinkageRule link, Outputs output){
		this.id = id;
		this.source = source;
		this.target = target;
		this.link = link;
		this.output = output;
	}
}