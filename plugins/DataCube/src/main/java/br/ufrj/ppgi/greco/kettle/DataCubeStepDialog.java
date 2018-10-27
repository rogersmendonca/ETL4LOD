package br.ufrj.ppgi.greco.kettle;

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.util.StringUtil;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import br.ufrj.ppgi.greco.kettle.plugin.tools.datatable.DataTable;
import br.ufrj.ppgi.greco.kettle.plugin.tools.swthelper.SwtHelper;

public class DataCubeStepDialog extends BaseStepDialog implements StepDialogInterface {
	
	private static Class<?> PKG = DataCubeStepMeta.class;

	private DataCubeStepMeta input;
	private SwtHelper swthlp;
	private String dialogTitle;

	private TableView wDimensionTable;
	private TableView wVocabularyTable;
	//private Button wKeepInputFields;
	private TextVar wDataCubeOutputFieldName;
	protected String[][] defaultPrefixes = { { "@base", "http://example.cubeviz.org/datacube/" },
			{ "owl", "http://www.w3.org/2002/07/owl#" }, { "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#" },
			{ "rdfs", "http://www.w3.org/2000/01/rdf-schema#" }, { "dc", "http://purl.org/dc/elements/1.1/" },
			{ "skos", "http://www.w3.org/2004/02/skos/core#" },
			{ "sdmx_code", "http://purl.org/linked-data/sdmx/2009/code#" },
			{ "sdmx_dimension", "http://purl.org/linked-data/sdmx/2009/dimension#" },
			{ "sdmx-attr", "http://purl.org/linked-data/sdmx/2009/attribute#" },
			{ "sdmx-meas", "http://purl.org/linked-data/sdmx/2009/measure#" },
			{ "cube", "http://purl.org/linked-data/cube#" }, { "ex", "http://meu.exemplo/datacube/" },
			{ "exProp", "http://meu.exemplo/datacube/properties/" } };

	public DataCubeStepDialog(Shell parent, Object stepMeta, TransMeta transMeta, String stepname) {
		super(parent, (BaseStepMeta) stepMeta, transMeta, stepname);

		input = (DataCubeStepMeta) baseStepMeta;
		swthlp = new SwtHelper(transMeta, this.props);

		dialogTitle = BaseMessages.getString(PKG, "DataCubeStep.Title");
	}

