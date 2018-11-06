package br.ufrj.ppgi.greco.kettle;

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

import br.ufrj.ppgi.greco.kettle.silk.SilkRunner;

public class LinkDiscoveryToolStep extends BaseStep implements StepInterface {
	
	private SilkRunner sr = null;
	
	public LinkDiscoveryToolStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		return super.init(smi, sdi);
	}

	@Override
	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		super.dispose(smi, sdi);
	}

	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {

		LinkDiscoveryToolStepMeta meta = (LinkDiscoveryToolStepMeta) smi;
		LinkDiscoveryToolStepData data = (LinkDiscoveryToolStepData) sdi;

		if (first) {
			first = false;
			
			RowMetaInterface rowMeta = new RowMeta();
			data.outputRowMeta = rowMeta.clone();
			
			meta.getFields(data.outputRowMeta, getStepname(), null, null, this);
			
			executeSilk(meta);
		}
		
		return first;
	}
	
	private void executeSilk(LinkDiscoveryToolStepMeta meta){
		try {
			this.logBasic("Silk Single Machine is running... ");
			sr = new SilkRunner(meta);
			String configFile = "";
			if (meta.getConfigFile() == null || meta.getConfigFile().equals("")){
				sr.saveXML();
				configFile = sr.getConfigFilename();
			} else {
				configFile = meta.getConfigFile();
			}
			this.logBasic("SLS file: " + configFile);
			this.logBasic("Linking resources. This may take a while...");
			sr.run(configFile);
			this.logBasic(sr.getLogs());
		} catch (Exception e) {
			this.logError(e.getMessage());
			e.printStackTrace();
		}
		this.logBasic("Silk has finished linking!");
	}
}
