package com.gc.jef.preferences;

import java.io.File;
import java.io.IOException;

import jef.common.Configuration;
import jef.tools.IOUtils;
import jef.ui.swt.GridLayoutHelper;
import jef.ui.swt.GridLayoutHelper.ButtonListener;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PropertyPage;

public class AdvancedPage extends PropertyPage implements IWorkbenchPreferencePage, ModifyListener, ButtonListener {
	public static final String PROPERTIES_FILENAME=".jef";
	
	public AdvancedPage() {
	}

	
	public static final String OBD2JAVA_PATH = "$OBD2JAVA_PATH";// obd2java.exe所在路径

	public static final String OBD_FILE_PATH = "$OBD_FILE_PATH";	// OBD文件所在路径

	public static final String WORK_PATH = "$WORK_PATH";	// 编译路径
	
	public static final String OBD2JAVA_ARGS = "$OBD2JAVA_ARGS";// 默认OBD2JAVA的执行参数
	private Text text1,  text3, text4;
	private Table table;

	// 定义一个IPreferenceStore对象
	private Configuration ps;

	public void init(IWorkbench workbench) {
	}

	// 设置obd2Java
	public void button1_onclick(Button button) {
		FileDialog dialog = new FileDialog(button.getShell(), SWT.OPEN);
		dialog.setFilterExtensions(new String[] { "obd2java.exe" });
		String result=dialog.open();
		if(result!=null)text1.setText(result);
	}

	// 设置OBD文件路径
	public void button2_onclick(Button button) {
        DirectoryDialog folderdlg=new DirectoryDialog(getShell());
        folderdlg.setText("OBD文件夹路径");
        folderdlg.setFilterPath("SystemDrive");
        folderdlg.setMessage("请选择OBD文件所在的文件夹");
        String result=folderdlg.open();
        if(result!=null){
        	for(TableItem item:table.getItems()){
        		if(result.equalsIgnoreCase(item.getText(0))){
        			return;
        		}
        	}
        	TableItem tableItem = new TableItem(table, SWT.NONE);
    		tableItem.setText(result);
        }
	}
	
	public void remove_onclick(Button button) {
		for(TableItem item:table.getSelection()  ){
    		table.remove(table.indexOf(item));
    	}
	}

	// 设置工作路径
	public void button3_onclick(Button button) {
        DirectoryDialog folderdlg=new DirectoryDialog(getShell());
        folderdlg.setText("工作路径");
        folderdlg.setFilterPath("SystemDrive");
        folderdlg.setMessage("请选择OBD生成Java的工作目录");
        String result=folderdlg.open();
        if(result!=null)text3.setText(result);
	}


	protected Control createContents(Composite parent) {
		Composite topComp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		topComp.setLayout(layout);

		GridLayoutHelper.createText(topComp, "obd2java.exe\u8DEF\u5F84:");
		text1 = GridLayoutHelper.createTextInput(topComp,false);
		GridLayoutHelper.createButton(topComp, "...", "button1_onclick", this,SWT.NONE);

		
		GridLayoutHelper.createText(topComp, "工作目录");
		text3 = GridLayoutHelper.createTextInput(topComp,false);
		GridLayoutHelper.createButton(topComp, "...", "button3_onclick", this,SWT.NONE);

		GridLayoutHelper.createText(topComp, "obd2Java运行参数:");
		text4 = GridLayoutHelper.createTextInput(topComp,false);
		GridLayoutHelper.createEmpty(topComp);

		GridLayoutHelper.createText(topComp, "obd文件路径");
		GridLayoutHelper.toNextRow(topComp);
		//text2 = GridLayoutHelper.createTextInput(topComp);
		GridLayoutHelper.createButton(topComp, "  Add   ", "button2_onclick", this,SWT.NONE);
		GridLayoutHelper.createButton(topComp, " Remove ", "remove_onclick", this,SWT.NONE);

		table=GridLayoutHelper.createTable(topComp,new String[]{"Path"},new int[]{300},3);
		
		loadVaues();

		// 添加事件监听器。
		text1.addModifyListener(this);
		//text2.addModifyListener(this);
		text3.addModifyListener(this);
		text4.addModifyListener(this);
		return topComp;
	}

	
	private void loadVaues() {
		
		IProject project = (IProject) (this.getElement().getAdapter(IProject.class));
		File pFile=project.getLocation().append(PROPERTIES_FILENAME).toFile();
		try {
			if(!pFile.exists())IOUtils.saveAsFile( pFile,"");
		} catch (IOException e) {
			MessageDialog.openInformation(getShell(), "Error", e.getMessage());
			return;
		}
		if(ps==null)ps=new Configuration(pFile);
//		text1.setText(ps.get(ConfigItem.OBD2JAVA_PATH, ""));
//		for(String path:ps.get(ConfigItem.OBD_FILE_PATH,"").split(";")){
//			TableItem tableItem = new TableItem(table, SWT.NONE);
//    		tableItem.setText(path);
//		}
//		
//		text3.setText(ps.get(ConfigItem.WORK_PATH,""));
//		text4.setText(ps.get(ConfigItem.OBD2JAVA_ARGS,"-OpenBroker -uobd"));
	}

	private void doSave() {
//		Map<ConfigItem, String> m=new HashMap<ConfigItem, String> ();
//		m.put(ConfigItem.OBD2JAVA_PATH, text1.getText());
//		StringBuilder sb=new StringBuilder();
//		
		
//		for(TableItem item:table.getItems()){
//			if(table.indexOf(item)>0){
//				sb.append(";");	
//			}
//			sb.append(item.getText(0));
//		}
//		m.put(ConfigItem.OBD_FILE_PATH, sb.toString());
//		m.put(ConfigItem.WORK_PATH, text3.getText());
//		m.put(ConfigItem.OBD2JAVA_ARGS, text4.getText());
//		ps.update(m);

	}

	// 方法中对输入值进行了验证并将“确定”、“应用”两按钮使能
	public void modifyText(ModifyEvent e) {
		// String errorStr = null;// 将原错误信息清空
		// errorStr = "密码不能为空！";
		// setErrorMessage(errorStr);// errorStr=null时复原为正常的提示文字
		// setValid(errorStr == null);// “确定”按钮
		// getApplyButton().setEnabled(errorStr == null);// “应用”按钮
	}

	protected void performApply() {
		doSave();
	}

	public boolean performOk() {
		doSave();
		return true;
	}
}
