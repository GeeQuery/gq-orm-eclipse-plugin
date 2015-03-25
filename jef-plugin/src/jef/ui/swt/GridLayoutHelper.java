package jef.ui.swt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jef.tools.Assert;
import jef.tools.StringUtils;
import jef.ui.swt.util.ButtonListener;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

public class GridLayoutHelper {
	// 设置控件跨行
	public static void setRowSpan(Control c, int count) {
		GridData gd = getGridData(c);
		if (gd == null)
			gd = GridDataHelper.getDefault();
		gd.verticalSpan = count;
		c.setLayoutData(gd);
	}

	// 设置控件跨列
	public static void setColSpan(Control c, int count) {
		GridData gd = getGridData(c);
		if (gd == null)
			gd = GridDataHelper.getDefault();
		gd.horizontalSpan = count;
		c.setLayoutData(gd);
	}

	// 设置控件最小高度
	public static void setHeight(Control c, int height) {
		if (height > 0) {
			GridData gd = getGridData(c);
			if (gd == null)
				gd = GridDataHelper.getDefault();
			gd.minimumHeight = height;
			gd.heightHint = height;
			c.setLayoutData(gd);
		}
	}

	public static void setAlignmentRight(Control c) {
		GridData gd = getGridData(c);
		if (gd == null)
			gd = GridDataHelper.getDefault();
		gd.horizontalAlignment = SWT.RIGHT;
		c.setLayoutData(gd);
	}

	public static void setAlignmentCenter(Control c) {
		GridData gd = getGridData(c);
		if (gd == null)
			gd = GridDataHelper.getDefault();
		gd.horizontalAlignment = SWT.CENTER;
		c.setLayoutData(gd);
	}

	public static void setAlignmentLeft(Control c) {
		GridData gd = getGridData(c);
		if (gd == null)
			gd = GridDataHelper.getDefault();
		gd.horizontalAlignment = SWT.LEFT;
		c.setLayoutData(gd);
	}

	// 设置控件最小宽度
	public static void setWidth(Control c, int width) {
		if (width > 0) {
			GridData gd = getGridData(c);
			if (gd == null)
				gd = GridDataHelper.getDefault();
			gd.minimumWidth = width;
			gd.widthHint = width;
			c.setLayoutData(gd);
		}
	}

	// 设置控件水平延伸
	public static void setWidthExtended(Control c) {
		GridData gd = getGridData(c);
		if (gd == null)
			gd = GridDataHelper.getDefault();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		c.setLayoutData(gd);
	}

	// 设置控件垂直延伸
	public static void setHeightExtended(Control c) {
		GridData gd = getGridData(c);
		if (gd == null)
			gd = GridDataHelper.getDefault();
		gd.grabExcessVerticalSpace = true;
		gd.verticalAlignment = SWT.FILL;
		c.setLayoutData(gd);
	}

	private static GridData getGridData(Control c) {
		GridData gd = null;
		Object obj = c.getLayoutData();
		if (obj != null && obj instanceof GridData) {
			gd = (GridData) obj;
		}
		return gd;
	}

	/**
	 * 将下一个网格标为空白
	 * 
	 * @param c
	 */
	public static void createEmpty(Composite c) {
		createEmpty(c, -1);
	}

	/**
	 * 将下一个网格标为空白
	 * 
	 * @param c
	 */
	public static void createEmpty(Composite c, int width) {
		Label l = new Label(c, SWT.NONE);
		setWidth(l, width);
	}

	public static Combo createCombo(Composite parent, Object[] options, int width) {
		Combo c = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (width > 0)
			setWidth(c, width);
		if (options != null) {
			List<String> items = new ArrayList<String>();
			for (Object obj : options) {
				items.add(StringUtils.toString(obj));
			}
			c.setItems(items.toArray(ArrayUtils.EMPTY_STRING_ARRAY));
			c.setData(options);
		}
		return c;
	}

	/**
	 * 创建文本
	 * 
	 * @param c
	 * @param text
	 */
	public static Label createText(Composite c, String text) {
		return createText(c, text, -1);
	}

	/**
	 * 创建文本并指定宽度 (文字超过宽度自动折行)
	 * 
	 * @param c
	 * @param text
	 * @param width
	 */
	public static Label createText(Composite c, String text, int width) {
		Label l = new Label(c, SWT.WRAP);
		l.setText(text);
		setWidth(l, width);
		return l;
	}

	public static Text createTextArea(Composite c, int width, ModifyListener... listeners) {
		Text text = new Text(c, SWT.BORDER | SWT.WRAP);
		setWidth(text, width);
		for (ModifyListener listener : listeners) {
			text.addModifyListener(listener);
		}
		return text;
	}

