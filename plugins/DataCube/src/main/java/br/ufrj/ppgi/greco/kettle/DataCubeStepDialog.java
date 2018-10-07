package br.ufrj.ppgi.greco.kettle;

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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.util.StringUtil;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.widget.ComboVar;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import br.ufrj.ppgi.greco.kettle.plugin.tools.swthelper.SwtHelper;

public class DataCubeStepDialog extends BaseStepDialog implements StepDialogInterface {

	private DataCubeStepMeta input;
	private SwtHelper swthlp;
	private String dialogTitle;

	private Group wInputGroup;
	private ComboVar wInputDimensao1;
	private ComboVar wInputDimensao2;
	private ComboVar wInputDimensao3;
	private ComboVar wInputDimensao4;
	private ComboVar wInputDimensao5;
	private ComboVar wInputDimensao6;
	private ComboVar wInputDimensao7;
	private ComboVar wInputDimensao8;
	private ComboVar wInputDimensao9;
	private ComboVar wInputDimensao10;

	private Group wOutputGroup;
	private Button wInnerKeepInput;
	private TextVar wOutputSaida;
	private TextVar wOutputTeste;
	private TextVar cabecalho1;
	private TextVar cabecalho2;
	private TextVar cabecalho3;
	private TextVar cabecalho4;
	private TextVar cabecalho5;
	private TextVar cabecalho6;
	private TextVar cabecalho7;
	private TextVar cabecalho8;
	private TextVar cabecalho9;
	private TextVar cabecalho10;
	private TextVar cabecalho11;

	private TextVar structureDefinition1;
	private TextVar structureDefinition2;
	private TextVar structureDefinition3;
	private TextVar structureDefinition4;
	private TextVar structureDefinition5;
	private TextVar structureDefinition6;
	private TextVar structureDefinition7;
	private TextVar structureDefinition8;
	private TextVar structureDefinition9;
	private TextVar structureDefinition10;

	private TextVar label1;
	private TextVar label2;
	private TextVar label3;
	private TextVar label4;
	private TextVar label5;
	private TextVar label6;
	private TextVar label7;
	private TextVar label8;
	private TextVar label9;
	private TextVar label10;

	public DataCubeStepDialog(Shell parent, Object stepMeta, TransMeta transMeta, String stepname) {
		super(parent, (BaseStepMeta) stepMeta, transMeta, stepname);

		input = (DataCubeStepMeta) baseStepMeta;
		swthlp = new SwtHelper(transMeta, this.props);

		dialogTitle = "Data Cube Transformator";
	}

	private ComboVar appendComboVar(Control lastControl, ModifyListener defModListener, Composite parent,
			String label) {
		ComboVar combo = swthlp.appendComboVarRow(parent, lastControl, label, defModListener);
		BaseStepDialog.getFieldsFromPrevious(combo, transMeta, stepMeta);
		return combo;
	}

