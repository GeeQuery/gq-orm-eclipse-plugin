package jef.ui.swt.util;

import jef.ui.swt.GridLayoutHelper;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public abstract class AbstractDialog extends Dialog implements ModifyListener {
	BeanBinding bind;
	int layoutColumn;
	
	public AbstractDialog(Shell parent, int style,int columns) {
		super(parent, style);
		this.layoutColumn=columns;
	}

	public AbstractDialog(Shell parent,int columns) {
		this(parent, 0, columns);
	}

	public AbstractDialog(int columns) {
		this(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 0,columns);
	}

	public boolean open(Object bean, String title,int... point) {
		try {
			Shell parent = getParent();
			Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			shell.setText((title != null) ? title : "对话框");
			
			if(point.length>1){
				shell.setLocation(point[0],point[1]); // setLocation here	
			}
			createFrameContents(shell, bean, layoutColumn);

			shell.pack();
			shell.open();

			Display display = parent.getDisplay();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			return !bind.isCancle;
		} catch (Throwable t) {
			t.printStackTrace();
			return false;
		}
	}
	
	private void createFrameContents(Shell shell, Object bean, int layoutColumn) {
		//Assert.isTrue(layoutColumn > 2);
		GridLayout layout = new GridLayout(layoutColumn, false);
		layout.marginHeight = 9;
		layout.marginWidth = 9;
		layout.horizontalSpacing = 6;
		layout.verticalSpacing = 6;

		shell.setLayout(layout);
		bind = new BeanBinding(shell, bean, layout);
		createContents(bind);
		bind.expandToRowEnd(true);
		createButtonBar(bind);
		bind.loadFromBean();
	}
	//创建工具条
	private void createButtonBar(BeanBinding bind) {
		Composite c=new Composite(bind.shell,SWT.NONE);
		GridData gd=new GridData(GridData.END, GridData.BEGINNING, false, false,layoutColumn, 1);
		c.setLayoutData(gd);
		
		GridLayout local=new GridLayout(2,false);
		local.marginWidth=0;
		local.marginHeight=0;
		c.setLayout(local);
		c.setFont(JFaceResources.getDialogFont());
		String okText=getOkButtonText();
		if(okText!=null){
			Button submit =GridLayoutHelper.createButton(c,okText , new OK(),SWT.NONE);
			submit.setFont(JFaceResources.getDialogFont());
			GridLayoutHelper.setAlignmentRight(submit);
			bind.shell.setDefaultButton(submit);
		}
		String cacleText=getCancleButtonText();	
		if(cacleText!=null){
			Button button = GridLayoutHelper.createButton(c, cacleText,new Cancle(),SWT.NONE);
			button.setFont(JFaceResources.getDialogFont());
			GridLayoutHelper.setAlignmentRight(button);
		}
		GridLayoutHelper.expandToRowEnd(c, true);
	}

	protected String getOkButtonText(){
		return " 确定 ";
	}
	protected String getCancleButtonText(){
		return " 取消 ";
	}
	
	class OK implements ButtonListener{
		@Override
		public void onClick(Button btn) {
			bind.updateBean();
			boolean isValid = onExit(bind.bean);
			if (isValid) {
				bind.isCancle = false;
				bind.shell.close();
			}
		}
	}
	
	class Cancle implements ButtonListener{
		@Override
		public void onClick(Button btn) {
			bind.shell.close();
		}
		
	}

	protected abstract void createContents(BeanBinding bind);

	protected boolean onExit(Object bean) {
		return true;
	}

	protected void onTextChange(Control control){
		
	}
	
	public final void modifyText(ModifyEvent e) {
		onTextChange((Control)e.getSource());
	}

}
