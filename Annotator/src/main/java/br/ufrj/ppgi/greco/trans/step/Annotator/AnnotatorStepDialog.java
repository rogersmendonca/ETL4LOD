package br.ufrj.ppgi.greco.trans.step.Annotator;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
//import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
//import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.widget.ComboVar;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

//import br.ufrj.ppgi.greco.kettle.plugin.tools.swthelper.SwtHelper;

/**
 * Interface de usuario do step Annotator.
 * 
 * @author Camila Carvalho Ferreira
 * 
 */
public class AnnotatorStepDialog extends BaseStepDialog implements
        StepDialogInterface
{
    // for i18n purposes, needed by Translator2!! $NON-NLS-1$
    private static Class<?> PKG = AnnotatorStepMeta.class;

    private AnnotatorStepMeta input;
    //private SwtHelper swthlp;
    private String dialogTitle;

    // Adicionar variaveis dos widgets
    // Campos Step - Input
    //private Group wInputGroup;
    private ComboVar wcSubject;
    private ComboVar wcPredicate;
    private ComboVar wcObject;
    private TextVar wtNTriple;
    //private Button wInnerIsLiteral;
    //private ComboVar wInputDataType;
    //private ComboVar wInputLangTag;
    private Label wlShape;
    private Label wlSubject;
    private Label wlObject;
    private Label wlPredicate;
    private Label wlNTriple;
    
    private Button wbBrowse;
    private Text wBrowse;
    
    private FormData fdlNTriple;
    private FormData fdtNTriple;
    private FormData fdlObject;
    private FormData fdcObject;
    private FormData fdlPredicate;
    private FormData fdcPredicate;
    private FormData fdlSubject;
    private FormData fdcSubject;
    private FormData fdlShape;
    private FormData fdbBrowse;
    private FormData fdBrowse;

    // Campos Step - Output
    //private Group wOutputGroup;
    //private Button wInnerKeepInput;

    
    public AnnotatorStepDialog(Shell parent, Object stepMeta,
            TransMeta transMeta, String stepname)
    {
        super(parent, (BaseStepMeta) stepMeta, transMeta, stepname);

        input = (AnnotatorStepMeta) baseStepMeta;
        //swthlp = new SwtHelper(transMeta, this.props);

        // Additional initialization here
        dialogTitle = BaseMessages.getString(PKG, "AnnotatorStep.Title");
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

    /*private ComboVar appendComboVar(Control lastControl,
            ModifyListener defModListener, Composite parent, String label)
    {
        ComboVar combo = swthlp.appendComboVarRow(parent, lastControl, label,
                defModListener);
        BaseStepDialog.getFieldsFromPrevious(combo, transMeta, stepMeta);
        return combo;
    }

    // Criar widgets especificos da janela
    private Control buildContents(Control lastControl,
            ModifyListener defModListener)
    {
        wInputGroup = swthlp.appendGroup(shell, lastControl,
                BaseMessages.getString(PKG, "AnnotatorStep.Group.Input.Label"));
        wInputSubject = appendComboVar(wInputGroup, defModListener,
                wInputGroup, BaseMessages.getString(PKG, "AnnotatorStep.SubjectField.Label"));
        wInputPredicate = appendComboVar(wInputSubject, defModListener,
                wInputGroup, BaseMessages.getString(PKG, "AnnotatorStep.PredicateField.Label"));
        wInputObject = appendComboVar(wInputPredicate, defModListener,
                wInputGroup, BaseMessages.getString(PKG, "AnnotatorStep.ObjectField.Label"));
        wInnerIsLiteral = swthlp.appendCheckboxRow(wInputGroup, wInputObject,
                BaseMessages.getString(PKG, "AnnotatorStep.LiteralCheckField.Label"), new SelectionListener()
                {
                    @Override
                    public void widgetDefaultSelected(SelectionEvent arg0)
                    {
                        widgetSelected(arg0);
                    }

                    @Override
                    public void widgetSelected(SelectionEvent e)
                    {
                        enableDataTypeAndLangTag(wInnerIsLiteral.getSelection());
                        input.setChanged(true);
                    }
                });
        wInputDataType = appendComboVar(wInnerIsLiteral, defModListener,
                wInputGroup, BaseMessages.getString(PKG, "AnnotatorStep.LiteralTypeField.Label"));
        wInputLangTag = appendComboVar(wInputDataType, defModListener,
                wInputGroup, BaseMessages.getString(PKG, "AnnotatorStep.LangtagField.Label"));

        wOutputGroup = swthlp.appendGroup(shell, wInputGroup, BaseMessages.getString(PKG, "AnnotatorStep.Group.Output.Label"));
        wInnerKeepInput = swthlp.appendCheckboxRow(wOutputGroup, wOutputGroup,
                BaseMessages.getString(PKG, "AnnotatorStep.PassCheckField.Label"),
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
        wOutputNTriple = swthlp.appendTextVarRow(wOutputGroup, wInnerKeepInput,
                BaseMessages.getString(PKG, "AnnotatorStep.NTripleField.Label"), defModListener);

        return wOutputGroup;
    }*/
    
    

    /*private void enableDataTypeAndLangTag(boolean enable)
    {
        wInputDataType.setEnabled(enable);
        wInputLangTag.setEnabled(enable);
        if (!enable)
        {
            wInputDataType.setText("");
            wInputLangTag.setText("");
        }
    }
    
    

    private void addSelectionListenerToControls(SelectionAdapter lsDef)
    {
        wOutputNTriple.addSelectionListener(lsDef);
    }*/


        // Adiciona um label e um input text no topo do dialog shell
        wlStepname = new Label(shell, SWT.RIGHT);
        wlStepname.setText(BaseMessages.getString(PKG, "AnnotatorStep.StepNameField.Label"));
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
        //Control lastControl = wStepname;
        
        wlSubject=new Label(shell, SWT.RIGHT);
		wlSubject.setText(BaseMessages.getString(PKG, "AnnotatorStep.SubjectField.Label"));
 		props.setLook(wlSubject);
		fdlSubject=new FormData();
		fdlSubject.left = new FormAttachment(0, 0);
		fdlSubject.top  = new FormAttachment(wStepname, margin);
		fdlSubject.right= new FormAttachment(middle, -margin);
		wlSubject.setLayoutData(fdlSubject);
		
		wcSubject=new ComboVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wcSubject);
 		wcSubject.addModifyListener(lsMod);
		fdcSubject=new FormData();
		fdcSubject.left = new FormAttachment(middle, 0);
		fdcSubject.right= new FormAttachment(100, 0);
		fdcSubject.top  = new FormAttachment(wStepname, margin);
		wcSubject.setLayoutData(fdcSubject);
		wcSubject.addFocusListener(new FocusListener()
		{
			public void focusLost(org.eclipse.swt.events.FocusEvent e)
            {
            }
            public void focusGained(org.eclipse.swt.events.FocusEvent e)
            {
                Cursor busy = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
                shell.setCursor(busy);
                BaseStepDialog.getFieldsFromPrevious(wcSubject, transMeta, stepMeta);
                shell.setCursor(null);
                busy.dispose();
            }
        }
    );
		
		wlPredicate=new Label(shell, SWT.RIGHT);
		wlPredicate.setText(BaseMessages.getString(PKG, "AnnotatorStep.PredicateField.Label"));
 		props.setLook(wlPredicate);
		fdlPredicate=new FormData();
		fdlPredicate.left = new FormAttachment(0, 0);
		fdlPredicate.top  = new FormAttachment(wcSubject, margin);
		fdlPredicate.right= new FormAttachment(middle, -margin);
		wlPredicate.setLayoutData(fdlPredicate);
		
		wcPredicate=new ComboVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wcPredicate);
 		wcPredicate.addModifyListener(lsMod);
		fdcPredicate=new FormData();
		fdcPredicate.left = new FormAttachment(middle, 0);
		fdcPredicate.right= new FormAttachment(100, 0);
		fdcPredicate.top  = new FormAttachment(wcSubject, margin);
		wcPredicate.setLayoutData(fdcPredicate);
		wcPredicate.addFocusListener(new FocusListener()
		{
			public void focusLost(org.eclipse.swt.events.FocusEvent e)
            {
            }
            public void focusGained(org.eclipse.swt.events.FocusEvent e)
            {
                Cursor busy = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
                shell.setCursor(busy);
                BaseStepDialog.getFieldsFromPrevious(wcPredicate, transMeta, stepMeta);
                shell.setCursor(null);
                busy.dispose();
            }
        }
    );
		
		wlObject=new Label(shell, SWT.RIGHT);
		wlObject.setText(BaseMessages.getString(PKG, "AnnotatorStep.ObjectField.Label"));
 		props.setLook(wlObject);
		fdlObject=new FormData();
		fdlObject.left = new FormAttachment(0, 0);
		fdlObject.top  = new FormAttachment(wcPredicate, margin);
		fdlObject.right= new FormAttachment(middle, -margin);
		wlObject.setLayoutData(fdlObject);
		
		wcObject=new ComboVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wcObject);
 		wcObject.addModifyListener(lsMod);
		fdcObject=new FormData();
		fdcObject.left = new FormAttachment(middle, 0);
		fdcObject.right= new FormAttachment(100, 0);
		fdcObject.top  = new FormAttachment(wcPredicate, margin);
		wcObject.setLayoutData(fdcObject);
		wcObject.addFocusListener(new FocusListener()
		{
			public void focusLost(org.eclipse.swt.events.FocusEvent e)
            {
            }
            public void focusGained(org.eclipse.swt.events.FocusEvent e)
            {
                Cursor busy = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
                shell.setCursor(busy);
                BaseStepDialog.getFieldsFromPrevious(wcObject, transMeta, stepMeta);
                shell.setCursor(null);
                busy.dispose();
            }
        }
    );
		
		wlNTriple=new Label(shell, SWT.RIGHT);
		wlNTriple.setText(BaseMessages.getString(PKG, "AnnotatorStep.NTripleField.Label")); //$NON-NLS-1$
 		props.setLook(wlNTriple);
		fdlNTriple=new FormData();
		fdlNTriple.left = new FormAttachment(0, 0);
		fdlNTriple.right= new FormAttachment(middle, -margin);
		fdlNTriple.top  = new FormAttachment(wcObject, margin);
		wlNTriple.setLayoutData(fdlNTriple);
		
		wtNTriple=new TextVar(transMeta,shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wtNTriple.setText(""); //$NON-NLS-1$
 		props.setLook(wtNTriple);
		wtNTriple.addModifyListener(lsMod);
		fdtNTriple=new FormData();
		fdtNTriple.left = new FormAttachment(middle, 0);
		fdtNTriple.top  = new FormAttachment(wcObject, margin);
		fdtNTriple.right= new FormAttachment(100, 0);
		wtNTriple.setLayoutData(fdtNTriple);

        // Chama metodo que adiciona os widgets especificos da janela
       // lastControl = buildContents(lastControl, lsMod);

        // Bottom buttons
        wOK = new Button(shell, SWT.PUSH);
        wOK.setText(BaseMessages.getString(PKG, "AnnotatorStep.Btn.OK")); //$NON-NLS-1$
        wCancel = new Button(shell, SWT.PUSH);
        wCancel.setText(BaseMessages.getString(PKG, "AnnotatorStep.Btn.Cancel")); //$NON-NLS-1$
        setButtonPositions(new Button[]
        { wOK, wCancel }, margin, wBrowse);
        
        wlShape=new Label(shell, SWT.RIGHT);
		wlShape.setText("Name of the mapping file ");
 		props.setLook(wlShape);
		fdlShape=new FormData();
		fdlShape.left = new FormAttachment(0, 0);
		fdlShape.top  = new FormAttachment(wtNTriple, margin);
		fdlShape.right= new FormAttachment(middle, -margin);
		wlShape.setLayoutData(fdlShape);
        
        wbBrowse=new Button(shell, SWT.PUSH| SWT.CENTER);
 		props.setLook(wbBrowse);
		wbBrowse.setText(BaseMessages.getString(PKG, "AnnotatorStep.Btn.Browse"));
		fdbBrowse=new FormData();
		fdbBrowse.right= new FormAttachment(100, 0);
		fdbBrowse.top  = new FormAttachment(wtNTriple, margin);
		wbBrowse.setLayoutData(fdbBrowse);
		
		wBrowse=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wBrowse);
		wBrowse.addModifyListener(lsMod);
		fdBrowse=new FormData();
		fdBrowse.left = new FormAttachment(middle, 0);
		fdBrowse.right= new FormAttachment(wbBrowse, -margin);
		fdBrowse.top  = new FormAttachment(wtNTriple, margin);
		wBrowse.setLayoutData(fdBrowse);

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
        
        wBrowse.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent arg0)
			{
				wBrowse.setToolTipText(transMeta.environmentSubstitute(wBrowse.getText()));
			}
		});
        
        wbBrowse.addSelectionListener
		(
			new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e) 
				{
					FileDialog dialog = new FileDialog(shell, SWT.OPEN);
					dialog.setFilterExtensions(new String[] {"*.xml;*.XML", "*"});
					if (wBrowse.getText()!=null)
					{
						dialog.setFileName(wBrowse.getText());
					}
						
					dialog.setFilterNames(new String[] {"Text files", "All files"});
						
					if (dialog.open()!=null)
					{
						String str = dialog.getFilterPath()+System.getProperty("file.separator")+dialog.getFileName();
						wBrowse.setText(str);
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
       // addSelectionListenerToControls(lsDef);

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

        wcSubject.setText(Const.NVL(input.getInputSubject(), ""));
        wcPredicate.setText(Const.NVL(input.getInputPredicate(), ""));
        wcObject.setText(Const.NVL(input.getInputObject(), ""));
        //wInputDataType.setText(Const.NVL(input.getInputDataType(), ""));
        //wInputLangTag.setText(Const.NVL(input.getInputLangTag(), ""));
        wtNTriple.setText(Const.NVL(input.getOutputNTriple(), ""));
        //wInnerIsLiteral.setSelection(input.getInnerIsLiteral());
        //enableDataTypeAndLangTag(wInnerIsLiteral.getSelection());
        //wInnerKeepInput.setSelection(input.getInnerKeepInputFields());
        wBrowse.setText(input.getBrowseFilename());
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
        input.setInputSubject(wcSubject.getText());
        input.setInputPredicate(wcPredicate.getText());
        input.setInputObject(wcObject.getText());
        //input.setInnerIsLiteral(wInnerIsLiteral.getSelection());
        //input.setInputDataType(wInputDataType.getText());
        //input.setInputLangTag(wInputLangTag.getText());
        //input.setInnerKeepInputFields(wInnerKeepInput.getSelection());
        input.setOutputNTriple(wtNTriple.getText());
        input.setBrowseFilename(wBrowse.getText());

        // Fecha janela
        dispose();
    }
}
