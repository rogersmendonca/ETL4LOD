package br.ufrj.ppgi.greco.kettle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
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
import org.eclipse.swt.widgets.Group;
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
import org.pentaho.di.ui.core.widget.ComboVar;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import br.ufrj.ppgi.greco.kettle.plugin.tools.datatable.DataTable;
import br.ufrj.ppgi.greco.kettle.plugin.tools.swthelper.SwtHelper;

public class DataPropertyMappingStepDialog extends BaseStepDialog implements StepDialogInterface {

	private static Class<?> PKG = DataPropertyMappingStepMeta.class;
	
	private DataPropertyMappingStepMeta input;
	private SwtHelper swthlp;
	private String dialogTitle;

	// Aba 'Mapeamento'
	private TextVar wRdfType;
	private ComboVar wSubjectFieldName;
	private TableView wMapTable;
	private org.eclipse.swt.widgets.List wRdfTypeList;

	// Aba 'Campos de saida'
	private Button wKeepInputFields;
	private TextVar wSubjectOutputFieldName;
	private TextVar wPredicateOutputFieldName;
	private TextVar wObjectOutputFieldName;
	private TextVar wDatatypeOutputFieldName;
	private TextVar wLangTagOutputFieldName;

	public DataPropertyMappingStepDialog(Shell parent, Object stepMeta, TransMeta transMeta, String stepname) {
		super(parent, (BaseStepMeta) stepMeta, transMeta, stepname);

		input = (DataPropertyMappingStepMeta) baseStepMeta;
		swthlp = new SwtHelper(transMeta, this.props);

		dialogTitle = BaseMessages.getString(PKG, "DataPropertyMappingStep.Title");
	}

