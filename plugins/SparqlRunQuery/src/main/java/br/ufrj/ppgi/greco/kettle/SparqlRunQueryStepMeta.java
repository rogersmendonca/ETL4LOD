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
import org.pentaho.di.core.row.value.ValueMetaInteger;
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

public class SparqlRunQueryStepMeta extends BaseStepMeta implements StepMetaInterface {

	// Fields for serialization
	public enum Field {
		// RDF_CONTENT,
		QUERY_CONTENT, PROTOCOL, HOSTNAME, PORT, PATH, USERNAME, PASSWORD, ENDPOINT_URL, OUT_CODE, OUT_MESSAGE
	}

	// Values - tipo refere-se ao tipo destas variaveis
	private String queryTextContentFieldName;
	private String endpointUrl;
	private String username;
	private String password;
	// Output - ATENCAO: tipo refere-se ao tipo dos campos cujos nomes sao
	// especificados por estas variaveis
	private String resultCodeFieldName;
	private String resultMessageFieldName;

	public SparqlRunQueryStepMeta() {
		setDefault();
	}

	// TODO Validar todos os campos para dar feedback ao usuario!
	@Override
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev,
			String[] input, String[] output, RowMetaInterface info) {

		CheckResultInterface ok = new CheckResult(CheckResult.TYPE_RESULT_OK, "", stepMeta);
		remarks.add(ok);

	}

	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		return new SparqlRunQueryStep(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	public StepDataInterface getStepData() {
		return new SparqlRunQueryStepData();
	}

	@Override
	public String getDialogClassName() {
		return SparqlRunQueryStepDialog.class.getName();
	}

	@Override
	public void loadXML(Node stepDomNode, List<DatabaseMeta> databases, Map<String, Counter> sequenceCounters)
			throws KettleXMLException {

		queryTextContentFieldName = XMLHandler.getTagValue(stepDomNode, Field.QUERY_CONTENT.name());
		endpointUrl = XMLHandler.getTagValue(stepDomNode, Field.ENDPOINT_URL.name());
		username = XMLHandler.getTagValue(stepDomNode, Field.USERNAME.name());
		password = XMLHandler.getTagValue(stepDomNode, Field.PASSWORD.name());
		resultCodeFieldName = XMLHandler.getTagValue(stepDomNode, Field.OUT_CODE.name());
		resultMessageFieldName = XMLHandler.getTagValue(stepDomNode, Field.OUT_MESSAGE.name());
	}

	@Override
	public String getXML() throws KettleException {
		StringBuilder xml = new StringBuilder();
		xml.append(XMLHandler.addTagValue(Field.QUERY_CONTENT.name(), queryTextContentFieldName));
		xml.append(XMLHandler.addTagValue(Field.ENDPOINT_URL.name(), endpointUrl));
		xml.append(XMLHandler.addTagValue(Field.USERNAME.name(), username));
		xml.append(XMLHandler.addTagValue(Field.PASSWORD.name(), password));
		xml.append(XMLHandler.addTagValue(Field.OUT_CODE.name(), resultCodeFieldName));
		xml.append(XMLHandler.addTagValue(Field.OUT_MESSAGE.name(), resultMessageFieldName));
		return xml.toString();
	}

	@Override
	public void readRep(Repository repository, ObjectId stepIdInRepository, List<DatabaseMeta> databases,
			Map<String, Counter> sequenceCounters) throws KettleException {

		// rdfContentFieldName =
		// repository.getStepAttributeString(stepIdInRepository,
		// Field.RDF_CONTENT.name());
		queryTextContentFieldName = repository.getStepAttributeString(stepIdInRepository, Field.QUERY_CONTENT.name());
		endpointUrl = repository.getStepAttributeString(stepIdInRepository, Field.ENDPOINT_URL.name());
		username = repository.getStepAttributeString(stepIdInRepository, Field.USERNAME.name());
		password = repository.getStepAttributeString(stepIdInRepository, Field.PASSWORD.name());
		resultCodeFieldName = repository.getStepAttributeString(stepIdInRepository, Field.OUT_CODE.name());
		resultMessageFieldName = repository.getStepAttributeString(stepIdInRepository, Field.OUT_MESSAGE.name());
	}

	@Override
	public void saveRep(Repository repository, ObjectId idOfTransformation, ObjectId idOfStep) throws KettleException {

		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.QUERY_CONTENT.name(),
				queryTextContentFieldName);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.ENDPOINT_URL.name(), endpointUrl);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.USERNAME.name(), username);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.PASSWORD.name(), password);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUT_CODE.name(), resultCodeFieldName);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUT_MESSAGE.name(), resultMessageFieldName);
	}

	public void setDefault() {

		queryTextContentFieldName = "QueryText";

		endpointUrl = "http://example.com:80/sparql-auth";
		username = "username";
		password = "";

		resultCodeFieldName = "status_code";
		resultMessageFieldName = "status_message";
	}

	public ValueMetaInterface getValueMeta(String name, Field field) {
		if (field == Field.PORT)
			return new ValueMetaInteger(name);
		if (field == Field.OUT_CODE)
			return new ValueMetaInteger(name);
		else
			return new ValueMetaString(name);
	}

	/**
	 * it describes what each output row is going to look like
	 */
	@Override
	public void getFields(RowMetaInterface inputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep,
			VariableSpace space) throws KettleStepException {

		ValueMetaInterface field = null;

		field = getValueMeta(resultCodeFieldName, Field.OUT_CODE);
		field.setOrigin(name);
		inputRowMeta.addValueMeta(field);

		field = getValueMeta(resultMessageFieldName, Field.OUT_MESSAGE);
		field.setOrigin(name);
		inputRowMeta.addValueMeta(field);

		ValueMetaInterface inUriField = inputRowMeta.searchValueMeta(this.queryTextContentFieldName);

		inputRowMeta.addValueMeta(inUriField);
	}

	// Getters & Setters
	public String getQueryTextContentFieldName() {
		return queryTextContentFieldName;
	}

	public void setQueryTextContentFieldName(String value) {
		this.queryTextContentFieldName = value;
	}

	public String getEndpointUrl() {
		return endpointUrl;
	}

	public void setEndpointUrl(String endpointUrl) {
		this.endpointUrl = endpointUrl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getResultCodeFieldName() {
		return resultCodeFieldName;
	}

	public void setResultCodeFieldName(String resultCodeFieldName) {
		this.resultCodeFieldName = resultCodeFieldName;
	}

	public String getResultMessageFieldName() {
		return resultMessageFieldName;
	}

	public void setResultMessageFieldName(String resultMessageFieldName) {
		this.resultMessageFieldName = resultMessageFieldName;
	}
}