	private Control buildContents(Control lastControl, ModifyListener defModListener) {

		CTabFolder wTabFolder = swthlp.appendTabFolder(shell, lastControl, 90);
		CTabItem item = new CTabItem(wTabFolder, SWT.NONE);

		item.setText(BaseMessages.getString(PKG, "DataCubeStep.Tab.Dimension"));
		Composite cpt = swthlp.appendComposite(wTabFolder, lastControl);
		ColumnInfo[] columns = new ColumnInfo[] {
				new ColumnInfo(BaseMessages.getString(PKG, "DataCubeStep.Tab.Dimension.ColumnA"), ColumnInfo.COLUMN_TYPE_CCOMBO, this.getFields(), true),
				new ColumnInfo(BaseMessages.getString(PKG, "DataCubeStep.Tab.Dimension.ColumnB"), ColumnInfo.COLUMN_TYPE_TEXT),
				new ColumnInfo(BaseMessages.getString(PKG, "DataCubeStep.Tab.Dimension.ColumnC"), ColumnInfo.COLUMN_TYPE_TEXT),
				new ColumnInfo(BaseMessages.getString(PKG, "DataCubeStep.Tab.Dimension.ColumnD"), ColumnInfo.COLUMN_TYPE_TEXT) };
		wDimensionTable = swthlp.appendTableView(cpt, null, columns, defModListener, 98);

		item.setControl(cpt);

		item = new CTabItem(wTabFolder, SWT.NONE);
		item.setText(BaseMessages.getString(PKG, "DataCubeStep.Tab.Vocabulary"));
		cpt = swthlp.appendComposite(wTabFolder, lastControl);

		columns = new ColumnInfo[] { new ColumnInfo(BaseMessages.getString(PKG, "DataCubeStep.Tab.Vocabulary.ColumnA"), ColumnInfo.COLUMN_TYPE_TEXT),
				new ColumnInfo(BaseMessages.getString(PKG, "DataCubeStep.Tab.Vocabulary.ColumnB"), ColumnInfo.COLUMN_TYPE_TEXT) };
		wVocabularyTable = swthlp.appendTableView(cpt, null, columns, defModListener, 90);

		swthlp.appendButtonsRow(cpt, wVocabularyTable, new String[] { BaseMessages.getString(PKG, "DataCubeStep.Btn.Clear"), 
				BaseMessages.getString(PKG, "DataCubeStep.Btn.Default") },
				new SelectionListener[] { new SelectionListener() {

					public void widgetSelected(SelectionEvent arg0) {
						wVocabularyTable.removeAll();
						input.setChanged();
					}

					public void widgetDefaultSelected(SelectionEvent arg0) {
						input.setChanged();
					}
				}, new SelectionListener() {

					public void widgetSelected(SelectionEvent arg0) {
						for (String[] row : defaultPrefixes)
							wVocabularyTable.add(row);
					}

					public void widgetDefaultSelected(SelectionEvent arg0) {
						input.setChanged();
					}
				} });

		item.setControl(cpt);

		item = new CTabItem(wTabFolder, SWT.NONE);
		item.setText(BaseMessages.getString(PKG, "DataCubeStep.Tab.Output") );
		cpt = swthlp.appendComposite(wTabFolder, lastControl);
		/*wKeepInputFields = swthlp.appendCheckboxRow(cpt, null, "Repassar campos de entrada para saída",
				new SelectionListener() {
					public void widgetDefaultSelected(SelectionEvent arg0) {
						widgetSelected(arg0);
					}

					public void widgetSelected(SelectionEvent e) {
						input.setChanged(true);
					}
				});*/
		wDataCubeOutputFieldName = swthlp.appendTextVarRow(cpt, null, BaseMessages.getString(PKG, "DataCubeStep.Tab.Output.Field") , defModListener);
		item.setControl(cpt);

		wTabFolder.setSelection(0);
		return wTabFolder;
	}

	private String[] getFields() {
		return getFields(-1);
	}

	private String[] getFields(int type) {

		List<String> result = new ArrayList<String>();

		try {
			RowMetaInterface inRowMeta = this.transMeta.getPrevStepFields(stepname);

			List<ValueMetaInterface> fields = inRowMeta.getValueMetaList();

			for (ValueMetaInterface field : fields) {
				if (field.getType() == type || type == -1)
					result.add(field.getName());
			}

		} catch (KettleStepException e) {
			e.printStackTrace();
		}

		return result.toArray(new String[result.size()]);
	}


	private void addSelectionListenerToControls(SelectionAdapter lsDef) {

	}

	@Override
	public String open() {

		Shell parent = getParent();
		Display display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX);
		props.setLook(shell);
		setShellImage(shell, input);

