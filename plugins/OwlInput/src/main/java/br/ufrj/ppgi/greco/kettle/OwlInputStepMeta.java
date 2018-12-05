package br.ufrj.ppgi.greco.kettle;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Shell;
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
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.w3c.dom.Node;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import br.ufrj.ppgi.greco.kettle.plugin.tools.datatable.DataTable;
import br.ufrj.ppgi.greco.kettle.plugin.tools.datatable.DataTableConverter;

public class OwlInputStepMeta extends BaseStepMeta implements StepMetaInterface {

	public enum Field {
		// Aba 'Ontologia'
		MAP_TABLE, MAP_TABLE_ONTOLOGY_NAME, MAP_TABLE_ONTOLOGY_URI, MAP_TABLE_ONTOLOGY_DESCRIPTION,
		
		// Aba 'Selecao'
		VOCAB_TABLE, VOCAB_TABLE_PREFIX, VOCAB_TABLE_URI, VOCAB_TABLE_PROPERTY, VOCAB_TABLE_TYPE, 
		
		// Aba 'Campos de saida'
		ONTOLOGY_OUT_FIELD_NAME, URI_OUT_FIELD_NAME, KEEP_INPUT_FIELDS
	}

	private DataTable<String> mapTable;
	private DataTable<String> vocabTable;
	private String ontologyOutputFieldName;
	private String uriOutputFieldName;
	private boolean keepInputFields;
	public OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);

	public OwlInputStepMeta() {
		setDefault();
	}

	public StepDialogInterface getDialog(Shell shell, StepMetaInterface meta, TransMeta transMeta, String name) {
		return new OwlInputStepDialog(shell, meta, transMeta, name);
	}

	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr, TransMeta transMeta,
			Trans disp) {
		return new OwlInputStep(stepMeta, stepDataInterface, cnr, transMeta, disp);
	}

	public StepDataInterface getStepData() {
		return new OwlInputStepData();
	}

	public void setDefault() {
		mapTable = new DataTable<String>(Field.MAP_TABLE.name(), Field.MAP_TABLE_ONTOLOGY_NAME.name(),
				Field.MAP_TABLE_ONTOLOGY_URI.name(), Field.MAP_TABLE_ONTOLOGY_DESCRIPTION.name());
		vocabTable = new DataTable<String>(Field.VOCAB_TABLE.name(), Field.VOCAB_TABLE_PREFIX.name(),
				Field.VOCAB_TABLE_URI.name(), Field.VOCAB_TABLE_PROPERTY.name(), 
				Field.VOCAB_TABLE_TYPE.name());
		ontologyOutputFieldName = "prefix";
		uriOutputFieldName = "uri";
		keepInputFields = false;
	}

	@Override
	public String getXML() throws KettleException {
		XStream xs = new XStream(new DomDriver());
		xs.alias("DataTable", DataTable.class);
		xs.registerConverter(new DataTableConverter());
		StringBuilder xml = new StringBuilder();
		xml.append(XMLHandler.addTagValue(Field.MAP_TABLE.name(), xs.toXML(mapTable)));
		xml.append(XMLHandler.addTagValue(Field.VOCAB_TABLE.name(), xs.toXML(vocabTable)));
		xml.append(XMLHandler.addTagValue(Field.ONTOLOGY_OUT_FIELD_NAME.name(), ontologyOutputFieldName));
		xml.append(XMLHandler.addTagValue(Field.URI_OUT_FIELD_NAME.name(), uriOutputFieldName));
		xml.append(XMLHandler.addTagValue(Field.KEEP_INPUT_FIELDS.name(), keepInputFields));
		return xml.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadXML(Node stepDomNode, List<DatabaseMeta> databases, Map<String, Counter> counters)
			throws KettleXMLException {
		try {
			XStream xs = new XStream(new DomDriver());
			xs.alias("DataTable", DataTable.class);
			xs.registerConverter(new DataTableConverter());
			mapTable = (DataTable<String>) xs.fromXML(XMLHandler.getTagValue(stepDomNode, Field.MAP_TABLE.name()));
			vocabTable = (DataTable<String>) xs.fromXML(XMLHandler.getTagValue(stepDomNode, Field.VOCAB_TABLE.name()));
			ontologyOutputFieldName = (String) XMLHandler.getTagValue(stepDomNode,
					Field.ONTOLOGY_OUT_FIELD_NAME.name());
			uriOutputFieldName = (String) XMLHandler.getTagValue(stepDomNode, Field.URI_OUT_FIELD_NAME.name());
			keepInputFields = "Y".equals(XMLHandler.getTagValue(stepDomNode, Field.KEEP_INPUT_FIELDS.name()));
		} catch (Exception e) {
			throw new KettleXMLException("Owl Input unable to read step info from XML node", e);
		}

	}

	public void saveRep(Repository rep, ObjectId idOfTransformation, ObjectId idOfStep) throws KettleException {
		try {
			repository.saveStepAttribute(idOfTransformation, idOfStep, Field.MAP_TABLE.name(),
					new XStream(new DomDriver()).toXML(mapTable));
			repository.saveStepAttribute(idOfTransformation, idOfStep, Field.VOCAB_TABLE.name(),
					new XStream(new DomDriver()).toXML(vocabTable));
			repository.saveStepAttribute(idOfTransformation, idOfStep, Field.ONTOLOGY_OUT_FIELD_NAME.name(),
					ontologyOutputFieldName);
			repository.saveStepAttribute(idOfTransformation, idOfStep, Field.URI_OUT_FIELD_NAME.name(),
					uriOutputFieldName);
			repository.saveStepAttribute(idOfTransformation, idOfStep, Field.KEEP_INPUT_FIELDS.name(), keepInputFields);
		} catch (Exception e) {
			throw new KettleException("Unable to save step into repository: " + idOfStep, e);
		}
	}

	@SuppressWarnings("unchecked")
	public void readRep(Repository rep, ObjectId id_step, List<DatabaseMeta> databases, Map<String, Counter> counters)
			throws KettleException {
		try {
			mapTable = (DataTable<String>) new XStream(new DomDriver())
					.fromXML(repository.getStepAttributeString(id_step, Field.MAP_TABLE.name()));
			vocabTable = (DataTable<String>) new XStream(new DomDriver())
					.fromXML(repository.getStepAttributeString(id_step, Field.VOCAB_TABLE.name()));
			ontologyOutputFieldName = repository.getStepAttributeString(id_step, Field.ONTOLOGY_OUT_FIELD_NAME.name());
			uriOutputFieldName = repository.getStepAttributeString(id_step, Field.URI_OUT_FIELD_NAME.name());
			keepInputFields = repository.getStepAttributeBoolean(id_step, Field.KEEP_INPUT_FIELDS.name());
		} catch (Exception e) {
			throw new KettleException("Unable to load step from repository", e);
		}
	}

	public void getFields(RowMetaInterface row, String origin, RowMetaInterface[] info, StepMeta nextStep,
			VariableSpace space) throws KettleStepException {

		if (!keepInputFields) {
			row.clear();
		}

		ValueMetaInterface ontologyName = new ValueMetaString(this.ontologyOutputFieldName);
		ontologyName.setOrigin(origin);
		ontologyName.setLength(30);
		row.addValueMeta(ontologyName);

		ValueMetaInterface uriName = new ValueMetaString(this.uriOutputFieldName);
		uriName.setOrigin(origin);
		uriName.setLength(190);
		row.addValueMeta(uriName);
		
	}

	public void check(List<CheckResultInterface> remarks, TransMeta transmeta, StepMeta stepMeta, RowMetaInterface prev,
			String input[], String output[], RowMetaInterface info) {
		CheckResult cr;

		if (input.length > 0) {
			cr = new CheckResult(CheckResult.TYPE_RESULT_OK, "Step is receiving input from other steps.", stepMeta);
			remarks.add(cr);
		} else {
			cr = new CheckResult(CheckResult.TYPE_RESULT_ERROR, "No input received from other steps.", stepMeta);
			remarks.add(cr);
		}

	}

	public String getOntologyOutputFieldName() {
		return ontologyOutputFieldName;
	}

	public void setOntologyOutputFieldName(String ontologyOutput) {
		this.ontologyOutputFieldName = ontologyOutput;
	}

	public String getUriOutputFieldName() {
		return uriOutputFieldName;
	}

	public void setUriOutputFieldName(String uriOutput) {
		this.uriOutputFieldName = uriOutput;
	}

	public boolean isKeepInputFields() {
		return keepInputFields;
	}

	public void setKeepInputFields(boolean keepInputFields) {
		this.keepInputFields = keepInputFields;
	}

	public DataTable<String> getMapTable() {
		return mapTable;
	}

	public void setMapTable(DataTable<String> mapTable) {
		this.mapTable = mapTable;
	}

	public DataTable<String> getVocabTable() {
		return vocabTable;
	}

	public void setVocabTable(DataTable<String> vocabTable) {
		this.vocabTable = vocabTable;
	}

}
