package com.gc.jef.actions.popup;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import jef.codegen.EntityEnhancer;
import jef.common.Configuration;
import jef.tools.StringUtils;
import jef.ui.swt.util.SWTUtils;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class EntityEnhanceAction extends AbstractAction {
	Configuration config;

	@Override
	protected void run(final IJavaProject project, IAction action) {
		initConsole();
		ProgressMonitorDialog pmd = new ProgressMonitorDialog(null);
		IRunnableWithProgress pro = new IRunnableWithProgress() {
			public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				EntityEnhancer en = new EntityEnhancer();
//				System.out.println("============");
//				for(String s:paths){
//					System.out.println(s);
//				}
//				System.out.println("============");
				try {
					String path = StringUtils.substringAfter(project.getOutputLocation().toString(), "/");
					path = StringUtils.substringAfter(path, "/");
					File root = new File(project.getResource().getLocation().toFile(), path);
					String output = SWTUtils.folderOpen("选择文件夹", root.getAbsolutePath());
					if (output == null)
						return;
					en.setRoot(new File(output));
					// en.setOut(out);
					en.enhance(new String[0]);
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
			}

		};
		try {
			pmd.run(true, true, pro);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
