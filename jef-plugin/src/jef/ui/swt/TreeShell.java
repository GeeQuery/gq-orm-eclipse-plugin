package jef.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class TreeShell {
	Display d;
	Shell s;

	public TreeShell() {
		d = new Display();
		s = new Shell(d);
		s.setSize(250, 200);
		s.setText("Tree example");
		s.setLayout(new FillLayout());
		// 创建具有单选和边框的Tree
		Tree t = new Tree(s, SWT.SINGLE | SWT.BORDER);
		// 创建主干项目1、2和3
		TreeItem child1 = new TreeItem(t, SWT.NONE, 0);
		child1.setText("1");
		child1.setImage(new Image(d, "icons/a4.gif"));
		TreeItem child2 = new TreeItem(t, SWT.NONE, 1);
		child2.setText("2");
		child2.setImage(new Image(d, "icons/a4.gif"));
		// 创建子项目的子项目2A和2B
		TreeItem child2a = new TreeItem(child2, SWT.NONE, 0);
		child2a.setText("2A");
		child2a.setImage(new Image(d, "icons/a4.gif"));
		TreeItem child2b = new TreeItem(child2, SWT.NONE, 1);
		child2b.setText("2B");
		child2b.setImage(new Image(d, "icons/a4.gif"));
		TreeItem child3 = new TreeItem(t, SWT.NONE, 2);
		child3.setText("3");
		child3.setImage(new Image(d, "icons/a4.gif"));
		t.addTreeListener(new TreeListener() {
			// 图像变成被合拢对象时的样子
			public void treeCollapsed(TreeEvent e) {
				TreeItem ti = (TreeItem) e.item;
				ti.setImage(new Image(d, "icons/a4.gif"));
			}

			// 图像变成被展开对象所需的样子
			public void treeExpanded(TreeEvent e) {
				TreeItem ti = (TreeItem) e.item;
				ti.setImage(new Image(d, "e:/workspace/rcp/orlstudio/icons/state.gif"));
			}
		});
		s.open();
		while (!s.isDisposed())
			if (!d.readAndDispatch())
				d.sleep();
		d.dispose();
	}

	public static void main(String args[]) {
		new TreeShell();
	}
}
