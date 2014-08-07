package br.ufrj.ppgi.greco.trans.step.SemanticLevelFramework;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

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

	        	XPath xPath =  XPathFactory.newInstance().newXPath();
	        	//open xml file
	        	DocumentBuilderFactory docBuilderFactory2 = DocumentBuilderFactory.newInstance();
	            DocumentBuilder docBuilder2 = docBuilderFactory2.newDocumentBuilder();
	            Document lovfile = docBuilder2.parse (new File(meta.getLOVFilename()));      	            
				
				DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	            Document rulefile = docBuilder.parse (new File(meta.getRulesFilename()));
	            String ruleExpression = "SemanticLevelFramework/Frame/Rule";
	            String levelExpression = "SemanticLevelFramework/Frame/Level";
	            //set variables to interpreter
	            i.set("inputSubject", inputSubject);
				i.set("inputPredicate", inputPredicate);
				i.set("inputObject", inputObject);
				String prefixo = "";
				//pega prefixo
				if(inputPredicate.contains(":")){
					int index = inputPredicate.indexOf(":");
					prefixo = inputPredicate.substring(0, index);
				}	
				//check if prefix is vocabulary or ontology
				i.set("isVocabulary", isVocabulary(prefixo, lovfile));
				i.set("isOntology", isOntology(prefixo, lovfile));
				//find the rules
				NodeList ruleList = (NodeList) xPath.compile(ruleExpression).evaluate(rulefile, XPathConstants.NODESET);
				NodeList levelList = (NodeList) xPath.compile(levelExpression).evaluate(rulefile, XPathConstants.NODESET);
				for (int k = 0; k < ruleList.getLength(); k++) {
	                    //validate the rules
	                    if((Boolean)i.eval(ruleList.item(k).getFirstChild().getNodeValue())){
	                    	outputNTriple = levelList.item(k).getFirstChild().getNodeValue();
	                    	//TODO avaliar sair do for
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
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				outputNTriple = "erro5";
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

	private boolean isOntology(String prefix, Document doc) throws XPathExpressionException {
		XPath xPath =  XPathFactory.newInstance().newXPath();
		//search for the prefixes and descritions
		String Expression = "sparql/results/result/binding/literal";
		NodeList literalsList = (NodeList) xPath.compile(Expression).evaluate(doc, XPathConstants.NODESET);
		for(int k=0; k<literalsList.getLength(); k++){
			if(prefix.equals(literalsList.item(k).getFirstChild().getNodeValue())){
				k++;
				//check if the vocabTitle has 'ontology'
				if(literalsList.item(k).getFirstChild().getNodeValue().toLowerCase().contains("ontology")){
					return true;
				}
				else{
					k++;
				}
				//check if the vobcabDescription has 'ontology'
				if(literalsList.item(k).getFirstChild().getNodeValue().toLowerCase().contains("ontology")){
					return true;
				}
			}
			else{
				k = k+2;
			}
		}
		return false;
	}

	
	
	public boolean isVocabulary(String prefix, Document doc) throws XPathExpressionException {
		XPath xPath =  XPathFactory.newInstance().newXPath();
		String Expression = "sparql/results/result/binding/literal";
		NodeList literalsList = (NodeList) xPath.compile(Expression).evaluate(doc, XPathConstants.NODESET);
		for(int k=0; k<literalsList.getLength(); k++){
			if(prefix.equals(literalsList.item(k).getFirstChild().getNodeValue())){
				k++;
				if(literalsList.item(k).getFirstChild().getNodeValue().toLowerCase().contains("vocabulary")){
					return true;
				}
				else{
					k++;
				}
				if(literalsList.item(k).getFirstChild().getNodeValue().toLowerCase().contains("vocabulary")){
					return true;
				}
			}
			else{
				k = k+2;
			}
		}
		return false;
	}
}

