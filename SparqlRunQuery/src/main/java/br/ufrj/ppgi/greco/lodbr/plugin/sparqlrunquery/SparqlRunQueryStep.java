package br.ufrj.ppgi.greco.lodbr.plugin.sparqlrunquery;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
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

import br.ufrj.ppgi.greco.lodbr.sparqlupdate.Response;
import br.ufrj.ppgi.greco.lodbr.sparqlupdate.SparqlUpdate;

public class SparqlRunQueryStep extends BaseStep implements StepInterface
{

   // private int MAX_TRIPLES_COUNT = 1000;

    public SparqlRunQueryStep(StepMeta stepMeta,
            StepDataInterface stepDataInterface, int copyNr,
            TransMeta transMeta, Trans trans)
    {
        super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
    }

    public boolean processRow(StepMetaInterface smi, StepDataInterface sdi)
            throws KettleException
    {

        SparqlRunQueryStepMeta meta = (SparqlRunQueryStepMeta) smi;
        SparqlRunQueryStepData data = (SparqlRunQueryStepData) sdi;

        Object[] row = getRow();
        
        if (row == null)
        { // Nao ha mais linhas de dados
            setOutputDone();
            return false;
        }
        // Executa apenas uma vez. Variavel first definida na superclasse com
        // valor true
        if (first)
        {
            first = false;
         
            // Adiciona os metadados do step atual
            meta.getFields(data.outputRowMeta, getStepname(), null, null, this);
            
            // Logica do step
            // Leitura de campos Input
            String inputQueryTextFieldName = getInputRowMeta().getString(row,
                    meta.getQueryTextContentFieldName(), "");

            // Cria objeto SparqlUpdate
            try
            {
                data.sparqlUpdate = new SparqlUpdate(new URI(
                        meta.getEndpointUrl()), meta.getUsername(),
                        meta.getPassword());
            }
            catch (URISyntaxException e)
            {
                throw new KettleException(e);
            }

            try
            {
               
                int Result = data.sparqlUpdate
                        .runQuery(inputQueryTextFieldName);
                
                if (Result != 200)
                {
                    throw new KettleException(
                            "Query Run Failed: "
                                    + inputQueryTextFieldName);
                }
            }
            catch (Exception e)
            {
                throw new KettleException(
                        "Query Run Failed.", e);
                
            }
        }
        
        return true;
}
    private void logException(Exception e)
    {
        log.logBasic("Erro ao tentar inserir bloco:\n");
        log.logBasic(e.toString());
    }

    private void logHttpResponse(HttpResponse result)
    {
        StatusLine sLine = result.getStatusLine();

        log.logBasic("Erro ao tentar inserir bloco: " + sLine.getStatusCode()
                + " " + sLine.getReasonPhrase());

        HeaderIterator hi = result.headerIterator();
        while (hi.hasNext())
        {
            Header h = hi.nextHeader();
            log.logBasic("   " + h.getName() + ": " + h.getValue());
        }

        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(result
                    .getEntity().getContent()));
            String line;
            while ((line = br.readLine()) != null)
            {
                log.logBasic(line);
            }
        }
        catch (Exception e)
        {
        }
    }

}
