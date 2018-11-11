package br.ufrj.ppgi.greco.kettle;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.util.StringUtil;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.ModelFactory;

import br.ufrj.ppgi.greco.kettle.owlutils.LOVApiV2;
import br.ufrj.ppgi.greco.kettle.owlutils.LOVAttributes;
import br.ufrj.ppgi.greco.kettle.plugin.tools.datatable.DataTable;
import br.ufrj.ppgi.greco.kettle.plugin.tools.swthelper.SwtHelper;

import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.logging.*;

public class OwlInputStepDialog extends BaseStepDialog implements StepDialogInterface {
	
	private static Class<?> PKG = OwlInputStepMeta.class;

	private OwlInputStepMeta meta;
	
	private static final Logger log = Logger.getLogger(OwlInputStep.class.getName());
	
	private String dialogTitle;
	private TextVar wHelloFieldName;
	private TableView wTable;
	private TableView wVocabTable;

	private Button wKeepInputFields;
	private TextVar wOntologyOutputFieldName;
	private TextVar wURIOutputFieldName;

	private SwtHelper swthlp;

	public OwlInputStepDialog(Shell parent, Object in, TransMeta transMeta, String sname) {
		super(parent, (BaseStepMeta) in, transMeta, sname);
		meta = (OwlInputStepMeta) in;
		swthlp = new SwtHelper(transMeta, this.props);
		
		dialogTitle = BaseMessages.getString(PKG, "OwlInputStep.Title");
	}

	private Control buildContents(Control lastControl, ModifyListener defModListener) {
		CTabFolder wTabFolder = swthlp.appendTabFolder(shell, lastControl, 90);
		CTabItem item = new CTabItem(wTabFolder, SWT.NONE);
		item.setText(BaseMessages.getString(PKG, "OwlInputStep.Tab.Owl"));
		Composite cpt = swthlp.appendComposite(wTabFolder, lastControl);
		wHelloFieldName = swthlp.appendTextVarRow(cpt, null, BaseMessages.getString(PKG, "OwlInputStep.Tab.Owl.Input"), defModListener);
		Composite btnRow = swthlp.appendButtonsRow(cpt, wHelloFieldName,
				new String[] { BaseMessages.getString(PKG, "OwlInputStep.Tab.Owl.Add"), BaseMessages.getString(PKG, "OwlInputStep.Tab.Owl.Openfile") }, new SelectionListener[] { new SelectionListener() {
					public void widgetSelected(SelectionEvent arg0) {
						widgetDefaultSelected(arg0);
					}

					public void widgetDefaultSelected(SelectionEvent arg0) {
						addUri();
						meta.setChanged(true);
					}
				}, new SelectionListener() {
					public void widgetSelected(SelectionEvent arg0) {
						widgetDefaultSelected(arg0);
					}

					public void widgetDefaultSelected(SelectionEvent arg0) {
						loadFile();
						meta.setChanged(true);
					}
				} });
		ColumnInfo[] columns = new ColumnInfo[] {
				new ColumnInfo(BaseMessages.getString(PKG, "OwlInputStep.Tab.Owl.ColumnA"), ColumnInfo.COLUMN_TYPE_TEXT),
				new ColumnInfo(BaseMessages.getString(PKG, "OwlInputStep.Tab.Owl.ColumnB"), ColumnInfo.COLUMN_TYPE_TEXT),
				new ColumnInfo(BaseMessages.getString(PKG, "OwlInputStep.Tab.Owl.ColumnC"), ColumnInfo.COLUMN_TYPE_TEXT) };
		wTable = swthlp.appendTableView(cpt, btnRow, columns, defModListener, 98);
		item.setControl(cpt);
		
		item = new CTabItem(wTabFolder, SWT.NONE);
		item.setText(BaseMessages.getString(PKG, "OwlInputStep.Tab.Select"));
		cpt = swthlp.appendComposite(wTabFolder, lastControl);
		Composite vocabBtn = swthlp.appendButtonsRow(cpt, wHelloFieldName,
				new String[] { BaseMessages.getString(PKG, "OwlInputStep.Tab.Select.Load"), BaseMessages.getString(PKG, "OwlInputStep.Tab.Select.Clear") }, new SelectionListener[] { new SelectionListener() {
					public void widgetSelected(SelectionEvent arg0) {
						widgetDefaultSelected(arg0);
					}

					public void widgetDefaultSelected(SelectionEvent arg0) {
						load();
						meta.setChanged(true);
					}
				}, new SelectionListener() {
					public void widgetSelected(SelectionEvent arg0) {
						widgetDefaultSelected(arg0);
					}

					public void widgetDefaultSelected(SelectionEvent arg0) {
						wVocabTable.clearAll();
						meta.setChanged(true);
					}
				} });
		columns = new ColumnInfo[] {
				new ColumnInfo(BaseMessages.getString(PKG, "OwlInputStep.Tab.Select.ColumnA"), ColumnInfo.COLUMN_TYPE_TEXT),
				new ColumnInfo(BaseMessages.getString(PKG, "OwlInputStep.Tab.Select.ColumnB"), ColumnInfo.COLUMN_TYPE_TEXT),
				new ColumnInfo(BaseMessages.getString(PKG, "OwlInputStep.Tab.Select.ColumnC"), ColumnInfo.COLUMN_TYPE_TEXT), 
				new ColumnInfo(BaseMessages.getString(PKG, "OwlInputStep.Tab.Select.ColumnD"), ColumnInfo.COLUMN_TYPE_TEXT) };
		wVocabTable = swthlp.appendTableView(cpt, vocabBtn, columns, defModListener, 98);
		item.setControl(cpt);

		item = new CTabItem(wTabFolder, SWT.NONE);
		item.setText(BaseMessages.getString(PKG, "OwlInputStep.Tab.Output"));
		cpt = swthlp.appendComposite(wTabFolder, lastControl);
		wKeepInputFields = swthlp.appendCheckboxRow(cpt, null, BaseMessages.getString(PKG, "OwlInputStep.Tab.Output.KeepInputFields"),
				new SelectionListener() {
					public void widgetDefaultSelected(SelectionEvent arg0) {
						widgetSelected(arg0);
					}

					public void widgetSelected(SelectionEvent e) {
						meta.setChanged(true);
					}
				});
		wOntologyOutputFieldName = swthlp.appendTextVarRow(cpt, wKeepInputFields, BaseMessages.getString(PKG, "OwlInputStep.Tab.Output.PrefixField"), defModListener);
		wURIOutputFieldName = swthlp.appendTextVarRow(cpt, wOntologyOutputFieldName, BaseMessages.getString(PKG, "OwlInputStep.Tab.Output.VocabularyField"), defModListener);
		item.setControl(cpt);

		wTabFolder.setSelection(0);
		return wTabFolder;
	}
	
