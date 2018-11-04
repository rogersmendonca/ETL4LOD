package br.ufrj.ppgi.greco.kettle.silk;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DataSources")
public class Datasets {
	
	@XmlElement(name = "Dataset")
	private List<Dataset> dataSources;
	
	public Datasets(){
		dataSources = new ArrayList<>();
	}
	
	public void add(Dataset dataset){
		this.dataSources.add(dataset);
	}
}