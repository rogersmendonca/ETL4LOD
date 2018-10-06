package br.ufrj.ppgi.greco.kettle.sparqlupdate;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.any23.Any23;
import org.apache.any23.source.DocumentSource;
import org.apache.any23.source.StringDocumentSource;
import org.apache.any23.writer.NTriplesWriter;
import org.apache.any23.writer.RDFXMLWriter;
import org.apache.any23.writer.TripleHandler;
import org.apache.any23.writer.TurtleWriter;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

public class SparqlUpdate {

	private String protocol;
	private String host;
	private String path;
	private int port;
	private String user;
	private String password;

	public SparqlUpdate(URI uri, String user, String password) {
		this.protocol = uri.getScheme();
		this.host = uri.getHost();
		this.port = uri.getPort();
		this.path = uri.getPath();
		this.user = user;
		this.password = password;
	}

	public SparqlUpdate(String protocol, String host, String path, int port, String user, String password) {
		this.protocol = protocol;
		this.host = host;
		this.path = path;
		this.port = port;
		this.user = user;
		this.password = password;
	}

	public int createGraph(String graphURL) throws Exception {
		int resultOperation = 0;

		CloseableHttpClient httpClient = this.createConnection();

		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("query", "CREATE GRAPH <" + graphURL + ">"));
		qparams.add(new BasicNameValuePair("format", "application/sparql-results+xml"));

		try {
			URI uri = new URIBuilder().setScheme(this.protocol).setHost(this.host).setPort(this.port).setPath(this.path)
					.setParameters(qparams).build();
			HttpGet httpget = new HttpGet(uri);
			HttpResponse response = httpClient.execute(httpget);
			resultOperation = response.getStatusLine().getStatusCode();
		} catch (Exception e) {
			printLog("SparqlUpdate", "createGraph");
			e.printStackTrace();
			throw new Exception(e);
		}

