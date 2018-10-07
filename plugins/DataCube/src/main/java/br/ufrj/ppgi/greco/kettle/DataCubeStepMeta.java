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

public class DataCubeStepMeta extends BaseStepMeta implements StepMetaInterface {

	// TODO Fields for serialization
	public enum Field {
		INPUT_DIMENSAO1_FIELD_NAME, INPUT_DIMENSAO2_FIELD_NAME, INPUT_DIMENSAO3_FIELD_NAME, INPUT_DIMENSAO4_FIELD_NAME, INPUT_DIMENSAO5_FIELD_NAME, INPUT_DIMENSAO6_FIELD_NAME, INPUT_DIMENSAO7_FIELD_NAME, INPUT_DIMENSAO8_FIELD_NAME, INPUT_DIMENSAO9_FIELD_NAME, INPUT_DIMENSAO10_FIELD_NAME, INNER_KEEP_INPUT_VALUE, OUTPUT_SAIDA_FIELD_NAME, OUTPUT_TESTE_FIELD_NAME, OUTPUT_CABECALHO1_FIELD_NAME, OUTPUT_CABECALHO2_FIELD_NAME, OUTPUT_CABECALHO3_FIELD_NAME, OUTPUT_CABECALHO4_FIELD_NAME, OUTPUT_CABECALHO5_FIELD_NAME, OUTPUT_CABECALHO6_FIELD_NAME, OUTPUT_CABECALHO7_FIELD_NAME, OUTPUT_CABECALHO8_FIELD_NAME, OUTPUT_CABECALHO9_FIELD_NAME, OUTPUT_CABECALHO10_FIELD_NAME, OUTPUT_CABECALHO11_FIELD_NAME,

		OUTPUT_STRUCTUREDEFINITION1_FIELD_NAME, OUTPUT_STRUCTUREDEFINITION2_FIELD_NAME, OUTPUT_STRUCTUREDEFINITION3_FIELD_NAME, OUTPUT_STRUCTUREDEFINITION4_FIELD_NAME, OUTPUT_STRUCTUREDEFINITION5_FIELD_NAME, OUTPUT_STRUCTUREDEFINITION6_FIELD_NAME, OUTPUT_STRUCTUREDEFINITION7_FIELD_NAME, OUTPUT_STRUCTUREDEFINITION8_FIELD_NAME, OUTPUT_STRUCTUREDEFINITION9_FIELD_NAME, OUTPUT_STRUCTUREDEFINITION10_FIELD_NAME,

		OUTPUT_LABEL1_FIELD_NAME, OUTPUT_LABEL2_FIELD_NAME, OUTPUT_LABEL3_FIELD_NAME, OUTPUT_LABEL4_FIELD_NAME, OUTPUT_LABEL5_FIELD_NAME, OUTPUT_LABEL6_FIELD_NAME, OUTPUT_LABEL7_FIELD_NAME, OUTPUT_LABEL8_FIELD_NAME, OUTPUT_LABEL9_FIELD_NAME, OUTPUT_LABEL10_FIELD_NAME,
	}

	// TODO Campos do step

	private String inputDimensao1;
	private String inputDimensao2;
	private String inputDimensao3;
	private String inputDimensao4;
	private String inputDimensao5;
	private String inputDimensao6;
	private String inputDimensao7;
	private String inputDimensao8;
	private String inputDimensao9;
	private String inputDimensao10;
	private String cabecalho1;
	private String cabecalho2;
	private String cabecalho3;
	private String cabecalho4;
	private String cabecalho5;
	private String cabecalho6;
	private String cabecalho7;
	private String cabecalho8;
	private String cabecalho9;
	private String cabecalho10;
	private String cabecalho11;
	private String structureDefinition1;
	private String structureDefinition2;
	private String structureDefinition3;
	private String structureDefinition4;
	private String structureDefinition5;
	private String structureDefinition6;
	private String structureDefinition7;
	private String structureDefinition8;
	private String structureDefinition9;
	private String structureDefinition10;
	private String label1;
	private String label2;
	private String label3;
	private String label4;
	private String label5;
	private String label6;
	private String label7;
	private String label8;
	private String label9;
	private String label10;

	private String outputSaida;
	private String outputTeste;
	private Boolean innerKeepInputFields;

	public DataCubeStepMeta() {
		setDefault();
	}

