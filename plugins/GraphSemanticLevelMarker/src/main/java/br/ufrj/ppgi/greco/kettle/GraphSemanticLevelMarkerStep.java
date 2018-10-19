package br.ufrj.ppgi.greco.kettle;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import bsh.EvalError;
import bsh.Interpreter;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

/**
 * Step GraphSemanticLevelMarker.
 * <p />
 * 
 * @author Kelli de Faria Cordeiro
 * 
 */
public class GraphSemanticLevelMarkerStep extends BaseStep implements StepInterface {

	static Integer assessedValueLevel;

	public GraphSemanticLevelMarkerStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
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
		GraphSemanticLevelMarkerStepMeta meta = (GraphSemanticLevelMarkerStepMeta) smi;
		GraphSemanticLevelMarkerStepData data = (GraphSemanticLevelMarkerStepData) sdi;

		String rulesFileName = meta.getRulesFilename();
		String LOVFileName = meta.getBrowseFilename();

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
			data.outputRowMeta = new RowMeta();

			// Adiciona os metadados do step atual
			meta.getFields(data.outputRowMeta, getStepname(), null, null, this);
		}

		// Logica do step
		// Leitura de campos Input
		RowMetaInterface rowMeta = getInputRowMeta();
		int indexGraph = rowMeta.indexOfValue(meta.getInputGraph());
		Object graph = (indexGraph >= 0) ? row[indexGraph] : null;

		// Set output row
		Method[] methods = graph.getClass().getMethods();
		boolean hasListStatements = false;
		for (Method method : methods) {
			if (method.getName().equals("listStatements")) {
				hasListStatements = true;
				break;
			}
		}

		if (hasListStatements) {
			tripleWriter(graph, null, data, rulesFileName, LOVFileName);
		}

		return true;
	}

	private int tripleWriter(Object model, Object[] row, GraphSemanticLevelMarkerStepData data, String rulesFileName,
			String LOVFileName) throws KettleStepException {
		int numPutRows = 0;

		try {
			// Recreates the graph sent by the previous step
			Model inputRecreatedGraph = recreateGraph(model);

			// Identify inputGraph Semantic Level
			Statement stamp = markGraphSemanticLevel(inputRecreatedGraph, rulesFileName, LOVFileName);

			// Creates output with the semantic level stamp
			Object[] outputRow = row;
			int i = 0;
			outputRow = RowDataUtil.addValueData(outputRow, i++, stamp.getSubject().toString());
			outputRow = RowDataUtil.addValueData(outputRow, i++, stamp.getPredicate().toString());
			outputRow = RowDataUtil.addValueData(outputRow, i++, stamp.getObject().toString());

			// Joga tripla no fluxo
			putRow(data.outputRowMeta, outputRow);

			numPutRows++;

		}

		catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return numPutRows;
	}

	private Model recreateGraph(Object model) {

		// Recreates a Model from a Object
		Object it = null;
		Model inputModel = ModelFactory.createDefaultModel();

		try {
			it = model.getClass().getMethod("listStatements").invoke(model);
			while ((Boolean) it.getClass().getMethod("hasNext").invoke(it)) {
				Object stmt = it.getClass().getMethod("next").invoke(it);

				String subject = stmt.getClass().getMethod("getSubject").invoke(stmt).toString();
				String predicate = stmt.getClass().getMethod("getPredicate").invoke(stmt).toString();
				String object = stmt.getClass().getMethod("getObject").invoke(stmt).toString();

				Resource r = ResourceFactory.createResource(subject);
				Property p = ResourceFactory.createProperty(predicate);
				inputModel.add(r, p, object);

			}

		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return inputModel;
	}

	private Statement markGraphSemanticLevel(Model inputModel, String rulesFileName, String LOVFileName) {
		// Variables initializations
		ResIterator resourceSet = inputModel.listSubjects();
		Model innerModel = ModelFactory.createDefaultModel();
		Resource r = resourceSet.nextResource();
		Property p = ResourceFactory.createProperty("sstamp:hassemanticlevel");

		// Tive que criar um model para trabalhar com um Resource
		Statement outputGraphSemanticLevel = innerModel.createStatement(r, p, "sstamp:notMarked");

		// Identify the levels of each statement on the inputGraph
		StmtIterator statementSet = inputModel.listStatements();
		Integer valueLevel = 0;

		while (statementSet.hasNext()) {
			Statement s = statementSet.nextStatement();

			String semanticLevel = assessSemanticLevel(s, rulesFileName, LOVFileName);

			if (valueLevel < assessedValueLevel) {
				outputGraphSemanticLevel = innerModel.createStatement(r, p, semanticLevel);
				valueLevel = assessedValueLevel;

			}

		}
		return outputGraphSemanticLevel;
	}

	private static String assessSemanticLevel(Statement s, String rulesFileName, String LOVFileName) {
		Interpreter i = new Interpreter();

		String assessedDescriptionLevel = "NotIdentified";

		// Is Literal?
		// if (s.getLiteral() != null)
		// assessedLevel="laiid:low";

		String inputSubject = s.getSubject().toString();
		String inputPredicate = s.getPredicate().toString();
		String inputObject = s.getObject().toString();

		try {
			// Open LOV xml file
			DocumentBuilderFactory docBuilderFactory2 = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder2 = docBuilderFactory2.newDocumentBuilder();
			Document doc2 = docBuilder2.parse(new File(LOVFileName.toString()));

			// Open SemanticLevelFrameWork files
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File(rulesFileName));
			NodeList listOfFrames = doc.getElementsByTagName("Frame");
			int totalFrames = listOfFrames.getLength();

			// valida variaveis para o interpreter
			i.set("inputSubject", inputSubject);
			i.set("inputPredicate", inputPredicate);
			i.set("inputObject", inputObject);

			// Get Prefix
			String prefixo = "";
			if (inputPredicate.contains(":")) {
				int index = inputPredicate.indexOf(":");
				prefixo = inputPredicate.substring(0, index);
			}

			i.set("isVocabulary", isVocabulary(prefixo, doc2));
			i.set("isOntology", isOntology(prefixo, doc2));
			i.set("s", s);

			// busca as regras
			for (int k = 0; k < totalFrames; k++) {
				Node ruleFrameNode = listOfFrames.item(k);
				if (ruleFrameNode.getNodeType() == Node.ELEMENT_NODE) {
					Element ruleFrameElement = (Element) ruleFrameNode;
					NodeList ruleList = ruleFrameElement.getElementsByTagName("Rule");
					Element ruleElement = (Element) ruleList.item(0);
					NodeList textRList = ruleElement.getChildNodes();
					NodeList levelValueList = ruleFrameElement.getElementsByTagName("LevelValue");
					Element levelValueElement = (Element) levelValueList.item(0);
					NodeList levelDescriptionList = ruleFrameElement.getElementsByTagName("LevelDescription");
					Element levelDescriptionElement = (Element) levelDescriptionList.item(0);

					NodeList textLValueList = levelValueElement.getChildNodes();
					NodeList textLDescriptionList = levelDescriptionElement.getChildNodes();

					// Rule Evaluation
					if ((Boolean) i.eval(textRList.item(0).getNodeValue().trim())) {
						assessedDescriptionLevel = textLDescriptionList.item(0).getNodeValue().trim();
						assessedValueLevel = Integer.valueOf(textLValueList.item(0).getNodeValue());

						// TODO avaliar sair do for
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
		} catch (EvalError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return assessedDescriptionLevel;
	}

	private static boolean isOntology(String prefix, Document doc) {
		NodeList listOfResults = doc.getElementsByTagName("result");
		int totalResults = listOfResults.getLength();
		for (int k = 0; k < totalResults; k++) {
			Node resultNode = listOfResults.item(k);
			Element resultElement = (Element) resultNode;
			NodeList bindingList = resultElement.getElementsByTagName("binding");
			Element bindingPrefixElement = (Element) bindingList.item(0);
			NodeList literalList = bindingPrefixElement.getElementsByTagName("literal");
			Element literalElement = (Element) literalList.item(0);
			NodeList textLiList = literalElement.getChildNodes();
			if (prefix.equals(textLiList.item(0).getNodeValue().trim())) {
				Element bindingDescElement = (Element) bindingList.item(3);
				NodeList literalDescList = bindingDescElement.getElementsByTagName("literal");
				Element literalDescElement = (Element) literalDescList.item(0);
				NodeList textDescList = literalDescElement.getChildNodes();
				Element bindingTitleElement = (Element) bindingList.item(2);
				NodeList literalTitleList = bindingTitleElement.getElementsByTagName("literal");
				Element literalTitleElement = (Element) literalTitleList.item(0);
				NodeList textTitleList = literalTitleElement.getChildNodes();
				if (textDescList.item(0).getNodeValue().trim().toLowerCase().contains("ontology")) {
					return true;
				}
				if (textTitleList.item(0).getNodeValue().trim().toLowerCase().contains("ontology")) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean isVocabulary(String prefix, Document doc) {
		NodeList listOfResults = doc.getElementsByTagName("result");
		int totalResults = listOfResults.getLength();
		for (int k = 0; k < totalResults; k++) {
			Node resultNode = listOfResults.item(k);
			Element resultElement = (Element) resultNode;
			NodeList bindingList = resultElement.getElementsByTagName("binding");
			Element bindingPrefixElement = (Element) bindingList.item(0);
			NodeList literalList = bindingPrefixElement.getElementsByTagName("literal");
			Element literalElement = (Element) literalList.item(0);
			NodeList textLiList = literalElement.getChildNodes();
			if (prefix.equals(textLiList.item(0).getNodeValue().trim())) {
				Element bindingDescElement = (Element) bindingList.item(3);
				NodeList literalDescList = bindingDescElement.getElementsByTagName("literal");
				Element literalDescElement = (Element) literalDescList.item(0);
				NodeList textDescList = literalDescElement.getChildNodes();
				Element bindingTitleElement = (Element) bindingList.item(2);
				NodeList literalTitleList = bindingTitleElement.getElementsByTagName("literal");
				Element literalTitleElement = (Element) literalTitleList.item(0);
				NodeList textTitleList = literalTitleElement.getChildNodes();
				if (textDescList.item(0).getNodeValue().trim().toLowerCase().contains("vocabulary")) {
					return true;
				}
				if (textTitleList.item(0).getNodeValue().trim().toLowerCase().contains("vocabulary")) {
					return true;
				}
			}
		}
		return false;
	}

}
