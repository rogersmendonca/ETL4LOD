package br.ufrj.ppgi.greco.kettle;

import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.util.StringUtil;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.widget.ComboVar;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import br.ufrj.ppgi.greco.kettle.plugin.tools.swthelper.SwtHelper;

public class NTripleGeneratorStepDialog extends BaseStepDialog implements StepDialogInterface {

	private static Class<?> PKG = NTripleGeneratorStepMeta.class;

	private NTripleGeneratorStepMeta input;
	private SwtHelper swthlp;
	private String dialogTitle;

	// Adicionar variaveis dos widgets
	// Campos Step - Input
	private Group wInputGroup;
	private ComboVar wInputSubject;
	private ComboVar wInputPredicate;
	private ComboVar wInputObject;
	private Button wInnerIsLiteral;
	private ComboVar wInputDataType;
	private ComboVar wInputLangTag;

	// Campos Step - Output
	private Group wOutputGroup;
	private Button wInnerKeepInput;
	private TextVar wOutputNTriple;

	public NTripleGeneratorStepDialog(Shell parent, Object stepMeta, TransMeta transMeta, String stepname) {
		super(parent, (BaseStepMeta) stepMeta, transMeta, stepname);

		input = (NTripleGeneratorStepMeta) baseStepMeta;
		swthlp = new SwtHelper(transMeta, this.props);

		// Additional initialization here
		dialogTitle = BaseMessages.getString(PKG, "NTripleGeneratorStep.Title");
	}

	private ComboVar appendComboVar(Control lastControl, ModifyListener defModListener, Composite parent,
			String label) {
		ComboVar combo = swthlp.appendComboVarRow(parent, lastControl, label, defModListener);
		BaseStepDialog.getFieldsFromPrevious(combo, transMeta, stepMeta);
		return combo;
	}

	// Criar widgets especificos da janela
	private Control buildContents(Control lastControl, ModifyListener defModListener) {
		wInputGroup = swthlp.appendGroup(shell, lastControl, BaseMessages.getString(PKG, "NTripleGeneratorStep.Group.Input.Label"));
		wInputSubject = appendComboVar(wInputGroup, defModListener, wInputGroup, BaseMessages.getString(PKG, "NTripleGeneratorStep.SubjectField.Label"));
		wInputPredicate = appendComboVar(wInputSubject, defModListener, wInputGroup, BaseMessages.getString(PKG, "NTripleGeneratorStep.PredicateField.Label"));
		wInputObject = appendComboVar(wInputPredicate, defModListener, wInputGroup, BaseMessages.getString(PKG, "NTripleGeneratorStep.ObjectField.Label"));
		wInnerIsLiteral = swthlp.appendCheckboxRow(wInputGroup, wInputObject, BaseMessages.getString(PKG, "NTripleGeneratorStep.LiteralCheckField.Label"),
				new SelectionListener() {

					public void widgetDefaultSelected(SelectionEvent arg0) {
						widgetSelected(arg0);
					}

					public void widgetSelected(SelectionEvent e) {
						enableDataTypeAndLangTag(wInnerIsLiteral.getSelection());
						input.setChanged(true);
					}
				});
		wInputDataType = appendComboVar(wInnerIsLiteral, defModListener, wInputGroup, BaseMessages.getString(PKG, "NTripleGeneratorStep.LiteralTypeField.Label"));
		wInputLangTag = appendComboVar(wInputDataType, defModListener, wInputGroup, BaseMessages.getString(PKG, "NTripleGeneratorStep.LangtagField.Label"));

		wOutputGroup = swthlp.appendGroup(shell, wInputGroup, BaseMessages.getString(PKG, "NTripleGeneratorStep.Group.Output.Label"));
		wInnerKeepInput = swthlp.appendCheckboxRow(wOutputGroup, wOutputGroup, 
				BaseMessages.getString(PKG,"NTripleGeneratorStep.PassCheckField.Label"),
				new SelectionListener() {

					public void widgetDefaultSelected(SelectionEvent arg0) {
						widgetSelected(arg0);
					}

					public void widgetSelected(SelectionEvent e) {
						input.setChanged(true);
					}
				});
		wOutputNTriple = swthlp.appendTextVarRow(wOutputGroup, 
				wInnerKeepInput, BaseMessages.getString(PKG, "NTripleGeneratorStep.NTripleField.Label"), defModListener);

		return wOutputGroup;
	}

	private void enableDataTypeAndLangTag(boolean enable) {
		wInputDataType.setEnabled(enable);
		wInputLangTag.setEnabled(enable);
		if (!enable) {
			wInputDataType.setText("");
			wInputLangTag.setText("");
		}
	}

	private void addSelectionListenerToControls(SelectionAdapter lsDef) {
		wOutputNTriple.addSelectionListener(lsDef);
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
		wlStepname.setText(BaseMessages.getString(PKG, "NTripleGeneratorStep.StepNameField.Label"));
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
		wOK.setText(BaseMessages.getString(PKG, "NTripleGeneratorStep.Btn.OK"));
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "NTripleGeneratorStep.Btn.Cancel"));
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

		wInputSubject.setText(Const.NVL(input.getInputSubject(), ""));
		wInputPredicate.setText(Const.NVL(input.getInputPredicate(), ""));
		wInputObject.setText(Const.NVL(input.getInputObject(), ""));
		wInputDataType.setText(Const.NVL(input.getInputDataType(), ""));
		wInputLangTag.setText(Const.NVL(input.getInputLangTag(), ""));
		wOutputNTriple.setText(Const.NVL(input.getOutputNTriple(), ""));
		wInnerIsLiteral.setSelection(input.getInnerIsLiteral());
		enableDataTypeAndLangTag(wInnerIsLiteral.getSelection());
		wInnerKeepInput.setSelection(input.getInnerKeepInputFields());
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

		// Pegar dados da GUI e colocar no StepMeta
		input.setInputSubject(wInputSubject.getText());
		input.setInputPredicate(wInputPredicate.getText());
		input.setInputObject(wInputObject.getText());
		input.setInnerIsLiteral(wInnerIsLiteral.getSelection());
		input.setInputDataType(wInputDataType.getText());
		input.setInputLangTag(wInputLangTag.getText());
		input.setInnerKeepInputFields(wInnerKeepInput.getSelection());
		input.setOutputNTriple(wOutputNTriple.getText());

		// Fecha janela
		dispose();
	}
}
