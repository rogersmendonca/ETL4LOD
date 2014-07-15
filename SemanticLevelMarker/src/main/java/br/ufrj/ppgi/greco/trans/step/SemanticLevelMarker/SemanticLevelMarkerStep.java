package br.ufrj.ppgi.greco.trans.step.SemanticLevelMarker;

import org.pentaho.di.core.exception.KettleException;
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
 * Step SemanticLevelMarker.
 * <p />
 * Gera um step para identificação do nível semântico de uma tripla
 *  
 * @author Kelli de Faria Cordeiro
 * 
 */
public class SemanticLevelMarkerStep extends BaseStep implements StepInterface
{
    // Constantes
	public static final String LITERAL_OBJECT_TRIPLE_FORMAT = "<%s> <%s> \"%s\" .";
    public static final String URI_OBJECT_TRIPLE_FORMAT = "<%s> <%s> <%s> .";
    public static final String RDF_TYPE_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

    public SemanticLevelMarkerStep(StepMeta stepMeta,
            StepDataInterface stepDataInterface, int copyNr,
            TransMeta transMeta, Trans trans)
    {
        super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
    }

    @Override
    public boolean init(StepMetaInterface smi, StepDataInterface sdi)
    {
        if (super.init(smi, sdi))
        {
            return true;
        }
        else
            return false;
    }

    @Override
    public void dispose(StepMetaInterface smi, StepDataInterface sdi)
    {
        super.dispose(smi, sdi);
    }

    /**
     * Metodo chamado para cada linha que entra no step
     */
    public boolean processRow(StepMetaInterface smi, StepDataInterface sdi)
            throws KettleException
    {
        SemanticLevelMarkerStepMeta meta = (SemanticLevelMarkerStepMeta) smi;
        SemanticLevelMarkerStepData data = (SemanticLevelMarkerStepData) sdi;

        // Obtem linha do fluxo de entrada e termina caso nao haja mais entrada
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

            // Obtem todas as colunas ateh o step anterior.
            // Chamar apenas apos chamar getRow()
            RowMetaInterface rowMeta = getInputRowMeta();
            data.outputRowMeta = meta.getInnerKeepInputFields() ? rowMeta
                   .clone() : new RowMeta();

            // Adiciona os metadados do step atual
            meta.getFields(data.outputRowMeta, getStepname(), null, null, this);
        }

        // Logica do step
        // Leitura de campos Input
        String inputSubject = removeSignals(getInputRowMeta().getString(row,
                meta.getInputSubject(), ""));
        String inputPredicate = removeSignals(getInputRowMeta().getString(row,
                meta.getInputPredicate(), ""));
        String inputObject = getInputRowMeta().getString(row,
                meta.getInputObject(), "");
        //String inputObject = removeSignals(getInputRowMeta().getString(row,
          //      meta.getInputObject(), ""));
        
        // Geracao do campo de saida
        String outputNTriple;
    		outputNTriple = String.format(URI_OBJECT_TRIPLE_FORMAT,
                   inputSubject, "sstamp:hassemanticlevel", "notMarked");
	    	if (inputObject.contains("<"))
	    		{
	        	outputNTriple = String.format(URI_OBJECT_TRIPLE_FORMAT,
	                  inputSubject, inputPredicate, inputObject);
	    		}
	        	else
	        	{
	        	outputNTriple = String.format(LITERAL_OBJECT_TRIPLE_FORMAT,
	  	              inputSubject, inputPredicate, inputObject);
	  	        		
	    		}
	       
 	
	    // Objeto é um literal?
        // sstamp:low
        if (inputObject.startsWith("'") || inputObject.contains("\"") || inputObject.contains("http"))
        	{
            outputNTriple = String.format(URI_OBJECT_TRIPLE_FORMAT,
                    inputSubject, "sstamp:hassemanticlevel", "sstamp:low");
        	}
        // Objeto ou Predicado está anotado com um prefixo?
        else if (inputObject.contains(":") || inputPredicate.contains(":") )
	       		{
	        	// O prefixo representa uma ontologia?
	        	// sstamp:high
	            if (inputObject.contains("owl") || inputPredicate.contains("owl"))
	            	{
	           		outputNTriple = String.format(URI_OBJECT_TRIPLE_FORMAT,
	                        inputSubject, "sstamp:hassemanticlevel", "sstamp:high");
	            	} 
	            else
	           		{
	            	// O prefixo representa um vocabulário?
	            	// sstamp:medium
		            outputNTriple = String.format(URI_OBJECT_TRIPLE_FORMAT,
		                    inputSubject, "sstamp:hassemanticlevel", "sstamp:medium");
		            	
	           		}
	       		}
        
   		// Set output row
        Object[] outputRow = meta.getInnerKeepInputFields() ? row
                : new Object[0];

        outputRow = RowDataUtil.addValueData(outputRow, outputRow.length,
                outputNTriple);

        putRow(data.outputRowMeta, outputRow);

        return true;
    }

    /**
     * Trata o valor passado como parametro, retirando os caracteres <, > e "
     * 
     * @param value
     * @return
     */
    private static String removeSignals(String value)
    {
        if (value != null)
        {
            return value.replaceAll("<", "").replaceAll(">", "")
                    .replaceAll("\"", "").trim();
        }
        else
        {
            return "";
        }
    }
}
