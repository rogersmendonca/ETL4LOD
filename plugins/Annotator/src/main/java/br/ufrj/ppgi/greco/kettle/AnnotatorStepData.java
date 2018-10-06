package br.ufrj.ppgi.greco.kettle;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

/**
 * Guarda dados usados durante processamento do step Annotator.
 * 
 * @author Camila Carvalho Ferreira
 * 
 */
public class AnnotatorStepData extends BaseStepData implements StepDataInterface {
	public RowMetaInterface outputRowMeta;
}
