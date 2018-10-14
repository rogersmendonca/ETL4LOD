package br.ufrj.ppgi.greco.kettle.owlutils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pentaho.di.core.exception.KettleException;

public final class LOVApiV2 {

	private static final String URL_VOCABULARY_SEARCH = "https://lov.linkeddata.es/dataset/lov/api/v2/vocabulary/search";

	private static Map<String, JSONArray> dataCache = new HashMap<String, JSONArray>();

	public static HttpsURLConnection getConnection(boolean ignoreInvalidCertificate, URL url) throws KeyManagementException, NoSuchAlgorithmException, IOException{
        SSLContext ctx = SSLContext.getInstance("TLS");
        if (ignoreInvalidCertificate){
            ctx.init(null, new TrustManager[] { new InvalidCertificateTrustManager() }, null);  
        }       
        SSLContext.setDefault(ctx);

        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setDoOutput(true);

        if (ignoreInvalidCertificate){
            connection.setHostnameVerifier(new InvalidCertificateHostVerifier());
        }

        return connection;
    }
	
	public static final LOVAttributes vocabularySearch(String prefix) throws Exception {
		LOVAttributes att = null;
		try {
			JSONArray rsJSON;
			if (dataCache.get(prefix) == null) {
				URL url = new URL(URL_VOCABULARY_SEARCH + "?q=" + prefix);
				HttpsURLConnection con = getConnection(true, url);
				
				int responseCode = con.getResponseCode();

				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				
				if (responseCode != 200) throw new Exception("Error response code " + responseCode);
				in.close();
				
				JSONObject jsonRoot = new JSONObject(response.toString());
				rsJSON = jsonRoot.getJSONArray("results");
				dataCache.put(prefix, rsJSON);
			} else {
				rsJSON = dataCache.get(prefix);
			}
			
			JSONObject vocab = rsJSON.getJSONObject(0);
			att = new LOVAttributes(vocab.getJSONObject("_source"));
		} catch (IOException e) {
			throw new KettleException("Connection with LOV server unsuccessful: ", e);
		} catch (JSONException e) {
			throw new KettleException("An error occured while parsing the JSON from LOVApi: ", e);
		}
		return att;
	}

	public static void main(String args[]) throws Exception {
		System.out.println(LOVApiV2.vocabularySearch("bibo").getURI());
	}

}