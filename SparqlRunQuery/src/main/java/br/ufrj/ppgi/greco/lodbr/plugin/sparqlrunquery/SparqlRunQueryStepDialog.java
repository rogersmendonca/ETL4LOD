package br.ufrj.ppgi.greco.lodbr.plugin.sparqlrunquery;

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
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.widget.ComboVar;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import br.ufrj.ppgi.greco.kettle.plugin.tools.swthelper.SwtHelper;

public class SparqlRunQueryStepDialog extends BaseStepDialog implements
        StepDialogInterface
{

    private SparqlRunQueryStepMeta input;
    private SwtHelper swthlp;

    private ComboVar wQueryTextFieldName;
    private TextVar wEndpointUrl;
    private TextVar wUserName;
    private TextVar wPassword;
    private TextVar wStatusCode;
    private TextVar wStatusMsg;

    public SparqlRunQueryStepDialog(Shell parent, Object stepMeta,
            TransMeta transMeta, String stepname)
    {
        super(parent, (BaseStepMeta) stepMeta, transMeta, stepname);
        // TODO Auto-generated constructor stub

        input = (SparqlRunQueryStepMeta) baseStepMeta;
        swthlp = new SwtHelper(transMeta, this.props);
    }

    @Override
    public String open()
    {
        // TODO Auto-generated method stub

        Shell parent = getParent();
        Display display = parent.getDisplay();

        shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN
                | SWT.MAX);
        props.setLook(shell);
        setShellImage(shell, input);

        ModifyListener lsMod = new ModifyListener()
        {

            @Override
            public void modifyText(ModifyEvent e)
            {
                // TODO Auto-generated method stub
                input.setChanged();
            }
        };
        boolean changed = input.hasChanged();

        FormLayout formLayout = new FormLayout();
        formLayout.marginWidth = Const.FORM_MARGIN;
        formLayout.marginHeight = Const.FORM_MARGIN;

        shell.setLayout(formLayout);

        shell.setText("Sparql Insert");

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

        // Adiciona
        Group wGroup1 = swthlp.appendGroup(shell, lastControl,
                "Query");
        {
        	wQueryTextFieldName = swthlp.appendComboVarRow(wGroup1, null,
                    "Query Text Field Name", lsMod);
        	wQueryTextFieldName.setItems(this
                    .getFields(ValueMetaInterface.TYPE_STRING));

        }

        Group wGroup2 = swthlp.appendGroup(shell, wGroup1,
                "Connection setup");
        {
            wEndpointUrl = swthlp.appendTextVarRow(wGroup2, null,
                    "URL do Sparql Update Endpoint", lsMod);

            wUserName = swthlp.appendTextVarRow(wGroup2, wEndpointUrl,
                    "User Name", lsMod);

            wPassword = swthlp.appendTextVarRow(wGroup2, wUserName, "Passowrd",
                    lsMod, true);
        }

        Group wGroup3 = swthlp.appendGroup(shell, wGroup2, "Output fields");
        {
            wStatusCode = swthlp.appendTextVarRow(wGroup3, null,
                    "Status Code", lsMod);
            wStatusMsg = swthlp.appendTextVarRow(wGroup3, wStatusCode,
                    "Status Message", lsMod);
        }

        lastControl = wGroup3;

        // Some buttons
        wOK = new Button(shell, SWT.PUSH);
        wOK.setText("OK"); //$NON-NLS-1$
        wCancel = new Button(shell, SWT.PUSH);
        wCancel.setText("Cancel"); //$NON-NLS-1$

        setButtonPositions(new Button[] { wOK, wCancel }, margin, lastControl);

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

        lsDef = new SelectionAdapter()
        {
            public void widgetDefaultSelected(SelectionEvent e)
            {
                ok();
            }
        };

        wStepname.addSelectionListener(lsDef);
        wQueryTextFieldName.addSelectionListener(lsDef);
        wEndpointUrl.addSelectionListener(lsDef);
        wUserName.addSelectionListener(lsDef);
        wPassword.addSelectionListener(lsDef);

        // Detect X or ALT-F4 or something that kills this window...
        shell.addShellListener(new ShellAdapter()
        {
            public void shellClosed(ShellEvent e)
            {
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
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
                display.sleep();
        }
        return stepname;
    }

    private void getData()
    {
        // TODO Auto-generated method stub
        wStepname.selectAll();

        try
        {
            wQueryTextFieldName
            			.setText(Const.NVL(input.getQueryTextContentFieldName(), ""));
            wEndpointUrl.setText(Const.NVL(input.getEndpointUrl(), ""));
            wUserName.setText(Const.NVL(input.getUsername(), ""));
            wPassword.setText(Const.NVL(input.getPassword(), ""));
            wStatusCode.setText(Const.NVL(input.getResultCodeFieldName(), ""));
            wStatusMsg
                    .setText(Const.NVL(input.getResultMessageFieldName(), ""));
        }
        catch (NullPointerException e)
        {

        }
    }

    protected void cancel()
    {
        // TODO Auto-generated method stub
        stepname = null;
        input.setChanged(changed);
        dispose();

    }

    protected void ok()
    {
        // TODO Auto-generated method stub
        if (Const.isEmpty(wStepname.getText()))
            return;

        stepname = wStepname.getText(); // return value

        try
        {
        	input.setQueryTextContentFieldName(wQueryTextFieldName.getText());
            input.setEndpointUrl(wEndpointUrl.getText());
            input.setUsername(wUserName.getText());
            input.setPassword(wPassword.getText());
            input.setResultCodeFieldName(wStatusCode.getText());
            input.setResultMessageFieldName(wStatusMsg.getText());
        }
        catch (NullPointerException e)
        {

        }

        dispose();

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

}
