package com.gc.jef.ui.launchConfigurations;



import java.util.ArrayList;
import java.util.List;

import jef.ui.swt.util.SWTUtils;
import jef.ui.swt.util.TreeContentProvider;
import jef.ui.swt.util.WidgetUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;


/**
 * created by @author dht on 2006-8-1
 *
 */
public class SourceTab extends AbstractLaunchConfigurationTab {

	private Text text_context;
	private Text text_unit;
	private Text text_group;
	private Text text_source;
	private CLabel selectedContextLabel;
	private CLabel selectedUnitLabel;
	private CLabel selectedGroupLabel;
	public static String source_label = "selected source:";
	public static String group_label = "selected group:";
	public static String unit_label = "selected unit:";
	public static String context_label = "selected context:";
	protected boolean fInitializing= false;
	private Tree tree;
	private CLabel selectedSourceLabel;
	
	private List sourceList = new ArrayList();
	private List groupList = new ArrayList();
	private List unitList = new ArrayList();
	private List contextList = new ArrayList();

	protected WidgetListener fListener= new WidgetListener();
	
	/**
	 * A listener to update for text modification and widget selection.
	 */
	protected class WidgetListener  implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			if (!fInitializing) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		}
	}
	
	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		mainComposite.setLayout(gridLayout);
		setControl(mainComposite);

		final TreeViewer treeViewer = new TreeViewer(mainComposite, SWT.MULTI | SWT.BORDER | SWT.CHECK);
		treeViewer.addOpenListener(new IOpenListener() {
			public void open(final OpenEvent e) {
			}
		});
		tree = treeViewer.getTree();
		tree.addTreeListener(new TreeAdapter() {
			public void treeExpanded(final TreeEvent e) {
				TreeItem item = (TreeItem) e.item;
				WidgetUtil.checkChildrenTreeItem(item, item.getChecked());
			}
		});
		tree.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				TreeItem item = (TreeItem) e.item;
				onTreeItemCheck(item);
			}
		});
		
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1));
		treeViewer.setLabelProvider(new LabelProvider());
		treeViewer.setContentProvider(new TreeContentProvider());
		treeViewer.setAutoExpandLevel(5);
//		SourceListHolder input;
//		try {
//			input = new SourceListHolder(null, true);
//			treeViewer.setInput(input);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}			
		selectedSourceLabel = new CLabel(mainComposite, SWT.NONE);
		selectedSourceLabel.setText(source_label);
		selectedSourceLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

		text_source = new Text(mainComposite, SWT.READ_ONLY);
		text_source.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		text_source.addModifyListener(fListener);

		selectedGroupLabel = new CLabel(mainComposite, SWT.NONE);
		selectedGroupLabel.setText(group_label);
		selectedGroupLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

		text_group = new Text(mainComposite, SWT.READ_ONLY);
		text_group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		text_group.addModifyListener(fListener);
		
		selectedUnitLabel = new CLabel(mainComposite, SWT.NONE);
		selectedUnitLabel.setText(unit_label);
		selectedUnitLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

		text_unit = new Text(mainComposite, SWT.READ_ONLY);
		text_unit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		text_unit.addModifyListener(fListener);
		
		selectedContextLabel = new CLabel(mainComposite, SWT.NONE);
		selectedContextLabel.setText(context_label);
		selectedContextLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

		text_context = new Text(mainComposite, SWT.READ_ONLY);
		text_context.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		text_context.addModifyListener(fListener);
		
	}

	public String getName() {
		return "Source Configuration";
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			fInitializing=true;
			 sourceList = configuration.getAttribute(source_label, new ArrayList());
			 groupList = configuration.getAttribute(group_label, new ArrayList());
			 unitList = configuration.getAttribute(unit_label, new ArrayList());
			 contextList = configuration.getAttribute(context_label, new ArrayList());
			countSelected();
			checkTree();
			fInitializing = false;
		} catch (CoreException e) {
			e.printStackTrace();
			SWTUtils.showError(getShell(),e.getMessage());
		}
	}

	private void countSelected() {
		text_source.setText(""+sourceList.size());
		text_group.setText(""+groupList.size());
		text_unit.setText(""+unitList.size());
		text_context.setText(""+contextList.size());
	}

	private void checkTree() {
		for(int i=0;i<tree.getItems().length;i++){
			checkTreeItem(tree.getItems()[i]);
		}
	}
	private void checkTreeItem(TreeItem item){
		
		for(int i=0;i<item.getItems().length;i++){
			checkTreeItem(item.getItems()[i]);			
		}
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		getResultFromTree();
		configuration.setAttribute(source_label, sourceList);
		configuration.setAttribute(group_label, groupList);
		configuration.setAttribute(unit_label, unitList);
		configuration.setAttribute(context_label, contextList);

	}

	private void getResultFromTree() {
		sourceList = new ArrayList();
		groupList = new ArrayList();
		unitList = new ArrayList();
		contextList = new ArrayList();
		getSourceListFromTree();		
	}

	private void getSourceListFromTree() {
		for (int i = 0; i < tree.getItems().length; i++) {
			if (tree.getItems()[i].getChecked()
					&& !tree.getItems()[i].getGrayed()) {
				sourceList.add(tree.getItems()[i].getText());
			} else if (tree.getItems()[i].getGrayed()) {
				getGroupListFromTree(tree.getItems()[i]);
			}
		}
	}

	private void getGroupListFromTree(TreeItem item) {
		for (int i = 0; i < item.getItems().length; i++) {
			if (item.getItems()[i].getChecked()
					&& !item.getItems()[i].getGrayed()) {
				groupList.add(item.getText() + ":"
						+ item.getItems()[i].getText());
			} else if (item.getItems()[i].getGrayed()) {
				getUnitListFromTree(item.getItems()[i]);
			}
		}
	}

	private void getUnitListFromTree(TreeItem item) {
		for (int i = 0; i < item.getItems().length; i++) {
			if (item.getItems()[i].getChecked()
					&& !item.getItems()[i].getGrayed()) {
				unitList.add(item.getParentItem().getText() + ":"
						+ item.getText() + ":" + item.getItems()[i].getText());
			} else if (item.getItems()[i].getGrayed()) {
				getContextListFromTree(item.getItems()[i]);
			}
		}
	}

	private void getContextListFromTree(TreeItem item) {
		for (int i = 0; i < item.getItems().length; i++) {
			if (item.getItems()[i].getChecked()
					&& !item.getItems()[i].getGrayed()) {
				contextList.add(item.getParentItem().getParentItem().getText()
						+ ":" + item.getParentItem().getText() + ":"
						+ item.getText() + ":" + item.getItems()[i].getText());
			}
		}
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		
	}

	private void onTreeItemCheck(TreeItem item) {
		if (item.getChecked()) {
			item.setGrayed(false);
			WidgetUtil.checkChildrenTreeItem(item, true);
			WidgetUtil.checkParentTreeItem(item);
		}else{
			WidgetUtil.setAllParentGray(item);
			WidgetUtil.checkChildrenTreeItem(item, false);
		}
		if(!fInitializing){
			setDirty(true);
			updateLaunchConfigurationDialog();
		}
	}

}
