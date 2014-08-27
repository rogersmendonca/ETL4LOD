package br.ufrj.ppgi.greco.trans.step.Silk;


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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.trans.step.BaseStepDialog;


/**
 * Interface de usuario do step Silk.
 * 
 * @author Camila Carvalho Ferreira
 * 
 */
public class SilkStepDialog extends BaseStepDialog implements
        StepDialogInterface
{
    // for i18n purposes, needed by Translator2!! $NON-NLS-1$
    private static Class<?> PKG = SilkStepMeta.class;

    private SilkStepMeta input;
    private String dialogTitle;

    // Adicionar variaveis dos widgets
    private Label wlXml;
    
    private Button wbXml;
    private Text wXml;

    private FormData fdlXml;
    private FormData fdbXml;
    private FormData fdXml;

    
    public SilkStepDialog(Shell parent, Object stepMeta,
            TransMeta transMeta, String stepname)
    {
        super(parent, (BaseStepMeta) stepMeta, transMeta, stepname);

        input = (SilkStepMeta) baseStepMeta;

        // Additional initialization here
        dialogTitle = BaseMessages.getString(PKG, "SilkStep.Title");
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
        wlStepname.setText(BaseMessages.getString(PKG, "SilkStep.StepNameField.Label"));
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

        // Bottom buttons
        wOK = new Button(shell, SWT.PUSH);
        wOK.setText(BaseMessages.getString(PKG, "SilkStep.Btn.OK")); //$NON-NLS-1$
        wCancel = new Button(shell, SWT.PUSH);
        wCancel.setText(BaseMessages.getString(PKG, "SilkStep.Btn.Cancel")); //$NON-NLS-1$
        setButtonPositions(new Button[]
        { wOK, wCancel }, margin, wXml);
        
        //Botoes para busca de arquivo 
        wlXml=new Label(shell, SWT.RIGHT);
		wlXml.setText("Name of the discription file ");
 		props.setLook(wlXml);
		fdlXml=new FormData();
		fdlXml.left = new FormAttachment(0, 0);
		fdlXml.top  = new FormAttachment(wStepname, margin);
		fdlXml.right= new FormAttachment(middle, -margin);
		wlXml.setLayoutData(fdlXml);
        
        wbXml=new Button(shell, SWT.PUSH| SWT.CENTER);
 		props.setLook(wbXml);
		wbXml.setText(BaseMessages.getString(PKG, "SilkStep.Btn.Browse"));
		fdbXml=new FormData();
		fdbXml.right= new FormAttachment(100, 0);
		fdbXml.top  = new FormAttachment(wStepname, margin);
		wbXml.setLayoutData(fdbXml);
		
		wXml=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wXml);
		wXml.addModifyListener(lsMod);
		fdXml=new FormData();
		fdXml.left = new FormAttachment(middle, 0);
		fdXml.right= new FormAttachment(wbXml, -margin);
		fdXml.top  = new FormAttachment(wStepname, margin);
		wXml.setLayoutData(fdXml);
		

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
        
        wXml.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent arg0)
			{
				wXml.setToolTipText(transMeta.environmentSubstitute(wXml.getText()));
			}
		});
        
        wbXml.addSelectionListener
		(
			new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e) 
				{
					FileDialog dialog = new FileDialog(shell, SWT.OPEN);
					dialog.setFilterExtensions(new String[] {"*.xml;*.XML", "*"});
					if (wXml.getText()!=null)
					{
						dialog.setFileName(wXml.getText());
					}
						
					dialog.setFilterNames(new String[] {"XML files", "All files"});
						
					if (dialog.open()!=null)
					{
						String str = dialog.getFilterPath()+System.getProperty("file.separator")+dialog.getFileName();
						wXml.setText(str);
					}
				}
			}
		);
        
        
     

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
        
        // Alarga um pouco mais a janela
        Rectangle shellBounds = shell.getBounds();
        shellBounds.width += 5;
        shell.setBounds(shellBounds);        

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

        wXml.setText(input.getXmlFilename());

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

        // Pegar dados da GUI e colocar no StepMeta
        input.setXmlFilename(wXml.getText());

        // Fecha janela
        dispose();
    }
}