		// ModifyListener padrao
		ModifyListener lsMod = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				input.setChanged();
			}
		};
		boolean changed = input.hasChanged();

		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);

		shell.setText(dialogTitle);

		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

		// Adiciona um label e um input text no topo do dialog shell
		wlStepname = new Label(shell, SWT.RIGHT);
		wlStepname.setText(BaseMessages.getString(PKG, "DataCubeStep.StepNameField.Label"));
		props.setLook(wlStepname);

		fdlStepname = new FormData();
		fdlStepname.left = new FormAttachment(0, 0);
		fdlStepname.right = new FormAttachment(middle, -margin);
		fdlStepname.top = new FormAttachment(0, margin);
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

		// Chama metodo que adiciona os widgets especificos da janela
		lastControl = buildContents(lastControl, lsMod);

		// Bottom buttons
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "DataCubeStep.Btn.OK")); //$NON-NLS-1$
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "DataCubeStep.Btn.Cancel")); //$NON-NLS-1$
		setButtonPositions(new Button[] { wOK, wCancel }, margin, lastControl);

		// Add listeners
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

		// It closes the window affirmatively when the user press enter in one
		// of the text input fields
		lsDef = new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				ok();
			}
		};

		wStepname.addSelectionListener(lsDef);
		addSelectionListenerToControls(lsDef);

		// Detect X or ALT-F4 or something that kills this window...
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				cancel();
			}
		});

		// Populate the data of the controls
		getData();

		// Set the shell size, based upon previous time...
		setSize();

		// Alarga um pouco mais a janela
		Rectangle shellBounds = shell.getBounds();
		shellBounds.width += 5;
		shell.setBounds(shellBounds);

		input.setChanged(changed);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return stepname;
	}

	private void getData() {

		wStepname.selectAll();

		DataTable<String> dtable = input.getDimensionTable();
		DataTable<String>.RowFactory rf = getRowFactoryRead(dtable);
		for (int i = 0; i < dtable.size(); i++) {
			wDimensionTable.add(dtable.getRowRange(i, rf).getRow());
		}
		wDimensionTable.remove(0);
		input.setDimensionTable(dtable);

		dtable = input.getVocabularyTable();
		rf = getRowFactoryRead(dtable);
		for (int i = 0; i < dtable.size(); i++) {
			wVocabularyTable.add(dtable.getRowRange(i, rf).getRow());
		}
		wVocabularyTable.remove(0);
		input.setVocabularyTable(dtable);

		//wKeepInputFields.setSelection(input.isKeepInputFields());
		wDataCubeOutputFieldName.setText(Const.NVL(input.getDataCubeOutputFieldName(), ""));

	}

	protected void cancel() {
		stepname = null;
		input.setChanged(changed);
		dispose();
	}

	protected void ok() {
		if (StringUtil.isEmpty(wStepname.getText()))
			return;

		stepname = wStepname.getText(); // return value

		DataTable<String> table = this.getDimensionDataTable();
		DataTable<String>.RowFactory rf = getDimensionRowFactoryWrite(table);
		for (int i = 0; i < wDimensionTable.getItemCount(); i++) {
			table.add(rf.newRow(wDimensionTable.getItem(i)).getFullRow());
		}
		input.setDimensionTable(table);

		table = this.getVocabularyDataTable();
		rf = this.getVocabularyRowFactoryWriteVocab(table);
		for (int i = 0; i < wVocabularyTable.getItemCount(); i++) {
			table.add(rf.newRow(wVocabularyTable.getItem(i)).getFullRow());
		}
		input.setVocabularyTable(table);

		//input.setKeepInputFields(wKeepInputFields.getSelection());
		input.setDataCubeOutputFieldName(this.wDataCubeOutputFieldName.getText());

		// Fecha janela
		dispose();
	}

	private DataTable<String> getDimensionDataTable() {
		return new DataTable<String>(DataCubeStepMeta.Field.DIMENSION_TABLE.name(),
				DataCubeStepMeta.Field.DIMENSION_TABLE_NAME.name(), 
				DataCubeStepMeta.Field.DIMENSION_TABLE_URI.name(),
				DataCubeStepMeta.Field.DIMENSION_TABLE_LABEL.name(),
				DataCubeStepMeta.Field.DIMENSION_TABLE_TYPE.name());
	}

	private DataTable<String>.RowFactory getDimensionRowFactoryWrite(DataTable<String> table) {
		return table.newRowFactory(DataCubeStepMeta.Field.DIMENSION_TABLE_NAME.name(),
				DataCubeStepMeta.Field.DIMENSION_TABLE_URI.name(), 
				DataCubeStepMeta.Field.DIMENSION_TABLE_LABEL.name(),
				DataCubeStepMeta.Field.DIMENSION_TABLE_TYPE.name());
	}

	private DataTable<String> getVocabularyDataTable() {
		return new DataTable<String>(DataCubeStepMeta.Field.VOCABULARY_TABLE.name(),
				DataCubeStepMeta.Field.VOCABULARY_TABLE_PREFIX.name(),
				DataCubeStepMeta.Field.VOCABULARY_TABLE_URI.name());
	}

	private DataTable<String>.RowFactory getVocabularyRowFactoryWriteVocab(DataTable<String> table) {
		return table.newRowFactory(DataCubeStepMeta.Field.VOCABULARY_TABLE_PREFIX.name(),
				DataCubeStepMeta.Field.VOCABULARY_TABLE_URI.name());
	}

	private DataTable<String>.RowFactory getRowFactoryRead(DataTable<String> table) {
		List<String> header = new ArrayList<String>();
		header.addAll(table.getHeader());
		return table.newRowFactory(header.toArray(new String[0]));
	}
}
