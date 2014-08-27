package br.ufrj.ppgi.greco.trans.step.Silk;

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

import br.ufrj.ppgi.greco.trans.step.Silk.SilkStep;
import br.ufrj.ppgi.greco.trans.step.Silk.SilkStepData;
import br.ufrj.ppgi.greco.trans.step.Silk.SilkStepDialog;

/**
 * Classe de metadados do step Silk.
 * 
 * @author Camila Carvalho Ferreira
 * 
 */
public class SilkStepMeta extends BaseStepMeta implements
        StepMetaInterface
{
	
    public enum Field
    {

        INPUT_XML_FILE_NAME,
    }

    // Campos Step - Input
    public String XmlFilename;

    public SilkStepMeta()
    {
        setDefault();
    }

    // TODO Validar todos os campos para dar feedback ao usuario! Argh!
    @Override
    public void check(List<CheckResultInterface> remarks, TransMeta transMeta,
            StepMeta stepMeta, RowMetaInterface prev, String[] input,
            String[] output, RowMetaInterface info)
    {

        CheckResultInterface ok = new CheckResult(CheckResult.TYPE_RESULT_OK,
                "", stepMeta);
        remarks.add(ok);
        // }
        if (XmlFilename==null || XmlFilename.length()==0 )
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
        return new SilkStep(stepMeta, stepDataInterface, copyNr,
                transMeta, trans);
    }

    @Override
    public StepDataInterface getStepData()
    {
        return new SilkStepData();
    }

    @Override
    public String getDialogClassName()
    {
        return SilkStepDialog.class.getName();
    }

    // Carregar campos a partir do XML de um .ktr
    @Override
    public void loadXML(Node stepDomNode, List<DatabaseMeta> databases,
            Map<String, Counter> sequenceCounters) throws KettleXMLException
    {
        
        XmlFilename = XMLHandler.getTagValue(stepDomNode, Field.INPUT_XML_FILE_NAME.name());
        
    }

    // Gerar XML para salvar um .ktr
    @Override
    public String getXML() throws KettleException
    {
        StringBuilder xml = new StringBuilder();

        xml.append(XMLHandler.addTagValue(Field.INPUT_XML_FILE_NAME.name(), XmlFilename));

        return xml.toString();
    }

    // Carregar campos a partir do repositorio
    @Override
    public void readRep(Repository repository, ObjectId stepIdInRepository,
            List<DatabaseMeta> databases, Map<String, Counter> sequenceCounters)
            throws KettleException
    {
        
        XmlFilename = repository.getStepAttributeString(stepIdInRepository, Field.INPUT_XML_FILE_NAME.name());
        
    }

    // Persistir campos no repositorio
    @Override
    public void saveRep(Repository repository, ObjectId idOfTransformation,
            ObjectId idOfStep) throws KettleException
    {
        repository.saveStepAttribute(idOfTransformation, idOfStep,
                Field.INPUT_XML_FILE_NAME.name(), XmlFilename);

    }

    // Inicializacoes default
    @Override
    public void setDefault()
    {

        XmlFilename = "";

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
    			   			
    }
    
    public String getXmlFilename()
    {
    	return XmlFilename;
    }
    
    public void setXmlFilename(String XmlFilename)
    {
    	this.XmlFilename = XmlFilename;
    }
    
}
