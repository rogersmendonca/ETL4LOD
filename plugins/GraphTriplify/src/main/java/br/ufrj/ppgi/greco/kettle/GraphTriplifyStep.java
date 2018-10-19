package br.ufrj.ppgi.greco.kettle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

/**
 * Step GraphTriplify.
 * <p />
 * 
 * @author Rogers Reiche de Mendonca
 * 
 */
public class GraphTriplifyStep extends BaseStep implements StepInterface {
	public GraphTriplifyStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
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
	 * Metodo chamado para cada linha que entra no step
	 */
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
		GraphTriplifyStepMeta meta = (GraphTriplifyStepMeta) smi;
		GraphTriplifyStepData data = (GraphTriplifyStepData) sdi;

		// Obtem linha do fluxo de entrada e termina caso nao haja mais entrada
		Object[] row = getRow();
		if (row == null) { // Nao ha mais linhas de dados
			setOutputDone();
			return false;
		}

		// Executa apenas uma vez. Variavel first definida na superclasse com
		// valor true
		if (first) {
			first = false;

			// Obtem todas as colunas ateh o step anterior.
			// Chamar apenas apos chamar getRow()
			data.outputRowMeta = new RowMeta();

			// Adiciona os metadados do step atual
			meta.getFields(data.outputRowMeta, getStepname(), null, null, this);
		}

		// Logica do step
		// Leitura de campos Input
		RowMetaInterface rowMeta = getInputRowMeta();
		int indexGraph = rowMeta.indexOfValue(meta.getInputGraph());
		Object graph = (indexGraph >= 0) ? row[indexGraph] : null;

		// Set output row
		Method[] methods = graph.getClass().getMethods();
		boolean hasListStatements = false;
		for (Method method : methods) {
			if (method.getName().equals("listStatements")) {
				hasListStatements = true;
				break;
			}
		}

		if (hasListStatements) {
			tripleWriter(graph, null, data);
		}

		return true;
	}

	private int tripleWriter(Object model, Object[] row, GraphTriplifyStepData data) throws KettleStepException {
		int numPutRows = 0;

		// StmtIterator it = model.listStatements();
		// Testando usar reflexion
		Object it = null;
		try {
			it = model.getClass().getMethod("listStatements").invoke(model);
			if (it == null)
				return 0;

			// while (it.hasNext())
			while ((Boolean) it.getClass().getMethod("hasNext").invoke(it)) {
				// Statement stmt = it.next();
				Object stmt = it.getClass().getMethod("next").invoke(it);

				incrementLinesInput();

				// String subject = stmt.getSubject().toString();
				String subject = stmt.getClass().getMethod("getSubject").invoke(stmt).toString();
				// String predicate = stmt.getPredicate().toString();
				String predicate = stmt.getClass().getMethod("getPredicate").invoke(stmt).toString();
				// String object = stmt.getObject().toString();
				String object = stmt.getClass().getMethod("getObject").invoke(stmt).toString();

				if (subject != null && !subject.isEmpty() && predicate != null && !predicate.isEmpty()
						&& object != null) {
					// Monta linha com a tripla
					Object[] outputRow = row;
					int i = 0;
					outputRow = RowDataUtil.addValueData(outputRow, i++, subject);
					outputRow = RowDataUtil.addValueData(outputRow, i++, predicate);
					outputRow = RowDataUtil.addValueData(outputRow, i++, object);

					// Joga tripla no fluxo
					putRow(data.outputRowMeta, outputRow);

					numPutRows++;
				} else {
					String triplaIgnorada = stmt.getClass().getMethod("getString").invoke(stmt).toString();
					logBasic("Tripla ignorada: " + triplaIgnorada);
				}
			}
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return numPutRows;
	}
}
