package br.ufrj.ppgi.greco.kettle;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

/**
 * Guarda dados usados durante processamento do step GraphTriplify.
 * 
 * @author Rogers Reiche de Mendonca
 * 
 */
public class GraphTriplifyStepData extends BaseStepData implements StepDataInterface {
	public RowMetaInterface outputRowMeta;
}