	private void load(){
		wVocabTable.clearAll();
		
		for (int k = 0; k < wTable.getItemCount(); k++) {
			String ontoField = wTable.getItem(k, 1);
			String owlFile = getOwlFile(wTable.getItem(k, 2));
			try {
				meta.model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
				meta.model.read(owlFile);
			} catch (Exception eox) {
				Collection<Lang> registeredLanguages = RDFLanguages.getRegisteredLanguages();
				for (Lang c : registeredLanguages) {
					try {
						meta.model.read(owlFile, c.getName());
						break;
					} catch (Exception e) {
						this.logBasic("File could not be read as " + c.getName() + ": " + e.getMessage());
					}
				}
			}
			
			if (!meta.model.isEmpty()) {
				for (Iterator<OntClass> i = meta.model.listClasses(); i.hasNext();) {
					OntClass cls = i.next();
					if (cls.getLocalName() != null) {
						this.insertRow(wVocabTable, new String[] {ontoField.trim(), cls.getURI(), "rdf:type", "rdfs:class"});
					}
				} 

				for (Iterator<OntProperty> j = meta.model.listAllOntProperties(); j.hasNext();) {
					OntProperty proper = j.next();
					if (proper.getLocalName() != null) {
						this.insertRow(wVocabTable, new String[] {ontoField.trim(), proper.getURI(), "rdf:type", "rdfs:property"});
					}
				}
			}
		}
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


	// The will close the window affirmatively when the user press Enter in one
	// of these text input fields
	private void addSelectionListenerToControls(SelectionAdapter lsDef) {
		wOntologyOutputFieldName.addSelectionListener(lsDef);
		wURIOutputFieldName.addSelectionListener(lsDef);
		wHelloFieldName.addSelectionListener(lsDef);
	}

	public String open() {

		Shell parent = getParent();
		Display display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX);
		props.setLook(shell);
		setShellImage(shell, meta);

		ModifyListener lsMod = new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				meta.setChanged();
			}
		};

		changed = meta.hasChanged();

		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);

		shell.setText(this.dialogTitle);

		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

		wlStepname = new Label(shell, SWT.RIGHT);
		wlStepname.setText(BaseMessages.getString(PKG, "OwlInputStep.StepNameField.Label"));
		props.setLook(wlStepname);

		fdlStepname = new FormData();
		fdlStepname.left = new FormAttachment(0, 0);
		fdlStepname.top = new FormAttachment(0, margin);
		fdlStepname.right = new FormAttachment(middle, -margin);
		wlStepname.setLayoutData(fdlStepname);

		wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wStepname.setText(stepname);
		props.setLook(wStepname);

		wStepname.addModifyListener(lsMod);
		fdStepname = new FormData();
		fdStepname.left = new FormAttachment(middle, 0);
		fdStepname.top = new FormAttachment(0, margin);
		fdStepname.right = new FormAttachment(100, 0);
		wStepname.setLayoutData(fdStepname);
		Control lastControl = wStepname;

		lastControl = buildContents(lastControl, lsMod);

		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "OwlInputStep.Btn.OK"));
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "OwlInputStep.Btn.Cancel"));
		setButtonPositions(new Button[] { wOK, wCancel }, margin, lastControl);

		lsCancel = new Listener() {
			public void handleEvent(Event e) {
				cancel();
			}
		};
		lsOK = new Listener() {
			public void handleEvent(Event e) {
				ok();
			}
		};

		wCancel.addListener(SWT.Selection, lsCancel);
		wOK.addListener(SWT.Selection, lsOK);

		lsDef = new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				ok();
			}
		};

		wStepname.addSelectionListener(lsDef);
		addSelectionListenerToControls(lsDef);

		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				cancel();
			}
		});

		setSize();

		populateDialog();

		meta.setChanged(changed);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		return stepname;
	}

	private void populateDialog() {
		wStepname.selectAll();
		DataTable<String> dtable = meta.getMapTable();
		DataTable<String>.RowFactory rf = getRowFactoryRead(dtable);
		for (int i = 0; i < dtable.size(); i++) {
			wTable.add(dtable.getRowRange(i, rf).getRow());
		}
		wTable.remove(0);
		meta.setMapTable(dtable);
		
		dtable = meta.getVocabTable();
		rf = getRowFactoryRead(dtable);
		for (int i = 0; i < dtable.size(); i++) {
			wVocabTable.add(dtable.getRowRange(i, rf).getRow());
		}
		wVocabTable.remove(0);
		meta.setVocabTable(dtable);

		wKeepInputFields.setSelection(meta.isKeepInputFields());
		wOntologyOutputFieldName.setText(Const.NVL(meta.getOntologyOutputFieldName(), ""));
		wURIOutputFieldName.setText(Const.NVL(meta.getUriOutputFieldName(), ""));

	}

	private void cancel() {
		stepname = null;
		meta.setChanged(changed);
		dispose();
	}

	private void ok() {
		if (StringUtil.isEmpty(wStepname.getText()))
			return;

		stepname = wStepname.getText(); 

		DataTable<String> table = getDataTable();
		DataTable<String>.RowFactory rf = getRowFactoryWrite(table);
		for (int i = 0; i < wTable.getItemCount(); i++) {
			table.add(rf.newRow(wTable.getItem(i)).getFullRow());
		}
		meta.setMapTable(table);
		
		table = this.getDataTableVocab();
		rf = this.getRowFactoryWriteVocab(table);
		for (int i = 0; i < wVocabTable.getItemCount(); i++) {
			table.add(rf.newRow(wVocabTable.getItem(i)).getFullRow());
		}
		meta.setVocabTable(table);
		
		meta.setKeepInputFields(wKeepInputFields.getSelection());
		meta.setOntologyOutputFieldName(wOntologyOutputFieldName.getText());
		meta.setUriOutputFieldName(wURIOutputFieldName.getText());
		dispose();
	}

	private DataTable<String> getDataTable() {
		return new DataTable<String>(OwlInputStepMeta.Field.MAP_TABLE.name(),
				OwlInputStepMeta.Field.MAP_TABLE_ONTOLOGY_NAME.name(),
				OwlInputStepMeta.Field.MAP_TABLE_ONTOLOGY_URI.name(),
				OwlInputStepMeta.Field.MAP_TABLE_ONTOLOGY_DESCRIPTION.name());
	}

	private DataTable<String>.RowFactory getRowFactoryWrite(DataTable<String> table) {
		return table.newRowFactory(OwlInputStepMeta.Field.MAP_TABLE_ONTOLOGY_NAME.name(),
				OwlInputStepMeta.Field.MAP_TABLE_ONTOLOGY_URI.name(),
				OwlInputStepMeta.Field.MAP_TABLE_ONTOLOGY_DESCRIPTION.name());
	}

	private DataTable<String> getDataTableVocab() {
		return new DataTable<String>(OwlInputStepMeta.Field.VOCAB_TABLE.name(),
				OwlInputStepMeta.Field.VOCAB_TABLE_PREFIX.name(),
				OwlInputStepMeta.Field.VOCAB_TABLE_URI.name(),
				OwlInputStepMeta.Field.VOCAB_TABLE_PROPERTY.name(),
				OwlInputStepMeta.Field.VOCAB_TABLE_TYPE.name());
	}

	private DataTable<String>.RowFactory getRowFactoryWriteVocab(DataTable<String> table) {
		return table.newRowFactory(OwlInputStepMeta.Field.VOCAB_TABLE_PREFIX.name(),
				OwlInputStepMeta.Field.VOCAB_TABLE_URI.name(),
				OwlInputStepMeta.Field.VOCAB_TABLE_PROPERTY.name(),
				OwlInputStepMeta.Field.VOCAB_TABLE_TYPE.name());
	}

	private DataTable<String>.RowFactory getRowFactoryRead(DataTable<String> table) {
		List<String> header = new ArrayList<String>();
		header.addAll(table.getHeader());
		return table.newRowFactory(header.toArray(new String[0]));
	}

	private void loadFile() {
		try {
			FileDialog dialog = new FileDialog(shell, SWT.OPEN);
			dialog.setText(BaseMessages.getString(PKG, "OwlInputStep.AddOwl"));
			dialog.open();
			String[] row = { dialog.getFileName(), dialog.getFilterPath() + "/" + dialog.getFileName(),
					BaseMessages.getString(PKG, "OwlInputStep.OwlDescription") };
			this.insertRow(wTable, row);
		} catch (Exception e) {
			log.log(Level.SEVERE, e.toString(), e);
		}

	}

	private void addUri() {
		String data = wHelloFieldName.getText().trim();
		wHelloFieldName.setText(data);
		if (data.equals("")) {
			MessageBox dialog = new MessageBox(shell, SWT.ICON_QUESTION | SWT.OK);
			dialog.setText(BaseMessages.getString(PKG, "OwlInputStep.OwlDialog"));
			dialog.setMessage(BaseMessages.getString(PKG, "OwlInputStep.OwlError"));

			dialog.open();
		} else {
			Pattern pat = Pattern.compile("^http.*");
			pat.matcher(wHelloFieldName.getText());

			LOVAttributes attributes = null;

			try {
				attributes = LOVApiV2.vocabularySearch(wHelloFieldName.getText().trim());
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (attributes != null && !attributes.isEmpty()) {
				String[] row = { attributes.getPrefix(), attributes.getURI(), attributes.getDescription() };
				this.insertRow(wTable, row);
			} else {
				MessageBox dialog = new MessageBox(shell, SWT.ICON_QUESTION | SWT.OK);
				dialog.setText(BaseMessages.getString(PKG, "OwlInputStep.OwlDialog"));
				dialog.setMessage(BaseMessages.getString(PKG, "OwlInputStep.OwlError"));

				dialog.open();
			}

		}
	}

	private void insertRow(TableView tvTable, String[] row) {
		if (tvTable.table.getItem(0).getText(1).equals("")) {
			for (int i = 0; i < row.length; i++)
				tvTable.table.getItem(0).setText(i + 1, row[i]);
		} else {
			tvTable.add(row);
		}
	}

}
