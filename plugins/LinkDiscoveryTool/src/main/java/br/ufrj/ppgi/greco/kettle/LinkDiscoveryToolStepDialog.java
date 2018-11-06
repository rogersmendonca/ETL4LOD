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
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
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
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.ComboVar;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import br.ufrj.ppgi.greco.kettle.plugin.tools.datatable.DataTable;
import br.ufrj.ppgi.greco.kettle.plugin.tools.swthelper.SwtHelper;
import br.ufrj.ppgi.greco.kettle.silk.Metric;

public class LinkDiscoveryToolStepDialog extends BaseStepDialog implements StepDialogInterface {

	// private static Class<?> PKG = LinkDiscoveryToolStepMeta.class;

	private LinkDiscoveryToolStepMeta input;
	private SwtHelper swthlp;
	private String dialogTitle;

	private TextVar wConfigFile;

	/* tab DataSources */
	private Group wSourceEndpointGroup;
	private TextVar wSourceEndpointURL;
	private TextVar wSourceGraph;
	private TextVar wSourceRestriction;

	private Group wTargetEndpointGroup;
	private TextVar wTargetEndpointURL;
	private TextVar wTargetGraph;
	private TextVar wTargetRestriction;

	/* tab Prefixes */
	private TableView wPrefixes;

	/* tab Linkage Rules */
	private TextVar wLinkageType;
	private ComboVar wAggregationType;
	private TableView wMetrics;

	/* tab Output */
	private Group wOutputGroup;
	private TextVar wOutputFolder;
	private Button wOutputSparql;
	private TextVar wOutputEndpoint;
	private TextVar wOutputGraph;

	/* constants */
	protected String[][] defaultPrefixes = { { "xsd", "http://www.w3.org/2001/XMLSchema#" },
			{ "rdfs", "http://www.w3.org/2000/01/rdf-schema#" },
			{ "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#" }, { "owl", "http://www.w3.org/2002/07/owl#" },
			{ "dbpedia", "http://dbpedia.org/ontology/" }, { "dbpprop", "http://dbpedia.org/property/" },
			{ "dc", "http://purl.org/dc/elements/1.1/" }, { "foaf", "http://xmlns.com/foaf/0.1/" },
			{ "vcard", "http://www.w3.org/2006/vcard/ns#" }, { "cc", "http://creativecommons.org/ns#" },
			{ "skos", "http://www.w3.org/2004/02/skos/core#" }, { "gn", "http://www.geonames.org/ontology#" } };
	private String[] aggregationTypes = { "average", "min", "max", "quadraticMean", "geometricMean" };

	public LinkDiscoveryToolStepDialog(Shell parent, Object stepMeta, TransMeta transMeta, String stepname) {
		super(parent, (BaseStepMeta) stepMeta, transMeta, stepname);

		input = (LinkDiscoveryToolStepMeta) baseStepMeta;
		swthlp = new SwtHelper(transMeta, this.props);

		dialogTitle = "Link Discovery Tool";
	}

	private void fileDialogFunction(int type, String[] fileExtensions, TextVar receptor, String[] filterNames) {
		FileDialog dialog = new FileDialog(shell, type);
		dialog.setFilterExtensions(fileExtensions);
		if (receptor.getText() != null) {
			dialog.setFileName(receptor.getText());
		}

		dialog.setFilterNames(filterNames);

		if (dialog.open() != null) {
			String str = dialog.getFilterPath() + System.getProperty("file.separator") + dialog.getFileName();
			receptor.setText(str);
		}
	}

