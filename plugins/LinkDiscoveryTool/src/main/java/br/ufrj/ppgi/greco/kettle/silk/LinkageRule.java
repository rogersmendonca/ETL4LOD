package br.ufrj.ppgi.greco.kettle.silk;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;

@XmlRootElement
public class LinkageRule {
	
	@XmlAttribute
	private String linkType; ;
	
	@XmlElement(name = "Aggregate")
	private Aggregate aggregation; //only one aggregation supported
	
	@XmlElement(name = "Compare")
	private List<Metric> metrics;
	
	public LinkageRule(){}
	
	public LinkageRule (String linkType, Aggregate aggregation){
		this.linkType = linkType;
		this.aggregation = aggregation;
	}
	
	public LinkageRule (String linkType){
		this.linkType = linkType;
	}
	
	public void addMetric(Metric m){
		this.metrics.add(m);
	}
	
	public void setMetrics(List<Metric> metrics){
		this.metrics = metrics;
	}
}