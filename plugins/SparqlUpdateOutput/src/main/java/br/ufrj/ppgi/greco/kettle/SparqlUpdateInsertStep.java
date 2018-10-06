package br.ufrj.ppgi.greco.kettle;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import br.ufrj.ppgi.greco.kettle.sparqlupdate.Response;
import br.ufrj.ppgi.greco.kettle.sparqlupdate.SparqlUpdate;

public class SparqlUpdateInsertStep extends BaseStep implements StepInterface {

	private int MAX_TRIPLES_COUNT = 1000;

	public SparqlUpdateInsertStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {

		SparqlUpdateInsertStepMeta meta = (SparqlUpdateInsertStepMeta) smi;
		SparqlUpdateInsertStepData data = (SparqlUpdateInsertStepData) sdi;

		Object[] row = getRow();

		if (row == null) {
			try {
				// Insere triplas restantes no buffer
				insertTripleList(meta, data, 0, data.tripleList.size());
			} catch (Exception e) {
				throw new KettleException("Não pôde inserir triplas.", e);
			} finally {
				// Limpa buffer
				if (data.tripleList != null) data.tripleList.clear();
			}

			setOutputDone();
			return false;
		}

		if (first) { // Executa apenas uma vez, first eh definido na superclasse
			first = false;

			// Valida URIs do grafo e do endpoint
			try {
				data.graphUri = new String(meta.getGraphUriValue().getBytes("UTF-8"));
				URI.create(data.graphUri);
				URI.create(meta.getEndpointUrl());
			} catch (Exception e) {
				throw new KettleException(e);
			}

			// Cria objeto Sparql Update
			try {
				URI uri = new URI(meta.getEndpointUrl());
				String username = meta.getUsername();
				String password = meta.getPassword();

				data.sparqlUpdate = new SparqlUpdate(uri, username, password);

			} catch (URISyntaxException e) {
				throw new KettleException(e);
			}

			RowMetaInterface rowMeta = getInputRowMeta(); // chamar apenas apos
															// chamar getRow()
			data.inputRowSize = rowMeta.size();

			data.outputRowMeta = rowMeta.clone();
			meta.getFields(data.outputRowMeta, getStepname(), null, null, this);

			data.stmtValidator = new StatementValidor();
			data.tripleList = new ArrayList<Object[]>(MAX_TRIPLES_COUNT);

			// Limpa grafo se necessario
			if (meta.getClearGraph()) {
				try {
					@SuppressWarnings("unused")
					int deleteResult = data.sparqlUpdate.deleteGraph(data.graphUri);
					int createResult = data.sparqlUpdate.createGraph(data.graphUri);

					if (createResult != 200) {
						throw new KettleException("Não foi possível criar grafo: " + data.graphUri);
					}
				} catch (Exception e) {
					throw new KettleException("Não foi possível recriar grafo.", e);
				}
			}
		}

		// Adiciona mais uma linha do fluxo no buffer
		data.tripleList.add(row);

		// Buffer cheio
		if (data.tripleList.size() == MAX_TRIPLES_COUNT) {
			try {
				// Insere bloco de triplas (pode quebrar em varios blocos caso
				// necessario)
				insertTripleList(meta, data, 0, data.tripleList.size());
			} catch (Exception e) {
				throw new KettleException("Não pôde inserir triplas.", e);
			} finally {
				// Limpa buffer
				data.tripleList.clear();
			}
		}

		return true;
	}

	private void insertTripleList(SparqlUpdateInsertStepMeta meta, SparqlUpdateInsertStepData data, int begin, int end)
			throws KettleStepException {
		if (end < begin)
			throw new Error("BIZARRO! begin > end"); // apenas teste!

		if (begin >= end)
			return; // Garante que ha pelo menos 1 tripla na iteracao

		HttpResponse response = null;
		try {
			String triples = serializeTriples(meta, data, begin, end);
			response = data.sparqlUpdate.insertNTriples(data.graphUri, triples);

			if (response.getStatusLine().getStatusCode() == 200) { // Inseriu
																	// com
																	// sucesso
				for (int i = begin; i < end; i++) {
					Object[] row = data.tripleList.get(i);
					putResultRow(data, response, row); // Set output row
				}
			} else { // Bloco falhou
				throw new Exception();
			}
		} catch (Exception e) {

			if (end == begin + 1) { // tripla invalida
				if (response != null) {
					logHttpResponse(response);
					putResultRow(data, response, data.tripleList.get(begin));
				} else {
					logException(e);
					putResultRow(data, new Response(400, "Bad Request"), data.tripleList.get(begin));
				}
			} else {
				// Divide em 2 blocos e tenta inserir cada um separadamente
				int middle = (begin + end + 1) / 2;
				insertTripleList(meta, data, begin, middle);
				insertTripleList(meta, data, middle, end);
			}
		}
	}

	private String serializeTriples(SparqlUpdateInsertStepMeta meta, SparqlUpdateInsertStepData data, int begin,
			int end) throws TripleSerializationException, KettleValueException {
		StringBuilder sb = new StringBuilder();

		RowMetaInterface rowMeta = getInputRowMeta();

		for (int i = begin; i < end; i++) {

			// Obtem linha do buffer
			Object[] row = data.tripleList.get(i);

			// Pega tripla do buffer
			String ntriple = rowMeta.getString(row, meta.getRdfContentFieldName(), "");

			// Parseia, valida e carrega tripla
			String triple = data.stmtValidator.parseAndGetTriple(ntriple);
			if (triple == null)
				throw new TripleSerializationException("Tripla inválida: " + ntriple);

			sb.append(triple);
			sb.append('\n');
		}

		return sb.toString();
	}

	private void putResultRow(SparqlUpdateInsertStepData data, Response response, Object[] row)
			throws KettleStepException {
		putResultRow(data, response.getStatusCode(), response.getStatusMessageString(), row);
	}

	private void putResultRow(SparqlUpdateInsertStepData data, HttpResponse response, Object[] row)
			throws KettleStepException {
		putResultRow(data, response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase(), row);
	}

	private void putResultRow(SparqlUpdateInsertStepData data, long code, String msg, Object[] row)
			throws KettleStepException {
		Object[] outputRow = row;

		outputRow = RowDataUtil.addValueData(outputRow, data.inputRowSize + 0, new Long(code));
		outputRow = RowDataUtil.addValueData(outputRow, data.inputRowSize + 1, msg);

		putRow(data.outputRowMeta, outputRow);
	}

	private void logException(Exception e) {
		log.logBasic("Erro ao tentar inserir bloco:\n");
		log.logBasic(e.toString());
	}

	private void logHttpResponse(HttpResponse result) {
		StatusLine sLine = result.getStatusLine();

		log.logBasic("Erro ao tentar inserir bloco: " + sLine.getStatusCode() + " " + sLine.getReasonPhrase());

		HeaderIterator hi = result.headerIterator();
		while (hi.hasNext()) {
			Header h = hi.nextHeader();
			log.logBasic("   " + h.getName() + ": " + h.getValue());
		}

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(result.getEntity().getContent()));
			String line;
			while ((line = br.readLine()) != null) {
				log.logBasic(line);
			}
		} catch (Exception e) {
		}
	}
}
