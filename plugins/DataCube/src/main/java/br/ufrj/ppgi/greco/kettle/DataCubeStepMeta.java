package br.ufrj.ppgi.greco.kettle;

/*
 * DATACUBE PLUGIN POR GABRIEL MARQUES
 * O C�DIGO EST� MUITO MAL OTIMIZADO
 * SINTA-SE AVONTADE PARA MELHOR�-LO
 * EXISTEM MUITOS OBJETOS REDUNDANTES E MUITA MEM�RIA GASTA ATOA
 * FUNCIONA 100% E AT� ENT�O N�O TIVE ERROS
 */

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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import br.ufrj.ppgi.greco.kettle.plugin.tools.datatable.DataTable;
import br.ufrj.ppgi.greco.kettle.plugin.tools.datatable.DataTableConverter;

public class DataCubeStepMeta extends BaseStepMeta implements StepMetaInterface {

	public enum Field {
		// Aba 'Dimensoes'
		DIMENSION_TABLE, DIMENSION_TABLE_NAME, DIMENSION_TABLE_URI, DIMENSION_TABLE_LABEL, DIMENSION_TABLE_TYPE,
		
		// Aba 'Vocabulário'
		VOCABULARY_TABLE, VOCABULARY_TABLE_PREFIX, VOCABULARY_TABLE_URI, 
		
		// Aba 'Campos de saida'
		DATACUBE_OUTPUT_FIELD_NAME, KEEP_INPUT_FIELDS
	}

	private DataTable<String> dimensionTable;
	private DataTable<String> vocabularyTable;
	private String dataCubeOutputFieldName;
	private boolean keepInputFields;

	public DataCubeStepMeta() {
		setDefault();
	}

	@Override
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev,
			String[] input, String[] output, RowMetaInterface info) {
		CheckResultInterface ok = new CheckResult(CheckResult.TYPE_RESULT_OK, "", stepMeta);
		remarks.add(ok);
	}

	@Override
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		return new DataCubeStep(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public StepDataInterface getStepData() {
		return new DataCubeStepData();
	}

	@Override
	public String getDialogClassName() {
		return DataCubeStepDialog.class.getName();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadXML(Node stepDomNode, List<DatabaseMeta> databases, Map<String, Counter> sequenceCounters)
			throws KettleXMLException {
		try {
			XStream xs = new XStream(new DomDriver());
			xs.alias("DataTable", DataTable.class);
			xs.registerConverter(new DataTableConverter());
			dimensionTable = (DataTable<String>) xs.fromXML(XMLHandler.getTagValue(stepDomNode, Field.DIMENSION_TABLE.name()));
			vocabularyTable = (DataTable<String>) xs.fromXML(XMLHandler.getTagValue(stepDomNode, Field.VOCABULARY_TABLE.name()));
			dataCubeOutputFieldName = (String) XMLHandler.getTagValue(stepDomNode,
					Field.DATACUBE_OUTPUT_FIELD_NAME.name());
			keepInputFields = Boolean.valueOf(XMLHandler.getTagValue(stepDomNode, Field.KEEP_INPUT_FIELDS.name()));
		} catch (Exception e) {
			throw new KettleXMLException("Data Cube unable to read step info from XML node", e);
		}	

	}

	// TODO Gerar XML para salvar um .ktr
	@Override
	public String getXML() throws KettleException {
		XStream xs = new XStream(new DomDriver());
		xs.alias("DataTable", DataTable.class);
		xs.registerConverter(new DataTableConverter());
		StringBuilder xml = new StringBuilder();
		xml.append(XMLHandler.addTagValue(Field.DIMENSION_TABLE.name(), xs.toXML(this.dimensionTable)));
		xml.append(XMLHandler.addTagValue(Field.VOCABULARY_TABLE.name(), xs.toXML(this.vocabularyTable)));
		xml.append(XMLHandler.addTagValue(Field.DATACUBE_OUTPUT_FIELD_NAME.name(), dataCubeOutputFieldName));
		xml.append(XMLHandler.addTagValue(Field.KEEP_INPUT_FIELDS.name(), keepInputFields));
		return xml.toString();
	}

	// TODO Carregar campos a partir do repositorio
	@Override
	public void readRep(Repository repository, ObjectId stepIdInRepository, List<DatabaseMeta> databases,
			Map<String, Counter> sequenceCounters) throws KettleException {
	

	}

	// TODO Persistir campos no repositorio
	@Override
	public void saveRep(Repository repository, ObjectId idOfTransformation, ObjectId idOfStep) throws KettleException {

	}

	@Override
	public void setDefault() {

		dimensionTable = new DataTable<String>(Field.DIMENSION_TABLE.name(), Field.DIMENSION_TABLE_NAME.name(),
				Field.DIMENSION_TABLE_URI.name(), Field.DIMENSION_TABLE_LABEL.name(), Field.DIMENSION_TABLE_TYPE.name());
		vocabularyTable = new DataTable<String>(Field.VOCABULARY_TABLE.name(), Field.VOCABULARY_TABLE_PREFIX.name(),
				Field.VOCABULARY_TABLE_URI.name());
		keepInputFields = false;
		dataCubeOutputFieldName = "datacube";

	}

	@Override
	public void getFields(RowMetaInterface inputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep,
			VariableSpace space) throws KettleStepException {
		if (!isKeepInputFields()) {
			inputRowMeta.clear();
		}

		addValueMetaString(inputRowMeta, getDataCubeOutputFieldName(), name);
	}

	private void addValueMetaString(RowMetaInterface inputRowMeta, String fieldName, String origin) {
		ValueMetaInterface field = new ValueMetaString(fieldName);
		field.setOrigin(origin);
		inputRowMeta.addValueMeta(field);
	}

	public DataTable<String> getDimensionTable() {
		return dimensionTable;
	}

	public void setDimensionTable(DataTable<String> dimensionTable) {
		this.dimensionTable = dimensionTable;
	}

	public DataTable<String> getVocabularyTable() {
		return vocabularyTable;
	}

	public void setVocabularyTable(DataTable<String> vocabularyTable) {
		this.vocabularyTable = vocabularyTable;
	}

	public String getDataCubeOutputFieldName() {
		return dataCubeOutputFieldName;
	}

	public void setDataCubeOutputFieldName(String dataCubeOutputFieldName) {
		this.dataCubeOutputFieldName = dataCubeOutputFieldName;
	}

	public boolean isKeepInputFields() {
		return keepInputFields;
	}

	public void setKeepInputFields(boolean keepInputFields) {
		this.keepInputFields = keepInputFields;
	}

}
