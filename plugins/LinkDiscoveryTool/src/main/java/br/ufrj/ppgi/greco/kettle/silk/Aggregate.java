package br.ufrj.ppgi.greco.kettle.silk;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAttribute;

@XmlRootElement(name = "Aggregate")
public class Aggregate {
	@XmlAttribute
	private String id;
	
	@XmlAttribute
	private String required;
	
	@XmlAttribute
	private String weight;
	
	@XmlAttribute
	private String type;
	
	@XmlElement(name = "Compare")
	private List<Metric> metrics = new ArrayList<>();
	
	public Aggregate(){}
	
	public Aggregate(String id, String type){
		this.id = id;
		this.required = "false";
		this.weight = "1";
		this.type = type;
	}
	
	public Aggregate(String id, String required, String weight, String type){
		this.id = id;
		this.required = required;
		this.weight = weight;
		this.type = type;
	}
	
	public void addMetric(Metric metric){
		this.metrics.add(metric);
	}
	
	public void setMetrics(List<Metric> m){
		this.metrics = m;
	}
}