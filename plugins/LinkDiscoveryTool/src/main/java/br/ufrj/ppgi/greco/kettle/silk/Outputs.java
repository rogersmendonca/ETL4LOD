package br.ufrj.ppgi.greco.kettle.silk;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Outputs {
	
	@XmlElement(name = "Output")
	private List<Output> outputs;
	
	public Outputs(){
		outputs = new ArrayList<>();
	}
	
	public void addOutput(Output output){
		this.outputs.add(output);
	}
}