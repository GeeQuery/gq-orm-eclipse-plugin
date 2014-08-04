package com.gc.jef.ui.launchConfigurations;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

/**
 * created by @author dht on 2006-8-1
 *
 */
public class AICodeTabGroup extends AbstractLaunchConfigurationTabGroup {

	public AICodeTabGroup() {
		
	}

	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
        ILaunchConfigurationTab tabs[] = {
                new SourceTab()
            };
            setTabs(tabs);
	}

}
