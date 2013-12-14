package br.ufrj.ppgi.greco.lodbr.plugin.triplemapping;


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
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import br.ufrj.ppgi.greco.kettle.plugin.tools.datatable.DataTable;
import br.ufrj.ppgi.greco.kettle.plugin.tools.swthelper.SwtHelper;


public class ObjectPropertyMappingStepDialog extends BaseStepDialog implements StepDialogInterface {
	
	private ObjectPropertyMappingStepMeta input;
	private SwtHelper swthlp;
	private String dialogTitle;

	// Variaveis dos widgets
	private TableView wMapTable;
	private Button  wKeepInputFields;
	private TextVar wSubjectOutputFieldName;
	private TextVar wPredicateOutputFieldName;
	private TextVar wObjectOutputFieldName;
	
	
	
	public ObjectPropertyMappingStepDialog(Shell parent, Object stepMeta, TransMeta transMeta, String stepname) {
		super(parent, (BaseStepMeta) stepMeta, transMeta, stepname);

		input = (ObjectPropertyMappingStepMeta) baseStepMeta;
		swthlp = new SwtHelper(transMeta, this.props);
		
		// Additional initialization here
		dialogTitle = "Mapeamento de triplas ObjectProperty";
		// ...
	}
	
	
	// Cria widgets especificos da janela
	private Control buildContents(Control lastControl, ModifyListener defModListener) {
		
		CTabFolder wTabFolder = swthlp.appendTabFolder(shell, lastControl, 90);
		
		CTabItem item = new CTabItem(wTabFolder, SWT.NONE);
		item.setText("Mapeamento");
		Composite cpt = swthlp.appendComposite(wTabFolder, lastControl);

			//
			ColumnInfo[] columns = new ColumnInfo [] {
				new ColumnInfo("Campo com sujeito (URI)", ColumnInfo.COLUMN_TYPE_CCOMBO, this.getFields(), true),
				new ColumnInfo("Predicado (ObjectProperty)                     \u00A0", ColumnInfo.COLUMN_TYPE_TEXT),
				new ColumnInfo("Campo com objeto (URI)", ColumnInfo.COLUMN_TYPE_CCOMBO, this.getFields(), true)
			};
			wMapTable = swthlp.appendTableView(cpt, null, columns, defModListener, 98);
		item.setControl(cpt);
		
		
		item = new CTabItem(wTabFolder, SWT.NONE);
		item.setText("Campos de saída");
			cpt = swthlp.appendComposite(wTabFolder, lastControl);
			wKeepInputFields = swthlp.appendCheckboxRow(cpt, null, "Repassar campos de entrada para saída",
					new SelectionListener() {
						
						@Override
						public void widgetDefaultSelected(SelectionEvent arg0) {
							widgetSelected(arg0);
						}
						
						@Override
						public void widgetSelected(SelectionEvent e) {
							input.setChanged(true);
						}
					});
			wSubjectOutputFieldName = swthlp.appendTextVarRow(cpt, wKeepInputFields, "Nome do campo do sujeito", defModListener);
			wPredicateOutputFieldName = swthlp.appendTextVarRow(cpt, wSubjectOutputFieldName, "Nome do campo do predicado", defModListener);
			wObjectOutputFieldName = swthlp.appendTextVarRow(cpt, wPredicateOutputFieldName, "Nome do campo do objeto", defModListener);
			Label wLabel = swthlp.appendLabel(cpt, wObjectOutputFieldName, "Atenção: todos os três campos acima precisam ser preenchidos.");
			wLabel.setAlignment(SWT.RIGHT);
		item.setControl(cpt);

		//
		wSubjectOutputFieldName.setToolTipText("Diferentemente do Mapeamento DataProperty, este campo é obrigatório.");

		//
		wTabFolder.setSelection(0);

		// Return the last created control here
		return wTabFolder;
	}
	
	private String [] getFields() {
		return getFields(-1);
	}
	
	
	private String [] getFields(int type) {
		
		List <String> result = new ArrayList<String>();

		try {
			RowMetaInterface inRowMeta = this.transMeta.getPrevStepFields(stepname);
			
			List<ValueMetaInterface> fields = inRowMeta.getValueMetaList();
			
			for (ValueMetaInterface field : fields) {
				if (field.getType() == type || type == -1) result.add(field.getName());
			}
			
		} catch (KettleStepException e) {
			e.printStackTrace();
		}
		
		return result.toArray(new String[result.size()]);
	}


