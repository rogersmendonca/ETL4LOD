package br.ufrj.ppgi.greco.kettle.silk;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.pentaho.di.core.Const;

import br.ufrj.ppgi.greco.kettle.LinkDiscoveryToolStepMeta;

@XmlRootElement(name = "Silk")
public class Silk {

	@XmlElement(name = "Prefixes")
	protected Prefixes prefixes = new Prefixes();

	@XmlElement(name = "DataSources")
	protected Datasets dataSources = new Datasets();

	@XmlElement(name = "Interlinks")
	protected Interlinks interlinks = new Interlinks();

	@XmlElement(name = "Outputs")
	protected Datasets outputs = new Datasets();

	public Silk() {
	}

	public Silk(LinkDiscoveryToolStepMeta input) {
		this.setPrefixes(input);
		this.setDataSources(input);
		this.setInterlinks(input);
		this.setOutputs(input);
	}

	/**
	 * Set the <Prefixes> tag in the Silk SLS file
	 * 
	 * @param input
	 *            information given by the user on Kettle
	 */
	public void setPrefixes(LinkDiscoveryToolStepMeta input) {
		final int PREFIX = 0;
		final int NAMESPACE = 1;
		for (int i = 0; i < input.getPrefixes().size(); i++) {
			List<String> prefix = input.getPrefixes().getRow(i);
			Prefix p = new Prefix(prefix.get(PREFIX), prefix.get(NAMESPACE));
			this.prefixes.add(p);
		}
	}

	/**
	 * Set the <DataSources> tag in the Silk SLS file
	 * 
	 * @param input
	 *            information given by the user on Kettle
	 */
	public void setDataSources(LinkDiscoveryToolStepMeta input) {
		if (input.isSparqlEndpoint(input.getSourceEndpoint())) {
			setDataSource(input, Dataset.SPARQL, "source", input.getSourceEndpoint(), input.getSourceGraph());
		} else {
			setDataSource(input, input.getFileType(input.getSourceEndpoint()), "source", input.getSourceEndpoint(),
					input.getSourceGraph());
		}

		if (input.isSparqlEndpoint(input.getTargetEndpoint())) {
			setDataSource(input, Dataset.SPARQL, "target", input.getTargetEndpoint(), input.getTargetGraph());
		} else {
			setDataSource(input, input.getFileType(input.getTargetEndpoint()), "target", input.getTargetEndpoint(),
					input.getTargetGraph());
		}
	}

	private String getFileFormat(String fileType) {
		switch (fileType) {
		case "rdf":
			return "RDF/XML";
		case "ttl":
			return "Turtle";
		case "nt":
			return "N-Triples";
		default:
			return "RDF/XML";
		}
	}

	public void setDataSource(LinkDiscoveryToolStepMeta input, String type, String id, String endpoint, String graph) {
		Dataset source = null;
		switch (type) {
		case Dataset.SPARQL:
			source = new Dataset(id, Dataset.SPARQL);
			source.add(new Param("endpointURI", Const.NVL(endpoint, "")));
			source.add(new Param("graph", Const.NVL(graph, "")));
			break;
		case "rdf":
		case "ttl":
		case "nt":
			source = new Dataset(id, Dataset.RDF);
			source.add(new Param("file", Const.NVL(endpoint, "")));
			source.add(new Param("format", Const.NVL(getFileFormat(type), "")));
			source.add(new Param("graph", Const.NVL(graph, "")));
			break;
		case "csv":
		case "xml":
			source = new Dataset(id, type);
			source.add(new Param("file", Const.NVL(endpoint, "")));
			break;
		}
		this.dataSources.add(source);
	}

	/**
	 * Set the <Interlinks> tag in the Silk SLS file
	 * 
	 * @param input
	 *            information given by the user on Kettle
	 */
	public void setInterlinks(LinkDiscoveryToolStepMeta input) {
		DataSource src = new DataSource("source", "a", input.getSourceRestriction());
		DataSource tgt = new DataSource("target", "b", input.getTargetRestriction());
		LinkageRule link = getLinkageRule(input);
		Outputs outputs = new Outputs();
		outputs.addOutput(new Output("output"));
		Interlink interlink = new Interlink("link", src, tgt, link, outputs);
		this.interlinks.add(interlink);
	}

	/**
	 * Set the <Outputs> tag in the Silk SLS file
	 * 
	 * @param input
	 *            information given by the user on Kettle
	 */
	public void setOutputs(LinkDiscoveryToolStepMeta input) {
		Dataset output = null;
		if (input.isSparqlOutput()){
			output = new Dataset("output", Dataset.SPARQL);
			output.add(new Param("endpointURI", Const.NVL(input.getOutputEndpoint(), "")));
			output.add(new Param("graph", Const.NVL(input.getOutputGraph(), "")));
		}else{
			output = new Dataset("output", "file");
			output.add(new Param("file", input.getFilePath()));
			output.add(new Param("format", "N-Triples"));
		}
		this.outputs.add(output);
	}
	
	public List<Metric> getMetrics(LinkDiscoveryToolStepMeta input){
		final int SOURCE_PATH = 0, TARGET_PATH = 1, METRIC = 2;
		List<Metric> metricList = new ArrayList<>();
		for (int i = 0; i < input.getMetrics().size(); i++) {
			List<String> metrics = input.getMetrics().getRow(i);
			Metric metric = new Metric(i+1, metrics.get(METRIC));
			metric.addInput(new Input("sourcePath" + (i+1), metrics.get(SOURCE_PATH)));
			metric.addInput(new Input("targetPath" + (i+1), metrics.get(TARGET_PATH)));
			metricList.add(metric);
		}
		return metricList;
	}

	public Aggregate createAggregation(LinkDiscoveryToolStepMeta input) {
		Aggregate aggregation = null;
		boolean hasNoAggregation = input.getMetrics() == null || input.getMetrics().size() == 0 || input.getMetrics().getValue(0, 2).isEmpty();
		if (!hasNoAggregation) {
			aggregation = new Aggregate(input.getAggregationType(), input.getAggregationType());
			aggregation.setMetrics(getMetrics(input));
		}
		return aggregation;
	}
	
	private LinkageRule getLinkageRule(LinkDiscoveryToolStepMeta input) {
		LinkageRule link = new LinkageRule(input.getLinkageType());
		if (input.getAggregationType() == null || input.getAggregationType().equals("")){
			link.setMetrics(getMetrics(input));
		} else {
			link = new LinkageRule(input.getLinkageType(), createAggregation(input));
		}
		return link;
	}

	public static void main(String[] args) {
		LinkDiscoveryToolStepMeta input = new LinkDiscoveryToolStepMeta();
		Silk s = new Silk(input);
		try {
			JAXBContext context = JAXBContext.newInstance(Silk.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(s, System.out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}