	/**
	 * 创建文本输入框
	 * 
	 * @param c
	 * @param width
	 * @return
	 */
	public static Text createTextInput(Composite c, int width, boolean isPassword, ModifyListener... listeners) {
		Text text = new Text(c, SWT.BORDER | (isPassword ? SWT.PASSWORD : SWT.NONE));
		setWidth(text, width);
		for (ModifyListener listener : listeners) {
			text.addModifyListener(listener);
		}
		return text;
	}

	/**
	 * 创建文本输入框
	 * 
	 * @param c
	 * @return
	 */
	public static Text createTextInput(Composite c, boolean isPassword, ModifyListener... listeners) {
		Text text = new Text(c, SWT.BORDER | (isPassword ? SWT.PASSWORD : SWT.NONE));
		setWidthExtended(text);
		for (ModifyListener listener : listeners) {
			text.addModifyListener(listener);
		}
		return text;
	}

	/**
	 * 创建按钮
	 * 
	 * @author Administrator
	 */
	public static Button createButton(Composite c, String text, final ButtonListener lis, int style) {
		try {
			final Button b = new Button(c, style);
			b.setText(text);
			if(lis!=null){
				b.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						Button c = (Button) e.getSource();
						lis.onClick(c);
					}
				});	
			}
			return b;
		} catch (SecurityException e1) {
			throw new RuntimeException(e1);
		}
	}

	/**
	 * 创建按钮
	 * 
	 * @author Administrator
	 */
	public static Button createButton(Composite c, String text, int width, final ButtonListener lis) {
		try {
			// final MethodEx m = BeanUtils.getCompatibleMethod(lis.getClass(),
			// onClick, Button.class);
			// if (m == null) {
			// MessageDialog.openInformation(c.getShell(), "错误", "类" +
			// lis.getClass().getName() + "中缺少方法" + onClick + "，不能创建按钮。");
			// throw new RuntimeException("类" + lis.getClass().getName() +
			// "中缺少方法" + onClick + "，不能创建按钮。");
			// }
			Button b = new Button(c, SWT.NONE);
			b.setText(text);
			setWidth(b, width);
			b.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Button c = (Button) e.getSource();
					lis.onClick(c);
				}
			});
			return b;
		} catch (SecurityException e1) {
			throw new RuntimeException(e1);
		}
	}

	/**
	 * 在本行剩下的位置填上空白，使得下一个控件位于行的开始
	 * 
	 * @param c
	 */
	public static void toNextRow(Composite c) {
		GridLayout gl = (GridLayout) c.getLayout();
		int columns = gl.numColumns;
		Control[] controls = c.getChildren();
		int left = columns - (controls.length % columns);
		for (int i = 0; i < left; i++) {
			createEmpty(c);
		}
	}

	/**
	 * 将本行剩下的位置全部合并到最后一个上
	 * 
	 * @param c
	 *            Composite
	 * @param expandControl
	 *            ，为true时控件也横向扩展，否则控件左对齐
	 */
	public static void expandToRowEnd(Composite c, boolean expandControl) {
		GridLayout gl = (GridLayout) c.getLayout();
		if (gl != null) {
			int columns = gl.numColumns;
			Control[] controls = c.getChildren();
			int gridNum = 0;
			for (Control cr : controls) {
				GridData data = getGridData(cr);
				gridNum += (data == null) ? 1 : data.horizontalSpan;
			}
			Control last = controls[controls.length - 1];

			int left = columns - (gridNum % columns);
			if (left == columns)
				left = 0;
			if (left > 0) {
				GridData data = getGridData(last);
				int my = (data == null) ? 1 : data.horizontalSpan;
				setColSpan(last, left + my);
			}

			if (!expandControl) {
				GridData gd = getGridData(c);
				if (gd == null)
					gd = GridDataHelper.getDefault();
				gd.grabExcessHorizontalSpace = true;
				gd.horizontalAlignment = SWT.LEFT;
				c.setLayoutData(gd);
			}
		}
	}

	static class GridDataHelper {
		// 返回缺省的单元格布局
		public static GridData getDefault() {
			GridData gd = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
			gd.minimumHeight = 5;
			gd.minimumWidth = 8;
			return gd;
		}

		// 返回自动水平扩展的单元格布局
		public static GridData getHorizontalExtendStyle() {
			GridData gd = getDefault();
			gd.grabExcessHorizontalSpace = true;
			return gd;
		}

		// 返回垂直允许扩展的单元格布局
		public static GridData getVerticalExtendStyle() {
			GridData gd = getDefault();
			gd.grabExcessVerticalSpace = true;
			return gd;
		}

		// 返回垂直和水平都允许扩展的单元格布局
		public static GridData getBothExtendStyle() {
			GridData gd = getDefault();
			gd.grabExcessHorizontalSpace = true;
			gd.grabExcessVerticalSpace = true;
			return gd;
		}

		// 返回垂直和水平都允许扩展，并且空间垂直拉升的单元格布局
		public static GridData getBothExtendWithControlStyle() {
			GridData gd = getDefault();
			gd.grabExcessHorizontalSpace = true;
			gd.grabExcessVerticalSpace = true;
			gd.verticalAlignment = SWT.FILL;
			return gd;
		}
	}

	// SWT.SINGLE、SWT.MULTI、SWT.CHECK. （单选，多选，可check）
	public static Tree createTree(Composite topComp, jef.ui.model.TreeNode root, String defaultSelection, int width, int gridColSpan, int style, boolean hideRoot, Provider... providers) {
		Tree tree = new Tree(topComp, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.VIRTUAL | style);
		Assert.notNull(root);
		setColSpan(tree, gridColSpan);
		setWidth(tree, width);
		setHeightExtended(tree);
		tree.setData(providers);
		fillTree(tree, root, hideRoot);
		return tree;
	}

	private static void fillTree(Tree tree, jef.ui.model.TreeNode root, boolean hideRoot) {
		Provider[] providers = (Provider[]) tree.getData();
		if (hideRoot) {
			int index = 0;
			if (root.getChildren() != null) {
				for (jef.ui.model.TreeNode node : root.getChildren()) {
					fillItems(tree, node, index++, providers);
				}
			}
		} else {
			TreeItem thisItem = new TreeItem(tree, SWT.NONE, 0);
			String label = null;
			for (Provider p : providers) {
				if (p instanceof ImgProvider) {
					ImageDescriptor img = ((ImgProvider) p).getImageDesc(root.getValue());
					if (img != null)
						thisItem.setImage(img.createImage());
				} else if (p instanceof TextProvider) {
					label = ((TextProvider) p).getLabel(root.getValue());
				}
			}
			thisItem.setText(label == null ? root.toString() : label);
			thisItem.setData(root);
			thisItem.setExpanded(true);
			int index = 0;
			if (root.getChildren() != null) {
				for (jef.ui.model.TreeNode node : root.getChildren()) {
					fillItems(thisItem, node, index++, providers);
				}
			}
		}
	}

	public static void resetTree(Tree tree, jef.ui.model.TreeNode root, boolean hideRoot) {
		tree.removeAll();
		fillTree(tree, root, hideRoot);
	}

	/**
	 * 节点的数据变化后，更新节点的显示内容。
	 * 
	 * @param item
	 */
	public static void resetText(TreeItem item) {
		Tree tree = item.getParent();
		Provider[] providers = (Provider[]) tree.getData();
		TreeNode node = (TreeNode) item.getData();
		if (providers == null || node == null)
			return;
		String label = null;
		for (Provider p : providers) {
			if (p instanceof ImgProvider) {
				ImageDescriptor img = ((ImgProvider) p).getImageDesc(node.getValue());
				if (img != null)
					item.setImage(img.createImage());
			} else if (p instanceof TextProvider) {
				label = ((TextProvider) p).getLabel(node.getValue());
			}
		}
		item.setText(label == null ? node.toString() : label);
	}

	private static void fillItems(Widget tree, jef.ui.model.TreeNode root, int index, Provider... provider) {
		TreeItem thisItem;
		if (tree instanceof Tree) {
			thisItem = new TreeItem((Tree) tree, SWT.NONE, index);
		} else if (tree instanceof TreeItem) {
			thisItem = new TreeItem((TreeItem) tree, SWT.NONE, index);
		} else {
			return;
		}
		String label = null;
		for (Provider p : provider) {
			if (p instanceof ImgProvider) {
				ImageDescriptor img = ((ImgProvider) p).getImageDesc(root.getValue());
				if (img != null)
					thisItem.setImage(img.createImage());
			} else if (p instanceof TextProvider) {
				label = ((TextProvider) p).getLabel(root.getValue());
			}
		}
		thisItem.setText(label == null ? root.toString() : label);
		thisItem.setData(root);
		int myindex = 0;
		if (root.getChildren() != null) {
			for (jef.ui.model.TreeNode node : root.getChildren()) {
				fillItems(thisItem, node, myindex++, provider);
			}
		}
	}

	public static Table createTable(Composite topComp, String[] columns, int[] widths, int gridColSpan) {
		Assert.isTrue(columns.length == widths.length, "The param columns and widths must be same length.");
		Table table = new Table(topComp, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		setColSpan(table, gridColSpan);
		setHeightExtended(table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		for (int j = 0; j < columns.length; j++) {
			TableColumn tableColumn = new TableColumn(table, SWT.NONE);
			tableColumn.setWidth(widths[j]);
			tableColumn.setText(columns[j]);
		}
		return table;
	}

	public static org.eclipse.swt.widgets.List createList(Shell shell, Map<String, ?> items, boolean multi) {
		org.eclipse.swt.widgets.List list = new org.eclipse.swt.widgets.List(shell, SWT.BORDER | (multi ? SWT.MULTI : 0) | SWT.V_SCROLL);
		list.setItems(items.keySet().toArray(new String[items.size()]));
		list.setData(items);
		return list;

	}
}
