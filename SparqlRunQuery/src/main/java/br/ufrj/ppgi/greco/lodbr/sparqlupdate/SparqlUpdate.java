package br.ufrj.ppgi.greco.lodbr.sparqlupdate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;

public class SparqlUpdate
{

    private String protocol;
    private String host;
    private String path;
    private int port;
    private String user;
    private String password;

    public SparqlUpdate(URI uri, String user, String password)
    {
        this.protocol = uri.getScheme();
        this.host = uri.getHost();
        this.port = uri.getPort();
        this.path = uri.getPath();
        this.user = user;
        this.password = password;
    }

    public SparqlUpdate(String protocol, String host, String path, int port,
            String user, String password)
    {
        this.protocol = protocol;
        this.host = host;
        this.path = path;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public HttpResponse runQuery(String graph, String query) throws Exception
    {
        // Response operationResult = null;

        DefaultHttpClient httpClient = this.createConnection();

        try
        {
            List<NameValuePair> qParams = new ArrayList<NameValuePair>();

            qParams.add(new BasicNameValuePair("query", query));

            URI uri = URIUtils.createURI(this.protocol, this.host, this.port,
                    this.path, null, null);
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setEntity(new UrlEncodedFormEntity(qParams, HTTP.UTF_8));

            HttpResponse resposta = httpClient.execute(httpPost);

            return resposta;

        }
        catch (Exception e)
        {
            printLog("SparqlUpdate", "runQuery");
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    private DefaultHttpClient createConnection()
    {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        if (this.password != null)
        {
            // authentication
            HttpHost targetHost = new HttpHost(this.host, this.port,
                    this.protocol);
            httpClient.getCredentialsProvider().setCredentials(
                    new AuthScope(targetHost.getHostName(),
                            targetHost.getPort()),
                    new UsernamePasswordCredentials(this.user, this.password));

            AuthCache authCache = new BasicAuthCache();
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(targetHost, basicAuth);
            BasicHttpContext localcontext = new BasicHttpContext();
            localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);
        }

        return httpClient;
    }

    private static void printLog(String classe, String method)
    {
        System.out.println("Problems:\n class:" + classe + "\n method:"
                + method + "\n\n");
    }

}
