package br.ufrj.ppgi.greco.lodbr.plugin.sparql;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;

/**
 * Contem metodos comuns utilizados pelas classes do plugin Sparql Endpoint
 * 
 * @author Expedito
 */
public class SparqlStepUtils {

	/**
	 * Converte uma lista de prefixos para uma String
	 * 
	 * @param prefixes lista de prefixos
	 * @return string representando a lista de prefixos na consulta SPARQL 
	 */
	public static String toPrefixesString(List<List<String>> prefixes) {
		StringBuilder sb = new StringBuilder();
		for (List<String> row : prefixes) {
			
			String prefix = row.get(0);
			String namespace = row.get(1);
			
			if (prefix != null && namespace != null
					&& !prefix.equals("") && !namespace.equals("")) {
				sb.append("PREFIX ");
				sb.append(row.get(0));
				sb.append(":<");
				sb.append(row.get(1));
				sb.append(">\n");
			}
		}
		return sb.toString();
	}
	
	
	/**
	 * Gera uma consulta completa a partir de uma lista de prefixos e o restante da consulta
	 * @param prefixes lista de prefixos
	 * @param queryString string de consulta fornecida pelo usuario
	 * @return consulta completa
	 */
	public static String toFullQueryString(List<List<String>> prefixes, String queryString) {
		return toPrefixesString(prefixes) + "\n" + queryString;
	}

	
	
	/**
	 * Valida consulta Sparql, retorna mensagem de erro se houver
	 * @param queryString consulta a ser validada
	 * @return mensagem de erro ou de consulta valida
	 */
	public static String validateSparql(String queryString) {
		try {
			Query query = QueryFactory.create(queryString);
			query.validate();
			return "Query is valid.";
		}
		catch (Throwable e) {
			return e.getMessage();
		}
	}
	
	
	/**
	 * Parseia uma consulta SPARQL e retorna uma lista descrevendo
	 * os metadados das variaveis de saida
	 *  
	 * @param varPrefix string contendo o prefixo das variaveis de retorno
	 * @param queryString string contendo a string de consulta (completa, ie, com prefixos)
	 * @return
	 */
	public static List<ValueMetaInterface> generateOutputVars(String varPrefix, String queryString) {
		try {
			if (varPrefix == null) varPrefix = "";
			
			ArrayList<ValueMetaInterface> outVars = new ArrayList<ValueMetaInterface>();
			
			Query query = QueryFactory.create(queryString);
			
			switch (query.getQueryType()) {
			case Query.QueryTypeAsk:
				outVars.add(new ValueMeta(varPrefix + "ask", ValueMetaInterface.TYPE_BOOLEAN));
				break;

			case Query.QueryTypeConstruct:
			case Query.QueryTypeDescribe:
				outVars.add(new ValueMeta(varPrefix + "subject", ValueMetaInterface.TYPE_STRING));
				outVars.add(new ValueMeta(varPrefix + "predicate", ValueMetaInterface.TYPE_STRING));
				outVars.add(new ValueMeta(varPrefix + "object", ValueMetaInterface.TYPE_STRING));
				break;
				
			case Query.QueryTypeSelect:
				List<String> vars = query.getResultVars();
				for (String s : vars) {
					outVars.add(new ValueMeta(varPrefix + s, ValueMetaInterface.TYPE_STRING));
				}				
				break;
			}

			return outVars;
		}
		catch (Throwable e) {
			return null;
		}
	}
	
	
	/**
	 * Cria um objeto QueryExecution 
	 * 
	 * @param query
	 * @param endpointUri
	 * @param defaultGraph
	 * @return
	 */
	public static QueryExecution createQueryExecution(Query query, String endpointUri, String defaultGraph) {
		QueryExecution qexec = null;
		
		// Se nao verificar o Default Graph ocorre uma Exception ao executar
		if (defaultGraph == null || defaultGraph.equals(""))
			qexec = QueryExecutionFactory.sparqlService(endpointUri, query);
		else
			qexec = QueryExecutionFactory.sparqlService(endpointUri, query, defaultGraph);

		return qexec;
	}


	
	/////////////////
	//    DEBUG    //
	/////////////////
	public static String runGC()	 {
		Runtime rt = Runtime.getRuntime();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("Before: ") ;
		sb.append(rt.totalMemory() - rt.freeMemory());
		
		rt.gc();

		sb.append("\nAfter: ") ;
		sb.append(rt.totalMemory() - rt.freeMemory());

		return sb.toString();
	}
	
	
//	private static Object [] getInputStreamBoladao(InputStream in) throws IOException {
//		int c;
//		StringBuilder sb = new StringBuilder();
//		while ( (c= in.read()) != -1) {
//		  sb.append((char)c);
//		}
//
//		final String s = sb.toString();
//
//		in = new InputStream() {
//			
//			int pos = 0;
//			byte [] bytes = s.getBytes();
//			java.io.FileWriter fw = new FileWriter("./InputStreamBoladao.log");
//
//			@Override
//			public int read() throws IOException {
//
//				if (pos == bytes.length) {
//					fw.close();
//					return -1;
//				}
//				
//				fw.write((char)bytes[pos]);
//				fw.flush();
//				
//				return bytes[pos++];
//			}
//		};
//
//		return new Object[] { in, s} ;
//	}
}
