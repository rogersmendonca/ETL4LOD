package br.ufrj.ppgi.greco.kettle;

import java.util.ArrayList;
import java.util.Arrays;

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
import org.pentaho.di.core.util.StringUtil;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import br.ufrj.ppgi.greco.kettle.plugin.tools.swthelper.SwtHelper;

/**
 * Adaptacoes: <br />
 * Na aba "Campos de Saida": <br />
 * - Exclusao dos campos: "Prefixos" e "Nomes dos campos" <br />
 * - Inclusao do campo: "Resultado" <br />
 * 
 * @author rogers
 * 
 */
public class GraphSparqlStepDialog extends BaseStepDialog implements StepDialogInterface {
	
	private static Class<?> PKG = GraphSparqlStepMeta.class;

	private GraphSparqlStepMeta input;
	private SwtHelper swthlp;
	private String dialogTitle;

	// TODO Adicionar variaveis dos widgets
	private TextVar wEndpointUri;
	private TextVar wDefaultGraph;
	private Text wQueryString;
	private Text wParserMessage;
	private CTabFolder wTabFolder;
	private TableView wPrefixes;
	protected String[][] defaultPrefixes = { { "xsd", "http://www.w3.org/2001/XMLSchema#" },
			{ "rdfs", "http://www.w3.org/2000/01/rdf-schema#" },
			{ "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#" }, { "owl", "http://www.w3.org/2002/07/owl#" },
			{ "dbpedia", "http://dbpedia.org/ontology/" }, { "dbpprop", "http://dbpedia.org/property/" },
			{ "dc", "http://purl.org/dc/elements/1.1/" }, { "foaf", "http://xmlns.com/foaf/0.1/" },
			{ "vcard", "http://www.w3.org/2006/vcard/ns#" }, { "cc", "http://creativecommons.org/ns#" },
			{ "skos", "http://www.w3.org/2004/02/skos/core#" } };
	private TextVar wOutputResult;

	public GraphSparqlStepDialog(Shell parent, Object stepMeta, TransMeta transMeta, String stepname) {
		super(parent, (BaseStepMeta) stepMeta, transMeta, stepname);

		input = (GraphSparqlStepMeta) baseStepMeta;
		swthlp = new SwtHelper(transMeta, this.props);

		// TODO: Additional initialization here
		dialogTitle = BaseMessages.getString(PKG, "GraphSparqlStep.Title");
	}

