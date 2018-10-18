package br.ufrj.ppgi.greco.kettle;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import br.ufrj.ppgi.greco.kettle.plugin.tools.datatable.DataTable;

public class DataCubeStep extends BaseStep implements StepInterface {

	int i = 0;
	public static final String OBJ = "exProp:%s ex:%s;";
	
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

	private String formatUri(String uri) {
		return "<" + uri + ">";
	}

	private String formatPrefix(String prefix) {
		if (!prefix.equals("@base")){
			return "@prefix " + prefix + ":";
		}else {
			return prefix;
		}
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

			DataTable<String> vocabularyTable = meta.getVocabularyTable();
			for (int k = 0; k < vocabularyTable.size(); k++) {
				String prefix = vocabularyTable.getValue(k, DataCubeStepMeta.Field.VOCABULARY_TABLE_PREFIX.name());
				String uri = vocabularyTable.getValue(k, DataCubeStepMeta.Field.VOCABULARY_TABLE_URI.name());
				putOutRow(row, meta, data, this.formatPrefix(prefix) + " " + this.formatUri(uri) + " .");
			}

			putOutRow(row, meta, data, "", 
					"<http://purl.org/linked-data/cube> a owl:Ontology ;", 
					"rdfs:label \"Example DataCube Knowledge Base\" ;",
					"dc:description \"This knowledgebase contains one Data Structure Definition with one Data Set. This Data Set has a couple of Components and Observations.\" .",
					"", 
					"# Data Structure Definitions", 
					"", 
					"ex:dsd a cube:DataStructureDefinition ;",
					"    rdfs:label \"A Data Structure Definition\"@en ;",
					"    rdfs:comment \"Defines the structure of a DataSet or slice.\" ;");

			DataTable<String> dimensionTable = meta.getDimensionTable();
			for (int k = 0; k < dimensionTable.size(); k++) {
				String dimURI = dimensionTable.getValue(k, DataCubeStepMeta.Field.DIMENSION_TABLE_URI.name());
				if (k == 0) {
					putOutRow(row, meta, data, "    cube:component  " + this.formatUri(dimURI) + ",");
				} else if (k == dimensionTable.size() - 1) {
					putOutRow(row, meta, data, "      " + this.formatUri(dimURI) + " .");
				} else {
					putOutRow(row, meta, data, "      " + this.formatUri(dimURI) + ",");
				}
			}

			putOutRow(row, meta, data, "", "# Component Specifications", "");

			for (int k = 0; k < dimensionTable.size(); k++) {
				String label = dimensionTable.getValue(k, DataCubeStepMeta.Field.DIMENSION_TABLE_LABEL.name());
				String dimURI = dimensionTable.getValue(k, DataCubeStepMeta.Field.DIMENSION_TABLE_URI.name());
				String dimName = dimensionTable.getValue(k, DataCubeStepMeta.Field.DIMENSION_TABLE_NAME.name());
				putOutRow(row, meta, data, 
						this.formatUri(dimURI) + " a cube:ComponentSpecification ;",
						"    rdfs:label \"" + label + "\" ;", 
						"    cube: cube:dimension exProp:" + dimName + " .", "");
			}

			putOutRow(row, meta, data, "# Data Set",
					"rdfs:label \"A DataSet\"^^<http://www.w3.org/2001/XMLSchema#string> ;",
					"rdfs:comment \"Represents a collection of observations and conforming to some common dimensional structure.\" ;",
					"cube:structure ex:dsd .", "", "# Dimensions, Unit and Measure");

			for (int k = 0; k < dimensionTable.size(); k++) {
				String type = dimensionTable.getValue(k, DataCubeStepMeta.Field.DIMENSION_TABLE_TYPE.name());
				String label = dimensionTable.getValue(k, DataCubeStepMeta.Field.DIMENSION_TABLE_LABEL.name());
				String dimName = dimensionTable.getValue(k, DataCubeStepMeta.Field.DIMENSION_TABLE_NAME.name());
				if (type.trim().equals("")) { // it's not a measure
					putOutRow(row, meta, data, 
							"exProp:" + dimName + " a cube:DimensionProperty ;",
							"    rdfs:label \"" + label + "\"@en .", "");
				} else {
					putOutRow(row, meta, data, "exProp:unit a cube:AttributeProperty ;",
							"    exProp:" + dimName + " a cube:MeasureProperty ;", 
							"    rdfs:label \"" + label + "\"@en .", "");
				}
			}

		}

		putOutRow(row, meta, data, "ex:ob" + i + " a cube:Observation;", 
				                   "    cube:dataSet ex:dataset;");

		DataTable<String> dimensionTable = meta.getDimensionTable();
		for (int k = 0; k < dimensionTable.size(); k++) {
			String dimName = dimensionTable.getValue(k, DataCubeStepMeta.Field.DIMENSION_TABLE_NAME.name());
			String type = dimensionTable.getValue(k, DataCubeStepMeta.Field.DIMENSION_TABLE_TYPE.name());
			String label = dimensionTable.getValue(k, DataCubeStepMeta.Field.DIMENSION_TABLE_LABEL.name());
			if (type.trim().equals("")) {
				putOutRow(row, meta, data, "    " + String.format(OBJ, dimName, getInputRowMeta().getString(row, dimName, "")));
			} else {
				putOutRow(row, meta, data, 
						"    exProp:unit \"" + label + "\";",
						"    exProp:value \"" + getInputRowMeta().getString(row, dimName, "") + "\"^^" + type + ";");
			}
		}

		putOutRow(row, meta, data, "    rdfs:label \"\".", "");

		i++;
		return true;
	}

	private void putOutRow(Object[] inputRow, DataCubeStepMeta meta, DataCubeStepData data, String... lines)
			throws KettleStepException {
		for (String arg : lines) {
			Object[] outputRow = null;
			outputRow = meta.isKeepInputFields() ? inputRow : new Object[0];
			outputRow = RowDataUtil.addValueData(outputRow, outputRow.length, arg);
			putRow(data.outputRowMeta, outputRow);
		}
	}

}