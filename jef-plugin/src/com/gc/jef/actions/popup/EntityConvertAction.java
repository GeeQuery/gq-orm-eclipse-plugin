package com.gc.jef.actions.popup;

import java.io.File;

import jef.codegen.EntityCastor;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.action.IAction;

public class EntityConvertAction extends AbstractAction {
	EntityCastor ec = new EntityCastor();

	@Override
	protected void run(ICompilationUnit unit, IAction action) {
		super.initConsole();
		File file = new File(unit.getJavaProject().getProject().getLocation().toFile().getParent(), unit.getPath().toString());
		ec.process(file);
		try {
			unit.getResource().refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
