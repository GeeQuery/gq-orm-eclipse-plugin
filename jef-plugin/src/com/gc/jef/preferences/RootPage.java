package com.gc.jef.preferences;

import jef.ui.swt.util.SWTUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PropertyPage;

public class RootPage extends PropertyPage implements IWorkbenchPreferencePage {
	public RootPage() {
	}

	public void init(IWorkbench workbench) {
	}

	protected Control createContents(Composite parent) {
		Composite topComp = new Composite(parent, SWT.NONE);
		RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
		rowLayout.pack = false;
		topComp.setLayout(rowLayout);
		{
			Label myText = new Label(topComp, SWT.WRAP);
			myText.setLayoutData(new RowData(364, 183));
			myText.setText("JEF Plug-in\r\n\r\n" + "mr.jiyi@gmail.com");
		}

		Label label = new Label(topComp, SWT.HORIZONTAL);
		label.setAlignment(SWT.CENTER);
		label.setImage(SWTUtils.toImage("icons/logo.png"));
		return topComp;

	}

}
