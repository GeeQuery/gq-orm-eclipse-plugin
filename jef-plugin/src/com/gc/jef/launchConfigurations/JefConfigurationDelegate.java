package com.gc.jef.launchConfigurations;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.internal.core.LaunchManager;
import org.eclipse.jdt.launching.JavaLaunchDelegate;

import com.gc.jef.PluginHelper;

/**
 * created by
 * 
 * @author dht on 2006-8-1
 * 
 */
@SuppressWarnings("restriction")
public class JefConfigurationDelegate implements ILaunchConfigurationDelegate {
	JavaLaunchDelegate ja = new JavaLaunchDelegate();

	public void launch(ILaunchConfiguration config, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		PluginHelper.fix(config);
		ja.launch(config, mode, launch, monitor);
	}

	protected LaunchManager getLaunchManager() {
		return (LaunchManager) DebugPlugin.getDefault().getLaunchManager();
	}
}