	private Control buildContents(Control lastControl, ModifyListener defModListener) {

		CTabFolder wTabFolder = swthlp.appendTabFolder(shell, lastControl, 90);

		CTabItem item = new CTabItem(wTabFolder, SWT.NONE);
		item.setText(BaseMessages.getString(PKG, "DataPropertyMappingStep.Tab.Mapping"));
		Composite cpt = swthlp.appendComposite(wTabFolder, lastControl);

		wSubjectFieldName = swthlp.appendComboVarRow(cpt, null, BaseMessages.getString(PKG, "DataPropertyMappingStep.Tab.Mapping.Subject"), defModListener);
		wSubjectFieldName.setEditable(false);
		wSubjectFieldName.setItems(this.getFields(ValueMetaInterface.TYPE_STRING));

		Group wGroup = swthlp.appendGroup(cpt, wSubjectFieldName, BaseMessages.getString(PKG, "DataPropertyMappingStep.Tab.Mapping.Rdftype"), 50);
		wRdfType = swthlp.appendTextVarWithButtonRow(wGroup, null, BaseMessages.getString(PKG, "DataPropertyMappingStep.Tab.Mapping.NewRdftype"), defModListener, "+",
				new SelectionListener() {
					public void widgetSelected(SelectionEvent arg0) {
						widgetDefaultSelected(arg0);
					}

					public void widgetDefaultSelected(SelectionEvent arg0) {
						String textToAdd = wRdfType.getText().trim();
						if (!textToAdd.isEmpty()) {
							wRdfTypeList.add(textToAdd);
							wRdfType.setText("");
						}
						input.setChanged(true);
					}
				});
		wRdfType.setToolTipText(BaseMessages.getString(PKG, "DataPropertyMappingStep.Tab.Mapping.Tooltip"));

		wRdfTypeList = swthlp.appendListRow(wGroup, wRdfType, BaseMessages.getString(PKG, "DataPropertyMappingStep.Tab.Mapping.Rdftypelist"), new SelectionAdapter() {
		}, 90);
		wRdfTypeList.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.DEL) {
					int index = wRdfTypeList.getSelectionIndex();
					if (index >= 0) {
						wRdfTypeList.remove(index);
						input.setChanged(true);
					}
				}
			}

			public void keyReleased(KeyEvent e) {
			}
		});
		wRdfTypeList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if (wRdfTypeList.getSelectionIndex() >= 0)
					wRdfType.setText(wRdfTypeList.getSelection()[0]);
			}
		});

		Label wListLabel = swthlp.appendLabel(wGroup, wRdfTypeList,
				BaseMessages.getString(PKG, "DataPropertyMappingStep.Tab.Mapping.Label"));
		wListLabel.setAlignment(SWT.RIGHT);

		ColumnInfo[] columns = new ColumnInfo[] {
				new ColumnInfo(BaseMessages.getString(PKG, "DataPropertyMappingStep.Tab.Mapping.ColumnA"),
						ColumnInfo.COLUMN_TYPE_CCOMBO, this.getFields(ValueMetaInterface.TYPE_STRING)),
				new ColumnInfo(BaseMessages.getString(PKG, "DataPropertyMappingStep.Tab.Mapping.ColumnB"), ColumnInfo.COLUMN_TYPE_CCOMBO, this.getFields(), true),
				new ColumnInfo(BaseMessages.getString(PKG, "DataPropertyMappingStep.Tab.Mapping.ColumnC"), ColumnInfo.COLUMN_TYPE_CCOMBO,
						new String[] { "Tentar descobrir", "xsd:integer", "xsd:float", "xsd:double", "xsd:decimal",
								"xsd:date", "xsd:dateTime", "xsd:string" }),
				new ColumnInfo(BaseMessages.getString(PKG, "DataPropertyMappingStep.Tab.Mapping.ColumnD"), ColumnInfo.COLUMN_TYPE_CCOMBO,
						new String[] { "en", "pt", "fr", "" }),
				new ColumnInfo(BaseMessages.getString(PKG, "DataPropertyMappingStep.Tab.Mapping.ColumnE"), ColumnInfo.COLUMN_TYPE_CCOMBO, this.getFields(),
						true),

		};
		wMapTable = swthlp.appendTableView(cpt, wGroup, columns, defModListener, 98);
		item.setControl(cpt);

		item = new CTabItem(wTabFolder, SWT.NONE);
		item.setText(BaseMessages.getString(PKG, "DataPropertyMappingStep.Tab.Output"));
		cpt = swthlp.appendComposite(wTabFolder, lastControl);
		wKeepInputFields = swthlp.appendCheckboxRow(cpt, null, BaseMessages.getString(PKG, "DataPropertyMappingStep.Tab.Output.KeepInputFields"),
				new SelectionListener() {

					public void widgetDefaultSelected(SelectionEvent arg0) {
						widgetSelected(arg0);
					}

					public void widgetSelected(SelectionEvent e) {
						input.setChanged(true);
					}
				});
		wSubjectOutputFieldName = swthlp.appendTextVarRow(cpt, wKeepInputFields, BaseMessages.getString(PKG, "DataPropertyMappingStep.Tab.Output.SubjectField"),
				defModListener);
		wPredicateOutputFieldName = swthlp.appendTextVarRow(cpt, wSubjectOutputFieldName, BaseMessages.getString(PKG, "DataPropertyMappingStep.Tab.Output.PredicateField"),
				defModListener);
		wObjectOutputFieldName = swthlp.appendTextVarRow(cpt, wPredicateOutputFieldName, BaseMessages.getString(PKG, "DataPropertyMappingStep.Tab.Output.ObjectField"),
				defModListener);
		wDatatypeOutputFieldName = swthlp.appendTextVarRow(cpt, wObjectOutputFieldName,
				BaseMessages.getString(PKG, "DataPropertyMappingStep.Tab.Output.Rdftype"), defModListener);
		wLangTagOutputFieldName = swthlp.appendTextVarRow(cpt, wDatatypeOutputFieldName,
				BaseMessages.getString(PKG, "DataPropertyMappingStep.Tab.Output.Rdflangtag"), defModListener);
		item.setControl(cpt);

		wSubjectOutputFieldName
				.setToolTipText(BaseMessages.getString(PKG, "DataPropertyMappingStep.Tab.Output.Tooltipsubject"));
		wLangTagOutputFieldName.setToolTipText(BaseMessages.getString(PKG, "DataPropertyMappingStep.Tab.Output.Tooltiplangtag"));

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

	// Adiciona listeners para widgets tratarem Enter
	// The will close the window affirmatively when the user press Enter in one
	// of these text input fields
	private void addSelectionListenerToControls(SelectionAdapter lsDef) {
		wRdfType.addSelectionListener(lsDef);
		wSubjectOutputFieldName.addSelectionListener(lsDef);
		wPredicateOutputFieldName.addSelectionListener(lsDef);
		wObjectOutputFieldName.addSelectionListener(lsDef);
		wDatatypeOutputFieldName.addSelectionListener(lsDef);
		wLangTagOutputFieldName.addSelectionListener(lsDef);
	}

	public String open() {

		Shell parent = getParent();
		Display display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX);
		props.setLook(shell);
		setShellImage(shell, input);

		// ModifyListener padrao
		ModifyListener lsMod = new ModifyListener() {

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
		wlStepname.setText(BaseMessages.getString(PKG, "DataPropertyMappingStep.StepNameField.Label"));
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

		lastControl = buildContents(lastControl, lsMod);

		// Bottom buttons
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "DataPropertyMappingStep.Btn.OK"));
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "DataPropertyMappingStep.Btn.Cancel"));
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

		// Recupera dados do StepMeta e adiciona na GUI
		try {
			wRdfType.setText("");

			List<String> typesUri = input.getRdfTypeUris();
			Iterator<String> it = typesUri.iterator();
			while (it.hasNext()) {
				String rdfTypeUri = (String) it.next();
				wRdfTypeList.add(rdfTypeUri);
			}

			wSubjectFieldName.setText(Const.NVL(input.getSubjectUriFieldName(), ""));

			DataTable<String> table = input.getMapTable();
			DataTable<String>.RowFactory rf = getRowFactoryRead(table);

			for (int i = 0; i < table.size(); i++) {
				wMapTable.add(table.getRowRange(i, rf).getRow());
			}
			wMapTable.remove(0);

			wKeepInputFields.setSelection(input.isKeepInputFields());
			wSubjectOutputFieldName.setText(Const.NVL(input.getSubjectOutputFieldName(), ""));
			wPredicateOutputFieldName.setText(Const.NVL(input.getPredicateOutputFieldName(), ""));
			wObjectOutputFieldName.setText(Const.NVL(input.getObjectOutputFieldName(), ""));
			wDatatypeOutputFieldName.setText(Const.NVL(input.getDatatypeOutputFieldName(), ""));
			wLangTagOutputFieldName.setText(Const.NVL(input.getLangTagOutputFieldName(), ""));
		} catch (NullPointerException e) {

		}
	}

	protected void cancel() {
		stepname = null;
		input.setChanged(changed);
		dispose();
	}

	protected void ok() {
		if (StringUtil.isEmpty(wStepname.getText()))
			return;

		stepname = wStepname.getText();

		// Pega dados da GUI e colocar no StepMeta
		List<String> typesUri = new ArrayList<String>();
		for (int i = 0; i < wRdfTypeList.getItemCount(); i++) {
			typesUri.add(wRdfTypeList.getItem(i));
		}
		input.setRdfTypeUris(typesUri);
		input.setSubjectUriFieldName(wSubjectFieldName.getText());

		DataTable<String> table = getDataTable();
		DataTable<String>.RowFactory rf = getRowFactoryWrite(table);
		for (int i = 0; i < wMapTable.getItemCount(); i++) {
			table.add(rf.newRow(wMapTable.getItem(i)).getFullRow());
		}
		input.setMapTable(table);

		input.setKeepInputFields(wKeepInputFields.getSelection());
		input.setSubjectOutputFieldName(wSubjectOutputFieldName.getText());
		input.setPredicateOutputFieldName(wPredicateOutputFieldName.getText());
		input.setObjectOutputFieldName(wObjectOutputFieldName.getText());
		input.setDatatypeOutputFieldName(wDatatypeOutputFieldName.getText());
		input.setLangTagOutputFieldName(wLangTagOutputFieldName.getText());

		// Fecha janela
		dispose();
	}

	private DataTable<String> getDataTable() {
		return new DataTable<String>(DataPropertyMappingStepMeta.Field.MAP_TABLE.name(),
				DataPropertyMappingStepMeta.Field.MAP_TABLE_PREDICATE_URI.name(),
				DataPropertyMappingStepMeta.Field.MAP_TABLE_OBJECT_FIELD_NAME.name(),
				DataPropertyMappingStepMeta.Field.MAP_TABLE_TYPED_LITERAL.name(),
				DataPropertyMappingStepMeta.Field.MAP_TABLE_LANGUAGE_TAG.name(),
				DataPropertyMappingStepMeta.Field.MAP_TABLE_LANGTAG_FIELD_NAME.name());
	}

	private DataTable<String>.RowFactory getRowFactoryWrite(DataTable<String> table) {
		return table.newRowFactory(DataPropertyMappingStepMeta.Field.MAP_TABLE_PREDICATE_URI.name(),
				DataPropertyMappingStepMeta.Field.MAP_TABLE_OBJECT_FIELD_NAME.name(),
				DataPropertyMappingStepMeta.Field.MAP_TABLE_TYPED_LITERAL.name(),
				DataPropertyMappingStepMeta.Field.MAP_TABLE_LANGUAGE_TAG.name(),
				DataPropertyMappingStepMeta.Field.MAP_TABLE_LANGTAG_FIELD_NAME.name());
	}

	private DataTable<String>.RowFactory getRowFactoryRead(DataTable<String> table) {
		List<String> header = new ArrayList<String>();
		header.addAll(table.getHeader());
		header.remove(DataPropertyMappingStepMeta.Field.MAP_TABLE_PREDICATE_FIELD_NAME.name());
		return table.newRowFactory(header.toArray(new String[0]));
	}

}