	// TODO Validar todos os campos para dar feedback ao usu�rio! Argh!
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

	// TODO Carregar campos a partir do XML de um .ktr
	@Override
	public void loadXML(Node stepDomNode, List<DatabaseMeta> databases, Map<String, Counter> sequenceCounters)
			throws KettleXMLException {

		inputDimensao1 = XMLHandler.getTagValue(stepDomNode, Field.INPUT_DIMENSAO1_FIELD_NAME.name());
		inputDimensao2 = XMLHandler.getTagValue(stepDomNode, Field.INPUT_DIMENSAO2_FIELD_NAME.name());
		inputDimensao3 = XMLHandler.getTagValue(stepDomNode, Field.INPUT_DIMENSAO3_FIELD_NAME.name());
		inputDimensao4 = XMLHandler.getTagValue(stepDomNode, Field.INPUT_DIMENSAO4_FIELD_NAME.name());
		inputDimensao5 = XMLHandler.getTagValue(stepDomNode, Field.INPUT_DIMENSAO5_FIELD_NAME.name());
		inputDimensao6 = XMLHandler.getTagValue(stepDomNode, Field.INPUT_DIMENSAO6_FIELD_NAME.name());
		inputDimensao7 = XMLHandler.getTagValue(stepDomNode, Field.INPUT_DIMENSAO7_FIELD_NAME.name());
		inputDimensao8 = XMLHandler.getTagValue(stepDomNode, Field.INPUT_DIMENSAO8_FIELD_NAME.name());
		inputDimensao9 = XMLHandler.getTagValue(stepDomNode, Field.INPUT_DIMENSAO9_FIELD_NAME.name());
		inputDimensao10 = XMLHandler.getTagValue(stepDomNode, Field.INPUT_DIMENSAO10_FIELD_NAME.name());
		innerKeepInputFields = "Y".equals(XMLHandler.getTagValue(stepDomNode, Field.INNER_KEEP_INPUT_VALUE.name()));

		outputSaida = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_SAIDA_FIELD_NAME.name());
		outputTeste = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_TESTE_FIELD_NAME.name());
		cabecalho1 = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_CABECALHO1_FIELD_NAME.name());
		cabecalho2 = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_CABECALHO2_FIELD_NAME.name());
		cabecalho3 = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_CABECALHO3_FIELD_NAME.name());
		cabecalho4 = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_CABECALHO4_FIELD_NAME.name());
		cabecalho5 = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_CABECALHO5_FIELD_NAME.name());
		cabecalho6 = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_CABECALHO6_FIELD_NAME.name());
		cabecalho7 = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_CABECALHO7_FIELD_NAME.name());
		cabecalho8 = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_CABECALHO8_FIELD_NAME.name());
		cabecalho9 = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_CABECALHO9_FIELD_NAME.name());
		cabecalho10 = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_CABECALHO10_FIELD_NAME.name());
		cabecalho11 = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_CABECALHO11_FIELD_NAME.name());

		structureDefinition1 = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_STRUCTUREDEFINITION1_FIELD_NAME.name());
		structureDefinition2 = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_STRUCTUREDEFINITION2_FIELD_NAME.name());
		structureDefinition3 = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_STRUCTUREDEFINITION3_FIELD_NAME.name());
		structureDefinition4 = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_STRUCTUREDEFINITION4_FIELD_NAME.name());
		structureDefinition5 = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_STRUCTUREDEFINITION5_FIELD_NAME.name());
		structureDefinition6 = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_STRUCTUREDEFINITION6_FIELD_NAME.name());
		structureDefinition7 = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_STRUCTUREDEFINITION7_FIELD_NAME.name());
		structureDefinition8 = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_STRUCTUREDEFINITION8_FIELD_NAME.name());
		structureDefinition9 = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_STRUCTUREDEFINITION9_FIELD_NAME.name());
		structureDefinition10 = XMLHandler.getTagValue(stepDomNode,
				Field.OUTPUT_STRUCTUREDEFINITION10_FIELD_NAME.name());

		label1 = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_LABEL1_FIELD_NAME.name());
		label2 = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_LABEL2_FIELD_NAME.name());
		label3 = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_LABEL3_FIELD_NAME.name());
		label4 = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_LABEL4_FIELD_NAME.name());
		label5 = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_LABEL5_FIELD_NAME.name());
		label6 = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_LABEL6_FIELD_NAME.name());
		label7 = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_LABEL7_FIELD_NAME.name());
		label8 = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_LABEL8_FIELD_NAME.name());
		label9 = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_LABEL9_FIELD_NAME.name());
		label10 = XMLHandler.getTagValue(stepDomNode, Field.OUTPUT_LABEL10_FIELD_NAME.name());

	}

	// TODO Gerar XML para salvar um .ktr
	@Override
	public String getXML() throws KettleException {
		StringBuilder xml = new StringBuilder();

		xml.append(XMLHandler.addTagValue(Field.INPUT_DIMENSAO1_FIELD_NAME.name(), inputDimensao1));
		xml.append(XMLHandler.addTagValue(Field.INPUT_DIMENSAO2_FIELD_NAME.name(), inputDimensao2));
		xml.append(XMLHandler.addTagValue(Field.INPUT_DIMENSAO3_FIELD_NAME.name(), inputDimensao3));
		xml.append(XMLHandler.addTagValue(Field.INPUT_DIMENSAO4_FIELD_NAME.name(), inputDimensao4));
		xml.append(XMLHandler.addTagValue(Field.INPUT_DIMENSAO5_FIELD_NAME.name(), inputDimensao5));
		xml.append(XMLHandler.addTagValue(Field.INPUT_DIMENSAO6_FIELD_NAME.name(), inputDimensao6));
		xml.append(XMLHandler.addTagValue(Field.INPUT_DIMENSAO7_FIELD_NAME.name(), inputDimensao7));
		xml.append(XMLHandler.addTagValue(Field.INPUT_DIMENSAO8_FIELD_NAME.name(), inputDimensao8));
		xml.append(XMLHandler.addTagValue(Field.INPUT_DIMENSAO9_FIELD_NAME.name(), inputDimensao9));
		xml.append(XMLHandler.addTagValue(Field.INPUT_DIMENSAO10_FIELD_NAME.name(), inputDimensao10));
		xml.append(XMLHandler.addTagValue(Field.INNER_KEEP_INPUT_VALUE.name(), innerKeepInputFields));

		xml.append(XMLHandler.addTagValue(Field.OUTPUT_SAIDA_FIELD_NAME.name(), outputSaida));

		xml.append(XMLHandler.addTagValue(Field.OUTPUT_CABECALHO1_FIELD_NAME.name(), cabecalho1));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_CABECALHO2_FIELD_NAME.name(), cabecalho2));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_CABECALHO3_FIELD_NAME.name(), cabecalho3));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_CABECALHO4_FIELD_NAME.name(), cabecalho4));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_CABECALHO5_FIELD_NAME.name(), cabecalho5));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_CABECALHO6_FIELD_NAME.name(), cabecalho6));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_CABECALHO7_FIELD_NAME.name(), cabecalho7));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_CABECALHO8_FIELD_NAME.name(), cabecalho8));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_CABECALHO9_FIELD_NAME.name(), cabecalho9));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_CABECALHO10_FIELD_NAME.name(), cabecalho10));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_CABECALHO11_FIELD_NAME.name(), cabecalho11));

		xml.append(XMLHandler.addTagValue(Field.OUTPUT_STRUCTUREDEFINITION1_FIELD_NAME.name(), structureDefinition1));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_STRUCTUREDEFINITION2_FIELD_NAME.name(), structureDefinition2));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_STRUCTUREDEFINITION3_FIELD_NAME.name(), structureDefinition3));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_STRUCTUREDEFINITION4_FIELD_NAME.name(), structureDefinition4));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_STRUCTUREDEFINITION5_FIELD_NAME.name(), structureDefinition5));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_STRUCTUREDEFINITION6_FIELD_NAME.name(), structureDefinition6));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_STRUCTUREDEFINITION7_FIELD_NAME.name(), structureDefinition7));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_STRUCTUREDEFINITION8_FIELD_NAME.name(), structureDefinition8));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_STRUCTUREDEFINITION9_FIELD_NAME.name(), structureDefinition9));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_STRUCTUREDEFINITION10_FIELD_NAME.name(), structureDefinition10));

		xml.append(XMLHandler.addTagValue(Field.OUTPUT_LABEL1_FIELD_NAME.name(), label1));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_LABEL2_FIELD_NAME.name(), label2));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_LABEL3_FIELD_NAME.name(), label3));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_LABEL4_FIELD_NAME.name(), label4));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_LABEL5_FIELD_NAME.name(), label5));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_LABEL6_FIELD_NAME.name(), label6));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_LABEL7_FIELD_NAME.name(), label7));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_LABEL8_FIELD_NAME.name(), label8));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_LABEL9_FIELD_NAME.name(), label9));
		xml.append(XMLHandler.addTagValue(Field.OUTPUT_LABEL10_FIELD_NAME.name(), label10));

		xml.append(XMLHandler.addTagValue(Field.OUTPUT_TESTE_FIELD_NAME.name(), outputTeste));

		return xml.toString();
	}

	// TODO Carregar campos a partir do repositorio
	@Override
	public void readRep(Repository repository, ObjectId stepIdInRepository, List<DatabaseMeta> databases,
			Map<String, Counter> sequenceCounters) throws KettleException {

		inputDimensao1 = repository.getStepAttributeString(stepIdInRepository, Field.INPUT_DIMENSAO1_FIELD_NAME.name());
		inputDimensao2 = repository.getStepAttributeString(stepIdInRepository, Field.INPUT_DIMENSAO2_FIELD_NAME.name());
		inputDimensao3 = repository.getStepAttributeString(stepIdInRepository, Field.INPUT_DIMENSAO3_FIELD_NAME.name());
		inputDimensao4 = repository.getStepAttributeString(stepIdInRepository, Field.INPUT_DIMENSAO4_FIELD_NAME.name());
		inputDimensao5 = repository.getStepAttributeString(stepIdInRepository, Field.INPUT_DIMENSAO5_FIELD_NAME.name());
		inputDimensao6 = repository.getStepAttributeString(stepIdInRepository, Field.INPUT_DIMENSAO6_FIELD_NAME.name());
		inputDimensao7 = repository.getStepAttributeString(stepIdInRepository, Field.INPUT_DIMENSAO7_FIELD_NAME.name());
		inputDimensao8 = repository.getStepAttributeString(stepIdInRepository, Field.INPUT_DIMENSAO8_FIELD_NAME.name());
		inputDimensao9 = repository.getStepAttributeString(stepIdInRepository, Field.INPUT_DIMENSAO9_FIELD_NAME.name());
		inputDimensao10 = repository.getStepAttributeString(stepIdInRepository,
				Field.INPUT_DIMENSAO10_FIELD_NAME.name());
		innerKeepInputFields = repository.getStepAttributeBoolean(stepIdInRepository,
				Field.INNER_KEEP_INPUT_VALUE.name());

		outputSaida = repository.getStepAttributeString(stepIdInRepository, Field.OUTPUT_SAIDA_FIELD_NAME.name());

		cabecalho1 = repository.getStepAttributeString(stepIdInRepository, Field.OUTPUT_CABECALHO1_FIELD_NAME.name());
		cabecalho2 = repository.getStepAttributeString(stepIdInRepository, Field.OUTPUT_CABECALHO2_FIELD_NAME.name());
		cabecalho3 = repository.getStepAttributeString(stepIdInRepository, Field.OUTPUT_CABECALHO3_FIELD_NAME.name());
		cabecalho4 = repository.getStepAttributeString(stepIdInRepository, Field.OUTPUT_CABECALHO4_FIELD_NAME.name());
		cabecalho5 = repository.getStepAttributeString(stepIdInRepository, Field.OUTPUT_CABECALHO5_FIELD_NAME.name());
		cabecalho6 = repository.getStepAttributeString(stepIdInRepository, Field.OUTPUT_CABECALHO6_FIELD_NAME.name());
		cabecalho7 = repository.getStepAttributeString(stepIdInRepository, Field.OUTPUT_CABECALHO7_FIELD_NAME.name());
		cabecalho8 = repository.getStepAttributeString(stepIdInRepository, Field.OUTPUT_CABECALHO8_FIELD_NAME.name());
		cabecalho9 = repository.getStepAttributeString(stepIdInRepository, Field.OUTPUT_CABECALHO9_FIELD_NAME.name());
		cabecalho10 = repository.getStepAttributeString(stepIdInRepository, Field.OUTPUT_CABECALHO10_FIELD_NAME.name());
		cabecalho11 = repository.getStepAttributeString(stepIdInRepository, Field.OUTPUT_CABECALHO11_FIELD_NAME.name());

		structureDefinition1 = repository.getStepAttributeString(stepIdInRepository,
				Field.OUTPUT_STRUCTUREDEFINITION1_FIELD_NAME.name());
		structureDefinition2 = repository.getStepAttributeString(stepIdInRepository,
				Field.OUTPUT_STRUCTUREDEFINITION2_FIELD_NAME.name());
		structureDefinition3 = repository.getStepAttributeString(stepIdInRepository,
				Field.OUTPUT_STRUCTUREDEFINITION3_FIELD_NAME.name());
		structureDefinition4 = repository.getStepAttributeString(stepIdInRepository,
				Field.OUTPUT_STRUCTUREDEFINITION4_FIELD_NAME.name());
		structureDefinition5 = repository.getStepAttributeString(stepIdInRepository,
				Field.OUTPUT_STRUCTUREDEFINITION5_FIELD_NAME.name());
		structureDefinition6 = repository.getStepAttributeString(stepIdInRepository,
				Field.OUTPUT_STRUCTUREDEFINITION6_FIELD_NAME.name());
		structureDefinition7 = repository.getStepAttributeString(stepIdInRepository,
				Field.OUTPUT_STRUCTUREDEFINITION7_FIELD_NAME.name());
		structureDefinition8 = repository.getStepAttributeString(stepIdInRepository,
				Field.OUTPUT_STRUCTUREDEFINITION8_FIELD_NAME.name());
		structureDefinition9 = repository.getStepAttributeString(stepIdInRepository,
				Field.OUTPUT_STRUCTUREDEFINITION9_FIELD_NAME.name());
		structureDefinition10 = repository.getStepAttributeString(stepIdInRepository,
				Field.OUTPUT_STRUCTUREDEFINITION10_FIELD_NAME.name());

		label1 = repository.getStepAttributeString(stepIdInRepository, Field.OUTPUT_LABEL1_FIELD_NAME.name());
		label2 = repository.getStepAttributeString(stepIdInRepository, Field.OUTPUT_LABEL2_FIELD_NAME.name());
		label3 = repository.getStepAttributeString(stepIdInRepository, Field.OUTPUT_LABEL3_FIELD_NAME.name());
		label4 = repository.getStepAttributeString(stepIdInRepository, Field.OUTPUT_LABEL4_FIELD_NAME.name());
		label5 = repository.getStepAttributeString(stepIdInRepository, Field.OUTPUT_LABEL5_FIELD_NAME.name());
		label6 = repository.getStepAttributeString(stepIdInRepository, Field.OUTPUT_LABEL6_FIELD_NAME.name());
		label7 = repository.getStepAttributeString(stepIdInRepository, Field.OUTPUT_LABEL7_FIELD_NAME.name());
		label8 = repository.getStepAttributeString(stepIdInRepository, Field.OUTPUT_LABEL8_FIELD_NAME.name());
		label9 = repository.getStepAttributeString(stepIdInRepository, Field.OUTPUT_LABEL9_FIELD_NAME.name());
		label10 = repository.getStepAttributeString(stepIdInRepository, Field.OUTPUT_LABEL10_FIELD_NAME.name());

		outputTeste = repository.getStepAttributeString(stepIdInRepository, Field.OUTPUT_TESTE_FIELD_NAME.name());

	}

	// TODO Persistir campos no repositorio
	@Override
	public void saveRep(Repository repository, ObjectId idOfTransformation, ObjectId idOfStep) throws KettleException {

		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.INPUT_DIMENSAO1_FIELD_NAME.name(),
				inputDimensao1);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.INPUT_DIMENSAO2_FIELD_NAME.name(),
				inputDimensao2);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.INPUT_DIMENSAO3_FIELD_NAME.name(),
				inputDimensao3);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.INPUT_DIMENSAO4_FIELD_NAME.name(),
				inputDimensao4);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.INPUT_DIMENSAO5_FIELD_NAME.name(),
				inputDimensao5);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.INPUT_DIMENSAO6_FIELD_NAME.name(),
				inputDimensao6);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.INPUT_DIMENSAO7_FIELD_NAME.name(),
				inputDimensao7);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.INPUT_DIMENSAO8_FIELD_NAME.name(),
				inputDimensao8);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.INPUT_DIMENSAO9_FIELD_NAME.name(),
				inputDimensao9);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.INPUT_DIMENSAO10_FIELD_NAME.name(),
				inputDimensao10);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.INNER_KEEP_INPUT_VALUE.name(),
				innerKeepInputFields);

		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_SAIDA_FIELD_NAME.name(), outputSaida);

		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_CABECALHO1_FIELD_NAME.name(),
				cabecalho1);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_CABECALHO2_FIELD_NAME.name(),
				cabecalho2);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_CABECALHO3_FIELD_NAME.name(),
				cabecalho3);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_CABECALHO4_FIELD_NAME.name(),
				cabecalho4);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_CABECALHO5_FIELD_NAME.name(),
				cabecalho5);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_CABECALHO6_FIELD_NAME.name(),
				cabecalho6);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_CABECALHO7_FIELD_NAME.name(),
				cabecalho7);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_CABECALHO8_FIELD_NAME.name(),
				cabecalho8);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_CABECALHO9_FIELD_NAME.name(),
				cabecalho9);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_CABECALHO10_FIELD_NAME.name(),
				cabecalho10);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_CABECALHO11_FIELD_NAME.name(),
				cabecalho11);

		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_STRUCTUREDEFINITION1_FIELD_NAME.name(),
				structureDefinition1);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_STRUCTUREDEFINITION2_FIELD_NAME.name(),
				structureDefinition2);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_STRUCTUREDEFINITION3_FIELD_NAME.name(),
				structureDefinition3);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_STRUCTUREDEFINITION4_FIELD_NAME.name(),
				structureDefinition4);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_STRUCTUREDEFINITION5_FIELD_NAME.name(),
				structureDefinition5);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_STRUCTUREDEFINITION6_FIELD_NAME.name(),
				structureDefinition6);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_STRUCTUREDEFINITION7_FIELD_NAME.name(),
				structureDefinition7);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_STRUCTUREDEFINITION8_FIELD_NAME.name(),
				structureDefinition8);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_STRUCTUREDEFINITION9_FIELD_NAME.name(),
				structureDefinition9);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_STRUCTUREDEFINITION10_FIELD_NAME.name(),
				structureDefinition10);

		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_LABEL1_FIELD_NAME.name(), label1);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_LABEL2_FIELD_NAME.name(), label2);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_LABEL3_FIELD_NAME.name(), label3);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_LABEL4_FIELD_NAME.name(), label4);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_LABEL5_FIELD_NAME.name(), label5);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_LABEL6_FIELD_NAME.name(), label6);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_LABEL7_FIELD_NAME.name(), label7);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_LABEL8_FIELD_NAME.name(), label8);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_LABEL9_FIELD_NAME.name(), label9);
		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_LABEL10_FIELD_NAME.name(), label10);

		repository.saveStepAttribute(idOfTransformation, idOfStep, Field.OUTPUT_TESTE_FIELD_NAME.name(), outputTeste);

	}

	// TODO Inicializacoes default
	@Override
	public void setDefault() {
		inputDimensao1 = "";
		inputDimensao2 = "";
		inputDimensao3 = "";
		inputDimensao4 = "";
		inputDimensao5 = "";
		inputDimensao6 = "";
		inputDimensao7 = "";
		inputDimensao8 = "";
		inputDimensao9 = "";
		inputDimensao10 = "";
		cabecalho1 = "http://example.cubeviz.org/datacube/";
		cabecalho2 = "http://www.w3.org/2002/07/owl#";
		cabecalho3 = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
		cabecalho4 = "http://www.w3.org/2000/01/rdf-schema#";
		cabecalho5 = "http://purl.org/dc/elements/1.1/";
		cabecalho6 = "http://www.w3.org/2004/02/skos/core#";
		cabecalho7 = "http://purl.org/linked-data/sdmx/2009/code#";
		cabecalho8 = "http://purl.org/linked-data/sdmx/2009/dimension#";
		cabecalho9 = "http://purl.org/linked-data/cube#";
		cabecalho10 = "http://meu.exemplo/datacube/";
		cabecalho11 = "http://meu.exemplo/datacube/properties/";
		structureDefinition1 = "";
		structureDefinition2 = "";
		structureDefinition3 = "";
		structureDefinition4 = "";
		structureDefinition5 = "";
		structureDefinition6 = "";
		structureDefinition7 = "";
		structureDefinition8 = "";
		structureDefinition9 = "";
		structureDefinition10 = "";

		label1 = "";
		label2 = "";
		label3 = "";
		label4 = "";
		label5 = "";
		label6 = "";
		label7 = "";
		label8 = "";
		label9 = "";
		label10 = "";

		innerKeepInputFields = false;

		outputSaida = "";
		outputTeste = "";

	}

	/**
	 * TODO It describes what each output row is going to look like
	 */
	@Override
	public void getFields(RowMetaInterface inputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep,
			VariableSpace space) throws KettleStepException {

		if (!innerKeepInputFields) {
			inputRowMeta.clear();
		}

		addValueMetaString(inputRowMeta, outputTeste, name);

		// gabriel

	}

	private void addValueMetaString(RowMetaInterface inputRowMeta, String fieldName, String origin) {
		ValueMetaInterface field = new ValueMetaString(fieldName);
		field.setOrigin(origin);
		inputRowMeta.addValueMeta(field);
	}

	public String getInputDimensao1() {
		return inputDimensao1;
	}

	public void setInputDimensao1(String inputDimensao1) {
		this.inputDimensao1 = inputDimensao1;
	}

	public String getInputDimensao2() {
		return inputDimensao2;
	}

	public void setInputDimensao2(String inputDimensao2) {
		this.inputDimensao2 = inputDimensao2;
	}

	public String getInputDimensao3() {
		return inputDimensao3;
	}

	public void setInputDimensao3(String inputDimensao3) {
		this.inputDimensao3 = inputDimensao3;
	}

	public String getInputDimensao4() {
		return inputDimensao4;
	}

	public void setInputDimensao4(String inputDimensao4) {
		this.inputDimensao4 = inputDimensao4;
	}

	public String getInputDimensao5() {
		return inputDimensao5;
	}

	public void setInputDimensao5(String inputDimensao5) {
		this.inputDimensao5 = inputDimensao5;
	}

	public String getInputDimensao6() {
		return inputDimensao6;
	}

	public void setInputDimensao6(String inputDimensao6) {
		this.inputDimensao6 = inputDimensao6;
	}

	public String getInputDimensao7() {
		return inputDimensao7;
	}

	public void setInputDimensao7(String inputDimensao7) {
		this.inputDimensao7 = inputDimensao7;
	}

	public String getInputDimensao8() {
		return inputDimensao8;
	}

	public void setInputDimensao8(String inputDimensao8) {
		this.inputDimensao8 = inputDimensao8;
	}

	public String getInputDimensao9() {
		return inputDimensao9;
	}

	public void setInputDimensao9(String inputDimensao9) {
		this.inputDimensao9 = inputDimensao9;
	}

	public String getInputDimensao10() {
		return inputDimensao10;
	}

	public void setInputDimensao10(String inputDimensao10) {
		this.inputDimensao10 = inputDimensao10;
	}

	public String getOutputSaida() {
		return outputSaida;
	}

	public void setOutputSaida(String outputSaida) {
		this.outputSaida = outputSaida;
	}

	public String getOutputTeste() {
		return outputTeste;
	}

	public void setOutputTeste(String outputTeste) {
		this.outputTeste = outputTeste;
	}

	public String getcabecalho1() {
		return cabecalho1;
	}

	public void setcabecalho1(String cabecalho1) {
		this.cabecalho1 = cabecalho1;
	}

	public String getcabecalho2() {
		return cabecalho2;
	}

	public void setcabecalho2(String cabecalho2) {
		this.cabecalho2 = cabecalho2;
	}

	public String getcabecalho3() {
		return cabecalho3;
	}

	public void setcabecalho3(String cabecalho3) {
		this.cabecalho3 = cabecalho3;
	}

	public String getcabecalho4() {
		return cabecalho4;
	}

	public void setcabecalho4(String cabecalho4) {
		this.cabecalho4 = cabecalho4;
	}

	public String getcabecalho5() {
		return cabecalho5;
	}

	public void setcabecalho5(String cabecalho5) {
		this.cabecalho5 = cabecalho5;
	}

	public String getcabecalho6() {
		return cabecalho6;
	}

	public void setcabecalho6(String cabecalho6) {
		this.cabecalho6 = cabecalho6;
	}

	public String getcabecalho7() {
		return cabecalho7;
	}

	public void setcabecalho7(String cabecalho7) {
		this.cabecalho7 = cabecalho7;
	}

	public String getcabecalho8() {
		return cabecalho8;
	}

	public void setcabecalho8(String cabecalho8) {
		this.cabecalho8 = cabecalho8;
	}

	public String getcabecalho9() {
		return cabecalho9;
	}

	public void setcabecalho9(String cabecalho9) {
		this.cabecalho9 = cabecalho9;
	}

	public String getcabecalho10() {
		return cabecalho10;
	}

	public void setcabecalho10(String cabecalho10) {
		this.cabecalho10 = cabecalho10;
	}

	public String getcabecalho11() {
		return cabecalho11;
	}

	public void setcabecalho11(String cabecalho11) {
		this.cabecalho11 = cabecalho11;
	}

	public String getstructureDefinition1() {
		return structureDefinition1;
	}

	public void setstructureDefinition1(String structureDefinition1) {
		this.structureDefinition1 = structureDefinition1;
	}

	public String getstructureDefinition2() {
		return structureDefinition2;
	}

	public void setstructureDefinition2(String structureDefinition2) {
		this.structureDefinition2 = structureDefinition2;
	}

	public String getstructureDefinition3() {
		return structureDefinition3;
	}

	public void setstructureDefinition3(String structureDefinition3) {
		this.structureDefinition3 = structureDefinition3;
	}

	public String getstructureDefinition4() {
		return structureDefinition4;
	}

	public void setstructureDefinition4(String structureDefinition4) {
		this.structureDefinition4 = structureDefinition4;
	}

	public String getstructureDefinition5() {
		return structureDefinition5;
	}

	public void setstructureDefinition5(String structureDefinition5) {
		this.structureDefinition5 = structureDefinition5;
	}

	public String getstructureDefinition6() {
		return structureDefinition6;
	}

	public void setstructureDefinition6(String structureDefinition6) {
		this.structureDefinition6 = structureDefinition6;
	}

	public String getstructureDefinition7() {
		return structureDefinition7;
	}

	public void setstructureDefinition7(String structureDefinition7) {
		this.structureDefinition7 = structureDefinition7;
	}

	public String getstructureDefinition8() {
		return structureDefinition8;
	}

	public void setstructureDefinition8(String structureDefinition8) {
		this.structureDefinition8 = structureDefinition8;
	}

	public String getstructureDefinition9() {
		return structureDefinition9;
	}

	public void setstructureDefinition9(String structureDefinition9) {
		this.structureDefinition9 = structureDefinition9;
	}

	public String getstructureDefinition10() {
		return structureDefinition10;
	}

	public void setstructureDefinition10(String structureDefinition10) {
		this.structureDefinition10 = structureDefinition10;
	}

	public String getlabel1() {
		return label1;
	}

	public void setlabel1(String label1) {
		this.label1 = label1;
	}

	public String getlabel2() {
		return label2;
	}

	public void setlabel2(String label2) {
		this.label2 = label2;
	}

	public String getlabel3() {
		return label3;
	}

	public void setlabel3(String label3) {
		this.label3 = label3;
	}

	public String getlabel4() {
		return label4;
	}

	public void setlabel4(String label4) {
		this.label4 = label4;
	}

	public String getlabel5() {
		return label5;
	}

	public void setlabel5(String label5) {
		this.label5 = label5;
	}

	public String getlabel6() {
		return label6;
	}

	public void setlabel6(String label6) {
		this.label6 = label6;
	}

	public String getlabel7() {
		return label7;
	}

	public void setlabel7(String label7) {
		this.label7 = label7;
	}

	public String getlabel8() {
		return label8;
	}

	public void setlabel8(String label8) {
		this.label8 = label8;
	}

	public String getlabel9() {
		return label9;
	}

	public void setlabel9(String label9) {
		this.label9 = label9;
	}

	public String getlabel10() {
		return label10;
	}

	public void setlabel10(String label10) {
		this.label10 = label10;
	}

	public Boolean getInnerKeepInputFields() {
		return innerKeepInputFields;
	}

	public void setInnerKeepInputFields(Boolean innerKeepInputFields) {
		this.innerKeepInputFields = innerKeepInputFields;
	}

}
