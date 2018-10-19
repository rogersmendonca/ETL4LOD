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
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.SimpleSelector;

/**
 * Adaptacoes: <br />
 * No output, em vez de passar campos separados, passar: <br />
 * (i) um objeto Graph (SELECT, DESCRIBE, CONSTRUCT) ou <br />
 * (ii) um objeto Boolean (ASK). <br />
 * 
 * @author rogers
 * 
 *         Change: Step grain changed to subjectItem (resource and its
 *         properties) instead of the resultRDF Graph result from CONSTRUCT
 *         sparql command
 * 
 * @author Kelli
 */
public class GraphSparqlStep extends BaseStep implements StepInterface {

	private static int MAX_ATTEMPTS = 4;

	public GraphSparqlStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
			Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	/**
	 * Metodo chamado para cada linha que entra no step.
	 */
	// Rogers(Nov/2012): Correcao de bug na ordenacao dos campos da consulta
	// SPARQL
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
		GraphSparqlStepMeta meta = (GraphSparqlStepMeta) smi;
		GraphSparqlStepData data = (GraphSparqlStepData) sdi;

		// Obtem linha do fluxo de entrada
		final Object[] row = getRow();

		if (first) {
			// Executa apenas uma vez. Variavel first definida na superclasse
			first = false;

			// Obtem todas as colunas ate o step anterior.
			// Chamar apenas apos chamar getRow()
			RowMetaInterface rowMeta = getInputRowMeta(row != null);
			data.outputRowMeta = rowMeta.clone();

			// Adiciona os metadados do step atual
			meta.getFields(data.outputRowMeta, getStepname(), null, null, this);

			data.inputRowSize = rowMeta.size();

			// Obtem string de consulta e constroi o objeto consulta
			String queryStr = GraphSparqlStepUtils.toFullQueryString(meta.getPrefixes(), meta.getQueryString());
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

			while (data.remainingTries > 0) { // Tenta executar este bloco ate'
												// MAX_ATTEMPTS vezes
				try {
					int numRows = runQueryAndPutResults(query, meta, data, row);

					if (numRows > 0) { // Este bloco de consulta rodou
						data.offset += data.limit;
						data.remainingTries = MAX_ATTEMPTS;

						return true;
					} else { // Nao ha mais resultados, ie, processRow() nao
								// sera'
								// chamado novamente
						setOutputDone();
						return false;
					}
				} catch (Throwable e) {
					handleError(e, MAX_ATTEMPTS - data.remainingTries + 1);
				}

				data.remainingTries--;
			}
		}

		// Nao funfou!
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

	// Rogers(Nov/2012): Correcao de bug na ordenacao dos campos da consulta
	// SPARQL
	private int runQueryAndPutResults(Query query, GraphSparqlStepMeta meta, GraphSparqlStepData data, Object[] row)
			throws KettleStepException {
		int numPutRows = 0;
		QueryExecution qexec = GraphSparqlStepUtils.createQueryExecution(query, meta.getEndpointUri(),
				meta.getDefaultGraph());

		try {
			Model model = null;
			switch (query.getQueryType()) {
			case Query.QueryTypeAsk:
				Boolean result = qexec.execAsk();
				incrementLinesInput();
				putRow(data.outputRowMeta, RowDataUtil.addValueData(row, data.inputRowSize, result));
				break;

			case Query.QueryTypeConstruct:
				model = qexec.execConstruct();
				ResIterator resourceSet = model.listSubjects();
				int count = 0;
				while (resourceSet.hasNext()) {
					Resource resource = resourceSet.nextResource();
					// gets a subgraph
					Model subjectItemGraph = createSubjectItemGraph(resource, model);

					// send a subGraph to the next step
					incrementLinesInput();
					putRow(data.outputRowMeta, RowDataUtil.addValueData(row, data.inputRowSize, subjectItemGraph));
					count++;
				}
				if (count == 0) {
					incrementLinesInput();
					putRow(data.outputRowMeta, RowDataUtil.addValueData(row, data.inputRowSize, model));
				}
				break;

			case Query.QueryTypeDescribe:
				model = qexec.execDescribe();
				ResIterator resourceSetD = model.listSubjects();
				int countD = 0;
				while (resourceSetD.hasNext()) {
					Resource resource = resourceSetD.nextResource();
					// gets a subgraph
					Model subjectItemGraph = createSubjectItemGraph(resource, model);

					// send a subGraph to the next step
					incrementLinesInput();
					putRow(data.outputRowMeta, RowDataUtil.addValueData(row, data.inputRowSize, subjectItemGraph));
					countD++;
				}
				if (countD == 0) {
					incrementLinesInput();
					putRow(data.outputRowMeta, RowDataUtil.addValueData(row, data.inputRowSize, model));
				}

				// incrementLinesInput();
				// putRow(data.outputRowMeta, RowDataUtil.addValueData(row,
				// data.inputRowSize, model));
				break;

			case Query.QueryTypeSelect:
				ResultSet resultSet = qexec.execSelect();
				model = resultSet.getResourceModel();

				Object extra = (model != null) ? model : resultSet;
				incrementLinesInput();
				putRow(data.outputRowMeta, RowDataUtil.addValueData(row, data.inputRowSize, extra));
				break;
			}
		} finally {
			qexec.close();
		}

		return numPutRows;
	}

	// Creates a subGraph with a Resource and its Properties
	private Model createSubjectItemGraph(Resource resource, Model model) {
		Model subjectItemGraph = null;
		Selector s = new SimpleSelector(resource, (Property) null, (RDFNode) null);
		subjectItemGraph = model.query(s);
		// StmtIterator i = model.listStatements(s);

		return subjectItemGraph;
	}
}