	// TODO Criar widgets especificos da janela
	private Control buildContents(Control lastControl, ModifyListener defModListener) {

		wTabFolder = swthlp.appendTabFolder(shell, lastControl, 90);

		// Create Tab
		CTabItem item = new CTabItem(wTabFolder, SWT.NONE);
		item.setText(BaseMessages.getString(PKG, "GraphSparqlStep.Tab.SPARQL"));
		Composite cpt = swthlp.appendComposite(wTabFolder, null);
		wEndpointUri = swthlp.appendTextVarRow(cpt, null, BaseMessages.getString(PKG, "GraphSparqlStep.Tab.SPARQL.Endpoint"), defModListener);
		wDefaultGraph = swthlp.appendTextVarRow(cpt, wEndpointUri, BaseMessages.getString(PKG, "GraphSparqlStep.Tab.SPARQL.Graph"), defModListener);
		item.setControl(cpt);

		// Create Tab
		item = new CTabItem(wTabFolder, SWT.NONE);
		item.setText(BaseMessages.getString(PKG, "GraphSparqlStep.Tab.Prefix"));
		ColumnInfo[] columns = new ColumnInfo[] { new ColumnInfo(BaseMessages.getString(PKG, "GraphSparqlStep.Tab.Prefix.ColumnA"), ColumnInfo.COLUMN_TYPE_TEXT),
				new ColumnInfo(BaseMessages.getString(PKG, "GraphSparqlStep.Tab.Prefix.ColumnB"), ColumnInfo.COLUMN_TYPE_TEXT) };
		cpt = swthlp.appendComposite(wTabFolder, null);
		wPrefixes = swthlp.appendTableView(cpt, null, columns, defModListener, 90);

		swthlp.appendButtonsRow(cpt, wPrefixes, new String[] { BaseMessages.getString(PKG, "GraphSparqlStep.Tab.Prefix.Clear"),  BaseMessages.getString(PKG, "GraphSparqlStep.Tab.Prefix.Default") },
				new SelectionListener[] { new SelectionListener() {
					public void widgetSelected(SelectionEvent arg0) {
						wPrefixes.removeAll();
						input.setChanged();
					}

					public void widgetDefaultSelected(SelectionEvent arg0) {
						input.setChanged();
					}
				}, new SelectionListener() {
					public void widgetSelected(SelectionEvent arg0) {
						for (String[] row : defaultPrefixes)
							wPrefixes.add(row);
					}

					public void widgetDefaultSelected(SelectionEvent arg0) {
						input.setChanged();
					}
				} });
		item.setControl(cpt);

		// Create Tab
		item = new CTabItem(wTabFolder, SWT.NONE);
		item.setText(BaseMessages.getString(PKG, "GraphSparqlStep.Tab.Query"));
		cpt = swthlp.appendComposite(wTabFolder, null);
		Label wLabel = swthlp.appendLabel(cpt, null, BaseMessages.getString(PKG, "GraphSparqlStep.Tab.Query.Label"));
		wQueryString = swthlp.appendMultiTextVarRow(cpt, wLabel, new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				input.setChanged();
				wParserMessage.setText("");
			}
		}, 55);

		SelectionListener[] listeners = new SelectionListener[] { new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				widgetDefaultSelected(arg0);
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {
				validateButton();
			}
		} };
		Composite ctl = swthlp.appendButtonsRow(cpt, wQueryString, new String[] { BaseMessages.getString(PKG, "GraphSparqlStep.Tab.Query.Validate") }, listeners);

		wParserMessage = swthlp.appendMultiTextVarRow(cpt, ctl, defModListener, 100);
		wParserMessage.setEditable(false);
		item.setControl(cpt);

		// Create Tab
		item = new CTabItem(wTabFolder, SWT.NONE);
		item.setText(BaseMessages.getString(PKG, "GraphSparqlStep.Tab.Output"));
		cpt = swthlp.appendComposite(wTabFolder, null);

		wOutputResult = swthlp.appendTextVarRow(cpt, null, BaseMessages.getString(PKG, "GraphSparqlStep.Tab.Output.Field"), defModListener);
		item.setControl(cpt);

		wTabFolder.setSelection(0);

		// Return the last created control here
		return wTabFolder;
	}

	private void validateButton() {
		try {
			String fullQueryStr = GraphSparqlStepUtils.toFullQueryString(this.getListOfPrefixesFromTableView(),
					wQueryString.getText());

			// update message
			wParserMessage.setText(GraphSparqlStepUtils.validateSparql(fullQueryStr));
		} catch (Throwable e) {

		}
	}

	// TODO Adicionar listeners para widgets tratarem Enter
	// The will close the window affirmatively when the user press Enter in one
	// of these text input fields
	private void addSelectionListenerToControls(SelectionAdapter lsDef) {
		wEndpointUri.addSelectionListener(lsDef);
		wDefaultGraph.addSelectionListener(lsDef);
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
		wlStepname.setText(BaseMessages.getString(PKG, "GraphSparqlStep.StepNameField.Label"));
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
		wOK.setText(BaseMessages.getString(PKG, "GraphSparqlStep.Btn.OK")); //$NON-NLS-1$
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "GraphSparqlStep.Btn.Cancel")); //$NON-NLS-1$
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
		try {
			wStepname.selectAll();

			// TODO Recuperar dados do StepMeta e adiciona na GUI
			String endpointUri = input.getEndpointUri();
			if (endpointUri != null)
				wEndpointUri.setText(endpointUri);

			String defaultGraph = input.getDefaultGraph();
			if (defaultGraph != null)
				wDefaultGraph.setText(defaultGraph);

			String queryStr = input.getQueryString();
			if (queryStr != null)
				wQueryString.setText(queryStr);

			java.util.List<java.util.List<String>> prefixes = input.getPrefixes();
			if (prefixes != null) {
				wPrefixes.removeAll();

				for (java.util.List<String> list : prefixes) {
					wPrefixes.add(list.get(0), list.get(1));
				}

				wPrefixes.remove(0);
			}

			wOutputResult.setText(Const.NVL(input.getVarResult(), ""));

			validateButton();
		} catch (Throwable t) {

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

		// TODO Pegar dados da GUI e colocar no StepMeta
		input.setEndpointUri(wEndpointUri.getText());
		input.setDefaultGraph(wDefaultGraph.getText());
		input.setQueryString(wQueryString.getText());

		try {
			input.setPrefixes(getListOfPrefixesFromTableView());
		} catch (Exception e) {
		}

		input.setVarResult(wOutputResult.getText());

		// Fecha janela
		dispose();
	}

	private java.util.List<java.util.List<String>> getListOfPrefixesFromTableView() {
		ArrayList<java.util.List<String>> prefixes = new ArrayList<java.util.List<String>>();
		for (int iRow = 0; iRow < wPrefixes.getItemCount(); iRow++) {
			String[] row = wPrefixes.getItem(iRow);
			prefixes.add(Arrays.asList(row[0], row[1]));
		}
		return prefixes;
	}
}
