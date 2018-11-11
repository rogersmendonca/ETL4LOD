package br.ufrj.ppgi.greco.kettle;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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

public class DataPropertyMappingStep extends BaseStep implements StepInterface {

	public DataPropertyMappingStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		if (super.init(smi, sdi)) {
			// TODO init something here if needed
			// ...
			return true;
		} else
			return false;
	}

	@Override
	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		super.dispose(smi, sdi);

		// TODO finalize something here if needed
		// ...
	}

	/**
	 * Metodo chamado para cada linha que entra no step
	 */
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {

		DataPropertyMappingStepMeta meta = (DataPropertyMappingStepMeta) smi;
		DataPropertyMappingStepData data = (DataPropertyMappingStepData) sdi;

		// Obtem linha do fluxo de entrada e termina caso nao haja mais entrada
		Object[] row = getRow();
		if (row == null) { // Nao ha mais linhas de dados
			setOutputDone();
			return false;
		}

		if (first) { // Executa apenas uma vez. Variavel first definida na
						// superclasse
			first = false;

			// Obtem todas as colunas ate o step anterior.
			// Chamar apenas apos chamar getRow()
			RowMetaInterface rowMeta = getInputRowMeta();
			data.outputRowMeta = rowMeta.clone();

			// Adiciona os metadados do step atual
			meta.getFields(data.outputRowMeta, getStepname(), null, null, this);

			// TODO Outras opera��es que devem ser executadas apenas uma vez
		}

		/*
		 * Logica do step: leitura de campos de entrada e internos e geracao do
		 * campo de saida
		 */

		// Add rdf:type
		String subject = getInputRowMeta().getString(row, meta.getSubjectUriFieldName(), "");

		List<String> typesUri = meta.getRdfTypeUris();
		Iterator<String> it = typesUri.iterator();
		while (it.hasNext()) {
			String type = (String) it.next();
			putOutRow(row, meta, data, subject, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", type);
		}

		// Add data properties
		DataTable<String> table = meta.getMapTable();
		for (int i = 0; i < table.size(); i++) {

			String predicateField = table.getValue(i, DataPropertyMappingStepMeta.Field.MAP_TABLE_PREDICATE_URI.name());
			String predicate = getInputRowMeta().getString(row, predicateField, predicateField);
			
			String objectField = table.getValue(i,
					DataPropertyMappingStepMeta.Field.MAP_TABLE_OBJECT_FIELD_NAME.name());
			int index = getInputRowMeta().indexOfValue(objectField);

			String datatype = table.getValue(i, DataPropertyMappingStepMeta.Field.MAP_TABLE_TYPED_LITERAL.name());

			String langtagValue = table.getValue(i, DataPropertyMappingStepMeta.Field.MAP_TABLE_LANGUAGE_TAG.name());
			String langtagField = table.getValue(i,
					DataPropertyMappingStepMeta.Field.MAP_TABLE_LANGTAG_FIELD_NAME.name());

			String langtag = null;
			if (langtagField != null && !langtagField.isEmpty()) {
				langtag = getInputRowMeta().getString(row, getInputRowMeta().indexOfValue(langtagField));
			}
			if (langtag == null || langtag.isEmpty())
				langtag = langtagValue;

			String object = null;

			try {
				if ("xsd:float".equals(datatype) || "xsd:double".equals(datatype) || "xsd:decimal".equals(datatype)) {
					object = new DecimalFormat("0.0#########", new DecimalFormatSymbols(Locale.US))
							.format(getInputRowMeta().getNumber(row, index));
				} else if ("xsd:dateTime".equals(datatype)) {
					object = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
							.format(getInputRowMeta().getDate(row, index));
				} else if ("xsd:date".equals(datatype)) {
					object = new SimpleDateFormat("yyyy-MM-dd").format(getInputRowMeta().getDate(row, index));
				} else if ("xsd:integer".equals(datatype)) {
					object = getInputRowMeta().getInteger(row, index).toString();
				} else {
					object = getInputRowMeta().getString(row, index);
				}
			} catch (Exception e) {
				object = "";
			}

			// Rogers (Jul./2012): Quando o repositorio e' database, o valor do
			// datatype quando vazio e' null.
			if (datatype != null) {
				datatype = datatype.replace("xsd:", "http://www.w3.org/2001/XMLSchema#");
			}

			if (subject != null && predicate != null && object != null && !"".equals(subject) && !"".equals(predicate)
					&& !"".equals(object)) {
				putOutRow(row, meta, data, subject, predicate, object, datatype, langtag);
			}
		}

		return true;
	}

	private void putOutRow(Object[] inputRow, DataPropertyMappingStepMeta meta, DataPropertyMappingStepData data,
			String subject, String predicate, String object) throws KettleStepException {
		putOutRow(inputRow, meta, data, subject, predicate, object, "", "");
	}

	/*
	 * Casos tratados: keep & !addS ==> S1 : outRow = inRow + p + o keep & addS
	 * ==> S2 : outRow = inRow + s + p + o !keep & addS ==> S3 : outRow = s + p
	 * + o !keep & !addS ==> S4 : outRow = uri + p + o (uri = campo de entrada)
	 * 
	 * addS = (uri != s) && defined(s)
	 */
	private void putOutRow(Object[] inputRow, DataPropertyMappingStepMeta meta, DataPropertyMappingStepData data,
			String subject, String predicate, String object, String datatype, String langtag)
			throws KettleStepException {

		int outputRowPos = 0;
		Object[] outputRow = null;

		// Determina se deve repassar campos de entrada
		if (meta.isKeepInputFields()) {
			outputRow = inputRow;
			outputRowPos = getInputRowMeta().size(); // S1, S2
		} else {
			outputRow = new Object[5]; // S3, S4
		}

		if (meta.isThereAdditionalSubjectOutputField()) { // addS
			outputRow = RowDataUtil.addValueData(outputRow, outputRowPos++, subject); // S2,
																						// S3
		} else {
			if (!meta.isKeepInputFields()) {
				outputRow = RowDataUtil.addValueData(outputRow, outputRowPos++, subject); // S4
			}
		}

		// S1, S2, S3, S4
		outputRow = RowDataUtil.addValueData(outputRow, outputRowPos++, predicate);
		outputRow = RowDataUtil.addValueData(outputRow, outputRowPos++, object);
		outputRow = RowDataUtil.addValueData(outputRow, outputRowPos++, datatype);

		if (datatype == null || datatype.isEmpty() || "http://www.w3.org/2001/XMLSchema#string".equals(datatype)) {
			outputRow = RowDataUtil.addValueData(outputRow, outputRowPos++, langtag);
		} else {
			outputRow = RowDataUtil.addValueData(outputRow, outputRowPos++, "");
		}

		// Coloca linha no fluxo
		putRow(data.outputRowMeta, outputRow);
	}
}
