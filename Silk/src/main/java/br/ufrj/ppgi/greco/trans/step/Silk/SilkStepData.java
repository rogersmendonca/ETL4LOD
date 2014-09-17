package br.ufrj.ppgi.greco.trans.step.Silk;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

/**
 * Guarda dados usados durante processamento do step Silk.
 * 
 * @author Camila Carvalho Ferreira
 * 
 */
public class SilkStepData extends BaseStepData implements
        StepDataInterface
{
    public RowMetaInterface outputRowMeta;
}
