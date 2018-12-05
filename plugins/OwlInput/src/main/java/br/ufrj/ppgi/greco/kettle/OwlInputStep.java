package br.ufrj.ppgi.greco.kettle;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
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

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.ModelFactory;

import br.ufrj.ppgi.greco.kettle.plugin.tools.datatable.DataTable;

public class OwlInputStep extends BaseStep implements StepInterface {
	boolean repetir = true;
	public OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);

	public OwlInputStep(StepMeta s, StepDataInterface stepDataInterface, int c, TransMeta t, Trans dis) {
		super(s, stepDataInterface, c, t, dis);
	}

	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		return super.init(smi, sdi);
	}

	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
		OwlInputStepMeta meta = (OwlInputStepMeta) smi;
		OwlInputStepData data = (OwlInputStepData) sdi;
		
		Object[] row = getRow();
		
		if (first) {
			first = false;
			RowMetaInterface rowMeta = getInputRowMeta(row != null);
			data.outputRowMeta = rowMeta.clone();
			meta.getFields(data.outputRowMeta, getStepname(), null, null, this);
		}
		
		DataTable<String> table = meta.getVocabTable();
		boolean isTableEmpty = table.getValue(0, OwlInputStepMeta.Field.VOCAB_TABLE_URI.name()).isEmpty();
		
		if (!isTableEmpty){
			for (int k = 0; k < table.size(); k++) {
				putOutRow(row, meta, data, 
						table.getValue(k, OwlInputStepMeta.Field.VOCAB_TABLE_PREFIX.name()),
						table.getValue(k, OwlInputStepMeta.Field.VOCAB_TABLE_URI.name()),
						table.getValue(k, OwlInputStepMeta.Field.VOCAB_TABLE_PROPERTY.name()),
						table.getValue(k, OwlInputStepMeta.Field.VOCAB_TABLE_TYPE.name()));
			}
		} else {
			table = meta.getMapTable();
			for (int k = 0; k < table.size(); k++) {
				String owlFile = getOwlFile(table.getValue(k, OwlInputStepMeta.Field.MAP_TABLE_ONTOLOGY_URI.name()));
				try {
					model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
					this.logBasic("Attempting to read " + owlFile + " as RDF/XML");
					model.read(owlFile);
					this.logBasic(owlFile + " has been read successfully");
				} catch (Exception eox) {
					this.logBasic("Error reading " + owlFile + " as RDF/XML: " + eox.getMessage());
					Collection<Lang> registeredLanguages = RDFLanguages.getRegisteredLanguages();
					for (Lang c : registeredLanguages) {
						try {
							this.logBasic("Trying to read the file as... " + c.getName());
							model.read(owlFile, c.getName());
							this.logBasic(owlFile + " has been read successfully");
							break;
						} catch (Exception e) {
							this.logBasic("File could not be read as " + c.getName() + ": " + e.getMessage());
						}
					}
				}
				
				if (!model.isEmpty()) {
					String ontoField = table.getValue(k, OwlInputStepMeta.Field.MAP_TABLE_ONTOLOGY_NAME.name());
					for (Iterator<OntClass> i = model.listClasses(); i.hasNext();) {
						OntClass cls = i.next();
						if (cls.getLocalName() != null) {
							putOutRow(row, meta, data, 
									ontoField.trim(), cls.getURI(), "rdf:type", "rdfs:class");
						}
					} 

					for (Iterator<OntProperty> j = model.listAllOntProperties(); j.hasNext();) {
						OntProperty proper = j.next();
						if (proper.getLocalName() != null) {
							putOutRow(row, meta, data, 
									ontoField.trim(), proper.getURI(), "rdf:type", "rdfs:property");
						}
					}
				}
			}
		}
		
		if (row == null){
			setOutputDone();
			return false;
		}
		
		return true;
	}
	
	/**
	 * Verifies whether src is an actual file or and URI.
	 * If the String src is a file, return it's valid path
	 * else return the URI
	 * 
	 * @param src the original file path
	 * @return a valid file path or an URI
	 */
	private String getOwlFile(String src) {
		File file = new File(src);
		if (!file.isDirectory()){
		   file = file.getParentFile();
		}
		if (file.exists()){
			return Paths.get(src).toUri().toString();
		}else {
			return src;
		}
	}

	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		super.dispose(smi, sdi);
	}
	
	private void putOutRow(Object[] inputRow, OwlInputStepMeta meta, OwlInputStepData data, String... var) throws KettleStepException {

		int outputRowPos = 0;
		Object[] outputRow = null;

		if (meta.isKeepInputFields()) {
			outputRow = inputRow;
			outputRowPos = getInputRowMeta().size();
		} else {
			outputRow = new Object[4];
		}
		
		if (outputRow != null){
			for (String arg : var) {
				outputRow = RowDataUtil.addValueData(outputRow, outputRowPos++, arg);
			}
		
			putRow(data.outputRowMeta, outputRow);
		}
	}
	
	private RowMetaInterface getInputRowMeta(boolean hasInputRow) {

		RowMetaInterface rowMeta = null;
		if (hasInputRow)
			rowMeta = getInputRowMeta();
		else
			rowMeta = new RowMeta();

		return rowMeta;
	}

}
