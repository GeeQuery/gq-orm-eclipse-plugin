package jef.ui.swt.view;

import java.util.ArrayList;
import java.util.List;

import jef.tools.Assert;
import jef.ui.model.TreeNode;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

public abstract class TreeView extends ViewPart {
	public static final String ID = "jef.abstract.treeview";

	protected TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;

	

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);// |
																				// SWT.H_SCROLL
		Tree tree = viewer.getTree();

		drillDownAdapter = new DrillDownAdapter(viewer);
		{
			TreeColumn treeColumn = new TreeColumn(tree, SWT.NONE);
			treeColumn.setWidth(900);
			treeColumn.setText("New Column");
		}
		viewer.setContentProvider(getViewContentProvider());
		viewer.setLabelProvider(getViewLabelProvider());
		viewer.setSorter(getNameSorter());
		viewer.setInput(getRootElement());
		getSite().setSelectionProvider(viewer);
		// Create the help context id for the viewer's control
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(),
		// "com.free.view.treeview.viewer");
		contributeToActions();
	}

	protected abstract TreeNode getRootElement();

	protected abstract ViewerSorter getNameSorter();

	protected abstract IBaseLabelProvider getViewLabelProvider();

	protected abstract IContentProvider getViewContentProvider();

	
	protected List<TreeNode> getSelections(Class<?> c) {
		ISelection selection = viewer.getSelection();
		List<?> list = ((IStructuredSelection) selection).toList();
		List<TreeNode> v=new ArrayList<TreeNode>();
		for(Object o: list){
			TreeNode node=(TreeNode)o;
			if(c.isAssignableFrom(node.getValue().getClass())){
				v.add(node);
			}
		}
		return v;
	}
	
	/**
	 * 得到选中节点包含的对象
	 * @param c
	 * @return
	 */
	protected TreeNode getSelectionNodeObject(Class<?> c) {
		ISelection selection = viewer.getSelection();
		List<?> list = ((IStructuredSelection) selection).toList();
		for(Object o: list){
			TreeNode node=(TreeNode)o;
			if(c.isAssignableFrom(node.getValue().getClass())){
				return node;
			}
		}
		return null;
	}
	
	/**
	 * 得到选中的节点
	 * @return
	 */
	protected TreeNode getSelection() {
		ISelection selection = viewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		Assert.isType(obj, TreeNode.class, "Invalid event Object: " + obj.getClass().getName());
		return (TreeNode) obj;
	}

	// 生成工具条
	private void contributeToActions() {
		// 处理双击事件
		final Action defaultAction = new Action() {
			public void run() {
				TreeNode obj = getSelection();
				defaultViewAction(obj);
			}
		};
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				defaultAction.run();
			}
		});

		// 处理上下文菜单
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {

				fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);

		// 生成菜单
		IMenuManager manager = getViewSite().getActionBars().getMenuManager();
		for (Action act : getBarActions()) {
			manager.add(act);
		}
		manager.add(new Separator());

		// 生成工具栏
		IToolBarManager manager2 = getViewSite().getActionBars().getToolBarManager();
		for (Action act : getBarActions()) {
			manager2.add(act);
		}
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);

	}

	protected abstract Action[] getBarActions();

	protected abstract Action[] getPopupActions(IStructuredSelection selection);

	// 生成上下文菜单
	private void fillContextMenu(IMenuManager manager) {
		ISelection selection = viewer.getSelection();
		for (Action act : getPopupActions((IStructuredSelection) selection)) {
			manager.add(act);
		}
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	// 定义双击产生的视图默认操作
	public abstract void defaultViewAction(TreeNode eventObj);

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void refresh() {
		viewer.refresh();
	}

	// 为视图设置节点并刷新整个视图
	public void setInput(TreeNode root) {
		viewer.setInput(root);
		viewer.refresh();
	}

	// 清除全部内容
	public void clearAll() {
		viewer.setInput(newRoot());
	}

	// 创建新的根节点
	public TreeNode newRoot() {
		return new TreeNode("");
	}
}
