package br.ufrj.ppgi.greco.trans.step.SemanticLevelMarker;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

/**
 * Guarda dados usados durante processamento do step SemanticLevelMarker.
 * 
 * @author Kelli de Faria Cordeiro
 * 
 */
public class SemanticLevelMarkerStepData extends BaseStepData implements
        StepDataInterface
{
    public RowMetaInterface outputRowMeta;
}
