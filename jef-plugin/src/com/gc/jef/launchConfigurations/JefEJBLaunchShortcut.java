package com.gc.jef.launchConfigurations;

import java.util.ArrayList;
import java.util.List;

import jef.ui.swt.util.SWTUtils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaApplicationLaunchShortcut;
import org.eclipse.jdt.internal.core.JavaElement;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.debug.ui.launcher.LauncherMessages;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;

import com.gc.jef.PluginHelper;

@SuppressWarnings("restriction")
public class JefEJBLaunchShortcut extends JavaApplicationLaunchShortcut {

	private ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}

	protected ILaunchConfigurationType getConfigurationType() {
		return getLaunchManager().getLaunchConfigurationType(PluginHelper.JEF_EJB_APPLICATION);
	}

	public void launch(ISelection selection, String mode) {
		if (!(selection instanceof IStructuredSelection)) {
			SWTUtils.messageBox("UNknown selection: " + selection.getClass().getName());
			return;
		}

		Object element = ((IStructuredSelection) selection).getFirstElement();
		IJavaProject jp = null;
		if (element instanceof IJavaProject) {
			jp = (IJavaProject) element;
		} else if (element instanceof JavaElement) {
			jp = ((JavaElement) element).getJavaProject();
		} else if (element instanceof IProject) {
			jp=JavaCore.create((IProject)element);
			if(jp==null){
				SWTUtils.messageBox("目标不是Java工程:"+ element.getClass().getName());
				return;
			}
		} else {
			SWTUtils.messageBox("目标不是Java工程:"+ element.getClass().getName());
			return;
		}
		if (!jp.isOpen())
			return;

		JefSearchEngine e = new JefSearchEngine();
		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(new IJavaElement[] { jp });
		IType type;
		try {
			type = e.searchJefClassLoader(scope);
			if (type == null) {
				SWTUtils.messageBox("没有找到JEFClassLoader");
				return;
			}
			launch(type, mode, jp);
		} catch (CoreException e1) {
			SWTUtils.messageBox(e1.getMessage());
		}
	}

	protected ILaunchConfiguration findLaunchConfiguration(IType type, ILaunchConfigurationType configType,IJavaProject jp) {
		List<ILaunchConfiguration> candidateConfigs = new ArrayList<ILaunchConfiguration>();
		try {
			ILaunchConfiguration configs[] = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(configType);
			for (int i = 0; i < configs.length; i++) {
				ILaunchConfiguration config = configs[i];
				if (config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "").equals(type.getFullyQualifiedName()) && config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "").equals(jp.getElementName()))
					candidateConfigs.add(config);
			}

		} catch (CoreException e) {
			JDIDebugUIPlugin.log(e);
		}
		int candidateCount = candidateConfigs.size();
		if (candidateCount == 1)
			return (ILaunchConfiguration) candidateConfigs.get(0);
		if (candidateCount > 1)
			return chooseConfiguration(candidateConfigs);
		else
			return null;
	}

	@SuppressWarnings("deprecation")
	protected ILaunchConfiguration createConfiguration(IType type, IJavaProject jp) {
		ILaunchConfiguration config = null;
		ILaunchConfigurationWorkingCopy wc = null;
		try {
			ILaunchConfigurationType configType = getConfigurationType();
			wc = configType.newInstance(null, getLaunchManager().generateUniqueLaunchConfigurationNameFrom(jp.getElementName()));
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, jp.getElementName());
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, type.getFullyQualifiedName());
			wc.setMappedResources(new IResource[] { type.getUnderlyingResource() });
			config = wc.doSave();
		} catch (CoreException exception) {
			MessageDialog.openError(JDIDebugUIPlugin.getActiveWorkbenchShell(), LauncherMessages.JavaLaunchShortcut_3, exception.getStatus().getMessage());
		}
		return config;
	}

	protected void launch(IType type, String mode, IJavaProject jp) {
		ILaunchConfiguration config = findLaunchConfiguration(type, getConfigurationType(),jp);
		if (config == null)
			config = createConfiguration(type, jp); // TYPE可能是外部工程的
		if (config != null)
			DebugUITools.launch(config, mode);
	}

	public void launch(IEditorPart editor, String mode) {
		return;
	}
}
