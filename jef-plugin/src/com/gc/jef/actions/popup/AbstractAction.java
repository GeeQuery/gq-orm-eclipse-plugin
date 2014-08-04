package com.gc.jef.actions.popup;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import jef.common.Configuration;
import jef.tools.IOUtils;
import jef.ui.swt.util.SWTUtils;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.gc.jef.preferences.AdvancedPage;

public abstract class AbstractAction implements IWorkbenchWindowActionDelegate {
	private Shell shell;

	MessageConsole console = new MessageConsole("JEF", null);
	PrintStream out;
	PrintStream err;
	public void initConsole() {
		if (console != null) {
			IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
			IConsole[] existing = manager.getConsoles();
			boolean exists = false;
			for (int i = 0; i < existing.length; i++) {
				if (console == existing[i])
					exists = true;
			}
			if (!exists) {
				manager.addConsoles(new IConsole[] { console });
			}
			manager.showConsoleView(console);
			MessageConsoleStream stream = console.newMessageStream();
			out = new PrintStream(stream);
			err=out;
			System.setOut(out);
			System.setErr(err);
		}
	}
	
	public AbstractAction() {
		super();
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	public void run(IAction action) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		
		if (window == null) return;
		
		ISelection selection = window.getSelectionService().getSelection();
		if (!(selection instanceof IStructuredSelection)) {
			SWTUtils.messageBox("UNknown selection: "+ selection.getClass().getName());
			return;
		}
		
		Object element = ((IStructuredSelection) selection).getFirstElement();
		
		
		if (element instanceof IJavaProject){
			run((IJavaProject)element,action);
		} else if (element instanceof IProject) {
			IJavaProject javap=JavaCore.create((IProject) element);
			if(javap==null){
				SWTUtils.messageBox("The project you select is not a Java Project.");
				return;
			}
			run(javap,action);
		} else if (element instanceof IPackageFragment) {
			run((IPackageFragment)element,action);
		} else if (element instanceof IPackageFragmentRoot) {
			run((IPackageFragmentRoot)element,action);
		} else if (element instanceof ICompilationUnit) {
			run((ICompilationUnit)element,action);
		} else{
			MessageDialog.openInformation(shell, "Jef", "target type" + element.getClass().getName()+" is invalid for this operation");
		}
	}

	protected Configuration getConfig(IProject project) {
		File pFile = project.getLocation().append(AdvancedPage.PROPERTIES_FILENAME).toFile();
		try {
			if (!pFile.exists())
				IOUtils.saveAsFile(pFile, "");
		} catch (IOException e) {
			MessageDialog.openInformation(shell, "Error", e.getMessage());
			return null;
		}
		Configuration config = new Configuration(pFile);
		return config;
	}

	protected void run(IJavaProject obj, IAction action){
	}
	protected void run(IJavaElement obj, IAction action){
	}
	protected void run(ICompilationUnit obj, IAction action){
	}


	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void dispose() {

	}

	public void init(IWorkbenchWindow window) {
	}

}
