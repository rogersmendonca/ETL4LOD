package br.ufrj.ppgi.greco.kettle;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
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
import org.pentaho.di.ui.core.widget.ComboVar;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import br.ufrj.ppgi.greco.kettle.plugin.tools.swthelper.SwtHelper;

public class SparqlRunQueryStepDialog extends BaseStepDialog implements StepDialogInterface {
	
	private static Class<?> PKG = SparqlRunQueryStepMeta.class;

	private SparqlRunQueryStepMeta input;
	private SwtHelper swthlp;
	private String dialogTitle;

	private ComboVar wQueryTextFieldName;
	private TextVar wEndpointUrl;
	private TextVar wUserName;
	private TextVar wPassword;
	private TextVar wStatusCode;
	private TextVar wStatusMsg;

	public SparqlRunQueryStepDialog(Shell parent, Object stepMeta, TransMeta transMeta, String stepname) {
		super(parent, (BaseStepMeta) stepMeta, transMeta, stepname);

		input = (SparqlRunQueryStepMeta) baseStepMeta;
		swthlp = new SwtHelper(transMeta, this.props);
		
		dialogTitle = BaseMessages.getString(PKG, "SparqlRunQueryStep.Title");
	}

	public String open() {

		Shell parent = getParent();
		Display display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX);
		props.setLook(shell);
		setShellImage(shell, input);

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

		shell.setText(this.dialogTitle);

		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

		// Adiciona um label e um input text no topo do dialog shell
		wlStepname = new Label(shell, SWT.RIGHT);
		wlStepname.setText(BaseMessages.getString(PKG, "SparqlRunQueryStep.StepNameField.Label"));
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

		// Adiciona
		Group wGroup1 = swthlp.appendGroup(shell, lastControl, BaseMessages.getString(PKG, "SparqlRunQueryStep.Query"));
		{
			wQueryTextFieldName = swthlp.appendComboVarRow(wGroup1, null, BaseMessages.getString(PKG, "SparqlRunQueryStep.Query.Field"), lsMod);
			wQueryTextFieldName.setItems(this.getFields(ValueMetaInterface.TYPE_STRING));

		}

		Group wGroup2 = swthlp.appendGroup(shell, wGroup1, BaseMessages.getString(PKG, "SparqlRunQueryStep.Connection"));
		{
			wEndpointUrl = swthlp.appendTextVarRow(wGroup2, null, BaseMessages.getString(PKG, "SparqlRunQueryStep.Connection.Endpoint"), lsMod);

			wUserName = swthlp.appendTextVarRow(wGroup2, wEndpointUrl, BaseMessages.getString(PKG, "SparqlRunQueryStep.Connection.User"), lsMod);

			wPassword = swthlp.appendTextVarRow(wGroup2, wUserName, BaseMessages.getString(PKG, "SparqlRunQueryStep.Connection.Password"), lsMod, true);
		}

		Group wGroup3 = swthlp.appendGroup(shell, wGroup2, BaseMessages.getString(PKG, "SparqlRunQueryStep.Output"));
		{
			wStatusCode = swthlp.appendTextVarRow(wGroup3, null, BaseMessages.getString(PKG, "SparqlRunQueryStep.Output.StatusCode"), lsMod);
			wStatusMsg = swthlp.appendTextVarRow(wGroup3, wStatusCode, BaseMessages.getString(PKG, "SparqlRunQueryStep.Output.StatusMessage"), lsMod);
		}

		lastControl = wGroup3;

		// Some buttons
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "SparqlRunQueryStep.Btn.OK"));
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "SparqlRunQueryStep.Btn.Cancel"));

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

		lsDef = new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				ok();
			}
		};

		wStepname.addSelectionListener(lsDef);
		wQueryTextFieldName.addSelectionListener(lsDef);
		wEndpointUrl.addSelectionListener(lsDef);
		wUserName.addSelectionListener(lsDef);
		wPassword.addSelectionListener(lsDef);

		// Detect X or ALT-F4 or something that kills this window...
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				cancel();
			}
		});

		// Populate the data of the controls
		//
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

		try {
			wQueryTextFieldName.setText(Const.NVL(input.getQueryTextContentFieldName(), ""));
			wEndpointUrl.setText(Const.NVL(input.getEndpointUrl(), ""));
			wUserName.setText(Const.NVL(input.getUsername(), ""));
			wPassword.setText(Const.NVL(input.getPassword(), ""));
			wStatusCode.setText(Const.NVL(input.getResultCodeFieldName(), ""));
			wStatusMsg.setText(Const.NVL(input.getResultMessageFieldName(), ""));
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

		stepname = wStepname.getText(); // return value

		try {
			input.setQueryTextContentFieldName(wQueryTextFieldName.getText());
			input.setEndpointUrl(wEndpointUrl.getText());
			input.setUsername(wUserName.getText());
			input.setPassword(wPassword.getText());
			input.setResultCodeFieldName(wStatusCode.getText());
			input.setResultMessageFieldName(wStatusMsg.getText());
		} catch (NullPointerException e) {

		}

		dispose();
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

}
