package com.gc.jef.launchConfigurations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jef.tools.Assert;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.internal.core.LaunchManager;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.eclipse.jdt.launching.JavaRuntime;

import com.gc.jef.PluginHelper;

/**
 * created by
 * 
 * @author dht on 2006-8-1
 * 
 */
@SuppressWarnings("restriction")
public class JefWebConfigurationDelegate implements ILaunchConfigurationDelegate {
	JavaLaunchDelegate ja = new JavaLaunchDelegate();

	public void launch(ILaunchConfiguration config, String mode, ILaunch launch, IProgressMonitor monitor) {
		try {
			IJavaProject project=ja.getJavaProject(config);
			Assert.notNull(project);
			PluginHelper.fixAsWeb(config,project);
			ja.launch(config, mode, launch, monitor);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	protected LaunchManager getLaunchManager() {
		return (LaunchManager) DebugPlugin.getDefault().getLaunchManager();
	}
	
    public String[] getClasspath(ILaunchConfiguration configuration)
            throws CoreException
        {
            IRuntimeClasspathEntry entries[] = JavaRuntime.computeUnresolvedRuntimeClasspath(configuration);
            entries = JavaRuntime.resolveRuntimeClasspath(entries, configuration);
            List<String> userEntries = new ArrayList<String>(entries.length);
            Set<String> set = new HashSet<String>(entries.length);
            for(int i = 0; i < entries.length; i++)
                if(entries[i].getClasspathProperty() == 3)
                {
                    String location = entries[i].getLocation();
                    if(location != null && !set.contains(location))
                    {
                        userEntries.add(location);
                        set.add(location);
                    }
                }

            return (String[])userEntries.toArray(new String[userEntries.size()]);
        }
}
