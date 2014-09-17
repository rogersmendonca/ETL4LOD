package br.ufrj.ppgi.greco.trans.step.Silk;

import java.io.File;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import de.fuberlin.wiwiss.silk.Silk;


/**
 * Step Silk.
 * <p />
 * Gera senten&ccedil;as RDF no formato N-Triple
 * 
 * 
 * @author Camila Carvalho Ferreira
 * 
 */
public class SilkStep extends BaseStep implements StepInterface
{
    // Constantes
	//public static final String LITERAL_OBJECT_TRIPLE_FORMAT = "<%s> <%s> %s.";
    //public static final String URI_OBJECT_TRIPLE_FORMAT = "<%s> <%s> <%s>.";

    public SilkStep(StepMeta stepMeta,
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
        SilkStepMeta meta = (SilkStepMeta) smi;
        SilkStepData data = (SilkStepData) sdi;

        // Obtem linha do fluxo de entrada e termina caso nao haja mais entrada
        
        Object[] row = getRow();
        
        File xmlfile = new File(meta.getXmlFilename());
        
        Silk.executeFile(xmlfile, "", 0, true);
		
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

            // Adiciona os metadados do step atual
            meta.getFields(data.outputRowMeta, getStepname(), null, null, this);
        }
	            
	          

        return true;
    }
}