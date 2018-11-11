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

public class ObjectPropertyMappingStep extends BaseStep implements StepInterface {

	public ObjectPropertyMappingStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
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

		ObjectPropertyMappingStepMeta meta = (ObjectPropertyMappingStepMeta) smi;
		ObjectPropertyMappingStepData data = (ObjectPropertyMappingStepData) sdi;

		// Obtem linha do fluxo de entrada e termina caso nao haja mais entrada
		Object[] row = getRow();
		if (row == null) { // N�o h� mais linhas de dados
			setOutputDone();
			return false;
		}

		if (first) { // Executa apenas uma vez. Variavel first definida na
						// superclasse
			first = false;

			// Obtem todas as colunas at� o step anterior.
			// Chamar apenas apos chamar getRow()
			RowMetaInterface rowMeta = getInputRowMeta();
			data.outputRowMeta = rowMeta.clone();

			// Adiciona os metadados do step atual
			meta.getFields(data.outputRowMeta, getStepname(), null, null, this);

			// TODO Outras operacoes que devem ser executadas apenas uma vez
		}

		/*
		 * Logica do step: leitura de campos de entrada e internos e geracao do
		 * campo de saida
		 */

		// Add data properties
		DataTable<String> table = meta.getMapTable();
		for (int i = 0; i < table.size(); i++) {

			String subjectField = table.getValue(i,
					ObjectPropertyMappingStepMeta.Field.MAP_TABLE_SUBJECT_FIELD_NAME.name());
			String subject = getInputRowMeta().getString(row, subjectField, "");

			String predicateField = table.getValue(i, ObjectPropertyMappingStepMeta.Field.MAP_TABLE_PREDICATE_URI.name());
			String predicate = getInputRowMeta().getString(row, predicateField, predicateField);

			String objectField = table.getValue(i,
					ObjectPropertyMappingStepMeta.Field.MAP_TABLE_OBJECT_FIELD_NAME.name());
			String object = getInputRowMeta().getString(row, objectField, "");

			if (!"".equals(predicate) && !"".equals(object)) {
				putOutRow(row, meta, data, subject, predicate, object);
			}
		}

		return true;
	}

	/*
	 *  
	 */
	private void putOutRow(Object[] inputRow, ObjectPropertyMappingStepMeta meta, ObjectPropertyMappingStepData data,
			String subject, String predicate, String object) throws KettleStepException {

		int outputRowPos = 0;
		Object[] outputRow = null;

		// Determina se deve repassar campos de entrada
		if (meta.isKeepInputFields()) {
			outputRow = inputRow;
			outputRowPos = getInputRowMeta().size();
		} else {
			outputRow = new Object[3];
		}

		outputRow = RowDataUtil.addValueData(outputRow, outputRowPos++, subject);
		outputRow = RowDataUtil.addValueData(outputRow, outputRowPos++, predicate);
		outputRow = RowDataUtil.addValueData(outputRow, outputRowPos++, object);

		// Coloca linha no fluxo
		putRow(data.outputRowMeta, outputRow);
	}

}