	// TODO Criar widgets especificos da janela
	private Control buildContents(Control lastControl, ModifyListener defModListener) {

		CTabFolder wTabFolder = swthlp.appendTabFolder(shell, lastControl, 90);
		CTabItem item = new CTabItem(wTabFolder, SWT.NONE);
		item.setText("Definicao das dimensoes");
		Composite cpt = swthlp.appendComposite(wTabFolder, lastControl);

		wOutputSaida = swthlp.appendTextVarRow(cpt, wOutputSaida, "URI do tipo da Medida", defModListener);

		wInputDimensao1 = appendComboVar(wOutputSaida, defModListener, cpt, "Dimensao 1");
		wInputDimensao2 = appendComboVar(wInputDimensao1, defModListener, cpt, " Dimensao 2");
		wInputDimensao3 = appendComboVar(wInputDimensao2, defModListener, cpt, "Dimensao 3");
		wInputDimensao4 = appendComboVar(wInputDimensao3, defModListener, cpt, "Dimensao 4");
		wInputDimensao5 = appendComboVar(wInputDimensao4, defModListener, cpt, "Dimensao 5");
		wInputDimensao6 = appendComboVar(wInputDimensao5, defModListener, cpt, "Dimensao 6");
		wInputDimensao7 = appendComboVar(wInputDimensao6, defModListener, cpt, "Dimensao 7");
		wInputDimensao8 = appendComboVar(wInputDimensao7, defModListener, cpt, "Dimensao 8");
		wInputDimensao9 = appendComboVar(wInputDimensao8, defModListener, cpt, "Dimensao 9");
		wInputDimensao10 = appendComboVar(wInputDimensao9, defModListener, cpt, "Medida");
		item.setControl(cpt);

		item = new CTabItem(wTabFolder, SWT.NONE);
		item.setText("Definicao dos vocabularios");
		cpt = swthlp.appendComposite(wTabFolder, lastControl);

		cabecalho1 = swthlp.appendTextVarRow(cpt, cabecalho1, "@base", defModListener);

		cabecalho2 = swthlp.appendTextVarRow(cpt, cabecalho1, "@prefix owl:", defModListener);

		cabecalho3 = swthlp.appendTextVarRow(cpt, cabecalho2, "@prefix rdf:", defModListener);

		cabecalho4 = swthlp.appendTextVarRow(cpt, cabecalho3, "@prefix rdfs:", defModListener);

		cabecalho5 = swthlp.appendTextVarRow(cpt, cabecalho4, "@prefix dc:", defModListener);

		cabecalho6 = swthlp.appendTextVarRow(cpt, cabecalho5, "@prefix skos:", defModListener);

		cabecalho7 = swthlp.appendTextVarRow(cpt, cabecalho6, "@prefix sdmx_code:", defModListener);

		cabecalho8 = swthlp.appendTextVarRow(cpt, cabecalho7, "@prefix sdmx_dimension:", defModListener);

		cabecalho9 = swthlp.appendTextVarRow(cpt, cabecalho8, "@prefix cube:", defModListener);

		cabecalho10 = swthlp.appendTextVarRow(cpt, cabecalho9, "@prefix ex:", defModListener);

		cabecalho11 = swthlp.appendTextVarRow(cpt, cabecalho10, "@prefix exProp:", defModListener);
		item.setControl(cpt);

		// Cria��o de tab
		item = new CTabItem(wTabFolder, SWT.NONE);
		item.setText("Definir a estrutura de cada dimensao (URIs)");
		cpt = swthlp.appendComposite(wTabFolder, lastControl);

		structureDefinition1 = swthlp.appendTextVarRow(cpt, structureDefinition1, "URI da primeira dimensao escolhida",
				defModListener);
		structureDefinition2 = swthlp.appendTextVarRow(cpt, structureDefinition1, "URI da segunda dimensao escolhida",
				defModListener);
		structureDefinition3 = swthlp.appendTextVarRow(cpt, structureDefinition2, "URI da terceira dimensao escolhida",
				defModListener);
		structureDefinition4 = swthlp.appendTextVarRow(cpt, structureDefinition3, "URI da quarta dimensao escolhida",
				defModListener);
		structureDefinition5 = swthlp.appendTextVarRow(cpt, structureDefinition4, "URI da quinta dimensao escolhida",
				defModListener);
		structureDefinition6 = swthlp.appendTextVarRow(cpt, structureDefinition5, "URI da sexta dimensao escolhida",
				defModListener);
		structureDefinition7 = swthlp.appendTextVarRow(cpt, structureDefinition6, "URI da setima dimensao escolhida",
				defModListener);
		structureDefinition8 = swthlp.appendTextVarRow(cpt, structureDefinition7, "URI da oitava dimensao escolhida",
				defModListener);
		structureDefinition9 = swthlp.appendTextVarRow(cpt, structureDefinition8, "URI da nona dimensao escolhida",
				defModListener);
		structureDefinition10 = swthlp.appendTextVarRow(cpt, structureDefinition9, "URI da medida", defModListener);
		item.setControl(cpt);

		// Cria��o de tab
		item = new CTabItem(wTabFolder, SWT.NONE);
		item.setText("Labels de cada Dimensao usada");
		cpt = swthlp.appendComposite(wTabFolder, lastControl);
		label1 = swthlp.appendTextVarRow(cpt, label1, "Label da primeira dimensao", defModListener);

		label2 = swthlp.appendTextVarRow(cpt, label1, "Label da segunda dimensao", defModListener);

		label3 = swthlp.appendTextVarRow(cpt, label2, "Label da terceira dimensao", defModListener);

		label4 = swthlp.appendTextVarRow(cpt, label3, "Label da quarta dimensao", defModListener);

		label5 = swthlp.appendTextVarRow(cpt, label4, "Label da quinta dimensao", defModListener);

		label6 = swthlp.appendTextVarRow(cpt, label5, "Label da sexta dimensao", defModListener);

		label7 = swthlp.appendTextVarRow(cpt, label6, "Label da setima dimensao", defModListener);

		label8 = swthlp.appendTextVarRow(cpt, label7, "Label da oitava dimensao", defModListener);

		label9 = swthlp.appendTextVarRow(cpt, label8, "Label da nona dimensao", defModListener);

		label10 = swthlp.appendTextVarRow(cpt, label9, "Label da medida", defModListener);

		item.setControl(cpt);

		wOutputGroup = swthlp.appendGroup(shell, wInputGroup, "Campo de URI");

		wInnerKeepInput = swthlp.appendCheckboxRow(wOutputGroup, wOutputGroup,
				"Visualisar os campos do CSV (nao realiza a transformacao)", new SelectionListener() {
					@Override
					public void widgetDefaultSelected(SelectionEvent arg0) {
						widgetSelected(arg0);
					}

					@Override
					public void widgetSelected(SelectionEvent e) {
						input.setChanged(true);
					}
				});

		wOutputTeste = swthlp.appendTextVarRow(wOutputGroup, wOutputSaida, "Teste", defModListener);

		wTabFolder.setSelection(0);
		return wTabFolder;
	}

