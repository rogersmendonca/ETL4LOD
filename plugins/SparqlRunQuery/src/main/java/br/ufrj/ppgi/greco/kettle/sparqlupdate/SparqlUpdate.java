package br.ufrj.ppgi.greco.kettle.sparqlupdate;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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
	
	/**
	 * Retorna um objeto {@link HttpResponse} com o resultado da query
	 * executada no endpoint sparql.
	 *
	 * @param  graph uma URI de um grafo rdf
	 * @param  query uma query rdf de atualização
	 * @return       o resultado da query
	 * @throws Exception quando a URI contem erros ou existe uma IOException ao tentar receber a HttpResponse 
	 * @see          HttpResponse
	 */
	public HttpResponse runQuery(String graph, String query) throws Exception {
		CloseableHttpClient httpClient = this.createConnection();

		try {
			List<NameValuePair> qParams = new ArrayList<NameValuePair>();

			qParams.add(new BasicNameValuePair("query", query));

			URI uri = new URIBuilder().setScheme(this.protocol).setHost(this.host).setPort(this.port).setPath(this.path)
					.build();
			HttpPost httpPost = new HttpPost(uri);
			httpPost.setEntity(new UrlEncodedFormEntity(qParams, StandardCharsets.UTF_8));

			HttpResponse resposta = httpClient.execute(httpPost);

			return resposta;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
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

}
