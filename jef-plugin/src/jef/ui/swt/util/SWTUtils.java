package jef.ui.swt.util;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jef.common.Entry;
import jef.common.wrapper.Holder;
import jef.tools.Assert;
import jef.ui.swt.GridLayoutHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.gc.jef.Activator;

public class SWTUtils {
	/**
	 * 将指定的Job在一个允许后台化的进度任务中运行
	 * 
	 * @param job
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */
	public static void perform(final Job job) throws InvocationTargetException, InterruptedException {
		IRunnableWithProgress progress = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InterruptedException {
				job.setUser(true);
				job.schedule();
				if (monitor.isCanceled()) {
					throw new InterruptedException("The long running operation was cancelled");
				}
			}
		};
		ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
		progressMonitorDialog.run(false, true, progress);
	}
	
	public static void perform(IRunnableWithProgress op){
		try {
			PlatformUI.getWorkbench().getProgressService().run(true, true, op);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 向前台显示一条信息
	 * 
	 * @param message
	 */
	public static void messageBox(final String message) {
		if (Display.getCurrent() == null) {
			new UIOper() {
				protected void execute() {
					messageBox(message);
				}
			};
		} else {
			MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Java", message);
		}
	}
	
	public static void showMessage(final Shell shell, String message) {
		MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.YES);
		box.setMessage(message);
		box.open();
	}
	public static boolean showQuestion(Shell shell, String message) {
		MessageBox box = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		box.setMessage(message);
		int ret = box.open();
		if(ret==SWT.YES) return true;
		return false;
	}
	public static void showError(final Shell shell, String message) {
		MessageBox box = new MessageBox(shell, SWT.ICON_ERROR | SWT.YES);
		box.setMessage(message);
		box.open();
	}
	public static void showMessage(String message) {
		showMessage(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), message);
	}
	public static void showError(String message) {
		showError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), message);
	}
	public static boolean showQuestion(String message) {
		return showQuestion(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), message);
	}
	public static void moveDialogCenter(Shell shell) {
		Rectangle bound = shell.getParent().getBounds();
		shell.setLocation(bound.x+(bound.width-shell.getBounds().width)/2, bound.y+(bound.height-shell.getBounds().height)/2);
	}
	
	public static IWorkbench getWorkbench() {
		return PlatformUI.getWorkbench();
	}

	public static IWorkbenchPage getActivePage() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		return window.getActivePage();
	}
	
	public static Shell getShell() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		return window.getShell();
	}

	public static IViewPart getView(String viewId) {
		IWorkbenchPage page = getActivePage();
		IViewPart view = page.findView(viewId);
		return view;
	}

	public static IViewPart openView(String viewId, String secondaryId) {
		IWorkbenchPage page = getActivePage();
		IViewPart view = page.findView(viewId);

		if (view == null) {
			try {
				view = page.showView(viewId, secondaryId, IWorkbenchPage.VIEW_ACTIVATE);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
		return view;
	}

	public static Image toImage(String key) {
		Image img = Activator.getDefault().getImageRegistry().get(key);
		if (img == null) {
			Activator.getDefault().getImageRegistry().put(key, (img = Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, key).createImage()));
		}
		return img;
	}

	public static ImageDescriptor toImgDesc(String key) {
		return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, key);
	}

	public static ImageDescriptor toShareImgDesc(String key) {
		return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(key);
	}

	public static class Columns{
		String[] names;
		int[]    widths;
		String[] valueBinds;
		
		public Columns(String[] names,int[] widths,String[] vbs){
			this.names=names;
			this.widths=widths;
			this.valueBinds=vbs;
		}
		
		static final Columns MAP=new Columns(new String[] { "文件","信息" }, new int[] { 190, 250}, new String[] { "key","value" });
		static final Columns LIST=new Columns(new String[] { "文件" }, new int[] { 380 }, new String[] { "toString()" });

		public void validate() {
			Assert.equals(names.length, widths.length,"列标题和列宽度数据数量不一致。");
			Assert.equals(names.length, valueBinds.length,"列标题和列数据域 数量不一致。");
		}
	}
	
	/**
	 * 显示表格
	 * @param msg
	 * @param list
	 * @param isConfirm
	 * @return
	 */
	public static boolean showList(final String msg, final List<?> list,final boolean isConfirm) {
		if (Display.getCurrent() == null) {
			final Holder<Boolean> holder=new Holder<Boolean>(false);
			new UIOper(true){
				@Override
				protected void execute() {
					holder.set(innerShowList(msg,list,isConfirm,Columns.MAP));
				}
			};
			return holder.get();
		}else{
			return innerShowList(msg,list,isConfirm,Columns.LIST);	
		}
	}
	
	/**
	 * 显示表格
	 * @param msg
	 * @param map
	 * @param isConfirm
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean showList(final String msg, Map map,final boolean isConfirm) {
		final List<Entry<?,?>> list=Entry.fromMap(map);
		if (Display.getCurrent() == null) {
			final Holder<Boolean> holder=new Holder<Boolean>(false);
			new UIOper(true){
				@Override
				protected void execute() {
					holder.set(innerShowList(msg,list,isConfirm,Columns.MAP));
				}
			};
			return holder.get();
		}else{
			return innerShowList(msg,list,isConfirm,Columns.MAP);
		}
	}
	
	private static boolean innerShowList(String msg,List<?> list,final boolean isConfirm,final Columns cfg){
		AbstractDialog dialog = new AbstractDialog(5) {
			@Override
			protected void createContents(BeanBinding bind) {
				bind.createTextFromField("msg");
				Table table=bind.createtable("value", cfg, 5);
				GridLayoutHelper.setHeight(table, 190);
			}

			@Override
			protected String getCancleButtonText() {
				if(!isConfirm)return " 关闭 ";
				return super.getCancleButtonText();
			}

			@Override
			protected String getOkButtonText() {
				if(!isConfirm)return null;
				return super.getOkButtonText();
			}
			
		};
		Map<String,Object> bean=new HashMap<String,Object>();
		bean.put("msg", msg);
		bean.put("value", list);
		return dialog.open(bean, "提示");
	}
	
	/**
	 * 提示输入
	 * @param prompt
	 * @param defaultValue
	 * @return
	 */
	public static String getInput(final String prompt , final String defaultValue){
		if(Display.getCurrent()==null){
			final Holder<String> holder=new Holder<String>(null);
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					InputDialog dlg = new InputDialog(getShell(),"输入", prompt,defaultValue, null);
				    if (Window.OK == dlg.open()) {
				    	holder.set(dlg.getValue());
				    }
				}
			});
			return holder.get();
		}else{
			InputDialog dlg = new InputDialog(getShell(),"输入", prompt,defaultValue, null);
		    if (Window.OK == dlg.open()) {
		        return dlg.getValue();
		    }else{
		    	return null;
		    }	
		}
	}
	
	/**
	 * 打开文件
	 * @param arg0
	 * @return
	 */
	public static String fileOpen(final String arg0){
		if(Display.getCurrent()==null){
			final Holder<String> holder=new Holder<String>(null);
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
					dialog.setFilterExtensions(new String[] { arg0 });
					String result=dialog.open();
					holder.set(result);
				}
			});
			return holder.get();
		}else{
			FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
			dialog.setFilterExtensions(new String[] { arg0 });
			String result=dialog.open();
			return result;
		}
	}
	
	
	public static <K,V> K firstKeyOf(Map<K,V> map, V value){
		for(Map.Entry<K,V> e:map.entrySet()){
			if(e.getValue().equals(value)){
				return e.getKey();
			}
		}
		return null;
	}

	public static String fileSave(final String arg0) {
		if(Display.getCurrent()==null){
			final Holder<String> holder=new Holder<String>(null);
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
					dialog.setFilterExtensions(new String[] { arg0 });
					String result=dialog.open();
					holder.set(result);
				}
			});
			return holder.get();
		}else{
			FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
			dialog.setFilterExtensions(new String[] { arg0 });
			String result=dialog.open();
			return result;	
		}
	}

	public static String folderOpen(final String arg0,final String path) {
		if(Display.getCurrent()==null){
			final Holder<String> holder=new Holder<String>(null);
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					DirectoryDialog folderdlg=new DirectoryDialog(getShell());
					folderdlg.setText("打开文件夹");
					folderdlg.setFilterPath(path);
					folderdlg.setMessage(arg0);
					String result=folderdlg.open();
					holder.set(result);
				}
			});
			return holder.get();
		}else{
			DirectoryDialog folderdlg=new DirectoryDialog(getShell());
			folderdlg.setText("打开文件夹");
			folderdlg.setFilterPath(path);
			folderdlg.setMessage(arg0);
			String result=folderdlg.open();
			return result;
		}
	}
	
	
	public static String getSelection(final String[] arg0, final String msg, final String defaultValue){
		if(Display.getCurrent()==null){
			final Holder<String> holder=new Holder<String>(null);
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					holder.set(innerGetSelection(arg0, msg, defaultValue));
				}
				
			});
			return holder.get();
		}else{
			return innerGetSelection(arg0,msg,defaultValue);	
		}
		
	}

	private static String innerGetSelection(final String[] arg0, String msg,String defaultValue) {
		AbstractDialog d=new AbstractDialog(3){
			@Override
			protected void createContents(BeanBinding bind) {
				bind.createTextFromField("msg");
				bind.expandToRowEnd(true);
				bind.createCombo("selection", arg0, 80);
			}
		};
		Map<String,String> bean=new HashMap<String,String>();
		bean.put("msg", msg);
		bean.put("selection", defaultValue);
		if(d.open(bean, "请选择")){
			return bean.get("selection");
		}
		return null;
	}
}
