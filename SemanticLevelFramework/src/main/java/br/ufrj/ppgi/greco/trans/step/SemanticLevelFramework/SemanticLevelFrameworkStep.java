package br.ufrj.ppgi.greco.trans.step.SemanticLevelFramework;

import java.io.File;
import java.io.IOException;

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
	       
	        try {
	        	//abre arquivos xml
	        	DocumentBuilderFactory docBuilderFactory2 = DocumentBuilderFactory.newInstance();
	            DocumentBuilder docBuilder2 = docBuilderFactory2.newDocumentBuilder();
	            Document doc2 = docBuilder2.parse (new File(meta.getBrowseFilename()));      	            
				
				DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	            Document doc = docBuilder.parse (new File(meta.getRulesFilename()));
	            NodeList listOfFrames = doc.getElementsByTagName("Frame");          
	            int totalFrames = listOfFrames.getLength();
	            //valida variaveis para o interpreter
	            i.set("inputSubject", inputSubject);
				i.set("inputPredicate", inputPredicate);
				i.set("inputObject", inputObject);
				String prefixo = "";
				//pega prefixo
				if(inputPredicate.contains(":")){
					int index = inputPredicate.indexOf(":");
					prefixo = inputPredicate.substring(0, index);
				}	
				//verifica se é vocabulario ou ontologia
				i.set("isVocabulary", isVocabulary(prefixo, doc2));
				i.set("isOntology", isOntology(prefixo, doc2));
				//busca as regras
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
	                    //valida a regra
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

	        
        // Set output row
      Object[] outputRow = meta.getInnerKeepInputFields() ? row
                : new Object[0];

        outputRow = RowDataUtil.addValueData(outputRow, outputRow.length,
                outputNTriple);

        putRow(data.outputRowMeta, outputRow);

        return true;
    }

	private boolean isOntology(String prefix, Document doc) {
		NodeList listOfResults = doc.getElementsByTagName("result");
		int totalResults = listOfResults.getLength();
		for(int k=0; k<totalResults; k++){
			Node resultNode = listOfResults.item(k);
			Element resultElement = (Element)resultNode;
        	NodeList bindingList = resultElement.getElementsByTagName("binding");
        	Element bindingPrefixElement = (Element)bindingList.item(0);
        	NodeList literalList = bindingPrefixElement.getElementsByTagName("literal");
        	Element literalElement = (Element)literalList.item(0);
        	NodeList textLiList = literalElement.getChildNodes();
        	if(prefix.equals(textLiList.item(0).getNodeValue().trim())){
        		//busca se é ontologia no 'vocabDescription'
        		Element bindingDescElement = (Element)bindingList.item(3);
        		NodeList literalDescList = bindingDescElement.getElementsByTagName("literal");
            	Element literalDescElement = (Element)literalDescList.item(0);
            	NodeList textDescList = literalDescElement.getChildNodes();
            	//busca se é ontologia no 'vocabTitle'
        		Element bindingTitleElement = (Element)bindingList.item(2); 
        		NodeList literalTitleList = bindingTitleElement.getElementsByTagName("literal");
        		Element literalTitleElement = (Element)literalTitleList.item(0);
        		NodeList textTitleList = literalTitleElement.getChildNodes();
            	if(textDescList.item(0).getNodeValue().trim().toLowerCase().contains("ontology")){
            		return true;
            	}
            	if(textTitleList.item(0).getNodeValue().trim().toLowerCase().contains("ontology")){
            		return true;
            	}
        	}
		}
		return false;
	}

	
	
	public boolean isVocabulary(String prefix, Document doc) {
		NodeList listOfResults = doc.getElementsByTagName("result");
		int totalResults = listOfResults.getLength();
		for(int k=0; k<totalResults; k++){
			Node resultNode = listOfResults.item(k);
			Element resultElement = (Element)resultNode;
        	NodeList bindingList = resultElement.getElementsByTagName("binding");
        	Element bindingPrefixElement = (Element)bindingList.item(0);
        	NodeList literalList = bindingPrefixElement.getElementsByTagName("literal");
        	Element literalElement = (Element)literalList.item(0);
        	NodeList textLiList = literalElement.getChildNodes();
        	if(prefix.equals(textLiList.item(0).getNodeValue().trim())){
        		//busca se é vocabulario no 'vocabDescription'
        		Element bindingDescElement = (Element)bindingList.item(3); 
        		NodeList literalDescList = bindingDescElement.getElementsByTagName("literal");
            	Element literalDescElement = (Element)literalDescList.item(0);
            	NodeList textDescList = literalDescElement.getChildNodes();
            	//busca se é vocabulario no 'vocabTitle'
            	Element bindingTitleElement = (Element)bindingList.item(2); 
        		NodeList literalTitleList = bindingTitleElement.getElementsByTagName("literal");
        		Element literalTitleElement = (Element)literalTitleList.item(0);
        		NodeList textTitleList = literalTitleElement.getChildNodes();
            	if(textDescList.item(0).getNodeValue().trim().toLowerCase().contains("vocabulary")){
            		return true;
            	}
            	if(textTitleList.item(0).getNodeValue().trim().toLowerCase().contains("vocabulary")){
            		return true;
            	}
        	}
		}
		return false;
	}

}

