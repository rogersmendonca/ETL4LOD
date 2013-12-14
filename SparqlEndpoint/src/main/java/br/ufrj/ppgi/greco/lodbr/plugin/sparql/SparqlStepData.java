package br.ufrj.ppgi.greco.lodbr.plugin.sparql;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

import com.hp.hpl.jena.query.Query;

public class SparqlStepData extends BaseStepData implements
		StepDataInterface {

	public RowMetaInterface outputRowMeta;
	public int inputRowSize;
	public Query originalQuery;
	public long offset;
	public long limit = 1000;
	public boolean runAtOnce = false;
	public int remainingTries = 3;
}
