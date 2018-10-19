package br.ufrj.ppgi.greco.kettle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

/**
 * Interface de usuario do step GraphTriplify.
 * 
 * @author Rogers Reiche de Mendonca
 * 
 */
public class GraphTriplifyStepDialog extends BaseStepDialog implements StepDialogInterface {
	// for i18n purposes, needed by Translator2!! $NON-NLS-1$
	private static Class<?> PKG = GraphTriplifyStepMeta.class;

	private GraphTriplifyStepMeta input;
	private SwtHelper swthlp;
	private String dialogTitle;

	// Adicionar variaveis dos widgets
	// Campos Step - Input
	private Group wInputGroup;
	private ComboVar wInputGraph;

	// Campos Step - Output
	private Group wOutputGroup;
	private TextVar wOutputSubject;
	private TextVar wOutputPredicate;
	private TextVar wOutputObject;

	public GraphTriplifyStepDialog(Shell parent, Object stepMeta, TransMeta transMeta, String stepname) {
		super(parent, (BaseStepMeta) stepMeta, transMeta, stepname);

		input = (GraphTriplifyStepMeta) baseStepMeta;
		swthlp = new SwtHelper(transMeta, this.props);

		// Additional initialization here
		dialogTitle = BaseMessages.getString(PKG, "GraphTriplifyStep.Title");
	}

	private ComboVar appendComboVar(Control lastControl, ModifyListener defModListener, Composite parent,
			String label) {
		ComboVar combo = swthlp.appendComboVarRow(parent, lastControl, label, defModListener);
		BaseStepDialog.getFieldsFromPrevious(combo, transMeta, stepMeta);
		return combo;
	}

	// Criar widgets especificos da janela
	private Control buildContents(Control lastControl, ModifyListener defModListener) {
		wInputGroup = swthlp.appendGroup(shell, lastControl,
				BaseMessages.getString(PKG, "GraphTriplifyStep.Group.Input.Label"));
		wInputGraph = appendComboVar(wInputGroup, defModListener, wInputGroup,
				BaseMessages.getString(PKG, "GraphTriplifyStep.GraphField.Label"));

		wOutputGroup = swthlp.appendGroup(shell, wInputGroup,
				BaseMessages.getString(PKG, "GraphTriplifyStep.Group.Output.Label"));
		wOutputSubject = swthlp.appendTextVarRow(wOutputGroup, wOutputGroup,
				BaseMessages.getString(PKG, "GraphTriplifyStep.SubjectField.Label"), defModListener);
		wOutputPredicate = swthlp.appendTextVarRow(wOutputGroup, wOutputSubject,
				BaseMessages.getString(PKG, "GraphTriplifyStep.PredicateField.Label"), defModListener);
		wOutputObject = swthlp.appendTextVarRow(wOutputGroup, wOutputPredicate,
				BaseMessages.getString(PKG, "GraphTriplifyStep.ObjectField.Label"), defModListener);

		return wOutputGroup;
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
		wlStepname.setText(BaseMessages.getString(PKG, "GraphTriplifyStep.StepNameField.Label"));
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
		wOK.setText(BaseMessages.getString(PKG, "GraphTriplifyStep.Btn.OK")); //$NON-NLS-1$
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "GraphTriplifyStep.Btn.Cancel")); //$NON-NLS-1$
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

	private void addSelectionListenerToControls(SelectionAdapter lsDef) {
		wOutputSubject.addSelectionListener(lsDef);
		wOutputPredicate.addSelectionListener(lsDef);
		wOutputObject.addSelectionListener(lsDef);
	}

	private void getData() {
		wStepname.selectAll();

		wInputGraph.setText(Const.NVL(input.getInputGraph(), ""));
		wOutputSubject.setText(Const.NVL(input.getOutputSubject(), ""));
		wOutputPredicate.setText(Const.NVL(input.getOutputPredicate(), ""));
		wOutputObject.setText(Const.NVL(input.getOutputObject(), ""));
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
		input.setInputGraph(wInputGraph.getText());
		input.setOutputSubject(wOutputSubject.getText());
		input.setOutputPredicate(wOutputPredicate.getText());
		input.setOutputObject(wOutputObject.getText());

		// Fecha janela
		dispose();
	}
}
