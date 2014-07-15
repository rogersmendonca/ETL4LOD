package br.ufrj.ppgi.greco.trans.step.SemanticLevelMarker;

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

import br.ufrj.ppgi.greco.trans.step.SemanticLevelMarker.SemanticLevelMarkerStepData;
import br.ufrj.ppgi.greco.trans.step.SemanticLevelMarker.SemanticLevelMarkerStepDialog;
import br.ufrj.ppgi.greco.trans.step.SemanticLevelMarker.SemanticLevelMarkerStep;

/**
 * Classe de metadados do step SemanticLevelMarker.
 * 
 * @author Kelli de Faria Cordeiro
 * 
 */
public class SemanticLevelMarkerStepMeta extends BaseStepMeta implements
        StepMetaInterface
{
    public enum Field
    {
        INPUT_SUBJECT_FIELD_NAME,
        INPUT_PREDICATE_FIELD_NAME,
        INPUT_OBJECT_FIELD_NAME,
        OUTPUT_NTRIPLE_FIELD_NAME,
        INNER_IS_LITERAL_VALUE,
        INNER_KEEP_INPUT_VALUE,
    }

    // Campos Step - Input
    private String inputSubject;
    private String inputPredicate;
    private String inputObject;
    
    // Campos Step - Output
    private String outputNTriple;

    // Campos Step - Inner
    private Boolean innerIsLiteral;
    private Boolean innerKeepInputFields;
    
    public SemanticLevelMarkerStepMeta()
    {
        setDefault();
    }

    // TODO Validar todos os campos para dar feedback ao usu�rio! Argh!
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
    }

    @Override
    public StepInterface getStep(StepMeta stepMeta,
            StepDataInterface stepDataInterface, int copyNr,
            TransMeta transMeta, Trans trans)
    {
        return new SemanticLevelMarkerStep(stepMeta, stepDataInterface, copyNr,
                transMeta, trans);
    }

    @Override
    public StepDataInterface getStepData()
    {
        return new SemanticLevelMarkerStepData();
    }

    @Override
    public String getDialogClassName()
    {
        return SemanticLevelMarkerStepDialog.class.getName();
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
        outputNTriple = XMLHandler.getTagValue(stepDomNode,
                Field.OUTPUT_NTRIPLE_FIELD_NAME.name());
        innerIsLiteral = "Y".equals(XMLHandler.getTagValue(stepDomNode,
                Field.INNER_IS_LITERAL_VALUE.name()));
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
        xml.append(XMLHandler.addTagValue(
                Field.OUTPUT_NTRIPLE_FIELD_NAME.name(), outputNTriple));
        xml.append(XMLHandler.addTagValue(Field.INNER_IS_LITERAL_VALUE.name(),
                innerIsLiteral));
        xml.append(XMLHandler.addTagValue(Field.INNER_KEEP_INPUT_VALUE.name(),
                innerKeepInputFields));
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
        outputNTriple = repository.getStepAttributeString(stepIdInRepository,
                Field.OUTPUT_NTRIPLE_FIELD_NAME.name());
        innerIsLiteral = repository.getStepAttributeBoolean(stepIdInRepository,
                Field.INNER_IS_LITERAL_VALUE.name());
        innerKeepInputFields = repository.getStepAttributeBoolean(
                stepIdInRepository, Field.INNER_KEEP_INPUT_VALUE.name());
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
        repository.saveStepAttribute(idOfTransformation, idOfStep,
                Field.OUTPUT_NTRIPLE_FIELD_NAME.name(), outputNTriple);
        repository.saveStepAttribute(idOfTransformation, idOfStep,
                Field.INNER_IS_LITERAL_VALUE.name(), innerIsLiteral);
        repository.saveStepAttribute(idOfTransformation, idOfStep,
                Field.INNER_KEEP_INPUT_VALUE.name(), innerKeepInputFields);
    }

    // Inicializacoes default
    @Override
    public void setDefault()
    {
        inputSubject = "";
        inputPredicate = "";
        inputObject = "";
        outputNTriple = "ntriple";
        innerIsLiteral = false;
        innerKeepInputFields = false;
    }

    /**
     * It describes what each output row is going to look like
     */
    @Override
    public void getFields(RowMetaInterface inputRowMeta, String name,
            RowMetaInterface[] info, StepMeta nextStep, VariableSpace space)
            throws KettleStepException
    {
    
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

        public String getOutputNTriple()
    {
        return outputNTriple;
    }

    public void setOutputNTriple(String outputNTriple)
    {
        this.outputNTriple = outputNTriple;
    }

    public Boolean getInnerIsLiteral()
    {
        return innerIsLiteral;
    }

    public void setInnerIsLiteral(Boolean innerIsLiteral)
    {
        this.innerIsLiteral = innerIsLiteral;
    }
    public Boolean getInnerKeepInputFields()
    {
        return innerKeepInputFields;
    }
    public void setInnerKeepInputFields(Boolean innerKeepInputFields)
    {
        this.innerKeepInputFields = innerKeepInputFields;
    }
       
}
