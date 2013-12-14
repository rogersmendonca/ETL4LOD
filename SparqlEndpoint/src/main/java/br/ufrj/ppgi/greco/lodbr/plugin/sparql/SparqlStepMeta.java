package br.ufrj.ppgi.greco.lodbr.plugin.sparql;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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

	// TODO Fields for serialization
	public enum Field {
		DATA_ROOT_NODE,
		ENDPOINT_URI,
		DEFAULT_GRAPH,
		QUERY_STRING,
		PREFIXES,
		VAR_PREFIX
	}
	

	// TODO Campos do step
	String endpointUri;
	String defaultGraph;
	String queryString;
	List <List<String>> prefixes;
	String varPrefix;
	//List<ValueMetaInterface> outputVars;	// non-persistable
	

	public SparqlStepMeta() {
		setDefault();
	}


	// TODO Validar todos os campos para dar feedback ao usuario!
	@Override
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta,
			StepMeta stepMeta, RowMetaInterface prev, String[] input, String[] output,
			RowMetaInterface info) {

//		if (Const.isEmpty(fieldName)) {
//			CheckResultInterface error = new CheckResult(
//					CheckResult.TYPE_RESULT_ERROR,
//					"error",
//					stepMeta);
//			remarks.add(error);
//		}
//		else {
			CheckResultInterface ok = new CheckResult(
					CheckResult.TYPE_RESULT_OK,
					"",
					stepMeta);
			remarks.add(ok);
//		}
	}

	@Override
	public StepInterface getStep(StepMeta stepMeta,
			StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		return new SparqlStep(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public StepDataInterface getStepData() {
		return new SparqlStepData();
	}
	
	@Override
	public String getDialogClassName() {
		return SparqlStepDialog.class.getName();
	}
	
	
	// TODO Carregar campos a partir do XML de um .ktr
	@SuppressWarnings("unchecked")
	@Override
	public void loadXML(Node stepDomNode, List<DatabaseMeta> databases,
			Map<String, Counter> sequenceCounters) throws KettleXMLException {

		try {
			XStream xs = new XStream(new DomDriver());
			
			StringWriter sw = new StringWriter();
			Transformer t = TransformerFactory.newInstance().newTransformer();
			// IPC: se algum colocar um caracter, seja qual for, no getXML() o getFirstChild() para de funcionar aqui!
			t.transform(new DOMSource(
					XMLHandler.getSubNode(stepDomNode, Field.DATA_ROOT_NODE.name()).getFirstChild()),
					new StreamResult(sw));
	
			Map<String, Object> data = (Map<String, Object>) xs.fromXML(sw.toString());

			endpointUri = (String) data.get(Field.ENDPOINT_URI.name());
			defaultGraph = (String) data.get(Field.DEFAULT_GRAPH.name());
			queryString = (String) data.get(Field.QUERY_STRING.name());
			prefixes = (List<List<String>>) data.get(Field.PREFIXES.name());
			varPrefix = (String) data.get(Field.VAR_PREFIX.name());
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}


	// TODO Gerar XML para salvar um .ktr
	@Override
	public String getXML() throws KettleException {
		
		XStream xs = new XStream();
		
		Map<String, Object> data = new TreeMap <String, Object> ();
		
		data.put(Field.ENDPOINT_URI.name(), endpointUri);
		data.put(Field.DEFAULT_GRAPH.name(), defaultGraph);
		data.put(Field.QUERY_STRING.name(), queryString);
		data.put(Field.PREFIXES.name(), prefixes);
		data.put(Field.VAR_PREFIX.name(), varPrefix);
				
		return String.format("<%1$s>%2$s</%1$s>", Field.DATA_ROOT_NODE.name(), xs.toXML(data));
	}


	// Rogers (2012): Carregar campos a partir do repositorio
	@SuppressWarnings("unchecked")
	@Override
	public void readRep(Repository repository, ObjectId stepIdInRepository,
			List<DatabaseMeta> databases, Map<String, Counter> sequenceCounters)
			throws KettleException {

		endpointUri = repository.getStepAttributeString(stepIdInRepository, Field.ENDPOINT_URI.name());
		defaultGraph = repository.getStepAttributeString(stepIdInRepository, Field.DEFAULT_GRAPH.name());
		queryString = repository.getStepAttributeString(stepIdInRepository, Field.QUERY_STRING.name());
		prefixes = (List<List<String>>) new XStream().fromXML(
				repository.getStepAttributeString(stepIdInRepository, Field.PREFIXES.name()));
		varPrefix = repository.getStepAttributeString(stepIdInRepository, Field.VAR_PREFIX.name());
	}

	
    // Rogers (2012): Persistir campos no repositorio
	@Override
	public void saveRep(Repository repository, ObjectId idOfTransformation, ObjectId idOfStep)
			throws KettleException {
		
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.ENDPOINT_URI.name(), endpointUri);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.DEFAULT_GRAPH.name(), defaultGraph);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.QUERY_STRING.name(), queryString);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.PREFIXES.name(), new XStream().toXML(prefixes));
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.VAR_PREFIX.name(), varPrefix);
	}

	
	// TODO Inicializacoes default
	@Override
	public void setDefault() {
		endpointUri = "";
		defaultGraph = "";
		queryString = "";
		prefixes = new ArrayList<List<String>>();
		varPrefix = "sparql_";
		//outputVars = null;
	}


	/**
	 * TODO It describes what each output row is going to look like
	 */
	@Override
	public void getFields(RowMetaInterface inputRowMeta, String name,
			RowMetaInterface[] info, StepMeta nextStep, VariableSpace space)
			throws KettleStepException {

		List<ValueMetaInterface> outVars = SparqlStepUtils.generateOutputVars(varPrefix, this.getFullQueryString());
		
		if (outVars != null) {
			
			//setOutputVars(outVars);
		
			for (ValueMetaInterface field : outVars) {
				field.setOrigin(name);
				inputRowMeta.addValueMeta(field);
			}
		}
	}

	
	// TODO Getters & Setters
	
	/**
	 * Generated on-the-fly
	 */
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
		if (defaultGraph == null) defaultGraph = "";
		this.defaultGraph = defaultGraph;
	}


	public String getQueryString() {
		return queryString;
	}
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
	
	
	public List <List<String>> getPrefixes() {
		return prefixes;
	}
	public void setPrefixes(List <List<String>> prefixes) {
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
