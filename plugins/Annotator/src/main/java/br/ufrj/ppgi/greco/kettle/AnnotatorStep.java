package br.ufrj.ppgi.greco.kettle;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
 * Step Annotator.
 * <p />
 * Gera senten&ccedil;as RDF no formato N-Triple
 * 
 * 
 * @author Camila Carvalho Ferreira
 * 
 */
public class AnnotatorStep extends BaseStep implements StepInterface {
	// Constantes
	public static final String LITERAL_OBJECT_TRIPLE_FORMAT = "<%s> <%s> \"%s\".";
	public static final String URI_OBJECT_TRIPLE_FORMAT = "<%s> <%s> <%s> .";
	public static final String RDF_TYPE_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

	public AnnotatorStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
			Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		if (super.init(smi, sdi)) {
			return true;
		} else
			return false;
	}

	@Override
	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		super.dispose(smi, sdi);
	}

	/**
	 * Metodo chamado para cada linha que entra no step
	 */
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
		AnnotatorStepMeta meta = (AnnotatorStepMeta) smi;
		AnnotatorStepData data = (AnnotatorStepData) sdi;

		// Obtem linha do fluxo de entrada e termina caso nao haja mais entrada

		Object[] row = getRow();

		if (row == null) { // Nao ha mais linhas de dados
			setOutputDone();
			return false;
		}

		// Executa apenas uma vez. Variavel first definida na superclasse com
		// valor true
		if (first) {
			first = false;

			// Obtem todas as colunas ateh o step anterior.
			// Chamar apenas apos chamar getRow()
			RowMetaInterface rowMeta = getInputRowMeta();
			data.outputRowMeta = meta.getInnerKeepInputFields() ? rowMeta.clone() : new RowMeta();

			// Adiciona os metadados do step atual
			meta.getFields(data.outputRowMeta, getStepname(), null, null, this);
		}

		String outputNTriple;

		// Logica do step
		// Leitura de campos Input
		String inputSubject = getInputRowMeta().getString(row, meta.getInputSubject(), "");
		String inputPredicate = getInputRowMeta().getString(row, meta.getInputPredicate(), "");
		String inputObject = getInputRowMeta().getString(row, meta.getInputObject(), "");
		String outputSubject = inputSubject;
		String outputPredicate = inputPredicate;
		String outputObject = inputObject;

		try {
			// abre arquivo xml
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File(meta.getBrowseFilename()));
			NodeList listOfMaps = doc.getElementsByTagName("map");
			int totalMaps = listOfMaps.getLength();
			// procura em cada node map as regras de anota
			for (int i = 0; i < totalMaps; i++) {
				Node fromMapNode = listOfMaps.item(i);
				if (fromMapNode.getNodeType() == Node.ELEMENT_NODE) {
					Element fromMapElement = (Element) fromMapNode;
					NodeList fromList = fromMapElement.getElementsByTagName("from");
					Element fromElement = (Element) fromList.item(0);
					NodeList textFList = fromElement.getChildNodes();
					NodeList toList = fromMapElement.getElementsByTagName("to");
					Element toElement = (Element) toList.item(0);
					NodeList textTList = toElement.getChildNodes();
					if (((Node) textFList.item(0)).getNodeValue().trim().contains(inputSubject)) {
						outputSubject = ((Node) textTList.item(0)).getNodeValue().trim();
					}
					if (((Node) textFList.item(0)).getNodeValue().trim().contains(inputPredicate)) {
						outputPredicate = ((Node) textTList.item(0)).getNodeValue().trim();
					}
					if (((Node) textFList.item(0)).getNodeValue().trim().contains(inputObject)) {
						outputObject = ((Node) textTList.item(0)).getNodeValue().trim();
					}
				}
			}

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (inputPredicate.equals(RDF_TYPE_URI)) {
			outputNTriple = String.format(URI_OBJECT_TRIPLE_FORMAT, outputSubject, outputPredicate, outputObject);
		} else {

			outputNTriple = String.format(LITERAL_OBJECT_TRIPLE_FORMAT, outputSubject, outputPredicate, outputObject);
		}

		// Set output row
		Object[] outputRow = meta.getInnerKeepInputFields() ? row : new Object[0];

		outputRow = RowDataUtil.addValueData(outputRow, outputRow.length, outputNTriple);

		putRow(data.outputRowMeta, outputRow);

		return true;
	}
}
