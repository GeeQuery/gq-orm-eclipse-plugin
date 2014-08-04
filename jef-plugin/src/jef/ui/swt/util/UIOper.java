package jef.ui.swt.util;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * 用于在非UI线程中访问UI对象。
 * 使用时必须实现execute方法。
 * @author Jiyi
 *
 */
public abstract class UIOper {
	protected Shell shell;
	protected IWorkbenchWindow window;
	protected IWorkbenchPage page;
	
	public UIOper(Boolean dummy){
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				shell=Display.getDefault().getActiveShell();
				window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				page = window.getActivePage();
				execute();
			}
		});
	}
	
	public UIOper(){
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				shell=Display.getDefault().getActiveShell();
				window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				page = window.getActivePage();
				execute();
			}
		});
	}
	
	/**
	 * 弹出提示对话框
	 * @param msg
	 */
	protected void messageBox(String msg,String... title){
		if(title==null || title.length==0){
			MessageDialog.openInformation(shell, "提示", msg);
		}else{
			MessageDialog.openInformation(shell, title[0], msg);	
		}
	}
	
	/**
	 * 需要执行的UI操作
	 */
	protected abstract void execute();

	/**
	 * 获得当前UI的Shell对象
	 * @return
	 */
	protected Shell getShell() {
		return shell;
	}
	/**
	 * 获得当前UI的IWorkbenchWindow对象
	 * @return
	 */
	protected IWorkbenchWindow getWindow() {
		return window;
	}
	/**
	 * 获得当前UI的IWorkbenchPage对象
	 * @return
	 */
	protected IWorkbenchPage getPage() {
		return page;
	}
//	/**
//	 * 打开指定的文件
//	 * @param file
//	 */
//	protected final void openFile(File file){
//		IFileStore fileStore= EFS.getLocalFileSystem().getStore(new Path(file.getAbsolutePath()));
//		if (!fileStore.fetchInfo().isDirectory() && fileStore.fetchInfo().exists()) {
//			IEditorInput input=  new FileStoreEditorInput(fileStore);
//			try {
//				page.openEditor(input, getEditorId(fileStore));
//			} catch (PartInitException e) {
//				throw new RuntimeException(e);
//			}
//		}	
//	}
	/**
	 * 返回已打开的View，如果该View没有打开，则在默认位置打开
	 * @param viewId
	 * @return
	 */
	protected final IViewPart getView(String viewId){
		IViewPart view =  page.findView(viewId);
		if (view == null) {
			try {
				page.showView(viewId, null, IWorkbenchPage.VIEW_ACTIVATE);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
			view =page.findView(viewId);
		}
		return view;
	}
//	private String getEditorId(IFileStore file) {
//		IEditorDescriptor descriptor;
//		try {
//			descriptor= IDE.getEditorDescriptor(file.getName());
//		} catch (PartInitException e) {
//			return null;
//		}
//		if (descriptor != null)
//			return descriptor.getId();
//		return null;
//	}

}