		return resultOperation;

	}

	public int deleteGraph(String graphURL) throws Exception {
		int resultOperation = 0;

		CloseableHttpClient httpClient = this.createConnection();

		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("query", "DROP GRAPH <" + graphURL + ">"));
		qparams.add(new BasicNameValuePair("format", "application/sparql-results+xml"));

		try {
			URI uri = new URIBuilder().setScheme(this.protocol).setHost(this.host).setPort(this.port).setPath(this.path)
					.setParameters(qparams).build();
			HttpGet httpget = new HttpGet(uri);
			HttpResponse resposta = httpClient.execute(httpget);
			resultOperation = resposta.getStatusLine().getStatusCode();

		} catch (Exception e) {
			printLog("SparqlUpdate", "deleteGraph");
			throw new Exception(e);
		}

		return resultOperation;
	}

	public HttpResponse insertNTriples(String graph, String triples) throws Exception {
		CloseableHttpClient httpClient = this.createConnection();

		try {
			List<NameValuePair> qParams = new ArrayList<NameValuePair>();
			String query = "INSERT INTO GRAPH <" + graph + "> {\n" + triples + "\n" + "}";
			qParams.add(new BasicNameValuePair("query", query));

			URI uri = new URIBuilder().setScheme(this.protocol).setHost(this.host).setPort(this.port).setPath(this.path)
					.build();
			HttpPost httpPost = new HttpPost(uri);
			httpPost.setEntity(new UrlEncodedFormEntity(qParams, StandardCharsets.UTF_8));

			HttpResponse resposta = httpClient.execute(httpPost);

			return resposta;

		} catch (Exception e) {
			printLog("SparqlUpdate", "insertTriples");
			e.printStackTrace();
			throw new Exception(e);
		}
	}

	public Response insertTriples(String graph, String RDFXML) throws Exception {
		Response operationResult = null;

		String n3 = this.triplify(graph, RDFXML);
		CloseableHttpClient httpClient = this.createConnection();

		try {

			List<NameValuePair> qParams = new ArrayList<NameValuePair>();
			String query = "INSERT DATA INTO <" + graph + "> {" + n3 + "}";
			qParams.add(new BasicNameValuePair("query", query));
			System.out.println(graph);

			URI uri = new URIBuilder().setScheme(this.protocol).setHost(this.host).setPort(this.port).setPath(this.path)
					.build();
			HttpPost httpPost = new HttpPost(uri);
			httpPost.setEntity(new UrlEncodedFormEntity(qParams, StandardCharsets.UTF_8));

			HttpResponse resposta = httpClient.execute(httpPost);

			operationResult = new Response(resposta.getStatusLine().getStatusCode(),
					resposta.getStatusLine().getReasonPhrase());

		} catch (Exception e) {
			printLog("SparqlUpdate", "insertTriples");
			e.printStackTrace();
			throw new Exception(e);
		}

		return operationResult;
	}

	public Response insertTriples(String graph, String[] n3) throws Exception {
		Response operationResult = null;
		try {
			StringBuffer sb = new StringBuffer();
			for (String triple : n3) {

				if (triple != null) {
					sb.append(triple);
					sb.append('\n');
				}
			}
			String triples = sb.toString();

			CloseableHttpClient httpClient = this.createConnection();
			List<NameValuePair> qParams = new ArrayList<NameValuePair>();
			String query = "INSERT DATA INTO <" + graph + "> {" + triples + "}";
			qParams.add(new BasicNameValuePair("query", query));

			URI uri = new URIBuilder().setScheme(this.protocol).setHost(this.host).setPort(this.port).setPath(this.path)
					.build();
			HttpPost httpPost = new HttpPost(uri);
			httpPost.setEntity(new UrlEncodedFormEntity(qParams, StandardCharsets.UTF_8));

			HttpResponse resposta = httpClient.execute(httpPost);

			operationResult = new Response(resposta.getStatusLine().getStatusCode(),
					resposta.getStatusLine().getReasonPhrase());

		} catch (Exception e) {
			printLog("SparqlUpdate", "insertTriples");
			e.printStackTrace();
			throw new Exception(e);
		}

		return operationResult;
	}

	public Response insertTriples(String graph, String rdfxml, int blockSize) throws Exception {
		Response operationResult = null;

		String[] n3 = this.triplify(graph, rdfxml).split("\n");

		int i = 0;
		String[] n2short = new String[blockSize];

		for (String triple : n3) {
			n2short[i] = triple;
			i++;

			if (i == blockSize) {
				operationResult = insertTriples(graph, n2short);

				i = 0;
				n2short = new String[blockSize];

				if (operationResult.getStatusCode() != 200)
					return operationResult;
			}
		}

		if (i > 0) {
			operationResult = insertTriples(graph, n2short);

			if (operationResult.getStatusCode() != 200)
				return operationResult;
		}

		n2short = null;

		return operationResult;
	}

	public int deleteTriple(String graph, String RDFXML) {
		int operationResult = 0;

		CloseableHttpClient httpClient = this.createConnection();

		String n3 = this.triplify(graph, RDFXML);

		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("query", "DELETE FROM <" + graph + ">{" + n3 + "}"));
		qparams.add(new BasicNameValuePair("format", "application/sparql-results+xml"));

		try {
			URI uri = new URIBuilder().setScheme(this.protocol).setHost(this.host).setPort(this.port).setPath(this.path)
					.setParameters(qparams).build();
			HttpGet httpget = new HttpGet(uri);
			HttpResponse resposta = httpClient.execute(httpget);

			operationResult = resposta.getStatusLine().getStatusCode();

		} catch (Exception e) {
			printLog("SparqlUpdate", "insertTriples");

		}

		return operationResult;
	}

	public enum Format {
		NTRIPLES, RDFXML, TURTLE
	}

	// wrong: rdf->n3, n3->turtle, rdf->n3
	public static String convert(Format inputFormat, String inputData, String defaultURI, Format outputFormat)
			throws Exception {
		String n3 = "";
		try {
			if (inputData == null || inputData.length() == 0)
				throw new NullPointerException("local");

			Any23 runner = new Any23();

			String inputMime = "";
			switch (inputFormat) {// TODO N3
			case NTRIPLES:
				inputMime = "text/plain";
				break;
			case RDFXML:
				inputMime = "application/rdf+xml";
				break;
			case TURTLE:
				inputMime = "text/turtle";
				break;
			default:
				throw new Exception("Formato de entrada invalido");
			}

			DocumentSource source = new StringDocumentSource(inputData, defaultURI, inputMime, "UTF-8");

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			TripleHandler handler = null;

			switch (outputFormat) {
			case NTRIPLES:
				handler = new NTriplesWriter(out);
				break;
			case RDFXML:
				handler = new RDFXMLWriter(out);
				break;
			case TURTLE:
				handler = new TurtleWriter(out);
				break;
			default:
				throw new Exception("Formato de saida invalido");
			}

			runner.extract(source, handler);
			n3 = out.toString("UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			printLog("SparqlUpdate", "convert");

			String msg = String.format("%s -> %s. INPUT=[[%s]]", inputFormat.name(), outputFormat.name(), inputData);

			throw new Exception(msg, e);
		}
		return n3;
	}

	private String triplify(String defaultURI, String rdfxml) {
		String n3 = "";
		try {
			Any23 runner = new Any23();

			DocumentSource source = new StringDocumentSource(rdfxml, defaultURI, "application/rdf+xml", "UTF-8");

			// DocumentSource source2 = new StringDocumentSource(rdfxml,
			// defaultURI,
			// "text/rdf+n3", "UTF-8");
			//
			// DocumentSource source = source1 == null ? source2 : source1;

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			TripleHandler handler = new NTriplesWriter(out);
			runner.extract(source, handler);
			n3 = out.toString("UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			printLog("SparqlUpdate", "triplify");
		}
		return n3;
	}

	private CloseableHttpClient createConnection() {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();

		if (this.password != null) {
			HttpHost targetHost = new HttpHost(this.host, this.port, this.protocol);
			CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			credentialsProvider.setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()),
					new UsernamePasswordCredentials(this.user, this.password));

			httpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();

			AuthCache authCache = new BasicAuthCache();
			BasicScheme basicAuth = new BasicScheme();
			authCache.put(targetHost, basicAuth);
			HttpClientContext context = HttpClientContext.create();
			context.setCredentialsProvider(credentialsProvider);
			context.setAuthCache(authCache);
		}

		return httpClient;
	}

	private static void printLog(String classe, String method) {
		System.out.println("Problems:\n class:" + classe + "\n method:" + method + "\n\n");
	}

}