	// Adiciona listeners para widgets tratarem Enter
	// The will close the window affirmatively when the user press Enter in one of these text input fields
	private void addSelectionListenerToControls(SelectionAdapter lsDef) {
		wSubjectOutputFieldName.addSelectionListener(lsDef);
		wPredicateOutputFieldName.addSelectionListener(lsDef);
		wObjectOutputFieldName.addSelectionListener(lsDef);
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
		wlStepname.setText("Nome do step");
		props.setLook(wlStepname);

		fdlStepname = new FormData();
		fdlStepname.left = new FormAttachment(0,0);
		fdlStepname.right = new FormAttachment(middle, -margin);
		fdlStepname.top = new FormAttachment(0,margin);
		wlStepname.setLayoutData(fdlStepname);

		wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wStepname.setText(stepname);
		props.setLook(wStepname);
		
		wStepname.addModifyListener(lsMod);
		fdStepname = new FormData();
		fdStepname.left = new FormAttachment(middle,0);
		fdStepname.top = new FormAttachment(0,margin);
		fdStepname.right = new FormAttachment(100,0);
		wStepname.setLayoutData(fdStepname);
		Control lastControl = wStepname;

		
		// Chama metodo que adiciona os widgets especificos da janela
		lastControl = buildContents(lastControl, lsMod);
		
		
		// Bottom buttons
		wOK=new Button(shell, SWT.PUSH);
		wOK.setText("OK"); //$NON-NLS-1$
		wCancel=new Button(shell, SWT.PUSH);
		wCancel.setText("Cancelar"); //$NON-NLS-1$
		setButtonPositions(new Button[] { wOK, wCancel }, margin, lastControl);

		// Add listeners
		lsCancel   = new Listener() { public void handleEvent(Event e) { cancel(); } };
		lsOK       = new Listener() { public void handleEvent(Event e) { ok();     } };
		
		wCancel.addListener(SWT.Selection, lsCancel);
		wOK.addListener    (SWT.Selection, lsOK    );
		
		// It closes the window affirmatively when the user press enter in one of the text input fields
		lsDef=new SelectionAdapter() { public void widgetDefaultSelected(SelectionEvent e) { ok(); } };
		wStepname.addSelectionListener( lsDef );
		addSelectionListenerToControls(lsDef);

		// Detect X or ALT-F4 or something that kills this window...
		shell.addShellListener(	new ShellAdapter() { public void shellClosed(ShellEvent e) { cancel(); } } );

		// Populate the data of the controls
		getData();

		// Set the shell size, based upon previous time...
		setSize();
		
		input.setChanged(changed);
	
		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch()) display.sleep();
		}
		return stepname;
	}

	private void getData() {
		wStepname.selectAll();

		// Recupera dados do StepMeta e adiciona na GUI
		try {
			DataTable<String> table = input.getMapTable();
			DataTable<String>.RowFactory rf = getRowFactory(table);
			
			for (int i = 0; i < table.size(); i++) {
				wMapTable.add(table.getRowRange(i, rf).getRow());
			}
			wMapTable.remove(0);

			wKeepInputFields.setSelection(input.isKeepInputFields());
			wSubjectOutputFieldName.setText(Const.NVL(input.getSubjectOutputFieldName(), ""));
			wPredicateOutputFieldName.setText(Const.NVL(input.getPredicateOutputFieldName(), ""));
			wObjectOutputFieldName.setText(Const.NVL(input.getObjectOutputFieldName(), ""));
		}
		catch (NullPointerException e) {
			
		}
	}
 
	protected void cancel() {
		stepname=null;
		input.setChanged(changed);
		dispose();
	}

	protected void ok() {
		if (Const.isEmpty(wStepname.getText())) return;

		stepname = wStepname.getText(); // return value

		// Pega dados da GUI e colocar no StepMeta
		DataTable<String> table = input.getMapTable();
		table.clear();
		DataTable<String>.RowFactory rf = getRowFactory(table);
	
		for (int i = 0; i < wMapTable.getItemCount(); i++) {
			table.add(rf.newRow(wMapTable.getItem(i)).getFullRow());
		}
		
		input.setKeepInputFields(wKeepInputFields.getSelection());
		input.setSubjectOutputFieldName(wSubjectOutputFieldName.getText());
		input.setPredicateOutputFieldName(wPredicateOutputFieldName.getText());
		input.setObjectOutputFieldName(wObjectOutputFieldName.getText());

		// Fecha janela
		dispose();
	}
	
	
	private DataTable<String>.RowFactory getRowFactory(DataTable<String> table) {
		return table.newRowFactory(
				ObjectPropertyMappingStepMeta.Field.MAP_TABLE_SUBJECT_FIELD_NAME.name(),
				ObjectPropertyMappingStepMeta.Field.MAP_TABLE_PREDICATE_URI.name(),
				ObjectPropertyMappingStepMeta.Field.MAP_TABLE_OBJECT_FIELD_NAME.name());
	}
}
