package br.ufrj.ppgi.greco.trans.step.SemanticLevelFramework;

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

import br.ufrj.ppgi.greco.trans.step.SemanticLevelFramework.SemanticLevelFrameworkStep;
import br.ufrj.ppgi.greco.trans.step.SemanticLevelFramework.SemanticLevelFrameworkStepData;
import br.ufrj.ppgi.greco.trans.step.SemanticLevelFramework.SemanticLevelFrameworkStepDialog;

/**
 * Classe de metadados do step SemanticLevelFramework.
 * 
 * @author Camila Carvalho Ferreira
 * 
 */
public class SemanticLevelFrameworkStepMeta extends BaseStepMeta implements
        StepMetaInterface
{
	
    public enum Field
    {
        INPUT_SUBJECT_FIELD_NAME,
        INPUT_PREDICATE_FIELD_NAME,
        INPUT_OBJECT_FIELD_NAME,
        OUTPUT_NTRIPLE_FIELD_NAME,
        INNER_KEEP_INPUT_VALUE,
        INPUT_LOV_FILE_NAME,
        INPUT_RULES_FILE_NAME,
    }

    // Campos Step - Input
    private String inputSubject;
    private String inputPredicate;
    private String inputObject;
    public String LOVFilename;
    public String rulesFilename;

    // Campos Step - Output
    private String outputNTriple;

    // Campos Step - Inner
    private Boolean innerKeepInputFields;

    public SemanticLevelFrameworkStepMeta()
    {
        setDefault();
    }

    // TODO Validar todos os campos para dar feedback ao usuário! Argh!
    @Override
    public void check(List<CheckResultInterface> remarks, TransMeta transMeta,
            StepMeta stepMeta, RowMetaInterface prev, String[] input,
            String[] output, RowMetaInterface info)
    {

        CheckResultInterface ok = new CheckResult(CheckResult.TYPE_RESULT_OK,
                "", stepMeta);
        remarks.add(ok);
        // }
        if (LOVFilename==null || LOVFilename.length()==0 )
		{
        	ok = new CheckResult(CheckResult.TYPE_RESULT_ERROR, "No files can be found to read.", stepMeta);
			remarks.add(ok);
		}
		else
		{
			ok = new CheckResult(CheckResult.TYPE_RESULT_OK, "Both shape file and the DBF file are defined.", stepMeta);
			remarks.add(ok);
		}
        if (rulesFilename==null || rulesFilename.length()==0 )
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
        return new SemanticLevelFrameworkStep(stepMeta, stepDataInterface, copyNr,
                transMeta, trans);
    }

    @Override
    public StepDataInterface getStepData()
    {
        return new SemanticLevelFrameworkStepData();
    }

    @Override
    public String getDialogClassName()
    {
        return SemanticLevelFrameworkStepDialog.class.getName();
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
        innerKeepInputFields = "Y".equals(XMLHandler.getTagValue(stepDomNode,
                Field.INNER_KEEP_INPUT_VALUE.name()));
        LOVFilename = XMLHandler.getTagValue(stepDomNode, Field.INPUT_LOV_FILE_NAME.name());
        rulesFilename = XMLHandler.getTagValue(stepDomNode, Field.INPUT_RULES_FILE_NAME.name());
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
        xml.append(XMLHandler.addTagValue(Field.INNER_KEEP_INPUT_VALUE.name(),
                innerKeepInputFields));
        xml.append(XMLHandler.addTagValue(Field.INPUT_LOV_FILE_NAME.name(), LOVFilename));
        xml.append(XMLHandler.addTagValue(Field.INPUT_RULES_FILE_NAME.name(), rulesFilename));

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
        innerKeepInputFields = repository.getStepAttributeBoolean(
                stepIdInRepository, Field.INNER_KEEP_INPUT_VALUE.name());
        LOVFilename = repository.getStepAttributeString(stepIdInRepository, Field.INPUT_LOV_FILE_NAME.name());
        rulesFilename = repository.getStepAttributeString(stepIdInRepository, Field.INPUT_RULES_FILE_NAME.name());
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
                Field.INNER_KEEP_INPUT_VALUE.name(), innerKeepInputFields);
        repository.saveStepAttribute(idOfTransformation, idOfStep,
                Field.INPUT_LOV_FILE_NAME.name(), LOVFilename);
        repository.saveStepAttribute(idOfTransformation, idOfStep,
                Field.INPUT_RULES_FILE_NAME.name(), rulesFilename);
    }

    // Inicializacoes default
    @Override
    public void setDefault()
    {
        inputSubject = "";
        inputPredicate = "";
        inputObject = "";
        outputNTriple = "output";
        innerKeepInputFields = false;
        LOVFilename = "";
        rulesFilename = "";
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
    			
    			// The filename...
    			ValueMetaInterface filename2 = new ValueMeta("filename", ValueMetaInterface.TYPE_STRING);
    			filename2.setOrigin(name);
    			filename2.setLength(255);
    			inputRowMeta.addValueMeta(filename2);
    			
    			// The file type
    			ValueMetaInterface ft2 = new ValueMeta("filetype", ValueMetaInterface.TYPE_STRING);
    			ft2.setLength(50);
    			ft2.setOrigin(name);
    			inputRowMeta.addValueMeta( ft2 ); 
    			
    			// The shape nr
    			ValueMetaInterface shnr2 = new ValueMeta("shapenr", ValueMetaInterface.TYPE_INTEGER);
    			shnr2.setOrigin(name);
    			inputRowMeta.addValueMeta( shnr2 ); 

    			// The part nr
    			ValueMetaInterface pnr2 = new ValueMeta("partnr", ValueMetaInterface.TYPE_INTEGER);
    			pnr2.setOrigin(name);
    			inputRowMeta.addValueMeta( pnr2 ); 

    			// The part nr
    			ValueMetaInterface nrp2 = new ValueMeta("nrparts", ValueMetaInterface.TYPE_INTEGER);
    			nrp2.setOrigin(name);
    			inputRowMeta.addValueMeta( nrp2 ); 

    			// The point nr
    			ValueMetaInterface ptnr2 = new ValueMeta("pointnr", ValueMetaInterface.TYPE_INTEGER);
    			ptnr2.setOrigin(name);
    			inputRowMeta.addValueMeta( ptnr2 ); 

    			// The nr of points
    			ValueMetaInterface nrpt2 = new ValueMeta("nrpointS", ValueMetaInterface.TYPE_INTEGER);
    			nrpt2.setOrigin(name);
    			inputRowMeta.addValueMeta( nrpt2 ); 

    			// The X coordinate
    			ValueMetaInterface x2 = new ValueMeta("x", ValueMetaInterface.TYPE_NUMBER);
    			x2.setOrigin(name);
    			inputRowMeta.addValueMeta( x2 );

    			// The Y coordinate
    			ValueMetaInterface y2 = new ValueMeta("y", ValueMetaInterface.TYPE_NUMBER);
    			y2.setOrigin(name);
    			inputRowMeta.addValueMeta( y2 );

    			// The measure
    			ValueMetaInterface m2 = new ValueMeta("measure", ValueMetaInterface.TYPE_NUMBER);
    			m2.setOrigin(name);
    			inputRowMeta.addValueMeta( m2 );
    			
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

    public String getOutputNTriple()
    {
        return outputNTriple;
    }

    public void setOutputNTriple(String outputNTriple)
    {
        this.outputNTriple = outputNTriple;
    }

    public Boolean getInnerKeepInputFields()
    {
        return innerKeepInputFields;
    }

    public void setInnerKeepInputFields(Boolean innerKeepInputFields)
    {
        this.innerKeepInputFields = innerKeepInputFields;
    }
    
    public String getLOVFilename()
    {
    	return LOVFilename;
    }
    
    public void setLOVFilename(String LOVFilename)
    {
    	this.LOVFilename = LOVFilename;
    }
    
    public String getRulesFilename()
    {
    	return rulesFilename;
    }
    
    public void setRulesFilename(String rulesFilename)
    {
    	this.rulesFilename = rulesFilename;
    }
}
