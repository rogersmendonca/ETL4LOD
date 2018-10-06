package br.ufrj.ppgi.greco.kettle;

import java.util.List;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

import br.ufrj.ppgi.greco.kettle.sparqlupdate.SparqlUpdate;

public class SparqlUpdateInsertStepData extends BaseStepData implements StepDataInterface {
	public RowMetaInterface outputRowMeta;
	public SparqlUpdate sparqlUpdate;
	public String graphUri;
	public int inputRowSize;
	public StatementValidor stmtValidator;
	public List<Object[]> tripleList;

	// Tem que colocar em outra thread pois estava travando o Kettle
	// public CommThread commth;
}
