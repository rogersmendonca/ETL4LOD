package br.ufrj.ppgi.greco.trans.step.ntriplegenerator;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

/**
 * Guarda dados usados durante processamento do step NTripleGenerator.
 * 
 * @author Rogers Reiche de Mendonca
 * 
 */
public class NTripleGeneratorStepData extends BaseStepData implements
        StepDataInterface
{
    public RowMetaInterface outputRowMeta;
}
