package br.ufrj.ppgi.greco.trans.step.Annotator;

import java.util.List;
import java.util.Map;

import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.w3c.dom.Node;

import br.ufrj.ppgi.greco.trans.step.Annotator.AnnotatorStep;
import br.ufrj.ppgi.greco.trans.step.Annotator.AnnotatorStepData;
import br.ufrj.ppgi.greco.trans.step.Annotator.AnnotatorStepDialog;

/**
 * Classe de metadados do step Annotator.
 * 
 * @author Camila Carvalho Ferreira
 * 
 */
public class AnnotatorStepMeta extends BaseStepMeta implements
        StepMetaInterface
{
	
    public enum Field
    {
        INPUT_SUBJECT_FIELD_NAME,
        INPUT_PREDICATE_FIELD_NAME,
        INPUT_OBJECT_FIELD_NAME,
        //INPUT_DATATYPE_FIELD_NAME,
        //INPUT_LANGTAG_FIELD_NAME,
        OUTPUT_NTRIPLE_FIELD_NAME,
        //INNER_IS_LITERAL_VALUE,
        INNER_KEEP_INPUT_VALUE,
        INPUT_BROWSE_FILE_NAME,
    }

    // Campos Step - Input
    private String inputSubject;
    private String inputPredicate;
    private String inputObject;
    //private String inputDataType;
    //private String inputLangTag;
    public String browseFilename;

    // Campos Step - Output
    private String outputNTriple;

    // Campos Step - Inner
    //private Boolean innerIsLiteral;
    private Boolean innerKeepInputFields;

    public AnnotatorStepMeta()
    {
        setDefault();
    }

    // TODO Validar todos os campos para dar feedback ao usuário! Argh!
    @Override
    public void check(List<CheckResultInterface> remarks, TransMeta transMeta,
            StepMeta stepMeta, RowMetaInterface prev, String[] input,
            String[] output, RowMetaInterface info)
    {

        // if (Const.isEmpty(fieldName)) {
        // CheckResultInterface error = new CheckResult(
        // CheckResult.TYPE_RESULT_ERROR,
        // "error",
        // stepMeta);
        // remarks.add(error);
        // }
        // else {
        CheckResultInterface ok = new CheckResult(CheckResult.TYPE_RESULT_OK,
                "", stepMeta);
        remarks.add(ok);
        // }
        if (browseFilename==null || browseFilename.length()==0 )
		{
        	ok = new CheckResult(CheckResult.TYPE_RESULT_ERROR, "No files can be found to read.", stepMeta);
			remarks.add(ok);
		}
		else
		{
			ok = new CheckResult(CheckResult.TYPE_RESULT_OK, "Both shape file and the DBF file are defined.", stepMeta);
			remarks.add(ok);
		}
    }

    @Override
    public StepInterface getStep(StepMeta stepMeta,
            StepDataInterface stepDataInterface, int copyNr,
            TransMeta transMeta, Trans trans)
    {
        return new AnnotatorStep(stepMeta, stepDataInterface, copyNr,
                transMeta, trans);
    }

    @Override
    public StepDataInterface getStepData()
    {
        return new AnnotatorStepData();
    }

    @Override
    public String getDialogClassName()
    {
        return AnnotatorStepDialog.class.getName();
    }

    // Carregar campos a partir do XML de um .ktr
    @Override
    public void loadXML(Node stepDomNode, List<DatabaseMeta> databases,
            Map<String, Counter> sequenceCounters) throws KettleXMLException
    {
        inputSubject = XMLHandler.getTagValue(stepDomNode,
                Field.INPUT_SUBJECT_FIELD_NAME.name());
        inputPredicate = XMLHandler.getTagValue(stepDomNode,
                Field.INPUT_PREDICATE_FIELD_NAME.name());
        inputObject = XMLHandler.getTagValue(stepDomNode,
                Field.INPUT_OBJECT_FIELD_NAME.name());
        /*inputDataType = XMLHandler.getTagValue(stepDomNode,
                Field.INPUT_DATATYPE_FIELD_NAME.name());
        inputLangTag = XMLHandler.getTagValue(stepDomNode,
                Field.INPUT_LANGTAG_FIELD_NAME.name());*/
        outputNTriple = XMLHandler.getTagValue(stepDomNode,
                Field.OUTPUT_NTRIPLE_FIELD_NAME.name());
        /*innerIsLiteral = "Y".equals(XMLHandler.getTagValue(stepDomNode,
                Field.INNER_IS_LITERAL_VALUE.name()));*/
        innerKeepInputFields = "Y".equals(XMLHandler.getTagValue(stepDomNode,
                Field.INNER_KEEP_INPUT_VALUE.name()));
        browseFilename = XMLHandler.getTagValue(stepDomNode, Field.INPUT_BROWSE_FILE_NAME.name());
    }

    // Gerar XML para salvar um .ktr
    @Override
    public String getXML() throws KettleException
    {
        StringBuilder xml = new StringBuilder();

        xml.append(XMLHandler.addTagValue(
                Field.INPUT_SUBJECT_FIELD_NAME.name(), inputSubject));
        xml.append(XMLHandler.addTagValue(
                Field.INPUT_PREDICATE_FIELD_NAME.name(), inputPredicate));
        xml.append(XMLHandler.addTagValue(Field.INPUT_OBJECT_FIELD_NAME.name(),
                inputObject));
        /*xml.append(XMLHandler.addTagValue(
                Field.INPUT_DATATYPE_FIELD_NAME.name(), inputDataType));
        xml.append(XMLHandler.addTagValue(
                Field.INPUT_LANGTAG_FIELD_NAME.name(), inputLangTag));*/
        xml.append(XMLHandler.addTagValue(
                Field.OUTPUT_NTRIPLE_FIELD_NAME.name(), outputNTriple));
        /*xml.append(XMLHandler.addTagValue(Field.INNER_IS_LITERAL_VALUE.name(),
                innerIsLiteral));*/
        xml.append(XMLHandler.addTagValue(Field.INNER_KEEP_INPUT_VALUE.name(),
                innerKeepInputFields));
        xml.append(XMLHandler.addTagValue(Field.INPUT_BROWSE_FILE_NAME.name(), browseFilename));

        return xml.toString();
    }

    // Carregar campos a partir do repositorio
    @Override
    public void readRep(Repository repository, ObjectId stepIdInRepository,
            List<DatabaseMeta> databases, Map<String, Counter> sequenceCounters)
            throws KettleException
    {
        inputSubject = repository.getStepAttributeString(stepIdInRepository,
                Field.INPUT_SUBJECT_FIELD_NAME.name());
        inputPredicate = repository.getStepAttributeString(stepIdInRepository,
                Field.INPUT_PREDICATE_FIELD_NAME.name());
        inputObject = repository.getStepAttributeString(stepIdInRepository,
                Field.INPUT_OBJECT_FIELD_NAME.name());
        /*inputDataType = repository.getStepAttributeString(stepIdInRepository,
                Field.INPUT_DATATYPE_FIELD_NAME.name());
        inputLangTag = repository.getStepAttributeString(stepIdInRepository,
                Field.INPUT_LANGTAG_FIELD_NAME.name());*/
        outputNTriple = repository.getStepAttributeString(stepIdInRepository,
                Field.OUTPUT_NTRIPLE_FIELD_NAME.name());
        /*innerIsLiteral = repository.getStepAttributeBoolean(stepIdInRepository,
                Field.INNER_IS_LITERAL_VALUE.name());*/
        innerKeepInputFields = repository.getStepAttributeBoolean(
                stepIdInRepository, Field.INNER_KEEP_INPUT_VALUE.name());
        browseFilename = repository.getStepAttributeString(stepIdInRepository, Field.INPUT_BROWSE_FILE_NAME.name());
    }

    // Persistir campos no repositorio
    @Override
    public void saveRep(Repository repository, ObjectId idOfTransformation,
            ObjectId idOfStep) throws KettleException
    {
        repository.saveStepAttribute(idOfTransformation, idOfStep,
                Field.INPUT_SUBJECT_FIELD_NAME.name(), inputSubject);
        repository.saveStepAttribute(idOfTransformation, idOfStep,
                Field.INPUT_PREDICATE_FIELD_NAME.name(), inputPredicate);
        repository.saveStepAttribute(idOfTransformation, idOfStep,
                Field.INPUT_OBJECT_FIELD_NAME.name(), inputObject);
        /*repository.saveStepAttribute(idOfTransformation, idOfStep,
                Field.INPUT_DATATYPE_FIELD_NAME.name(), inputDataType);
        repository.saveStepAttribute(idOfTransformation, idOfStep,
                Field.INPUT_LANGTAG_FIELD_NAME.name(), inputLangTag);*/
        repository.saveStepAttribute(idOfTransformation, idOfStep,
                Field.OUTPUT_NTRIPLE_FIELD_NAME.name(), outputNTriple);
        /*repository.saveStepAttribute(idOfTransformation, idOfStep,
                Field.INNER_IS_LITERAL_VALUE.name(), innerIsLiteral);*/
        repository.saveStepAttribute(idOfTransformation, idOfStep,
                Field.INNER_KEEP_INPUT_VALUE.name(), innerKeepInputFields);
        repository.saveStepAttribute(idOfTransformation, idOfStep,
                Field.INPUT_BROWSE_FILE_NAME.name(), browseFilename);
    }

    // Inicializacoes default
    @Override
    public void setDefault()
    {
        inputSubject = "";
        inputPredicate = "";
        inputObject = "";
        //inputDataType = "";
        //inputLangTag = "";
        outputNTriple = "ntriple";
        //innerIsLiteral = false;
        innerKeepInputFields = false;
        browseFilename = "";
    }

    /**
     * It describes what each output row is going to look like
     */
    @Override
    public void getFields(RowMetaInterface inputRowMeta, String name,
            RowMetaInterface[] info, StepMeta nextStep, VariableSpace space)
            throws KettleStepException
    {
    	// The filename...
    			ValueMetaInterface filename = new ValueMeta("filename", ValueMetaInterface.TYPE_STRING);
    			filename.setOrigin(name);
    			filename.setLength(255);
    			inputRowMeta.addValueMeta(filename);
    			
    			// The file type
    			ValueMetaInterface ft = new ValueMeta("filetype", ValueMetaInterface.TYPE_STRING);
    			ft.setLength(50);
    			ft.setOrigin(name);
    			inputRowMeta.addValueMeta( ft ); 
    			
    			// The shape nr
    			ValueMetaInterface shnr = new ValueMeta("shapenr", ValueMetaInterface.TYPE_INTEGER);
    			shnr.setOrigin(name);
    			inputRowMeta.addValueMeta( shnr ); 

    			// The part nr
    			ValueMetaInterface pnr = new ValueMeta("partnr", ValueMetaInterface.TYPE_INTEGER);
    			pnr.setOrigin(name);
    			inputRowMeta.addValueMeta( pnr ); 

    			// The part nr
    			ValueMetaInterface nrp = new ValueMeta("nrparts", ValueMetaInterface.TYPE_INTEGER);
    			nrp.setOrigin(name);
    			inputRowMeta.addValueMeta( nrp ); 

    			// The point nr
    			ValueMetaInterface ptnr = new ValueMeta("pointnr", ValueMetaInterface.TYPE_INTEGER);
    			ptnr.setOrigin(name);
    			inputRowMeta.addValueMeta( ptnr ); 

    			// The nr of points
    			ValueMetaInterface nrpt = new ValueMeta("nrpointS", ValueMetaInterface.TYPE_INTEGER);
    			nrpt.setOrigin(name);
    			inputRowMeta.addValueMeta( nrpt ); 

    			// The X coordinate
    			ValueMetaInterface x = new ValueMeta("x", ValueMetaInterface.TYPE_NUMBER);
    			x.setOrigin(name);
    			inputRowMeta.addValueMeta( x );

    			// The Y coordinate
    			ValueMetaInterface y = new ValueMeta("y", ValueMetaInterface.TYPE_NUMBER);
    			y.setOrigin(name);
    			inputRowMeta.addValueMeta( y );

    			// The measure
    			ValueMetaInterface m = new ValueMeta("measure", ValueMetaInterface.TYPE_NUMBER);
    			m.setOrigin(name);
    			inputRowMeta.addValueMeta( m );
    			
        if (!innerKeepInputFields)
        {
            inputRowMeta.clear();
        }

        // Adiciona os metadados dos campos de output
        addValueMeta(inputRowMeta, outputNTriple,
                ValueMetaInterface.TYPE_STRING, name);
    }

    private void addValueMeta(RowMetaInterface inputRowMeta, String fieldName,
            int type, String origin)
    {
        ValueMetaInterface field = new ValueMeta(fieldName, type);
        field.setOrigin(origin);
        inputRowMeta.addValueMeta(field);
    }

    public String getInputSubject()
    {
        return inputSubject;
    }

    public void setInputSubject(String inputSubject)
    {
        this.inputSubject = inputSubject;
    }

    public String getInputPredicate()
    {
        return inputPredicate;
    }

    public void setInputPredicate(String inputPredicate)
    {
        this.inputPredicate = inputPredicate;
    }

    public String getInputObject()
    {
        return inputObject;
    }

    public void setInputObject(String inputObject)
    {
        this.inputObject = inputObject;
    }

    /*public String getInputDataType()
    {
        return inputDataType;
    }

    public void setInputDataType(String inpuDataType)
    {
        this.inputDataType = inpuDataType;
    }*/

    public String getOutputNTriple()
    {
        return outputNTriple;
    }

    public void setOutputNTriple(String outputNTriple)
    {
        this.outputNTriple = outputNTriple;
    }

    /*public Boolean getInnerIsLiteral()
    {
        return innerIsLiteral;
    }

    public void setInnerIsLiteral(Boolean innerIsLiteral)
    {
        this.innerIsLiteral = innerIsLiteral;
    }

    public String getInputLangTag()
    {
        return inputLangTag;
    }

    public void setInputLangTag(String inpuLangTag)
    {
        this.inputLangTag = inpuLangTag;
    }*/

    public Boolean getInnerKeepInputFields()
    {
        return innerKeepInputFields;
    }

    public void setInnerKeepInputFields(Boolean innerKeepInputFields)
    {
        this.innerKeepInputFields = innerKeepInputFields;
    }
    
    public String getBrowseFilename()
    {
    	return browseFilename;
    }
    
    public void setBrowseFilename(String browseFilename)
    {
    	this.browseFilename = browseFilename;
    }
}
