package br.ufrj.ppgi.greco.kettle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

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

import org.apache.jena.query.ARQ;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryException;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

public class SparqlStep extends BaseStep implements StepInterface {

	private static int MAX_ATTEMPTS = 4;

	public SparqlStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
			Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
		SparqlStepMeta meta = (SparqlStepMeta) smi;
		SparqlStepData data = (SparqlStepData) sdi;

		final Object[] row = getRow();

		if (first) {
			first = false;

			RowMetaInterface rowMeta = getInputRowMeta(row != null);
			data.outputRowMeta = rowMeta.clone();

			// Adiciona os metadados do step atual
			meta.getFields(data.outputRowMeta, getStepname(), null, null, this);

			data.inputRowSize = rowMeta.size();

			// Obtem string de consulta e constroi o objeto consulta
			String queryStr = SparqlStepUtils.toFullQueryString(meta.getPrefixes(), meta.getQueryString());
			try {
				data.originalQuery = QueryFactory.create(queryStr);
			} catch (QueryException e) {
				// Se consulta for invalida nao pode continuar
				throw new KettleException(e);
			}

			// Se nao usar SAX o execSelect() nao funciona
			ARQ.set(ARQ.useSAX, true);

			// Offset e Limit para Construct/select/describe quando limit nao
			// especificado
			if (!data.originalQuery.hasLimit() && (data.originalQuery.getQueryType() != Query.QueryTypeAsk)
					&& (data.originalQuery.getQueryType() != Query.QueryTypeDescribe)) {
				// Consulta eh quebrada em varias usando OFFSET e LIMIT
				data.offset = data.originalQuery.hasOffset() ? data.originalQuery.getOffset() : 0;
				data.limit = 1000;
				data.runAtOnce = false;
			} else {
				data.runAtOnce = true;
			}

			data.remainingTries = MAX_ATTEMPTS;

			return true;
		}

		Query query = null;
		if (data.runAtOnce) {
			// Roda consulta num unico HTTP Request
			query = data.originalQuery;

			while (data.remainingTries > 0) {
				// Tenta executar consulta ate MAX_ATTEMPTS vezes
				try {
					runQueryAndPutResults(query, meta, data, row);

					setOutputDone();
					return false; // Nao ha mais resultados, ie, processRow()
									// nao sera' chamado novamente
				} catch (Throwable e) {
					handleError(e, MAX_ATTEMPTS - data.remainingTries + 1);
				}

				data.remainingTries--;
			}
		} else {
			// Cria consulta que representa o bloco atual
			query = data.originalQuery.cloneQuery();
			query.setOffset(data.offset);
			query.setLimit(data.limit);

			while (data.remainingTries > 0) {
				try {
					int numRows = runQueryAndPutResults(query, meta, data, row);

					if (numRows > 0) {
						data.offset += data.limit;
						data.remainingTries = MAX_ATTEMPTS;
						return true;
					} else { 
						setOutputDone();
						return false;
					}
				} catch (Throwable e) {
					handleError(e, MAX_ATTEMPTS - data.remainingTries + 1);
				}

				data.remainingTries--;
			}
		}

		StringBuilder sb = new StringBuilder();
		sb.append("Todas as tentativas de executar a consulta falharam. ");
		sb.append("Verifique conexão de rede e o SPARQL Endpoint.\n");
		sb.append("Endpoint: ");
		sb.append(meta.getEndpointUri());
		sb.append('\n');
		sb.append("Grafo padrão: ");
		sb.append(meta.getDefaultGraph());
		sb.append('\n');
		sb.append("Consulta:\n");
		sb.append(query.toString());
		sb.append('\n');

