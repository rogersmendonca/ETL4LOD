package br.ufrj.ppgi.greco.kettle;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

public class DataCubeStep extends BaseStep implements StepInterface {

	int i = 0;

	public static final String OBJ1 = "exProp:%s ex:%s;";
	public static final String OBJ2 = "exProp:%s ex:%s;";
	public static final String OBJ3 = "exProp:%s ex:%s;";
	public static final String OBJ4 = "exProp:%s ex:%s;";
	public static final String OBJ5 = "exProp:%s ex:%s;";
	public static final String OBJ6 = "exProp:%s ex:%s;";
	public static final String OBJ7 = "exProp:%s ex:%s;";
	public static final String OBJ8 = "exProp:%s ex:%s;";
	public static final String OBJ9 = "exProp:%s ex:%s;";

	public DataCubeStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
			Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		if (super.init(smi, sdi)) {
			return true;
		} else
			return false;
	}

	@Override
	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		super.dispose(smi, sdi);
	}

	/**
	 * Método chamado para cada linha que entra no step
	 */
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {

		DataCubeStepMeta meta = (DataCubeStepMeta) smi;
		DataCubeStepData data = (DataCubeStepData) sdi;

		Object[] row = getRow();
		if (row == null) { 
			setOutputDone();
			return false;
		}

		if (first) { 
			first = false;

			RowMetaInterface rowMeta = getInputRowMeta();
			data.outputRowMeta = rowMeta.clone();

			// Adiciona os metadados do step atual
			meta.getFields(data.outputRowMeta, getStepname(), null, null, this);

			if (meta.getcabecalho1() != null) {
				Object[] outputCabecalho1 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputCabecalho1 = RowDataUtil.addValueData(outputCabecalho1, outputCabecalho1.length,
						"@base <" + meta.getcabecalho1() + ">.");
				putRow(data.outputRowMeta, outputCabecalho1);
			}
			if (meta.getcabecalho2() != null) {
				Object[] outputCabecalho2 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputCabecalho2 = RowDataUtil.addValueData(outputCabecalho2, outputCabecalho2.length,
						"@prefix owl: <" + meta.getcabecalho2() + ">.");
				putRow(data.outputRowMeta, outputCabecalho2);
			}
			if (meta.getcabecalho3() != null) {
				Object[] outputCabecalho3 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputCabecalho3 = RowDataUtil.addValueData(outputCabecalho3, outputCabecalho3.length,
						"@prefix rdf: <" + meta.getcabecalho3() + ">.");
				putRow(data.outputRowMeta, outputCabecalho3);
			}
			if (meta.getcabecalho4() != null) {
				Object[] outputCabecalho4 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputCabecalho4 = RowDataUtil.addValueData(outputCabecalho4, outputCabecalho4.length,
						"@prefix rdfs: <" + meta.getcabecalho4() + ">.");
				putRow(data.outputRowMeta, outputCabecalho4);
			}
			if (meta.getcabecalho5() != null) {
				Object[] outputCabecalho5 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputCabecalho5 = RowDataUtil.addValueData(outputCabecalho5, outputCabecalho5.length,
						"@prefix dc: <" + meta.getcabecalho5() + ">.");
				putRow(data.outputRowMeta, outputCabecalho5);
			}
			if (meta.getcabecalho6() != null) {
				Object[] outputCabecalho6 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputCabecalho6 = RowDataUtil.addValueData(outputCabecalho6, outputCabecalho6.length,
						"@prefix skos: <" + meta.getcabecalho6() + ">.");
				putRow(data.outputRowMeta, outputCabecalho6);
			}
			if (meta.getcabecalho7() != null) {
				Object[] outputCabecalho7 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputCabecalho7 = RowDataUtil.addValueData(outputCabecalho7, outputCabecalho7.length,
						"@prefix sdmx-code: <" + meta.getcabecalho7() + ">.");
				putRow(data.outputRowMeta, outputCabecalho7);
			}
			if (meta.getcabecalho8() != null) {
				Object[] outputCabecalho8 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputCabecalho8 = RowDataUtil.addValueData(outputCabecalho8, outputCabecalho8.length,
						"@prefix sdmx-dimension: <" + meta.getcabecalho8() + ">.");
				putRow(data.outputRowMeta, outputCabecalho8);
			}
			if (meta.getcabecalho9() != null) {
				Object[] outputCabecalho9 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputCabecalho9 = RowDataUtil.addValueData(outputCabecalho9, outputCabecalho9.length,
						"@prefix cube: <" + meta.getcabecalho9() + ">.");
				putRow(data.outputRowMeta, outputCabecalho9);
			}
			if (meta.getcabecalho10() != null) {
				Object[] outputCabecalho10 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputCabecalho10 = RowDataUtil.addValueData(outputCabecalho10, outputCabecalho10.length,
						"@prefix ex: <" + meta.getcabecalho10() + ">.");
				putRow(data.outputRowMeta, outputCabecalho10);
			}
			if (meta.getcabecalho11() != null) {
				Object[] outputCabecalho15 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputCabecalho15 = RowDataUtil.addValueData(outputCabecalho15, outputCabecalho15.length,
						"@prefix exProp: <" + meta.getcabecalho11() + ">.");
				putRow(data.outputRowMeta, outputCabecalho15);
			}

			Object[] outputCabecalho11 = meta.getInnerKeepInputFields() ? row : new Object[0];
			outputCabecalho11 = RowDataUtil.addValueData(outputCabecalho11, outputCabecalho11.length,
					"<> a owl:Ontology ;");
			putRow(data.outputRowMeta, outputCabecalho11);

			Object[] outputCabecalho12 = meta.getInnerKeepInputFields() ? row : new Object[0];
			outputCabecalho12 = RowDataUtil.addValueData(outputCabecalho12, outputCabecalho12.length,
					"rdfs:label \"Example DataCube Knowledge Base\" ;");
			putRow(data.outputRowMeta, outputCabecalho12);

			Object[] outputCabecalho13 = meta.getInnerKeepInputFields() ? row : new Object[0];
			outputCabecalho13 = RowDataUtil.addValueData(outputCabecalho13, outputCabecalho13.length,
					"dc:description \"This knowledgebase contains one Data Structure Definition with one Data Set. This Data Set has a couple of Components and Observations.\" .");
			putRow(data.outputRowMeta, outputCabecalho13);

			Object[] outputCabecalho14 = meta.getInnerKeepInputFields() ? row : new Object[0];
			outputCabecalho14 = RowDataUtil.addValueData(outputCabecalho14, outputCabecalho14.length, "");
			putRow(data.outputRowMeta, outputCabecalho14);

			Object[] outputDefinition1 = meta.getInnerKeepInputFields() ? row : new Object[0];
			outputDefinition1 = RowDataUtil.addValueData(outputDefinition1, outputDefinition1.length,
					"# Data Structure Definitions");
			putRow(data.outputRowMeta, outputDefinition1);

			Object[] outputDefinition2 = meta.getInnerKeepInputFields() ? row : new Object[0];
			outputDefinition2 = RowDataUtil.addValueData(outputDefinition2, outputDefinition2.length, "");
			putRow(data.outputRowMeta, outputDefinition2);

			Object[] outputDefinition3 = meta.getInnerKeepInputFields() ? row : new Object[0];
			outputDefinition3 = RowDataUtil.addValueData(outputDefinition3, outputDefinition3.length,
					"ex:dsd a cube:DataStructureDefinition ;");
			putRow(data.outputRowMeta, outputDefinition3);

			Object[] outputDefinition4 = meta.getInnerKeepInputFields() ? row : new Object[0];
			outputDefinition4 = RowDataUtil.addValueData(outputDefinition4, outputDefinition4.length,
					"    rdfs:label \"A Data Structure Definition\"@en ;");
			putRow(data.outputRowMeta, outputDefinition4);

			Object[] outputDefinition5 = meta.getInnerKeepInputFields() ? row : new Object[0];
			outputDefinition5 = RowDataUtil.addValueData(outputDefinition5, outputDefinition5.length,
					"    rdfs:comment \"Defines the structure of a DataSet or slice.\" ;");
			putRow(data.outputRowMeta, outputDefinition5);

			if (meta.getstructureDefinition1() != null) {
				Object[] outputStructureDefinition1 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputStructureDefinition1 = RowDataUtil.addValueData(outputStructureDefinition1,
						outputStructureDefinition1.length,
						"    cube:component  <" + meta.getstructureDefinition1() + ">,");
				putRow(data.outputRowMeta, outputStructureDefinition1);
			}
			if (meta.getstructureDefinition2() != null) {
				Object[] outputStructureDefinition2 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputStructureDefinition2 = RowDataUtil.addValueData(outputStructureDefinition2,
						outputStructureDefinition2.length, "             <" + meta.getstructureDefinition2() + ">,");
				putRow(data.outputRowMeta, outputStructureDefinition2);
			}
			if (meta.getstructureDefinition3() != null) {
				Object[] outputStructureDefinition3 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputStructureDefinition3 = RowDataUtil.addValueData(outputStructureDefinition3,
						outputStructureDefinition3.length, "             <" + meta.getstructureDefinition3() + ">,");
				putRow(data.outputRowMeta, outputStructureDefinition3);
			}
			if (meta.getstructureDefinition4() != null) {
				Object[] outputStructureDefinition4 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputStructureDefinition4 = RowDataUtil.addValueData(outputStructureDefinition4,
						outputStructureDefinition4.length, "             <" + meta.getstructureDefinition4() + ">,");
				putRow(data.outputRowMeta, outputStructureDefinition4);
			}
			if (meta.getstructureDefinition5() != null) {
				Object[] outputStructureDefinition5 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputStructureDefinition5 = RowDataUtil.addValueData(outputStructureDefinition5,
						outputStructureDefinition5.length, "             <" + meta.getstructureDefinition5() + ">,");
				putRow(data.outputRowMeta, outputStructureDefinition5);
			}
			if (meta.getstructureDefinition6() != null) {
				Object[] outputStructureDefinition6 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputStructureDefinition6 = RowDataUtil.addValueData(outputStructureDefinition6,
						outputStructureDefinition6.length, "             <" + meta.getstructureDefinition6() + ">,");
				putRow(data.outputRowMeta, outputStructureDefinition6);
			}
			if (meta.getstructureDefinition7() != null) {
				Object[] outputStructureDefinition7 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputStructureDefinition7 = RowDataUtil.addValueData(outputStructureDefinition7,
						outputStructureDefinition7.length, "             <" + meta.getstructureDefinition7() + ">,");
				putRow(data.outputRowMeta, outputStructureDefinition7);
			}
			if (meta.getstructureDefinition8() != null) {
				Object[] outputStructureDefinition8 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputStructureDefinition8 = RowDataUtil.addValueData(outputStructureDefinition8,
						outputStructureDefinition8.length, "             <" + meta.getstructureDefinition8() + ">,");
				putRow(data.outputRowMeta, outputStructureDefinition8);
			}
			if (meta.getstructureDefinition9() != null) {
				Object[] outputStructureDefinition9 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputStructureDefinition9 = RowDataUtil.addValueData(outputStructureDefinition9,
						outputStructureDefinition9.length, "             <" + meta.getstructureDefinition9() + ">,");
				putRow(data.outputRowMeta, outputStructureDefinition9);
			}
			if (meta.getstructureDefinition10() != null) {
				Object[] outputStructureDefinition10 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputStructureDefinition10 = RowDataUtil.addValueData(outputStructureDefinition10,
						outputStructureDefinition10.length, "             <" + meta.getstructureDefinition10() + ">.");
				putRow(data.outputRowMeta, outputStructureDefinition10);
			}

			Object[] outputStructureDefinition11 = meta.getInnerKeepInputFields() ? row : new Object[0];
			outputStructureDefinition11 = RowDataUtil.addValueData(outputStructureDefinition11,
					outputStructureDefinition11.length, "");
			putRow(data.outputRowMeta, outputStructureDefinition11);

			Object[] outputComponentSpecifications1 = meta.getInnerKeepInputFields() ? row : new Object[0];
			outputComponentSpecifications1 = RowDataUtil.addValueData(outputComponentSpecifications1,
					outputComponentSpecifications1.length, "# Component Specifications");
			putRow(data.outputRowMeta, outputComponentSpecifications1);

			Object[] outputComponentSpecifications2 = meta.getInnerKeepInputFields() ? row : new Object[0];
			outputComponentSpecifications2 = RowDataUtil.addValueData(outputComponentSpecifications2,
					outputComponentSpecifications2.length, "");
			putRow(data.outputRowMeta, outputComponentSpecifications2);

			if (meta.getlabel1() != null) {
				Object[] outputLabel1 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputLabel1 = RowDataUtil.addValueData(outputLabel1, outputLabel1.length,
						"<" + meta.getstructureDefinition1() + "> a cube:ComponentSpecification ;");
				putRow(data.outputRowMeta, outputLabel1);
				Object[] outputLabel2 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputLabel2 = RowDataUtil.addValueData(outputLabel2, outputLabel2.length,
						"rdfs:label \"" + meta.getlabel1() + "\" ;");
				putRow(data.outputRowMeta, outputLabel2);
				Object[] outputLabel3 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputLabel3 = RowDataUtil.addValueData(outputLabel3, outputLabel3.length,
						"cube: cube:dimension exProp:" + meta.getInputDimensao1() + ".");
				putRow(data.outputRowMeta, outputLabel3);
				Object[] outputln1 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputln1 = RowDataUtil.addValueData(outputln1, outputln1.length, "");
				putRow(data.outputRowMeta, outputln1);
			}
			if (meta.getlabel2() != null) {
				Object[] outputLabel4 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputLabel4 = RowDataUtil.addValueData(outputLabel4, outputLabel4.length,
						"<" + meta.getstructureDefinition2() + "> a cube:ComponentSpecification ;");
				putRow(data.outputRowMeta, outputLabel4);
				Object[] outputLabel5 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputLabel5 = RowDataUtil.addValueData(outputLabel5, outputLabel5.length,
						"rdfs:label \"" + meta.getlabel2() + "\" ;");
				putRow(data.outputRowMeta, outputLabel5);
				Object[] outputLabel6 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputLabel6 = RowDataUtil.addValueData(outputLabel6, outputLabel6.length,
						"cube: cube:dimension exProp:" + meta.getInputDimensao2() + ".");
				putRow(data.outputRowMeta, outputLabel6);
				Object[] outputln1 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputln1 = RowDataUtil.addValueData(outputln1, outputln1.length, "");
				putRow(data.outputRowMeta, outputln1);
			}
			if (meta.getlabel3() != null) {
				Object[] outputLabel7 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputLabel7 = RowDataUtil.addValueData(outputLabel7, outputLabel7.length,
						"<" + meta.getstructureDefinition3() + "> a cube:ComponentSpecification ;");
				putRow(data.outputRowMeta, outputLabel7);
				Object[] outputLabel8 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputLabel8 = RowDataUtil.addValueData(outputLabel8, outputLabel8.length,
						"rdfs:label \"" + meta.getlabel3() + "\" ;");
				putRow(data.outputRowMeta, outputLabel8);
				Object[] outputLabel9 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputLabel9 = RowDataUtil.addValueData(outputLabel9, outputLabel9.length,
						"cube: cube:dimension exProp:" + meta.getInputDimensao3() + ".");
				putRow(data.outputRowMeta, outputLabel9);
				Object[] outputln1 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputln1 = RowDataUtil.addValueData(outputln1, outputln1.length, "");
				putRow(data.outputRowMeta, outputln1);
			}
			if (meta.getlabel4() != null) {
				Object[] outputLabel10 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputLabel10 = RowDataUtil.addValueData(outputLabel10, outputLabel10.length,
						"<" + meta.getstructureDefinition4() + "> a cube:ComponentSpecification ;");
				putRow(data.outputRowMeta, outputLabel10);
				Object[] outputLabel11 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputLabel11 = RowDataUtil.addValueData(outputLabel11, outputLabel11.length,
						"rdfs:label \"" + meta.getlabel4() + "\" ;");
				putRow(data.outputRowMeta, outputLabel11);
				Object[] outputLabel12 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputLabel12 = RowDataUtil.addValueData(outputLabel12, outputLabel12.length,
						"cube: cube:dimension exProp:" + meta.getInputDimensao4() + ".");
				putRow(data.outputRowMeta, outputLabel12);
				Object[] outputln1 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputln1 = RowDataUtil.addValueData(outputln1, outputln1.length, "");
				putRow(data.outputRowMeta, outputln1);
			}
			if (meta.getlabel5() != null) {
				Object[] outputLabel13 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputLabel13 = RowDataUtil.addValueData(outputLabel13, outputLabel13.length,
						"<" + meta.getstructureDefinition5() + "> a cube:ComponentSpecification ;");
				putRow(data.outputRowMeta, outputLabel13);
				Object[] outputLabel14 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputLabel14 = RowDataUtil.addValueData(outputLabel14, outputLabel14.length,
						"rdfs:label \"" + meta.getlabel5() + "\" ;");
				putRow(data.outputRowMeta, outputLabel14);
				Object[] outputLabel15 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputLabel15 = RowDataUtil.addValueData(outputLabel15, outputLabel15.length,
						"cube: cube:dimension exProp:" + meta.getInputDimensao5() + ".");
				putRow(data.outputRowMeta, outputLabel15);
				Object[] outputln1 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputln1 = RowDataUtil.addValueData(outputln1, outputln1.length, "");
				putRow(data.outputRowMeta, outputln1);
			}
			if (meta.getlabel6() != null) {
				Object[] outputLabel16 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputLabel16 = RowDataUtil.addValueData(outputLabel16, outputLabel16.length,
						"<" + meta.getstructureDefinition6() + "> a cube:ComponentSpecification ;");
				putRow(data.outputRowMeta, outputLabel16);
				Object[] outputLabel17 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputLabel17 = RowDataUtil.addValueData(outputLabel17, outputLabel17.length,
						"rdfs:label \"" + meta.getlabel6() + "\" ;");
				putRow(data.outputRowMeta, outputLabel17);
				Object[] outputLabel18 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputLabel18 = RowDataUtil.addValueData(outputLabel18, outputLabel18.length,
						"cube: cube:dimension exProp:" + meta.getInputDimensao6() + ".");
				putRow(data.outputRowMeta, outputLabel18);
				Object[] outputln1 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputln1 = RowDataUtil.addValueData(outputln1, outputln1.length, "");
				putRow(data.outputRowMeta, outputln1);
			}
			if (meta.getlabel7() != null) {
				Object[] outputLabel19 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputLabel19 = RowDataUtil.addValueData(outputLabel19, outputLabel19.length,
						"<" + meta.getstructureDefinition7() + "> a cube:ComponentSpecification ;");
				putRow(data.outputRowMeta, outputLabel19);
				Object[] outputLabel20 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputLabel20 = RowDataUtil.addValueData(outputLabel20, outputLabel20.length,
						"rdfs:label \"" + meta.getlabel7() + "\" ;");
				putRow(data.outputRowMeta, outputLabel20);
				Object[] outputLabel21 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputLabel21 = RowDataUtil.addValueData(outputLabel21, outputLabel21.length,
						"cube: cube:dimension exProp:" + meta.getInputDimensao7() + ".");
				putRow(data.outputRowMeta, outputLabel21);
				Object[] outputln1 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputln1 = RowDataUtil.addValueData(outputln1, outputln1.length, "");
				putRow(data.outputRowMeta, outputln1);
			}
			if (meta.getlabel8() != null) {
				Object[] outputLabel22 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputLabel22 = RowDataUtil.addValueData(outputLabel22, outputLabel22.length,
						"<" + meta.getstructureDefinition8() + "> a cube:ComponentSpecification ;");
				putRow(data.outputRowMeta, outputLabel22);
				Object[] outputLabel23 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputLabel23 = RowDataUtil.addValueData(outputLabel23, outputLabel23.length,
						"rdfs:label \"" + meta.getlabel8() + "\" ;");
				putRow(data.outputRowMeta, outputLabel23);
				Object[] outputLabel24 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputLabel24 = RowDataUtil.addValueData(outputLabel24, outputLabel24.length,
						"cube: cube:dimension exProp:" + meta.getInputDimensao8() + ".");
				putRow(data.outputRowMeta, outputLabel24);
				Object[] outputln1 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputln1 = RowDataUtil.addValueData(outputln1, outputln1.length, "");
				putRow(data.outputRowMeta, outputln1);
			}
			if (meta.getlabel9() != null) {
				Object[] outputLabel25 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputLabel25 = RowDataUtil.addValueData(outputLabel25, outputLabel25.length,
						"<" + meta.getstructureDefinition9() + "> a cube:ComponentSpecification ;");
				putRow(data.outputRowMeta, outputLabel25);
				Object[] outputLabel26 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputLabel26 = RowDataUtil.addValueData(outputLabel26, outputLabel26.length,
						"rdfs:label \"" + meta.getlabel9() + "\" ;");
				putRow(data.outputRowMeta, outputLabel26);
				Object[] outputLabel27 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputLabel27 = RowDataUtil.addValueData(outputLabel27, outputLabel27.length,
						"cube: cube:dimension exProp:" + meta.getInputDimensao9() + ".");
				putRow(data.outputRowMeta, outputLabel27);
				Object[] outputln1 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputln1 = RowDataUtil.addValueData(outputln1, outputln1.length, "");
				putRow(data.outputRowMeta, outputln1);
			}
			if (meta.getlabel10() != null) {
				Object[] outputLabel28 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputLabel28 = RowDataUtil.addValueData(outputLabel28, outputLabel28.length,
						"<" + meta.getstructureDefinition10() + "> a cube:ComponentSpecification ;");
				putRow(data.outputRowMeta, outputLabel28);
				Object[] outputLabel29 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputLabel29 = RowDataUtil.addValueData(outputLabel29, outputLabel29.length,
						"rdfs:label \"" + meta.getlabel10() + "\" ;");
				putRow(data.outputRowMeta, outputLabel29);
				Object[] outputLabel30 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputLabel30 = RowDataUtil.addValueData(outputLabel30, outputLabel30.length,
						"cube: cube:dimension exProp:" + meta.getInputDimensao10() + ".");
				putRow(data.outputRowMeta, outputLabel30);
				Object[] outputln1 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputln1 = RowDataUtil.addValueData(outputln1, outputln1.length, "");
				putRow(data.outputRowMeta, outputln1);
			}

			Object[] outputDataSet = meta.getInnerKeepInputFields() ? row : new Object[0];
			outputDataSet = RowDataUtil.addValueData(outputDataSet, outputDataSet.length, "# Data Set");
			putRow(data.outputRowMeta, outputDataSet);
			putRow(data.outputRowMeta, outputDefinition2);

			Object[] outputDataSet2 = meta.getInnerKeepInputFields() ? row : new Object[0];
			outputDataSet2 = RowDataUtil.addValueData(outputDataSet2, outputDataSet2.length,
					"rdfs:label \"A DataSet\"^^<http://www.w3.org/2001/XMLSchema#string> ;");
			putRow(data.outputRowMeta, outputDataSet2);

			Object[] outputDataSet3 = meta.getInnerKeepInputFields() ? row : new Object[0];
			outputDataSet3 = RowDataUtil.addValueData(outputDataSet3, outputDataSet3.length,
					"rdfs:comment \"Represents a collection of observations and conforming to some common dimensional structure.\" ;");
			putRow(data.outputRowMeta, outputDataSet3);

			Object[] outputDataSet4 = meta.getInnerKeepInputFields() ? row : new Object[0];
			outputDataSet4 = RowDataUtil.addValueData(outputDataSet4, outputDataSet4.length, "cube:structure ex:dsd .");
			putRow(data.outputRowMeta, outputDataSet4);
			putRow(data.outputRowMeta, outputStructureDefinition11);

			Object[] outputaux = meta.getInnerKeepInputFields() ? row : new Object[0];
			outputaux = RowDataUtil.addValueData(outputaux, outputaux.length, "# Dimensions, Unit and Measure");
			putRow(data.outputRowMeta, outputaux);
			putRow(data.outputRowMeta, outputStructureDefinition11);

			if (meta.getlabel1() != null) {
				Object[] outputDataSet6 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputDataSet6 = RowDataUtil.addValueData(outputDataSet6, outputDataSet6.length,
						"exProp:" + meta.getInputDimensao1() + " a cube:DimensionProperty ;");
				putRow(data.outputRowMeta, outputDataSet6);
				Object[] outputDataSet7 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputDataSet7 = RowDataUtil.addValueData(outputDataSet7, outputDataSet7.length,
						"rdfs:label \"" + meta.getlabel1() + "\"@en .");
				putRow(data.outputRowMeta, outputDataSet7);
				putRow(data.outputRowMeta, outputStructureDefinition11);

			}
			if (meta.getlabel2() != null) {
				Object[] outputDataSet8 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputDataSet8 = RowDataUtil.addValueData(outputDataSet8, outputDataSet8.length,
						"exProp:" + meta.getInputDimensao2() + " a cube:DimensionProperty ;");
				putRow(data.outputRowMeta, outputDataSet8);
				Object[] outputDataSet9 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputDataSet9 = RowDataUtil.addValueData(outputDataSet9, outputDataSet9.length,
						"rdfs:label \"" + meta.getlabel2() + "\"@en .");
				putRow(data.outputRowMeta, outputDataSet9);
				putRow(data.outputRowMeta, outputStructureDefinition11);
			}
			if (meta.getlabel3() != null) {
				Object[] outputDataSet10 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputDataSet10 = RowDataUtil.addValueData(outputDataSet10, outputDataSet10.length,
						"exProp:" + meta.getInputDimensao3() + " a cube:DimensionProperty ;");
				putRow(data.outputRowMeta, outputDataSet10);
				Object[] outputDataSet11 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputDataSet11 = RowDataUtil.addValueData(outputDataSet11, outputDataSet11.length,
						"rdfs:label \"" + meta.getlabel3() + "\"@en .");
				putRow(data.outputRowMeta, outputDataSet11);
				putRow(data.outputRowMeta, outputStructureDefinition11);
			}
			if (meta.getlabel4() != null) {
				Object[] outputDataSet12 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputDataSet12 = RowDataUtil.addValueData(outputDataSet12, outputDataSet12.length,
						"exProp:" + meta.getInputDimensao4() + " a cube:DimensionProperty ;");
				putRow(data.outputRowMeta, outputDataSet12);
				Object[] outputDataSet13 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputDataSet13 = RowDataUtil.addValueData(outputDataSet13, outputDataSet13.length,
						"rdfs:label \"" + meta.getlabel4() + "\"@en .");
				putRow(data.outputRowMeta, outputDataSet13);
				putRow(data.outputRowMeta, outputStructureDefinition11);
			}
			if (meta.getlabel5() != null) {
				Object[] outputDataSet14 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputDataSet14 = RowDataUtil.addValueData(outputDataSet14, outputDataSet14.length,
						"exProp:" + meta.getInputDimensao5() + " a cube:DimensionProperty ;");
				putRow(data.outputRowMeta, outputDataSet14);
				Object[] outputDataSet15 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputDataSet15 = RowDataUtil.addValueData(outputDataSet15, outputDataSet15.length,
						"rdfs:label \"" + meta.getlabel5() + "\"@en .");
				putRow(data.outputRowMeta, outputDataSet15);
				putRow(data.outputRowMeta, outputStructureDefinition11);
			}
			if (meta.getlabel6() != null) {
				Object[] outputDataSet16 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputDataSet16 = RowDataUtil.addValueData(outputDataSet16, outputDataSet16.length,
						"exProp:" + meta.getInputDimensao6() + " a cube:DimensionProperty ;");
				putRow(data.outputRowMeta, outputDataSet16);
				Object[] outputDataSet17 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputDataSet17 = RowDataUtil.addValueData(outputDataSet17, outputDataSet17.length,
						"rdfs:label \"" + meta.getlabel6() + "\"@en .");
				putRow(data.outputRowMeta, outputDataSet17);
				putRow(data.outputRowMeta, outputStructureDefinition11);
			}
			if (meta.getlabel7() != null) {
				Object[] outputDataSet18 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputDataSet18 = RowDataUtil.addValueData(outputDataSet18, outputDataSet18.length,
						"exProp:" + meta.getInputDimensao7() + " a cube:DimensionProperty ;");
				putRow(data.outputRowMeta, outputDataSet18);
				Object[] outputDataSet19 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputDataSet19 = RowDataUtil.addValueData(outputDataSet19, outputDataSet19.length,
						"rdfs:label \"" + meta.getlabel7() + "\"@en .");
				putRow(data.outputRowMeta, outputDataSet19);
				putRow(data.outputRowMeta, outputStructureDefinition11);
			}
			if (meta.getlabel8() != null) {
				Object[] outputDataSet20 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputDataSet20 = RowDataUtil.addValueData(outputDataSet20, outputDataSet20.length,
						"exProp:" + meta.getInputDimensao8() + " a cube:DimensionProperty ;");
				putRow(data.outputRowMeta, outputDataSet20);
				Object[] outputDataSet21 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputDataSet21 = RowDataUtil.addValueData(outputDataSet21, outputDataSet21.length,
						"rdfs:label \"" + meta.getlabel8() + "\"@en .");
				putRow(data.outputRowMeta, outputDataSet21);
				putRow(data.outputRowMeta, outputStructureDefinition11);
			}
			if (meta.getlabel9() != null) {
				Object[] outputDataSet22 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputDataSet22 = RowDataUtil.addValueData(outputDataSet22, outputDataSet22.length,
						"exProp:" + meta.getInputDimensao9() + " a cube:DimensionProperty ;");
				putRow(data.outputRowMeta, outputDataSet22);
				Object[] outputDataSet23 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputDataSet23 = RowDataUtil.addValueData(outputDataSet23, outputDataSet23.length,
						"rdfs:label \"" + meta.getlabel9() + "\"@en .");
				putRow(data.outputRowMeta, outputDataSet23);
				putRow(data.outputRowMeta, outputStructureDefinition11);
			}

			Object[] outputaux2 = meta.getInnerKeepInputFields() ? row : new Object[0];
			outputaux2 = RowDataUtil.addValueData(outputaux2, outputaux2.length,
					"exProp:unit a cube:AttributeProperty ;");
			putRow(data.outputRowMeta, outputaux2);

			if (meta.getlabel10() != null) {
				Object[] outputDataSet24 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputDataSet24 = RowDataUtil.addValueData(outputDataSet24, outputDataSet24.length,
						"exProp:" + meta.getInputDimensao10() + " a cube:MeasureProperty ;");
				putRow(data.outputRowMeta, outputDataSet24);
				Object[] outputDataSet25 = meta.getInnerKeepInputFields() ? row : new Object[0];
				outputDataSet25 = RowDataUtil.addValueData(outputDataSet25, outputDataSet25.length,
						"rdfs:label \"" + meta.getlabel10() + "\"@en .");
				putRow(data.outputRowMeta, outputDataSet25);
				putRow(data.outputRowMeta, outputStructureDefinition11);
			}

		}

		String inputDimensao1 = removeSignals(getInputRowMeta().getString(row, meta.getInputDimensao1(), ""));
		String inputDimensao2 = removeSignals(getInputRowMeta().getString(row, meta.getInputDimensao2(), ""));
		String inputDimensao3 = removeSignals(getInputRowMeta().getString(row, meta.getInputDimensao3(), ""));
		String inputDimensao4 = removeSignals(getInputRowMeta().getString(row, meta.getInputDimensao4(), ""));
		String inputDimensao5 = removeSignals(getInputRowMeta().getString(row, meta.getInputDimensao5(), ""));
		String inputDimensao6 = removeSignals(getInputRowMeta().getString(row, meta.getInputDimensao6(), ""));
		String inputDimensao7 = removeSignals(getInputRowMeta().getString(row, meta.getInputDimensao7(), ""));
		String inputDimensao8 = removeSignals(getInputRowMeta().getString(row, meta.getInputDimensao8(), ""));
		String inputDimensao9 = removeSignals(getInputRowMeta().getString(row, meta.getInputDimensao9(), ""));
		String inputDimensao10 = removeSignals(getInputRowMeta().getString(row, meta.getInputDimensao10(), ""));

		// TODO Logica do step: leitura de campos de entrada e internos e
		// gera��o do campo de sa�da
		// ..

		String obj1;
		String obj2;
		String obj3;
		String obj4;
		String obj5;
		String obj6;
		String obj7;
		String obj8;
		String obj9;

		Object[] outputLinha1 = meta.getInnerKeepInputFields() ? row : new Object[0];
		outputLinha1 = RowDataUtil.addValueData(outputLinha1, outputLinha1.length,
				"ex:ob" + i + " a cube:Observation;");
		putRow(data.outputRowMeta, outputLinha1);

		Object[] outputLinha2 = meta.getInnerKeepInputFields() ? row : new Object[0];
		outputLinha2 = RowDataUtil.addValueData(outputLinha2, outputLinha2.length, "cube:dataSet ex:dataset;");
		putRow(data.outputRowMeta, outputLinha2);

		if (!inputDimensao1.isEmpty()) {
			obj1 = String.format(OBJ1, meta.getInputDimensao1(), inputDimensao1);
			Object[] outputObj1 = meta.getInnerKeepInputFields() ? row : new Object[0];
			outputObj1 = RowDataUtil.addValueData(outputObj1, outputObj1.length, obj1);
			putRow(data.outputRowMeta, outputObj1);
		}

		if (!inputDimensao2.isEmpty()) {
			obj2 = String.format(OBJ2, meta.getInputDimensao2(), inputDimensao2);
			Object[] outputObj2 = meta.getInnerKeepInputFields() ? row : new Object[0];
			outputObj2 = RowDataUtil.addValueData(outputObj2, outputObj2.length, obj2);
			putRow(data.outputRowMeta, outputObj2);
		}

		if (!inputDimensao3.isEmpty()) {
			obj3 = String.format(OBJ3, meta.getInputDimensao3(), inputDimensao3);
			Object[] outputObj3 = meta.getInnerKeepInputFields() ? row : new Object[0];
			outputObj3 = RowDataUtil.addValueData(outputObj3, outputObj3.length, obj3);
			putRow(data.outputRowMeta, outputObj3);
		}

		if (!inputDimensao4.isEmpty()) {
			obj4 = String.format(OBJ4, meta.getInputDimensao4(), inputDimensao4);
			Object[] outpuObj4 = meta.getInnerKeepInputFields() ? row : new Object[0];
			outpuObj4 = RowDataUtil.addValueData(outpuObj4, outpuObj4.length, obj4);
			putRow(data.outputRowMeta, outpuObj4);
		}

		if (!inputDimensao5.isEmpty()) {
			obj5 = String.format(OBJ5, meta.getInputDimensao5(), inputDimensao5);
			Object[] outputObj5 = meta.getInnerKeepInputFields() ? row : new Object[0];
			outputObj5 = RowDataUtil.addValueData(outputObj5, outputObj5.length, obj5);
			putRow(data.outputRowMeta, outputObj5);
		}

		if (!inputDimensao6.isEmpty()) {
			obj6 = String.format(OBJ6, meta.getInputDimensao6(), inputDimensao6);
			Object[] outputObj6 = meta.getInnerKeepInputFields() ? row : new Object[0];
			outputObj6 = RowDataUtil.addValueData(outputObj6, outputObj6.length, obj6);
			putRow(data.outputRowMeta, outputObj6);
		}

		if (!inputDimensao7.isEmpty()) {
			obj7 = String.format(OBJ7, meta.getInputDimensao7(), inputDimensao7);
			Object[] outputObj7 = meta.getInnerKeepInputFields() ? row : new Object[0];
			outputObj7 = RowDataUtil.addValueData(outputObj7, outputObj7.length, obj7);
			putRow(data.outputRowMeta, outputObj7);
		}

		if (!inputDimensao8.isEmpty()) {
			obj8 = String.format(OBJ8, meta.getInputDimensao8(), inputDimensao8);
			Object[] outputObj8 = meta.getInnerKeepInputFields() ? row : new Object[0];
			outputObj8 = RowDataUtil.addValueData(outputObj8, outputObj8.length, obj8);
			putRow(data.outputRowMeta, outputObj8);
		}

		if (!inputDimensao9.isEmpty()) {
			obj9 = String.format(OBJ9, meta.getInputDimensao9(), inputDimensao9);
			Object[] outputObj9 = meta.getInnerKeepInputFields() ? row : new Object[0];
			outputObj9 = RowDataUtil.addValueData(outputObj9, outputObj9.length, obj9);
			putRow(data.outputRowMeta, outputObj9);
		}

		Object[] outputLinha3 = meta.getInnerKeepInputFields() ? row : new Object[0];
		outputLinha3 = RowDataUtil.addValueData(outputLinha3, outputLinha3.length,
				"exProp:unit \"" + meta.getlabel10() + "\";");
		putRow(data.outputRowMeta, outputLinha3);

		Object[] outputLinha4 = meta.getInnerKeepInputFields() ? row : new Object[0];
		outputLinha4 = RowDataUtil.addValueData(outputLinha4, outputLinha4.length,
				"exProp:value \"" + inputDimensao10 + "^^" + meta.getOutputSaida() + ";");
		putRow(data.outputRowMeta, outputLinha4);

		Object[] outputLinha5 = meta.getInnerKeepInputFields() ? row : new Object[0];
		outputLinha5 = RowDataUtil.addValueData(outputLinha5, outputLinha5.length, "rdfs:label \"\".");
		putRow(data.outputRowMeta, outputLinha5);

		Object[] outputFim = meta.getInnerKeepInputFields() ? row : new Object[0];
		outputFim = RowDataUtil.addValueData(outputFim, outputFim.length, "");
		putRow(data.outputRowMeta, outputFim);

		i++;
		return true;
	}

	private static String removeSignals(String value) {
		if (value != null) {
			return value.replaceAll("á", "a").replaceAll("à", "a").replaceAll("ä", "a").replaceAll("ã", "a")
					.replaceAll("ú", "u").replaceAll("ù", "u").replaceAll("é", "e").replaceAll("è", "e")
					.replaceAll("ó", "o").replaceAll("ò", "o").replaceAll("ú", "u").replaceAll("ç", "c")
					.replaceAll("í", "i").replaceAll("ì", "i").replaceAll(" ", "").trim();
		} else {
			return "";
		}
	}
}