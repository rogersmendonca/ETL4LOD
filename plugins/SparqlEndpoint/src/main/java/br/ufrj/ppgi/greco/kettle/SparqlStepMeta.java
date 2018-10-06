package br.ufrj.ppgi.greco.kettle;

import java.util.ArrayList;
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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class SparqlStepMeta extends BaseStepMeta implements StepMetaInterface {

	public enum Field {
		DATA_ROOT_NODE, ENDPOINT_URI, DEFAULT_GRAPH, QUERY_STRING, PREFIXES, VAR_PREFIX
	}

	String endpointUri;
	String defaultGraph;
	String queryString;
	String varPrefix;
	List<List<String>> prefixes;

	public SparqlStepMeta() {
		setDefault();
	}

	// TODO Validar todos os campos para dar feedback ao usuario!
	@Override
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev,
			String[] input, String[] output, RowMetaInterface info) {

		// if (Const.isEmpty(fieldName)) {
		// CheckResultInterface error = new CheckResult(
		// CheckResult.TYPE_RESULT_ERROR,
		// "error",
		// stepMeta);
		// remarks.add(error);
		// }
		// else {
		CheckResultInterface ok = new CheckResult(CheckResult.TYPE_RESULT_OK, "", stepMeta);
		remarks.add(ok);
		// }
	}

	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		return new SparqlStep(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	public StepDataInterface getStepData() {
		return new SparqlStepData();
	}

	@Override
	public String getDialogClassName() {
		return SparqlStepDialog.class.getName();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadXML(Node stepDomNode, List<DatabaseMeta> databases, Map<String, Counter> sequenceCounters)
			throws KettleXMLException {
		
		XStream xs = new XStream(new DomDriver());		
		endpointUri = XMLHandler.getTagValue(stepDomNode, Field.ENDPOINT_URI.name());
		defaultGraph = XMLHandler.getTagValue(stepDomNode, Field.DEFAULT_GRAPH.name());
		queryString = XMLHandler.getTagValue(stepDomNode, Field.QUERY_STRING.name());
		prefixes = (List<List<String>>) xs.fromXML(XMLHandler.getTagValue(stepDomNode, Field.PREFIXES.name()));
		varPrefix = XMLHandler.getTagValue(stepDomNode, Field.VAR_PREFIX.name());

	}
	
	@Override
	public String getXML() throws KettleException {
		StringBuilder xml = new StringBuilder();
		XStream xs = new XStream(new DomDriver());
		xml.append(XMLHandler.addTagValue(Field.ENDPOINT_URI.name(), endpointUri));
		xml.append(XMLHandler.addTagValue(Field.DEFAULT_GRAPH.name(), defaultGraph));
		xml.append(XMLHandler.addTagValue(Field.QUERY_STRING.name(), queryString));
		xml.append(XMLHandler.addTagValue(Field.PREFIXES.name(), xs.toXML(prefixes)));
		xml.append(XMLHandler.addTagValue(Field.VAR_PREFIX.name(), varPrefix));
		return xml.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readRep(Repository repository, ObjectId stepIdInRepository, List<DatabaseMeta> databases,
			Map<String, Counter> sequenceCounters) throws KettleException {

		endpointUri = repository.getStepAttributeString(stepIdInRepository, Field.ENDPOINT_URI.name());
		defaultGraph = repository.getStepAttributeString(stepIdInRepository, Field.DEFAULT_GRAPH.name());
		queryString = repository.getStepAttributeString(stepIdInRepository, Field.QUERY_STRING.name());
		prefixes = (List<List<String>>) new XStream(new DomDriver())
				.fromXML(repository.getStepAttributeString(stepIdInRepository, Field.PREFIXES.name()));
		varPrefix = repository.getStepAttributeString(stepIdInRepository, Field.VAR_PREFIX.name());
	}

	@Override
	public void saveRep(Repository repository, ObjectId idOfTransformation, ObjectId idOfStep) throws KettleException {

		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.ENDPOINT_URI.name(), endpointUri);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.DEFAULT_GRAPH.name(), defaultGraph);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.QUERY_STRING.name(), queryString);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.PREFIXES.name(),
				new XStream(new DomDriver()).toXML(prefixes));
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.VAR_PREFIX.name(), varPrefix);
	}

	public void setDefault() {
		endpointUri = "";
		defaultGraph = "";
		queryString = "";
		prefixes = new ArrayList<List<String>>();
		varPrefix = "sparql_";
	}

	@Override
	public void getFields(RowMetaInterface inputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep,
			VariableSpace space) throws KettleStepException {

		List<ValueMetaInterface> outVars = SparqlStepUtils.generateOutputVars(varPrefix, this.getFullQueryString());

		if (outVars != null) {
			for (ValueMetaInterface field : outVars) {
				field.setOrigin(name);
				inputRowMeta.addValueMeta(field);
			}
		}
	}
	
	public List<ValueMetaInterface> getOutputVars() {
		return SparqlStepUtils.generateOutputVars(varPrefix, getFullQueryString());
	}

	public String getEndpointUri() {
		return endpointUri;
	}

	public void setEndpointUri(String endpointUri) {
		this.endpointUri = endpointUri;
	}

	public String getDefaultGraph() {
		return defaultGraph;
	}

	public void setDefaultGraph(String defaultGraph) {
		if (defaultGraph == null)
			defaultGraph = "";
		this.defaultGraph = defaultGraph;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public List<List<String>> getPrefixes() {
		return prefixes;
	}

	public void setPrefixes(List<List<String>> prefixes) {
		this.prefixes = prefixes;
	}

	public String getVarPrefix() {
		return varPrefix;
	}

	public void setVarPrefix(String varPrefix) {
		this.varPrefix = varPrefix;
	}

	public String getFullQueryString() {
		return SparqlStepUtils.toFullQueryString(prefixes, queryString);
	}

}
