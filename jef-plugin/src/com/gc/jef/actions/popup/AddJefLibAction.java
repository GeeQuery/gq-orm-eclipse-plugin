package com.gc.jef.actions.popup;

import jef.codegen.EntityCastor;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;

import com.gc.jef.PluginHelper;

public class AddJefLibAction extends AbstractAction {
	EntityCastor ec = new EntityCastor();

	@Override
	protected void run(ICompilationUnit unit, IAction action) {
		PluginHelper.addClassPath(unit.getJavaProject());
	}
	protected void run(IJavaProject obj, IAction action){
		PluginHelper.addClassPath(obj);
	}
	
}
