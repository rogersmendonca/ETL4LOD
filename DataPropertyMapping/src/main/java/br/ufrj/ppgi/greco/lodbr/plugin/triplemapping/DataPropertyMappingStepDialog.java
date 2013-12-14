package br.ufrj.ppgi.greco.lodbr.plugin.triplemapping;

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

public class DataPropertyMappingStepDialog extends BaseStepDialog implements
        StepDialogInterface
{

    private DataPropertyMappingStepMeta input;
    private SwtHelper swthlp;
    private String dialogTitle;

    // Variaveis dos widgets

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

    public DataPropertyMappingStepDialog(Shell parent, Object stepMeta,
            TransMeta transMeta, String stepname)
    {
        super(parent, (BaseStepMeta) stepMeta, transMeta, stepname);

        input = (DataPropertyMappingStepMeta) baseStepMeta;
        swthlp = new SwtHelper(transMeta, this.props);

        // Additional initialization here
        dialogTitle = "Mapeamento de triplas DataProperty";
        // ...
    }

    // Cria widgets especificos da janela
    private Control buildContents(Control lastControl,
            ModifyListener defModListener)
    {

        CTabFolder wTabFolder = swthlp.appendTabFolder(shell, lastControl, 90);

        CTabItem item = new CTabItem(wTabFolder, SWT.NONE);
        item.setText("Mapeamento");
        Composite cpt = swthlp.appendComposite(wTabFolder, lastControl);
        //
        wSubjectFieldName = swthlp.appendComboVarRow(cpt, null,
                "Campo contendo URI do sujeito", defModListener);
        wSubjectFieldName.setEditable(false);
        wSubjectFieldName.setItems(this
                .getFields(ValueMetaInterface.TYPE_STRING));

        //
        Group wGroup = swthlp.appendGroup(cpt, wSubjectFieldName,
                "RDF Types (rdf:type da linha)", 50);
        wRdfType = swthlp.appendTextVarWithButtonRow(wGroup, null,
                "RDF Type a adicionar", defModListener, "+",
                new SelectionListener()
                {
                    @Override
                    public void widgetSelected(SelectionEvent arg0)
                    {
                        widgetDefaultSelected(arg0);
                    }

                    @Override
                    public void widgetDefaultSelected(SelectionEvent arg0)
                    {
                        String textToAdd = wRdfType.getText().trim();
                        if (!textToAdd.isEmpty())
                            wRdfTypeList.add(textToAdd);
                        input.setChanged(true);
                    }
                });
        wRdfType.setToolTipText("Digite o rdf:type de cada linha vinda do fluxo e clique no botão '+' para adicionar");

        wRdfTypeList = swthlp.appendListRow(wGroup, wRdfType,
                "Tipos RDF da linha", new SelectionAdapter()
                {
                }, 90);
        wRdfTypeList.addKeyListener(new KeyListener()
        {
            public void keyPressed(KeyEvent e)
            {
                if (e.keyCode == SWT.DEL)
                {
                    int index = wRdfTypeList.getSelectionIndex();
                    if (index >= 0)
                    {
                        wRdfTypeList.remove(index);
                        input.setChanged(true);
                    }
                }
            }

            public void keyReleased(KeyEvent e)
            {
            }
        });
        wRdfTypeList.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseDoubleClick(MouseEvent e)
            {
                if (wRdfTypeList.getSelectionIndex() >= 0)
                    wRdfType.setText(wRdfTypeList.getSelection()[0]);
            }
        });

        Label wListLabel = swthlp
                .appendLabel(
                        wGroup,
                        wRdfTypeList,
                        "Para remover um linha selecione-a e pressione DEL. Para editar dê um duplo-clique.");
        wListLabel.setAlignment(SWT.RIGHT);

        //
        ColumnInfo[] columns = new ColumnInfo[]
        {
                new ColumnInfo(
                        "Predicado (DataProperty)                             \u00A0",
                        ColumnInfo.COLUMN_TYPE_TEXT),
                new ColumnInfo("Campo com valor do objeto",
                        ColumnInfo.COLUMN_TYPE_CCOMBO, this.getFields(), true),
                new ColumnInfo("Tipo do literal           \u00A0",
                        ColumnInfo.COLUMN_TYPE_CCOMBO, new String[]
                        { "Tentar descobrir", "xsd:integer", "xsd:float",
                                "xsd:double", "xsd:decimal", "xsd:date",
                                "xsd:dateTime", "xsd:string" }),
                new ColumnInfo("Tag de linguagem",
                        ColumnInfo.COLUMN_TYPE_CCOMBO, new String[]
                        { "en", "pt", "fr", "" }),
                new ColumnInfo("Campo contendo tag de linguagem",
                        ColumnInfo.COLUMN_TYPE_CCOMBO, this.getFields(), true),

        };
        wMapTable = swthlp.appendTableView(cpt, wGroup, columns,
                defModListener, 98);
        item.setControl(cpt);

        item = new CTabItem(wTabFolder, SWT.NONE);
        item.setText("Campos de saída");
        cpt = swthlp.appendComposite(wTabFolder, lastControl);
        wKeepInputFields = swthlp.appendCheckboxRow(cpt, null,
                "Repassar campos de entrada para saída",
                new SelectionListener()
                {

                    @Override
                    public void widgetDefaultSelected(SelectionEvent arg0)
                    {
                        widgetSelected(arg0);
                    }

                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                        input.setChanged(true);
                    }
                });
        wSubjectOutputFieldName = swthlp.appendTextVarRow(cpt,
                wKeepInputFields, "Nome do campo do sujeito", defModListener);
        wPredicateOutputFieldName = swthlp.appendTextVarRow(cpt,
                wSubjectOutputFieldName, "Nome do campo do predicado",
                defModListener);
        wObjectOutputFieldName = swthlp.appendTextVarRow(cpt,
                wPredicateOutputFieldName, "Nome do campo do objeto",
                defModListener);
        wDatatypeOutputFieldName = swthlp.appendTextVarRow(cpt,
                wObjectOutputFieldName,
                "Nome do campo do tipo do literal (objeto)", defModListener);
        wLangTagOutputFieldName = swthlp.appendTextVarRow(cpt,
                wDatatypeOutputFieldName,
                "Nome do campo contendo marca de linguagem (objeto)",
                defModListener);
        item.setControl(cpt);

        //
        wSubjectOutputFieldName
                .setToolTipText("Para utilizar o mesmo campo de entrada, deixe em branco ou repita o nome");
        wLangTagOutputFieldName
                .setToolTipText("Será repassado para a saída apenas se datatype for \"\" ou xsd:string");

        //
        wTabFolder.setSelection(0);

        // Return the last created control here
        return wTabFolder;
    }

    private String[] getFields()
    {
        return getFields(-1);
    }

    private String[] getFields(int type)
    {

        List<String> result = new ArrayList<String>();

        try
        {
            RowMetaInterface inRowMeta = this.transMeta
                    .getPrevStepFields(stepname);

            List<ValueMetaInterface> fields = inRowMeta.getValueMetaList();

            for (ValueMetaInterface field : fields)
            {
                if (field.getType() == type || type == -1)
                    result.add(field.getName());
            }

        }
        catch (KettleStepException e)
        {
            e.printStackTrace();
        }

        return result.toArray(new String[result.size()]);
    }

    // Adiciona listeners para widgets tratarem Enter
    // The will close the window affirmatively when the user press Enter in one
    // of these text input fields
    private void addSelectionListenerToControls(SelectionAdapter lsDef)
    {
        wRdfType.addSelectionListener(lsDef);
        wSubjectOutputFieldName.addSelectionListener(lsDef);
        wPredicateOutputFieldName.addSelectionListener(lsDef);
        wObjectOutputFieldName.addSelectionListener(lsDef);
        wDatatypeOutputFieldName.addSelectionListener(lsDef);
        wLangTagOutputFieldName.addSelectionListener(lsDef);
    }

    @Override
    public String open()
    {

        Shell parent = getParent();
        Display display = parent.getDisplay();

        shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN
                | SWT.MAX);
        props.setLook(shell);
        setShellImage(shell, input);

        // ModifyListener padrao
        ModifyListener lsMod = new ModifyListener()
        {

            @Override
            public void modifyText(ModifyEvent e)
            {
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
        wCancel.setText("Cancelar"); //$NON-NLS-1$
        setButtonPositions(new Button[]
        { wOK, wCancel }, margin, lastControl);

        // Add listeners
        lsCancel = new Listener()
        {
            public void handleEvent(Event e)
            {
                cancel();
            }
        };
        lsOK = new Listener()
        {
            public void handleEvent(Event e)
            {
                ok();
            }
        };

        wCancel.addListener(SWT.Selection, lsCancel);
        wOK.addListener(SWT.Selection, lsOK);

        // It closes the window affirmatively when the user press enter in one
        // of the text input fields
        lsDef = new SelectionAdapter()
        {
            public void widgetDefaultSelected(SelectionEvent e)
            {
                ok();
            }
        };
        wStepname.addSelectionListener(lsDef);
        addSelectionListenerToControls(lsDef);

        // Detect X or ALT-F4 or something that kills this window...
        shell.addShellListener(new ShellAdapter()
        {
            public void shellClosed(ShellEvent e)
            {
                cancel();
            }
        });

        // Populate the data of the controls
        getData();

        // Set the shell size, based upon previous time...
        setSize();

        input.setChanged(changed);

        shell.open();
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
                display.sleep();
        }
        return stepname;
    }

    private void getData()
    {
        wStepname.selectAll();

        // Recupera dados do StepMeta e adiciona na GUI
        try
        {
            wRdfType.setText("");

            List<String> typesUri = input.getRdfTypeUris();
            Iterator<String> it = typesUri.iterator();
            while (it.hasNext())
            {
                String rdfTypeUri = (String) it.next();
                wRdfTypeList.add(rdfTypeUri);
            }

            wSubjectFieldName.setText(Const.NVL(input.getSubjectUriFieldName(),
                    ""));

            DataTable<String> table = input.getMapTable();
            DataTable<String>.RowFactory rf = getRowFactoryRead(table);

            for (int i = 0; i < table.size(); i++)
            {
                wMapTable.add(table.getRowRange(i, rf).getRow());
            }
            wMapTable.remove(0);

            wKeepInputFields.setSelection(input.isKeepInputFields());
            wSubjectOutputFieldName.setText(Const.NVL(
                    input.getSubjectOutputFieldName(), ""));
            wPredicateOutputFieldName.setText(Const.NVL(
                    input.getPredicateOutputFieldName(), ""));
            wObjectOutputFieldName.setText(Const.NVL(
                    input.getObjectOutputFieldName(), ""));
            wDatatypeOutputFieldName.setText(Const.NVL(
                    input.getDatatypeOutputFieldName(), ""));
            wLangTagOutputFieldName.setText(Const.NVL(
                    input.getLangTagOutputFieldName(), ""));
        }
        catch (NullPointerException e)
        {

        }
    }

    protected void cancel()
    {
        stepname = null;
        input.setChanged(changed);
        dispose();
    }

    protected void ok()
    {
        if (Const.isEmpty(wStepname.getText()))
            return;

        stepname = wStepname.getText(); // return value

        // Pega dados da GUI e colocar no StepMeta
        List<String> typesUri = new ArrayList<String>();
        for (int i = 0; i < wRdfTypeList.getItemCount(); i++)
        {
            typesUri.add(wRdfTypeList.getItem(i));
        }
        input.setRdfTypeUris(typesUri);
        input.setSubjectUriFieldName(wSubjectFieldName.getText());

        DataTable<String> table = getDataTable();
        DataTable<String>.RowFactory rf = getRowFactoryWrite(table);
        for (int i = 0; i < wMapTable.getItemCount(); i++)
        {
            /*
            String[] rowData = fillTableRow(wMapTable.getItem(i), table
                    .getHeader().size());
            */
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

    private DataTable<String> getDataTable()
    {
        return new DataTable<String>(
                DataPropertyMappingStepMeta.Field.MAP_TABLE.name(),
                DataPropertyMappingStepMeta.Field.MAP_TABLE_PREDICATE_URI
                        .name(),
                DataPropertyMappingStepMeta.Field.MAP_TABLE_OBJECT_FIELD_NAME
                        .name(),
                DataPropertyMappingStepMeta.Field.MAP_TABLE_TYPED_LITERAL
                        .name(),
                DataPropertyMappingStepMeta.Field.MAP_TABLE_LANGUAGE_TAG.name(),
                DataPropertyMappingStepMeta.Field.MAP_TABLE_LANGTAG_FIELD_NAME
                        .name());
    }

    private DataTable<String>.RowFactory getRowFactoryWrite(
            DataTable<String> table)
    {
        return table
                .newRowFactory(
                        DataPropertyMappingStepMeta.Field.MAP_TABLE_PREDICATE_URI
                                .name(),
                        DataPropertyMappingStepMeta.Field.MAP_TABLE_OBJECT_FIELD_NAME
                                .name(),
                        DataPropertyMappingStepMeta.Field.MAP_TABLE_TYPED_LITERAL
                                .name(),
                        DataPropertyMappingStepMeta.Field.MAP_TABLE_LANGUAGE_TAG
                                .name(),
                        DataPropertyMappingStepMeta.Field.MAP_TABLE_LANGTAG_FIELD_NAME
                                .name());
    }

    // Rogers (Jul./2012): Ajuste para nao dar erro na mudanca de versao
    private DataTable<String>.RowFactory getRowFactoryRead(
            DataTable<String> table)
    {
        List<String> header = new ArrayList<String>();
        header.addAll(table.getHeader());
        header.remove(DataPropertyMappingStepMeta.Field.MAP_TABLE_PREDICATE_FIELD_NAME
                .name());
        return table.newRowFactory(header.toArray(new String[0]));
    }

    // Rogers (Jul./2012): Ajuste para nao dar erro na mudanca de versao
    /*
    private String[] fillTableRow(String[] tableRow, int size)
    {
        if (tableRow.length < size)
        {
            String[] newTableRow = new String[size];
            Arrays.fill(newTableRow, "");
            for (int i = 0; i < tableRow.length; i++)
                newTableRow[i] = tableRow[i];
            for (int i = tableRow.length; i < size; i++)
                newTableRow[i] = "";
            return newTableRow;
        }
        else
            return tableRow;
    }
    */
}