package br.ufrj.ppgi.greco.kettle;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.w3c.dom.Node;

import br.ufrj.ppgi.greco.kettle.plugin.tools.datatable.DataTable;
import br.ufrj.ppgi.greco.kettle.plugin.tools.datatable.DataTableConverter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class LinkDiscoveryToolStepMeta extends BaseStepMeta implements StepMetaInterface {

	public enum Field {
		CONFIG_FILE,

		// Data Sources
		SOURCE_ENDPOINT, SOURCE_GRAPH, SOURCE_RESTRICTION, TARGET_ENDPOINT, TARGET_GRAPH, TARGET_RESTRICTION,

		// Prefixes
		PREFIXES_TABLE, PREFIXES_TABLE_PREFIX, PREFIXES_TABLE_NAMESPACE,

		// Linkage Rules
		LINKAGE_TYPE, AGGREGATION_TYPE, METRICS_TABLE, METRICS_TABLE_SOURCE, METRICS_TABLE_TARGET, METRICS_TABLE_METRIC,

		// Output
		OUTPUT_FOLDER, OUTPUT_FILENAME, OUTPUT_GRAPH, OUTPUT_SPARQL, OUTPUT_ENDPOINT
	}

	private String configFile;

	private String sourceEndpoint;
	private String sourceGraph;
	private String sourceRestriction;

	private String targetEndpoint;
	private String targetGraph;
	private String targetRestriction;

	private DataTable<String> prefixes;

	private String linkageType;
	private String aggregationType;
	private DataTable<String> metrics;

	private boolean sparqlOutput;
	private String outputFolder;
	private String outputFilename;
	private String outputGraph;
	private String outputEndpoint;

	public LinkDiscoveryToolStepMeta() {
		setDefault();
	}

	@Override
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev,
			String[] input, String[] output, RowMetaInterface info) {
		CheckResultInterface ok = new CheckResult(CheckResult.TYPE_RESULT_OK, "", stepMeta);
		remarks.add(ok);
	}

	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		return new LinkDiscoveryToolStep(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	public StepDataInterface getStepData() {
		return new LinkDiscoveryToolStepData();
	}

	@Override
	public String getDialogClassName() {
		return LinkDiscoveryToolStepDialog.class.getName();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadXML(Node stepDomNode, List<DatabaseMeta> databases, Map<String, Counter> sequenceCounters)
			throws KettleXMLException {

		try {
			XStream xs = new XStream(new DomDriver());
			xs.alias("DataTable", DataTable.class);
			xs.registerConverter(new DataTableConverter());

			this.configFile = (String) XMLHandler.getTagValue(stepDomNode, Field.CONFIG_FILE.name());

			this.sourceEndpoint = (String) XMLHandler.getTagValue(stepDomNode, Field.SOURCE_ENDPOINT.name());
			this.sourceGraph = (String) XMLHandler.getTagValue(stepDomNode, Field.SOURCE_GRAPH.name());
			this.sourceRestriction = (String) XMLHandler.getTagValue(stepDomNode, Field.SOURCE_RESTRICTION.name());

			this.targetEndpoint = (String) XMLHandler.getTagValue(stepDomNode, Field.TARGET_ENDPOINT.name());
			this.targetGraph = (String) XMLHandler.getTagValue(stepDomNode, Field.TARGET_GRAPH.name());
			this.targetRestriction = (String) XMLHandler.getTagValue(stepDomNode, Field.TARGET_RESTRICTION.name());

			this.prefixes = (DataTable<String>) xs
					.fromXML(XMLHandler.getTagValue(stepDomNode, Field.PREFIXES_TABLE.name()));

			this.linkageType = (String) XMLHandler.getTagValue(stepDomNode, Field.LINKAGE_TYPE.name());
			this.aggregationType = (String) XMLHandler.getTagValue(stepDomNode, Field.AGGREGATION_TYPE.name());
			this.metrics = (DataTable<String>) xs
					.fromXML(XMLHandler.getTagValue(stepDomNode, Field.METRICS_TABLE.name()));

			this.sparqlOutput = "Y".equals(XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_SPARQL.name()));
			this.outputFolder = (String) XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_FOLDER.name());
			this.outputFilename = (String) XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_FILENAME.name());
			this.outputEndpoint = (String) XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_ENDPOINT.name());
			this.outputGraph = (String) XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_GRAPH.name());

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getXML() throws KettleException {
		XStream xs = new XStream(new DomDriver());
		xs.alias("DataTable", DataTable.class);
		xs.registerConverter(new DataTableConverter());

		StringBuilder xml = new StringBuilder();

		xml.append(XMLHandler.addTagValue(Field.CONFIG_FILE.name(), this.configFile));

		xml.append(XMLHandler.addTagValue(Field.SOURCE_ENDPOINT.name(), this.sourceEndpoint));
		xml.append(XMLHandler.addTagValue(Field.SOURCE_GRAPH.name(), this.sourceGraph));
		xml.append(XMLHandler.addTagValue(Field.SOURCE_RESTRICTION.name(), this.sourceRestriction));

		xml.append(XMLHandler.addTagValue(Field.TARGET_ENDPOINT.name(), this.targetEndpoint));
		xml.append(XMLHandler.addTagValue(Field.TARGET_GRAPH.name(), this.targetGraph));
		xml.append(XMLHandler.addTagValue(Field.TARGET_RESTRICTION.name(), this.targetRestriction));

		xml.append(XMLHandler.addTagValue(Field.PREFIXES_TABLE.name(), xs.toXML(this.prefixes)));

		xml.append(XMLHandler.addTagValue(Field.LINKAGE_TYPE.name(), this.linkageType));
		xml.append(XMLHandler.addTagValue(Field.AGGREGATION_TYPE.name(), this.aggregationType));
		xml.append(XMLHandler.addTagValue(Field.METRICS_TABLE.name(), xs.toXML(this.metrics)));

		xml.append(XMLHandler.addTagValue(Field.OUTPUT_SPARQL.name(), this.sparqlOutput));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_FOLDER.name(), this.outputFolder));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_FILENAME.name(), this.outputFilename));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_GRAPH.name(), this.outputGraph));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_ENDPOINT.name(), this.outputEndpoint));

		return xml.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readRep(Repository repository, ObjectId stepIdInRepository, List<DatabaseMeta> databases,
			Map<String, Counter> sequenceCounters) throws KettleException {
		try {

			XStream xs = new XStream(new DomDriver());
			xs.alias("DataTable", DataTable.class);
			xs.registerConverter(new DataTableConverter());

			this.configFile = repository.getStepAttributeString(stepIdInRepository, Field.CONFIG_FILE.name());

			this.sourceEndpoint = repository.getStepAttributeString(stepIdInRepository, Field.SOURCE_ENDPOINT.name());
			this.sourceGraph = repository.getStepAttributeString(stepIdInRepository, Field.SOURCE_GRAPH.name());
			this.sourceRestriction = repository.getStepAttributeString(stepIdInRepository,
					Field.SOURCE_RESTRICTION.name());

			this.targetEndpoint = repository.getStepAttributeString(stepIdInRepository, Field.TARGET_ENDPOINT.name());
			this.targetGraph = repository.getStepAttributeString(stepIdInRepository, Field.TARGET_GRAPH.name());
			this.targetRestriction = repository.getStepAttributeString(stepIdInRepository,
					Field.TARGET_RESTRICTION.name());

			this.prefixes = (DataTable<String>) xs
					.fromXML(repository.getStepAttributeString(stepIdInRepository, Field.PREFIXES_TABLE.name()));

			this.linkageType = repository.getStepAttributeString(stepIdInRepository, Field.LINKAGE_TYPE.name());
			this.aggregationType = (String) repository.getStepAttributeString(stepIdInRepository,
					Field.AGGREGATION_TYPE.name());
			this.metrics = (DataTable<String>) xs
					.fromXML(repository.getStepAttributeString(stepIdInRepository, Field.METRICS_TABLE.name()));

			this.sparqlOutput = "Y"
					.equals(repository.getStepAttributeString(stepIdInRepository, Field.OUTPUT_SPARQL.name()));
			this.outputFolder = repository.getStepAttributeString(stepIdInRepository, Field.OUTPUT_FOLDER.name());
			this.outputFilename = repository.getStepAttributeString(stepIdInRepository, Field.OUTPUT_FILENAME.name());
			this.outputGraph = repository.getStepAttributeString(stepIdInRepository, Field.OUTPUT_GRAPH.name());
			this.outputEndpoint = repository.getStepAttributeString(stepIdInRepository, Field.OUTPUT_ENDPOINT.name());

		} catch (Exception e) {
			throw new KettleException(
					"Unable to read step information from the repository for id_step=" + stepIdInRepository, e);
		}

	}

	@Override
	public void saveRep(Repository repository, ObjectId idOfTransformation, ObjectId idOfStep) throws KettleException {
		try {

			XStream xs = new XStream(new DomDriver());
			xs.alias("DataTable", DataTable.class);
			xs.registerConverter(new DataTableConverter());

			repository.saveStepAttribute(idOfTransformation, idOfStep, Field.CONFIG_FILE.name(), this.configFile);

			repository.saveStepAttribute(idOfTransformation, idOfStep, Field.SOURCE_ENDPOINT.name(),
					this.sourceEndpoint);
			repository.saveStepAttribute(idOfTransformation, idOfStep, Field.SOURCE_GRAPH.name(), this.sourceGraph);
			repository.saveStepAttribute(idOfTransformation, idOfStep, Field.SOURCE_RESTRICTION.name(),
					this.sourceRestriction);

			repository.saveStepAttribute(idOfTransformation, idOfStep, Field.TARGET_ENDPOINT.name(),
					this.targetEndpoint);
			repository.saveStepAttribute(idOfTransformation, idOfStep, Field.TARGET_GRAPH.name(), this.targetGraph);
			repository.saveStepAttribute(idOfTransformation, idOfStep, Field.TARGET_RESTRICTION.name(),
					this.targetRestriction);

			repository.saveStepAttribute(idOfTransformation, idOfStep, Field.PREFIXES_TABLE.name(),
					xs.toXML(this.prefixes));

			repository.saveStepAttribute(idOfTransformation, idOfStep, Field.LINKAGE_TYPE.name(), this.linkageType);
			repository.saveStepAttribute(idOfTransformation, idOfStep, Field.AGGREGATION_TYPE.name(),
					this.aggregationType);
			repository.saveStepAttribute(idOfTransformation, idOfStep, Field.METRICS_TABLE.name(),
					xs.toXML(this.metrics));

			repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_SPARQL.name(), this.sparqlOutput);
			repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_FOLDER.name(), this.outputFolder);
			repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_FILENAME.name(),
					this.outputFilename);
			repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_GRAPH.name(), this.outputGraph);
			repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_ENDPOINT.name(), this.outputEndpoint);

		} catch (Exception e) {
			throw new KettleException("Unable to save step information to the repository for id_step=" + idOfStep, e);
		}
	}

	public void setDefault() {
		this.sourceEndpoint = "";
		this.sourceGraph = "";
		this.sourceRestriction = "";
		this.targetEndpoint = "";
		this.targetGraph = "";
		this.targetRestriction = "";

		this.prefixes = new DataTable<String>(Field.PREFIXES_TABLE.name(), Field.PREFIXES_TABLE_PREFIX.name(),
				Field.PREFIXES_TABLE_NAMESPACE.name());

		this.linkageType = "owl:sameAs";
		this.aggregationType = "";
		this.metrics = new DataTable<String>(Field.METRICS_TABLE.name(), Field.METRICS_TABLE_SOURCE.name(),
				Field.METRICS_TABLE_TARGET.name(), Field.METRICS_TABLE_METRIC.name());

		this.outputFolder = "";
		this.outputFilename = "ntriples.nt";
		this.configFile = "";
		this.outputGraph = "";
		this.sparqlOutput = false;
		this.outputEndpoint = "";
	}

	@Override
	public void getFields(RowMetaInterface inputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep,
			VariableSpace space) throws KettleStepException {
		ValueMetaInterface field = null;

		field = new ValueMetaString("subject");
		field.setOrigin(name);
		inputRowMeta.addValueMeta(field);

		field = new ValueMetaString("predicate");
		field.setOrigin(name);
		inputRowMeta.addValueMeta(field);

		field = new ValueMetaString("object");
		field.setOrigin(name);
		inputRowMeta.addValueMeta(field);
	}

	public String getConfigFile() {
		return configFile;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

	public String getSourceEndpoint() {
		return sourceEndpoint;
	}

	public void setSourceEndpoint(String sourceEndpoint) {
		this.sourceEndpoint = sourceEndpoint;
	}

	public String getSourceGraph() {
		return sourceGraph;
	}

	public void setSourceGraph(String sourceGraph) {
		this.sourceGraph = sourceGraph;
	}

	public String getSourceRestriction() {
		return sourceRestriction;
	}

	public void setSourceRestriction(String sourceRestriction) {
		this.sourceRestriction = sourceRestriction;
	}

	public String getTargetEndpoint() {
		return targetEndpoint;
	}

	public void setTargetEndpoint(String targetEndpoint) {
		this.targetEndpoint = targetEndpoint;
	}

	public String getTargetGraph() {
		return targetGraph;
	}

	public void setTargetGraph(String targetGraph) {
		this.targetGraph = targetGraph;
	}

	public String getTargetRestriction() {
		return targetRestriction;
	}

	public void setTargetRestriction(String targetRestriction) {
		this.targetRestriction = targetRestriction;
	}

	public DataTable<String> getPrefixes() {
		return prefixes;
	}

	public void setPrefixes(DataTable<String> prefixes) {
		this.prefixes = prefixes;
	}

	public String getLinkageType() {
		return linkageType;
	}

	public void setLinkageType(String linkageType) {
		this.linkageType = linkageType;
	}

	public String getAggregationType() {
		return aggregationType;
	}

	public void setAggregationType(String aggregationType) {
		this.aggregationType = aggregationType;
	}

	public DataTable<String> getMetrics() {
		return metrics;
	}

	public void setMetrics(DataTable<String> metrics) {
		this.metrics = metrics;
	}

	public String getOutputFolder() {
		return outputFolder;
	}

	public void setOutputFolder(String outputFolder) {
		Path p = Paths.get(outputFolder);
		this.setOutputFilename(p.getFileName().toString());
		this.outputFolder = p.getParent() != null ? p.getParent().toString() : "";
	}

	public String getOutputFilename() {
		return outputFilename;
	}

	public void setOutputFilename(String outputFilename) {
		this.outputFilename = outputFilename;
	}

	public String getFilePath() {
		return Paths.get(this.getOutputFolder(), this.getOutputFilename()).toString();
	}

	public boolean isSparqlEndpoint(String location) {
		if (location != null && !location.isEmpty()) {
			String s = location.trim().toLowerCase();
			return s.startsWith("http://") || s.startsWith("https://") || s.startsWith("www.");
		}
		return false;
	}

	public String getFileType(String location) {
		if (location != null && !location.isEmpty()) {
			String s = location.trim().toLowerCase();
			if (s.lastIndexOf(".") != -1 && s.lastIndexOf(".") != 0)
				return s.substring(s.lastIndexOf(".") + 1);
			else
				return "";
		} else {
			return "";
		}
	}

	public boolean isSparqlOutput() {
		return sparqlOutput;
	}

	public void setSparqlOutput(boolean sparqlOutput) {
		this.sparqlOutput = sparqlOutput;
	}

	public String getOutputGraph() {
		return outputGraph;
	}

	public void setOutputGraph(String outputGraph) {
		this.outputGraph = outputGraph;
	}

	public String getOutputEndpoint() {
		return outputEndpoint;
	}

	public void setOutputEndpoint(String outputEndpoint) {
		this.outputEndpoint = outputEndpoint;
	}

}
