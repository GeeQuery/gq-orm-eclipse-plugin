package jef.ui.swt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jef.common.wrapper.Holder;
import jef.ui.model.InputModel;
import jef.ui.model.MColor;
import jef.ui.model.MCombo;
import jef.ui.model.MDate;
import jef.ui.model.MFile;
import jef.ui.model.MFolderOpen;
import jef.ui.model.MListBox;
import jef.ui.model.MTable;
import jef.ui.model.MText;
import jef.ui.model.MTree;
import jef.ui.swt.GridLayoutHelper.ButtonListener;
import jef.ui.swt.util.AbstractDialog;
import jef.ui.swt.util.BeanBinding;
import jef.ui.swt.util.SWTUtils;
import jef.ui.swt.util.SWTUtils.Columns;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class RcpShellImpl {
	IWorkbenchWindow window;
	public RcpShellImpl(){
		this.window=PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}
	
	public String fileOpen(String arg0) {
		return SWTUtils.fileOpen(arg0);
	}

	public String fileSave(String arg0) {
		return SWTUtils.fileSave(arg0);
	}

	public String folderOpen(String arg0) {
		return SWTUtils.folderOpen("Please select",arg0);
	}

	public String getInput(String arg0, String arg1) {
		return SWTUtils.getInput(arg0,arg1);
	}

	public void messageBox(String arg0) {
		SWTUtils.messageBox(arg0);
	}
	public boolean showList(String arg0, Map<?,?> arg1, boolean arg2) {
		return SWTUtils.showList(arg0, arg1, arg2);
	}
	public boolean showList(String arg0, List<?> arg1, boolean arg2) {
		return SWTUtils.showList(arg0, arg1, arg2);
	}

	public String select(String[] arg0, String msg,String defaultValue) {
		return SWTUtils.getSelection(arg0,msg,defaultValue);
	}

	public boolean showTree(String arg0, List<?> arg1, boolean arg2) {
		return false;
	}

	public boolean showTree(String arg0, Map<?, ?> arg1, boolean arg2) {
		return false;
	}

	public boolean inputDialog(final String arg0, final InputModel<?>... arg1) {
		if(Display.getCurrent()==null){
			final Holder<Boolean> holder=new Holder<Boolean>(false);
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					holder.set(innerDialog(arg0,arg1));
				}
			});
			return holder.get();
		}else{
			return innerDialog(arg0,arg1);
		}
	}
	
	private boolean innerDialog(final String arg0,final InputModel<?>[] arg1){
		Map<String,InputModel<?>> map=new HashMap<String,InputModel<?>>();
		for(InputModel<?> m: arg1){
			map.put(m.getLabel(), m);
		}
		AbstractDialog d=new AbstractDialog(5){
			@SuppressWarnings("rawtypes")
			@Override
			protected void createContents(BeanBinding bind) {
				bind.createText(arg0);
				bind.expandToRowEnd(true);
				for(InputModel<?> m: arg1){
					if(m instanceof MCombo){
						bind.createText(m.getLabel());
						bind.createCombo(m.getLabel(), ((MCombo) m).getSelection(),m.getWidth());
						bind.expandToRowEnd(true);
					}else if(m instanceof MListBox){
						bind.createText(m.getLabel());
						bind.createListBox(m.getLabel(),((MListBox)m).getOptions(),true,m.getWidth());
						bind.expandToRowEnd(true);
					}else if(m instanceof MColor){
						throw new UnsupportedOperationException();
					}else if(m instanceof MDate){
						bind.createText(m.getLabel());
						bind.createDateInput(m.getLabel(),3,m.getWidth(),((MDate) m).isWithTime());
						bind.expandToRowEnd(false);
					}else if(m instanceof MFile){
						bind.createText(m.getLabel());
						final Text input=bind.createTextInput(m.getLabel(), ((MFile) m).getWidth(), 3, false);
						final MFile model=(MFile)m;
						bind.createButton("...", "run", new ButtonListener(){
							@SuppressWarnings("unused")
							public void run(Button b){
								String str=StringUtils.join(model.getFilter(),";");
								String result=null;
								if(model.isOpen()){
									result=SWTUtils.fileOpen(str);									
								}else{
									result=SWTUtils.fileSave(str);
								}
								if(result!=null){
									input.setText(result);
								}
							}
						});
						bind.expandToRowEnd(true);
					}else if(m instanceof MFolderOpen){
						bind.createText(m.getLabel());
						final Text input=bind.createTextInput(m.getLabel(), ((MFolderOpen) m).getWidth(), 3, false);
						bind.createButton("...", "run", new ButtonListener(){
							@SuppressWarnings("unused")
							public void run(Button b){
								String result=SWTUtils.folderOpen("",input.getText());
								if(result!=null){
									input.setText(result);
								}
							}
						});
						bind.expandToRowEnd(true);
					}else if(m instanceof MTree){
						bind.createText(m.getLabel());
						bind.expandToRowEnd(true);
						Tree tree=bind.createTree(m.getLabel(), ((MTree) m).getRoot(), ((MTree) m).getWidth(), 1, false);
						GridLayoutHelper.setHeight(tree, ((MTree) m).getHeight());
						bind.expandToRowEnd(true);
					}else if(m instanceof MTable){
						bind.createText(m.getLabel());
						bind.expandToRowEnd(true);
						Columns cfg=new Columns(((MTable) m).getColumnNames(),((MTable) m).getColumnWidths(),((MTable) m).getColumnValues());
						Table t=bind.createtable(m.getLabel(), cfg, 5);
						GridLayoutHelper.setHeight(t, ((MTable) m).getHeight());
						GridLayoutHelper.setWidth(t, ((MTable) m).getWidth());
						bind.expandToRowEnd(true);
					}else if(m instanceof MText){
						bind.createText(m.getLabel());
						bind.createTextInput(m.getLabel(), ((MText) m).getWidth(), 1, false);
						bind.expandToRowEnd(true);
					}
				}
				
			}
		};
		return d.open(map, "输入");
	}
	
}
