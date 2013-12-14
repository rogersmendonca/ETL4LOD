package br.ufrj.ppgi.greco.lodbr.plugin.triplemapping;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

public class ObjectPropertyMappingStepData extends BaseStepData implements
		StepDataInterface {

	public RowMetaInterface outputRowMeta;
}
