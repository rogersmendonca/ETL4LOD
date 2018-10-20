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

public class TurtleGeneratorStep extends BaseStep implements StepInterface {

	int j = 0;

	public TurtleGeneratorStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
			Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		if (super.init(smi, sdi)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		super.dispose(smi, sdi);
	}

	/**
	 * Metodo chamado para cada linha que entra no step
	 */
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {

		TurtleGeneratorStepMeta meta = (TurtleGeneratorStepMeta) smi;
		TurtleGeneratorStepData data = (TurtleGeneratorStepData) sdi;

		// Obtem linha do fluxo de entrada e termina caso nao haja mais entrada
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

			// Prefixos
			String prefix = meta.getPrefixes().toString();
			prefix = prefix.replace("[", "");
			prefix = prefix.replace(", ", " <");
			prefix = prefix.replace("]", ">. ");
			prefix = prefix.replace("<@", "@");
			prefix = prefix.replace(">. >.", ">.");
			prefix = prefix.replace(" <>.  ", "");
			prefix = prefix.replace(".  ", "." + System.getProperty("line.separator"));
			
			putOutRow(row, meta, data, prefix);
			putOutRow(row, meta, data, "");
			putOutRow(row, meta, data, "#");
			putOutRow(row, meta, data, "# PROPERTIES DEFINITION");
			putOutRow(row, meta, data, "#");
			putOutRow(row, meta, data, "");
			
			DataTable<String> table = meta.getMapTable();
			for (int i = 0; i < table.size(); i++) {
				String dimensionField = table.getValue(i,
						TurtleGeneratorStepMeta.Field.MAP_TABLE_DIMENSIONS_FIELD_NAME.name());
				String labelDimensao = table.getValue(i, TurtleGeneratorStepMeta.Field.MAP_TABLE_LABELS_FIELD_NAME.name());
				String dimensionURIType = table.getValue(i,
						TurtleGeneratorStepMeta.Field.MAP_TABLE_URI_TYPE_FIELD_NAME.name());
				if (dimensionField != null) {
					putOutRow(row, meta, data, "exProp:" + removeSignals(dimensionField).toLowerCase() + " owl:sameAs <" + dimensionURIType + ">;");
					putOutRow(row, meta, data, "	rdfs:label \"" + labelDimensao + "\"@en .");
					putOutRow(row, meta, data, "");
				}
			}

			if (TurtleGeneratorStepMeta.Field.MAP_TABLE_HIERARCHY_FIELD_NAME.name() != null) {

				putOutRow(row, meta, data, "#");
				putOutRow(row, meta, data, "# HIERARCHIES");
				putOutRow(row, meta, data, "#");
				putOutRow(row, meta, data, "");
								
				table = meta.getMapTable2();
				for (int i = 0; i < table.size(); i++) {
					String hierarquia = table.getValue(i, TurtleGeneratorStepMeta.Field.MAP_TABLE_HIERARCHY_FIELD_NAME.name());
					String hierarquiaDe = table.getValue(i,
							TurtleGeneratorStepMeta.Field.MAP_TABLE_HIERARCHY_DE_FIELD_NAME.name());
					String hierarquiaLabel = table.getValue(i,
							TurtleGeneratorStepMeta.Field.MAP_TABLE_HIERARCHY_LABEL_FIELD_NAME.name());
					String hierarquiaPara = table.getValue(i,
							TurtleGeneratorStepMeta.Field.MAP_TABLE_HIERARCHY_PARA_FIELD_NAME.name());
					if (hierarquia != null && hierarquiaDe != null && hierarquiaPara != null) {
						putOutRow(row, meta, data, "ex:" + removeSignals(hierarquiaDe).toLowerCase() + " a ex:"
								+ removeSignals(hierarquia).toLowerCase() + " ;");
						putOutRow(row, meta, data, "	obo:part_of ex:" + removeSignals(hierarquiaPara) + ";");
						putOutRow(row, meta, data, "	rdfs:label \"" + hierarquiaLabel + "\"@en .");
						putOutRow(row, meta, data, "");
					}
				}
			}
		}

		DataTable<String> table = meta.getMapTable();
		String line = "";

		for (int i = 0; i < table.size(); i++) {
			String dimensionField = table.getValue(i, TurtleGeneratorStepMeta.Field.MAP_TABLE_DIMENSIONS_FIELD_NAME.name());
			String dimension = getInputRowMeta().getString(row, dimensionField, "");
			if (!dimension.startsWith("http")) {
				if (i == 0) {
					line = " exProp:" + removeSignals(dimensionField).toLowerCase() + " " + "\"" + dimension + "\";";
				} else
					line = "	exProp:" + removeSignals(dimensionField).toLowerCase() + " " + "\"" + dimension + "\";";
			} else {
				line = "	exProp:" + removeSignals(dimensionField).toLowerCase() + " " + "<" + dimension + ">;";
			}
			if (i == 0) {
				line = "ex:obj" + j + line;
			}
			putOutRow(row, meta, data, line);
		}

		putOutRow(row, meta, data, "	rdfs:label \"" + meta.getunity() + "\" .");
		putOutRow(row, meta, data, "");

		j++;

		return true;
	}
	
	private void putOutRow(Object[] inputRow, TurtleGeneratorStepMeta meta, TurtleGeneratorStepData data, String turtle)
			throws KettleStepException {
		
		Object[] outputRow = null;

		outputRow = new Object[0];

		outputRow = RowDataUtil.addValueData(outputRow, outputRow.length, turtle);
		// Coloca linha no fluxo
		putRow(data.outputRowMeta, outputRow);
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