		throw new KettleException(sb.toString());
	}

	private RowMetaInterface getInputRowMeta(boolean hasInputRow) {

		RowMetaInterface rowMeta = null;
		if (hasInputRow)
			rowMeta = getInputRowMeta();
		else
			rowMeta = new RowMeta();

		return rowMeta;
	}

	private void handleError(Throwable e, int attempts) {

		try {
			String msg = String.format("Falha ao executar consulta (tentativa %d de %d): ", attempts, MAX_ATTEMPTS);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(msg.getBytes());

			e.printStackTrace(new PrintWriter(baos, true));

			long sleepTime = (long) (500 * Math.pow(2, attempts));
			msg = String.format("Tentando novamente em %d milissegundos...", sleepTime);
			baos.write(msg.getBytes());

			log.logBasic(baos.toString());

			Thread.sleep(sleepTime);

		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}
	}

	private int tripleWriter(Model model, Object[] row, SparqlStepData data) throws KettleStepException {
		int numPutRows = 0;
		StmtIterator it = model.listStatements();

		while (it.hasNext()) {
			Statement stmt = it.next();

			incrementLinesInput();

			String subject = stmt.getSubject().toString();
			String predicate = stmt.getPredicate().toString();
			String object = stmt.getObject().toString();

			if (subject != null && !subject.isEmpty() && predicate != null && !predicate.isEmpty() && object != null) {
				// Monta linha com a tripla
				Object[] outputRow = row;
				outputRow = RowDataUtil.addValueData(outputRow, data.inputRowSize + 0, subject);
				outputRow = RowDataUtil.addValueData(outputRow, data.inputRowSize + 1, predicate);
				outputRow = RowDataUtil.addValueData(outputRow, data.inputRowSize + 2, object);

				// Joga tripla no fluxo
				putRow(data.outputRowMeta, outputRow);

				numPutRows++;
			} else
				logBasic("Tripla ignorada: " + stmt.getString());
		}

		return numPutRows;
	}

	private int runQueryAndPutResults(Query query, SparqlStepMeta meta, SparqlStepData data, Object[] row)
			throws KettleStepException {
		int numPutRows = 0;
		Model model = null;
		QueryExecution qexec = SparqlStepUtils.createQueryExecution(query, meta.getEndpointUri(),
				meta.getDefaultGraph());

		try {
			switch (query.getQueryType()) {
			case Query.QueryTypeAsk:
				boolean result = qexec.execAsk();
				incrementLinesInput();
				putRow(data.outputRowMeta, RowDataUtil.addValueData(row, data.inputRowSize, result));
				break;

			case Query.QueryTypeConstruct:
				model = qexec.execConstruct();
				numPutRows = tripleWriter(model, row, data);
				break;

			case Query.QueryTypeDescribe:
				model = qexec.execDescribe();
				numPutRows = tripleWriter(model, row, data);
				break;

			case Query.QueryTypeSelect:
				ResultSet resultSet = qexec.execSelect();
				// Gera linhas
				while (resultSet.hasNext()) {
					QuerySolution qs = resultSet.next();

					// Diz ao Kettle que leu mais uma linha da entrada
					incrementLinesInput();

					// Gera uma linha de saida do fluxo
					Object[] outputRow = row;
					String[] fieldNames = data.outputRowMeta.getFieldNames();
					int posAddValueData = (outputRow != null) ? outputRow.length : 0;
					for (int i = 0; i < fieldNames.length; i++) {
						// Retira o prefixo para obter o nome do campo da
						// consulta SPARQL
						String var = fieldNames[i].replaceAll(meta.getVarPrefix(), "");

						// Obtem o node RDF
						RDFNode node = qs.get(var);

						// Obtem o valor do node RDF
						String value = null;
						if (node instanceof Literal) {
							value = qs.getLiteral(var).getString();
						} else {
							value = qs.getResource(var).getURI();
						}

						// Set output row
						outputRow = RowDataUtil.addValueData(outputRow, posAddValueData++, value);
					}

					putRow(data.outputRowMeta, outputRow);
					numPutRows++;
				}
				break;
			}
		} finally {
			qexec.close();
		}

		return numPutRows;
	}
}