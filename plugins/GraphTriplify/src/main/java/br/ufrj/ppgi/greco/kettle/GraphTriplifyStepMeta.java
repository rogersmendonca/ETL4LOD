package br.ufrj.ppgi.greco.kettle;

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

/**
 * Classe de metadados do step GraphTriplify.
 * 
 * @author Rogers Reiche de Mendonca
 * 
 */
public class GraphTriplifyStepMeta extends BaseStepMeta implements StepMetaInterface {
	public enum Field {
		INPUT_GRAPH, OUTPUT_SUBJECT, OUTPUT_PREDICATE, OUTPUT_OBJECT
	}

	// Campos Step - Input
	private String inputGraph;

	// Campos Step - Output
	private String outputSubject;
	private String outputPredicate;
	private String outputObject;

	public GraphTriplifyStepMeta() {
		setDefault();
	}

	// TODO Validar todos os campos para dar feedback ao usuario! Argh!
	@Override
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev,
			String[] input, String[] output, RowMetaInterface info) {
		CheckResultInterface ok = new CheckResult(CheckResult.TYPE_RESULT_OK, "", stepMeta);
		remarks.add(ok);
	}

	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		return new GraphTriplifyStep(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	public StepDataInterface getStepData() {
		return new GraphTriplifyStepData();
	}

	@Override
	public String getDialogClassName() {
		return GraphTriplifyStepDialog.class.getName();
	}

	// Carregar campos a partir do XML de um .ktr
	@Override
	public void loadXML(Node stepDomNode, List<DatabaseMeta> databases, Map<String, Counter> sequenceCounters)
			throws KettleXMLException {
		inputGraph = XMLHandler.getTagValue(stepDomNode, Field.INPUT_GRAPH.name());
		outputSubject = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_SUBJECT.name());
		outputPredicate = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_PREDICATE.name());
		outputObject = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_OBJECT.name());
	}

	// Gerar XML para salvar um .ktr
	@Override
	public String getXML() throws KettleException {
		StringBuilder xml = new StringBuilder();

		xml.append(XMLHandler.addTagValue(Field.INPUT_GRAPH.name(), inputGraph));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_SUBJECT.name(), outputSubject));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_PREDICATE.name(), outputPredicate));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_OBJECT.name(), outputObject));
		return xml.toString();
	}

	// Carregar campos a partir do repositorio
	@Override
	public void readRep(Repository repository, ObjectId stepIdInRepository, List<DatabaseMeta> databases,
			Map<String, Counter> sequenceCounters) throws KettleException {
		inputGraph = repository.getStepAttributeString(stepIdInRepository, Field.INPUT_GRAPH.name());
		outputSubject = repository.getStepAttributeString(stepIdInRepository, Field.OUTPUT_SUBJECT.name());
		outputPredicate = repository.getStepAttributeString(stepIdInRepository, Field.OUTPUT_PREDICATE.name());
		outputObject = repository.getStepAttributeString(stepIdInRepository, Field.OUTPUT_OBJECT.name());
	}

	// Persistir campos no repositorio
	@Override
	public void saveRep(Repository repository, ObjectId idOfTransformation, ObjectId idOfStep) throws KettleException {
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.INPUT_GRAPH.name(), inputGraph);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_SUBJECT.name(), outputSubject);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_PREDICATE.name(), outputPredicate);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_OBJECT.name(), outputObject);
	}

	// Inicializacoes default
	public void setDefault() {
		inputGraph = "";
		outputSubject = "subject";
		outputPredicate = "predicate";
		outputObject = "object";
	}

	/**
	 * It describes what each output row is going to look like
	 */
	@Override
	public void getFields(RowMetaInterface outputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep,
			VariableSpace space) throws KettleStepException {
		outputRowMeta.clear();

		// Adiciona os metadados dos campos de output
		addValueMeta(outputRowMeta, outputSubject, name);
		addValueMeta(outputRowMeta, outputPredicate, name);
		addValueMeta(outputRowMeta, outputObject, name);
	}

	private void addValueMeta(RowMetaInterface rowMeta, String fieldName, String origin) {
		ValueMetaInterface field = new ValueMetaString(fieldName);
		field.setOrigin(origin);
		rowMeta.addValueMeta(field);
	}

	public String getInputGraph() {
		return inputGraph;
	}

	public void setInputGraph(String inputGraph) {
		this.inputGraph = inputGraph;
	}

	public String getOutputSubject() {
		return outputSubject;
	}

	public void setOutputSubject(String outputSubject) {
		this.outputSubject = outputSubject;
	}

	public String getOutputPredicate() {
		return outputPredicate;
	}

	public void setOutputPredicate(String outputPredicate) {
		this.outputPredicate = outputPredicate;
	}

	public String getOutputObject() {
		return outputObject;
	}

	public void setOutputObject(String outputObject) {
		this.outputObject = outputObject;
	}
}