	// TODO Adicionar listeners para widgets tratarem Enter
	// The will close the window affirmatively when the user press Enter in one
	// of these text input fields
	private void addSelectionListenerToControls(SelectionAdapter lsDef) {

		wOutputSaida.addSelectionListener(lsDef);
		wOutputTeste.addSelectionListener(lsDef);
		cabecalho1.addSelectionListener(lsDef);
		cabecalho2.addSelectionListener(lsDef);
		cabecalho3.addSelectionListener(lsDef);
		cabecalho4.addSelectionListener(lsDef);
		cabecalho5.addSelectionListener(lsDef);
		cabecalho6.addSelectionListener(lsDef);
		cabecalho7.addSelectionListener(lsDef);
		cabecalho8.addSelectionListener(lsDef);
		cabecalho9.addSelectionListener(lsDef);
		cabecalho10.addSelectionListener(lsDef);
		cabecalho11.addSelectionListener(lsDef);
		structureDefinition1.addSelectionListener(lsDef);
		structureDefinition2.addSelectionListener(lsDef);
		structureDefinition3.addSelectionListener(lsDef);
		structureDefinition4.addSelectionListener(lsDef);
		structureDefinition5.addSelectionListener(lsDef);
		structureDefinition6.addSelectionListener(lsDef);
		structureDefinition7.addSelectionListener(lsDef);
		structureDefinition8.addSelectionListener(lsDef);
		structureDefinition9.addSelectionListener(lsDef);
		structureDefinition10.addSelectionListener(lsDef);
		label1.addSelectionListener(lsDef);
		label2.addSelectionListener(lsDef);
		label3.addSelectionListener(lsDef);
		label4.addSelectionListener(lsDef);
		label5.addSelectionListener(lsDef);
		label6.addSelectionListener(lsDef);
		label7.addSelectionListener(lsDef);
		label8.addSelectionListener(lsDef);
		label9.addSelectionListener(lsDef);
		label10.addSelectionListener(lsDef);

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
		wlStepname.setText("Data Cube Transformation");
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
		wOK.setText("OK"); //$NON-NLS-1$
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText("Cancel"); //$NON-NLS-1$
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

		wInputDimensao1.setText(Const.NVL(input.getInputDimensao1(), ""));
		wInputDimensao2.setText(Const.NVL(input.getInputDimensao2(), ""));
		wInputDimensao3.setText(Const.NVL(input.getInputDimensao3(), ""));
		wInputDimensao4.setText(Const.NVL(input.getInputDimensao4(), ""));
		wInputDimensao5.setText(Const.NVL(input.getInputDimensao5(), ""));
		wInputDimensao6.setText(Const.NVL(input.getInputDimensao6(), ""));
		wInputDimensao7.setText(Const.NVL(input.getInputDimensao7(), ""));
		wInputDimensao8.setText(Const.NVL(input.getInputDimensao8(), ""));
		wInputDimensao9.setText(Const.NVL(input.getInputDimensao9(), ""));
		wInputDimensao10.setText(Const.NVL(input.getInputDimensao10(), ""));

		wOutputSaida.setText(Const.NVL(input.getOutputSaida(), ""));
		wOutputTeste.setText(Const.NVL(input.getOutputTeste(), ""));
		cabecalho1.setText(Const.NVL(input.getcabecalho1(), ""));
		cabecalho2.setText(Const.NVL(input.getcabecalho2(), ""));
		cabecalho3.setText(Const.NVL(input.getcabecalho3(), ""));
		cabecalho4.setText(Const.NVL(input.getcabecalho4(), ""));
		cabecalho5.setText(Const.NVL(input.getcabecalho5(), ""));
		cabecalho6.setText(Const.NVL(input.getcabecalho6(), ""));
		cabecalho7.setText(Const.NVL(input.getcabecalho7(), ""));
		cabecalho8.setText(Const.NVL(input.getcabecalho8(), ""));
		cabecalho9.setText(Const.NVL(input.getcabecalho9(), ""));
		cabecalho10.setText(Const.NVL(input.getcabecalho10(), ""));
		cabecalho11.setText(Const.NVL(input.getcabecalho11(), ""));
		structureDefinition1.setText(Const.NVL(input.getstructureDefinition1(), ""));
		structureDefinition2.setText(Const.NVL(input.getstructureDefinition2(), ""));
		structureDefinition3.setText(Const.NVL(input.getstructureDefinition3(), ""));
		structureDefinition4.setText(Const.NVL(input.getstructureDefinition4(), ""));
		structureDefinition5.setText(Const.NVL(input.getstructureDefinition5(), ""));
		structureDefinition6.setText(Const.NVL(input.getstructureDefinition6(), ""));
		structureDefinition7.setText(Const.NVL(input.getstructureDefinition7(), ""));
		structureDefinition8.setText(Const.NVL(input.getstructureDefinition8(), ""));
		structureDefinition9.setText(Const.NVL(input.getstructureDefinition9(), ""));
		structureDefinition10.setText(Const.NVL(input.getstructureDefinition10(), ""));
		label1.setText(Const.NVL(input.getlabel1(), ""));
		label2.setText(Const.NVL(input.getlabel2(), ""));
		label3.setText(Const.NVL(input.getlabel3(), ""));
		label4.setText(Const.NVL(input.getlabel4(), ""));
		label5.setText(Const.NVL(input.getlabel5(), ""));
		label6.setText(Const.NVL(input.getlabel6(), ""));
		label7.setText(Const.NVL(input.getlabel7(), ""));
		label8.setText(Const.NVL(input.getlabel8(), ""));
		label9.setText(Const.NVL(input.getlabel9(), ""));
		label10.setText(Const.NVL(input.getlabel10(), ""));
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
		input.setInputDimensao1(wInputDimensao1.getText());
		input.setInputDimensao2(wInputDimensao2.getText());
		input.setInputDimensao3(wInputDimensao3.getText());
		input.setInputDimensao4(wInputDimensao4.getText());
		input.setInputDimensao5(wInputDimensao5.getText());
		input.setInputDimensao6(wInputDimensao6.getText());
		input.setInputDimensao7(wInputDimensao7.getText());
		input.setInputDimensao8(wInputDimensao8.getText());
		input.setInputDimensao9(wInputDimensao9.getText());
		input.setInputDimensao10(wInputDimensao10.getText());
		input.setInnerKeepInputFields(wInnerKeepInput.getSelection());
		input.setOutputSaida(wOutputSaida.getText());
		input.setOutputTeste(wOutputTeste.getText());
		input.setcabecalho1(cabecalho1.getText());
		input.setcabecalho2(cabecalho2.getText());
		input.setcabecalho3(cabecalho3.getText());
		input.setcabecalho4(cabecalho4.getText());
		input.setcabecalho5(cabecalho5.getText());
		input.setcabecalho6(cabecalho6.getText());
		input.setcabecalho7(cabecalho7.getText());
		input.setcabecalho8(cabecalho8.getText());
		input.setcabecalho9(cabecalho9.getText());
		input.setcabecalho10(cabecalho10.getText());
		input.setcabecalho11(cabecalho11.getText());
		input.setstructureDefinition1(structureDefinition1.getText());
		input.setstructureDefinition2(structureDefinition2.getText());
		input.setstructureDefinition3(structureDefinition3.getText());
		input.setstructureDefinition4(structureDefinition4.getText());
		input.setstructureDefinition5(structureDefinition5.getText());
		input.setstructureDefinition6(structureDefinition6.getText());
		input.setstructureDefinition7(structureDefinition7.getText());
		input.setstructureDefinition8(structureDefinition8.getText());
		input.setstructureDefinition9(structureDefinition9.getText());
		input.setstructureDefinition10(structureDefinition10.getText());
		input.setlabel1(label1.getText());
		input.setlabel2(label2.getText());
		input.setlabel3(label3.getText());
		input.setlabel4(label4.getText());
		input.setlabel5(label5.getText());
		input.setlabel6(label6.getText());
		input.setlabel7(label7.getText());
		input.setlabel8(label8.getText());
		input.setlabel9(label9.getText());
		input.setlabel10(label10.getText());

		// Fecha janela
		dispose();
	}
}
