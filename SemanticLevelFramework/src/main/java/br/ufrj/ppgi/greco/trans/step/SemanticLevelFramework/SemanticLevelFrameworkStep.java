package br.ufrj.ppgi.greco.trans.step.SemanticLevelFramework;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import bsh.EvalError;
import bsh.Interpreter;

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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Step SemanticLevelFramework.
 * <p />
 * Gera senten&ccedil;as RDF no formato N-Triple
 * 
 * 
 * @author Camila Carvalho Ferreira
 * 
 */
public class SemanticLevelFrameworkStep extends BaseStep implements StepInterface
{
    // Constantes
	//public static final String LITERAL_OBJECT_TRIPLE_FORMAT = "<%s> <%s> %s.";
    //public static final String URI_OBJECT_TRIPLE_FORMAT = "<%s> <%s> <%s>.";

    public SemanticLevelFrameworkStep(StepMeta stepMeta,
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
        SemanticLevelFrameworkStepMeta meta = (SemanticLevelFrameworkStepMeta) smi;
        SemanticLevelFrameworkStepData data = (SemanticLevelFrameworkStepData) sdi;

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
        
        String outputNTriple = "";

        // Logica do step
        // Leitura de campos Input
        	String inputSubject = getInputRowMeta().getString(row,
	                meta.getInputSubject(), "");
	        String inputPredicate = getInputRowMeta().getString(row,
	                meta.getInputPredicate(), "");
	        String inputObject = getInputRowMeta().getString(row,
	                meta.getInputObject(), "");
	        
	        Interpreter i = new Interpreter();
	        
	        /*try {
				FileInputStream arquivo = new FileInputStream(meta.getBrowseFilename());
				InputStreamReader console = new InputStreamReader(arquivo);
				IterableFile entrada = new IterableFile(console);
			
				for(String linha : entrada) {
					 String parte[] = linha.split(";");
					 String regra = parte[0];
					 String nivel = parte[1];
					 i.set("inputSubject", inputSubject);
					 i.set("inputPredicate", inputPredicate);
					 i.set("inputObject", inputObject);
					 try {
					 if((Boolean)i.eval(regra)){
						 outputNTriple = nivel;
					 }
					 } catch (EvalError e) {
				            // TODO Auto-generated catch block
						     inputPredicate = "falha teste1";
				            e.printStackTrace();
				     }
				}
	
				entrada.close();
					
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				 inputPredicate = "falha teste32";
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				 inputPredicate = "falha teste4";
			}*/
	        
	        try {
	        	DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	            Document doc = docBuilder.parse (new File(meta.getBrowseFilename()));
	            NodeList listOfFrames = doc.getElementsByTagName("Frame");
	            int totalFrames = listOfFrames.getLength();
	            i.set("inputSubject", inputSubject);
				i.set("inputPredicate", inputPredicate);
				i.set("inputObject", inputObject);
	            for(int k=0; k<totalFrames; k++){
	            	Node ruleFrameNode = listOfFrames.item(k);
	            	if(ruleFrameNode.getNodeType() == Node.ELEMENT_NODE){
	                	Element ruleFrameElement = (Element)ruleFrameNode;
	                	NodeList ruleList = ruleFrameElement.getElementsByTagName("Rule");
	                	Element ruleElement = (Element)ruleList.item(0);
	                	NodeList textRList = ruleElement.getChildNodes();
	                	NodeList levelList = ruleFrameElement.getElementsByTagName("Level");
	                    Element levelElement = (Element)levelList.item(0);
	                    NodeList textLList = levelElement.getChildNodes();
	                    if((Boolean)i.eval(textRList.item(0).getNodeValue().trim())){
	                    	outputNTriple = textLList.item(0).getNodeValue().trim();
	                    	//TODO avaliar sair do for
	                    }                  
	            	}		 
	            }
	            
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				outputNTriple = "erro1";
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				outputNTriple = "erro2";
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				outputNTriple = "erro3";
				e.printStackTrace();
			} catch (EvalError e) {
				// TODO Auto-generated catch block
				outputNTriple = "erro4";
				e.printStackTrace();
			}
	        
        // Geracao do campo de saida
	     /*   if(inputObject.contains("\"")){
	        	outputNTriple = String.format(LITERAL_OBJECT_TRIPLE_FORMAT,
                        outputSubject, outputPredicate, outputObject);
	        }
	        else{
	        	outputNTriple = String.format(URI_OBJECT_TRIPLE_FORMAT,
                        outputSubject, outputPredicate, outputObject);
	        }*/
            

        // Set output row
      Object[] outputRow = meta.getInnerKeepInputFields() ? row
                : new Object[0];

        outputRow = RowDataUtil.addValueData(outputRow, outputRow.length,
                outputNTriple);

        putRow(data.outputRowMeta, outputRow);

        return true;
    }


    /**
     * Le o arquivo linha a linha
     * 
     */
    
    public class IterableFile extends BufferedReader implements Iterable<String> {

    	public IterableFile(Reader in) {
    		super(in); // faça o que a classe pai faria
    	}
    	
    	public StringIterator iterator() {
    		return new StringIterator();
    	}
    	
    	public class StringIterator implements Iterator<String> {
    		private String linha = null;
    		
    		public StringIterator() {
    			try {
    				linha = readLine();
    			} catch (IOException e) {
    				linha = null;
    			}
    		}
    		
    		public boolean hasNext() {
    			return linha != null;
    		}
    		
    		public String next() {
    			String aux = linha;
    			try {
    				linha = readLine();
    			} catch (IOException e) {
    				linha = null;
    			}
    			return aux;
    		}
    		
    		public void remove() {
    			
    		}
    	}

    }

}

