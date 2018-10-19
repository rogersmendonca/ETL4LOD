package br.ufrj.ppgi.greco.kettle;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

/**
 * Guarda dados usados durante processamento do step GraphSemanticLevelMarker.
 * 
 * @author Kelli de Faria Cordeiro
 * 
 */
public class GraphSemanticLevelMarkerStepData extends BaseStepData implements StepDataInterface {
	public RowMetaInterface outputRowMeta;
}