	private Control buildContents(Control lastControl, ModifyListener defModListener) {

		CTabFolder wTabFolder = swthlp.appendTabFolder(shell, lastControl, 90);

		CTabItem item = new CTabItem(wTabFolder, SWT.NONE);
		item.setText("Data Sources");
		Composite cpt = swthlp.appendComposite(wTabFolder, lastControl);

		wConfigFile = textVarWithButton(cpt, null, "Config file", defModListener, "...", new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fileDialogFunction(SWT.OPEN, new String[] { "*.xml" }, wConfigFile, new String[] { "XML files" });
			}
		});
		wConfigFile.setToolTipText("If a config file is set, all other fields are not going to be used!");

		wSourceEndpointGroup = swthlp.appendGroup(cpt, wConfigFile, "Source");
		wSourceEndpointURL = textVarWithButton(wSourceEndpointGroup, wSourceEndpointGroup, "Endpoint URL",
				defModListener, "...", new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						fileDialogFunction(SWT.OPEN, new String[] { "*.csv; *.rdf; *.ttl; *.nt; *.xml" },
								wSourceEndpointURL, new String[] { ".(csv, rdf, ttl, nt, xml) files" });
					}
				});
		wSourceGraph = swthlp.appendTextVarRow(wSourceEndpointGroup, wSourceEndpointURL, "Graph", defModListener);
		wSourceRestriction = swthlp.appendTextVarRow(wSourceEndpointGroup, wSourceGraph, "Restriction", defModListener);

		wTargetEndpointGroup = swthlp.appendGroup(cpt, wSourceEndpointGroup, "Target");
		wTargetEndpointURL = textVarWithButton(wTargetEndpointGroup, wTargetEndpointGroup, "Endpoint URL",
				defModListener, "...", new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						fileDialogFunction(SWT.OPEN, new String[] { "*.csv; *.rdf; *.ttl; *.nt; *.xml" },
								wTargetEndpointURL, new String[] { ".(csv, rdf, ttl, nt, xml) files" });
					}
				});
		wTargetGraph = swthlp.appendTextVarRow(wTargetEndpointGroup, wTargetEndpointURL, "Graph", defModListener);
		wTargetRestriction = swthlp.appendTextVarRow(wTargetEndpointGroup, wTargetGraph, "Restriction", defModListener);

		item.setControl(cpt);

		item = new CTabItem(wTabFolder, SWT.NONE);
		item.setText("Prefixes");

		ColumnInfo[] columns = new ColumnInfo[] { new ColumnInfo("Prefix", ColumnInfo.COLUMN_TYPE_TEXT),
				new ColumnInfo("Namespace                     \u00A0", ColumnInfo.COLUMN_TYPE_TEXT) };
		cpt = swthlp.appendComposite(wTabFolder, null);
		wPrefixes = swthlp.appendTableView(cpt, null, columns, defModListener, 90);

		swthlp.appendButtonsRow(cpt, wPrefixes, new String[] { "Clear", "Defaults" },
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
							insertRow(wPrefixes, row);
					}

					public void widgetDefaultSelected(SelectionEvent arg0) {
						input.setChanged();
					}
				} });
		item.setControl(cpt);

		item = new CTabItem(wTabFolder, SWT.NONE);
		item.setText("Linkage Rules");
		cpt = swthlp.appendComposite(wTabFolder, null);

		wLinkageType = swthlp.appendTextVarRow(cpt, null, "Linkage Type", defModListener);
		wAggregationType = swthlp.appendComboVarRow(cpt, wLinkageType, "Aggregation Type", defModListener);
		wAggregationType.setItems(aggregationTypes);

		columns = new ColumnInfo[] { new ColumnInfo("Source Path", ColumnInfo.COLUMN_TYPE_TEXT),
				new ColumnInfo("Target Path", ColumnInfo.COLUMN_TYPE_TEXT),
				new ColumnInfo("Metric", ColumnInfo.COLUMN_TYPE_CCOMBO, Metric.getMetricsNames()) };

		wMetrics = swthlp.appendTableView(cpt, wAggregationType, columns, defModListener, 90);

		item.setControl(cpt);

		item = new CTabItem(wTabFolder, SWT.NONE);
		item.setText("Output");
		cpt = swthlp.appendComposite(wTabFolder, lastControl);

		wOutputGroup = swthlp.appendGroup(cpt, null, "Output Config");
		
		wOutputFolder = textVarWithButton(wOutputGroup, wOutputGroup, "Output File", defModListener, "...",
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						fileDialogFunction(SWT.SAVE, new String[] { "*.nt" }, wOutputFolder,
								new String[] { "RDF/N-TRIPLES files" });
					}
				});
		
		wOutputSparql = swthlp.appendCheckboxRow(wOutputGroup, wOutputFolder,
				"Output to a Sparql Endpoint?", new SelectionListener() {

					public void widgetDefaultSelected(SelectionEvent arg0) {
						widgetSelected(arg0);
					}

					public void widgetSelected(SelectionEvent e) {
						enableSparqlGraph(wOutputSparql.getSelection());
						input.setChanged(true);
					}
				});

		wOutputEndpoint = swthlp.appendTextVarRow(wOutputGroup, wOutputSparql, "Endpoint URL", defModListener);
		wOutputGraph = swthlp.appendTextVarRow(wOutputGroup, wOutputEndpoint, "Graph", defModListener);
		
		item.setControl(cpt);

		wTabFolder.setSelection(0);

		return wTabFolder;
	}
	
	private void enableSparqlGraph(boolean enable) {
		wOutputFolder.setEnabled(!enable);
		wOutputGraph.setEnabled(enable);
		wOutputEndpoint.setEnabled(enable);
	}

	private TextVar textVarWithButton(Composite parent, Control lastControl, String label, ModifyListener lsMod,
			String btnLabel, SelectionListener listener) {
		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;
		Label wLabel = new Label(parent, SWT.RIGHT);
		wLabel.setText(label);
		props.setLook(wLabel);
		FormData fdLabel = new FormData();
		fdLabel.left = new FormAttachment(0, 0);
		fdLabel.top = new FormAttachment(lastControl, margin);
		fdLabel.right = new FormAttachment(middle, -margin);
		wLabel.setLayoutData(fdLabel);

		Button button = new Button(parent, SWT.PUSH | SWT.CENTER);
		props.setLook(button);
		button.setText(btnLabel);
		FormData fdButton = new FormData();
		fdButton.right = new FormAttachment(100, 0);
		fdButton.top = new FormAttachment(lastControl, margin);
		button.setLayoutData(fdButton);

		TextVar text = new TextVar(transMeta, parent, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(text);
		text.addModifyListener(lsMod);
		FormData fdText = new FormData();
		fdText.left = new FormAttachment(middle, 0);
		fdText.right = new FormAttachment(button, -margin);
		fdText.top = new FormAttachment(lastControl, margin);
		text.setLayoutData(fdText);

		button.addSelectionListener(listener);
		return text;
	}

	private void addSelectionListenerToControls(SelectionAdapter lsDef) {
		wSourceEndpointURL.addSelectionListener(lsDef);
		wSourceGraph.addSelectionListener(lsDef);
		wSourceRestriction.addSelectionListener(lsDef);
		wTargetEndpointURL.addSelectionListener(lsDef);
		wTargetGraph.addSelectionListener(lsDef);
		wTargetRestriction.addSelectionListener(lsDef);
		wLinkageType.addSelectionListener(lsDef);
		wAggregationType.addSelectionListener(lsDef);
		wOutputFolder.addSelectionListener(lsDef);
		wConfigFile.addSelectionListener(lsDef);
		wOutputGraph.addSelectionListener(lsDef);
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

		shell.setText(dialogTitle);

		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

		// Adiciona um label e um input text no topo do dialog shell
		wlStepname = new Label(shell, SWT.RIGHT);
		wlStepname.setText("Step Name");
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

		try {
			DataTable<String> table = input.getMetrics();
			DataTable<String>.RowFactory rf = this.getRowFactoryRead(table);

			for (int i = 0; i < table.size(); i++) {
				wMetrics.add(table.getRowRange(i, rf).getRow());
			}
			wMetrics.remove(0);

			table = input.getPrefixes();
			rf = this.getRowFactoryRead(table);

			for (int i = 0; i < table.size(); i++) {
				wPrefixes.add(table.getRowRange(i, rf).getRow());
			}
			wPrefixes.remove(0);

			this.wConfigFile.setText(Const.NVL(input.getConfigFile(), ""));

			this.wSourceEndpointURL.setText(Const.NVL(input.getSourceEndpoint(), ""));
			this.wSourceGraph.setText(Const.NVL(input.getSourceGraph(), ""));
			this.wSourceRestriction.setText(Const.NVL(input.getSourceRestriction(), ""));
			this.wTargetEndpointURL.setText(Const.NVL(input.getTargetEndpoint(), ""));
			this.wTargetGraph.setText(Const.NVL(input.getTargetGraph(), ""));
			this.wTargetRestriction.setText(Const.NVL(input.getTargetRestriction(), ""));

			this.wLinkageType.setText(Const.NVL(input.getLinkageType(), ""));
			this.wAggregationType.setText(Const.NVL(input.getAggregationType(), ""));
			
			this.wOutputFolder.setText(Const.NVL(input.getFilePath(), ""));
			wOutputSparql.setSelection(input.isSparqlOutput());
			this.enableSparqlGraph(wOutputSparql.getSelection());
			this.wOutputEndpoint.setText(Const.NVL(input.getOutputEndpoint(), ""));
			this.wOutputGraph.setText(Const.NVL(input.getOutputGraph(), ""));

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

		DataTable<String> table = new DataTable<String>(LinkDiscoveryToolStepMeta.Field.METRICS_TABLE.name(),
				LinkDiscoveryToolStepMeta.Field.METRICS_TABLE_SOURCE.name(),
				LinkDiscoveryToolStepMeta.Field.METRICS_TABLE_TARGET.name(),
				LinkDiscoveryToolStepMeta.Field.METRICS_TABLE_METRIC.name());
		DataTable<String>.RowFactory rf = getRowFactoryMetrics(table);
		for (int i = 0; i < wMetrics.getItemCount(); i++) {
			table.add(rf.newRow(wMetrics.getItem(i)).getFullRow());
		}
		input.setMetrics(table);

		table = new DataTable<String>(LinkDiscoveryToolStepMeta.Field.PREFIXES_TABLE.name(),
				LinkDiscoveryToolStepMeta.Field.PREFIXES_TABLE_PREFIX.name(),
				LinkDiscoveryToolStepMeta.Field.PREFIXES_TABLE_NAMESPACE.name());
		rf = getRowFactoryPrefixes(table);
		for (int i = 0; i < wPrefixes.getItemCount(); i++) {
			table.add(rf.newRow(wPrefixes.getItem(i)).getFullRow());
		}
		input.setPrefixes(table);

		input.setConfigFile(this.wConfigFile.getText());

		input.setSourceEndpoint(this.wSourceEndpointURL.getText());
		input.setSourceGraph(this.wSourceGraph.getText());
		input.setSourceRestriction(this.wSourceRestriction.getText());
		input.setTargetEndpoint(this.wTargetEndpointURL.getText());
		input.setTargetGraph(this.wTargetGraph.getText());
		input.setTargetRestriction(this.wTargetRestriction.getText());

		input.setLinkageType(this.wLinkageType.getText());
		input.setAggregationType(this.wAggregationType.getText());
		
		input.setSparqlOutput(this.wOutputSparql.getSelection());
		input.setOutputFolder(this.wOutputFolder.getText());
		input.setOutputEndpoint(this.wOutputEndpoint.getText());
		input.setOutputGraph(this.wOutputGraph.getText());

		dispose();
	}

	private DataTable<String>.RowFactory getRowFactoryMetrics(DataTable<String> table) {
		return table.newRowFactory(LinkDiscoveryToolStepMeta.Field.METRICS_TABLE_SOURCE.name(),
				LinkDiscoveryToolStepMeta.Field.METRICS_TABLE_TARGET.name(),
				LinkDiscoveryToolStepMeta.Field.METRICS_TABLE_METRIC.name());
	}

	private DataTable<String>.RowFactory getRowFactoryPrefixes(DataTable<String> table) {
		return table.newRowFactory(LinkDiscoveryToolStepMeta.Field.PREFIXES_TABLE_PREFIX.name(),
				LinkDiscoveryToolStepMeta.Field.PREFIXES_TABLE_NAMESPACE.name());
	}

	private DataTable<String>.RowFactory getRowFactoryRead(DataTable<String> table) {
		List<String> header = new ArrayList<String>();
		header.addAll(table.getHeader());
		return table.newRowFactory(header.toArray(new String[0]));
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
