package jef.ui.swt.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.ReflectionException;

import jef.tools.Assert;
import jef.tools.DateFormats;
import jef.tools.DateUtils;
import jef.tools.StringUtils;
import jef.tools.reflect.BeanWrapperImpl;
import jef.tools.reflect.ClassEx;
import jef.tools.reflect.ConvertUtils;
import jef.ui.model.InputModel;
import jef.ui.model.TreeNode;
import jef.ui.swt.GridLayoutHelper;
import jef.ui.swt.Provider;
import jef.ui.swt.util.SWTUtils.Columns;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class BeanBinding {
	GridLayout layout;
	public Object bean;
	private MyWrapper w;
	Shell shell;
	boolean isCancle = true;
	Map<String, Control> bindMapping = new HashMap<String, Control>();

	public Control getBindControl(String fieldName) {
		return bindMapping.get(fieldName);
	}

	public int getColumnCount() {
		return layout.numColumns;
	}

	public BeanBinding(Shell shell, Object bean, GridLayout layout) {
		this.shell = shell;
		this.bean = bean;
		this.layout = layout;
		w = new MyWrapper(bean);
	}

	public Combo createCombo(String fieldName, Object[] options, int width) {
		Combo co = GridLayoutHelper.createCombo(shell, options, width);
		bindMapping.put(fieldName, co);
		return co;
	}

	public Tree createTree(String fieldName, jef.ui.model.TreeNode root, int width, int colSpan, boolean hideRoot, Provider... provs) {
		Tree tree = GridLayoutHelper.createTree(shell, root, "", width, colSpan, SWT.MULTI | SWT.CHECK, hideRoot, provs);
		bindMapping.put(fieldName, tree);
		return tree;
	}

	public Tree createSingleTree(String fieldName, jef.ui.model.TreeNode root, int width, int colSpan, boolean hideRoot, Provider... provs) {
		Tree tree = GridLayoutHelper.createTree(shell, root, "", width, colSpan, SWT.SINGLE, hideRoot, provs);
		bindMapping.put(fieldName, tree);
		return tree;
	}

	public Label createText(String text) {
		return GridLayoutHelper.createText(shell, text);
	}

	public Label createTextFromField(String string) {
		return GridLayoutHelper.createText(shell, StringUtils.toString(w.getPropertyValue(string)));
	}

	public Table createtable(String fieldName, Columns cfg, int colSpan) {
		cfg.validate();
		String[] columns = cfg.names;
		int[] widths = cfg.widths;
		String[] bindPropertyName = cfg.valueBinds;
		Table table = GridLayoutHelper.createTable(shell, columns, widths, colSpan);
		TableColumn[] col = table.getColumns();
		for (int i = 0; i < col.length; i++) {
			col[i].setData(bindPropertyName[i]);
		}
		bindMapping.put(fieldName, table);
		return table;
	}

	public Text createTextArea(String fieldName, int width, int colSpan, boolean bind) {
		Text c;
		Assert.isTrue(w.isReadableProperty(fieldName));
		Assert.isTrue(w.isWritableProperty(fieldName));
		c = GridLayoutHelper.createTextArea(shell, width);
		if (bind)
			bindMapping.put(fieldName, c);
		if (colSpan > 1)
			GridLayoutHelper.setColSpan(c, colSpan);
		return c;
	}

	public Text createTextArea(String fieldName, int width, int colSpan) {
		Text c;
		Assert.isTrue(w.isReadableProperty(fieldName));
		Assert.isTrue(w.isWritableProperty(fieldName));
		c = GridLayoutHelper.createTextArea(shell, width);
		bindMapping.put(fieldName, c);
		if (colSpan > 1)
			GridLayoutHelper.setColSpan(c, colSpan);
		return c;
	}

	public Text createTextInput(String fieldName, int width, int colSpan, boolean... isPasswords) {
		Text c;
		Assert.isTrue(w.isReadableProperty(fieldName));
		Assert.isTrue(w.isWritableProperty(fieldName));
		boolean isPassword = (isPasswords.length > 0 && isPasswords[0]);
		c = GridLayoutHelper.createTextInput(shell, width, isPassword);
		bindMapping.put(fieldName, c);
		if (colSpan > 1)
			GridLayoutHelper.setColSpan(c, colSpan);
		return c;
	}

	public Button createButton(String text, ButtonListener li) {
		Button b = GridLayoutHelper.createButton(shell, text, li, SWT.NONE);
		b.setData(this);
		return b;
	}

	/**
	 * 创建CheckBox
	 * 
	 * @param fieldName
	 * @param text
	 * @param methodName
	 * @param li
	 * @return
	 */
	public Button createCheckBox(String fieldName, String text, ButtonListener li) {
		Button b = GridLayoutHelper.createButton(shell, text, li, SWT.CHECK);
		b.setData(this);
		if (StringUtils.isNotEmpty(fieldName)) {
			bindMapping.put(fieldName, b);
		}
		return b;
	}

	/**
	 * 创建ListBox
	 * 
	 * @param fieldName
	 * @param items
	 * @param width
	 */
	public org.eclipse.swt.widgets.List createListBox(String fieldName, Map<String, ?> items, boolean multi, int width) {
		org.eclipse.swt.widgets.List list = GridLayoutHelper.createList(shell, items, multi);
		GridLayoutHelper.setWidth(list, width);
		bindMapping.put(fieldName, list);
		return list;
	}

	public void createEmpty() {
		GridLayoutHelper.createEmpty(shell);
	}

	public void createEmpty(int width) {
		GridLayoutHelper.createEmpty(shell, width);
	}

	public void expandToRowEnd(boolean expandControl) {
		GridLayoutHelper.expandToRowEnd(shell, expandControl);
	}

	@SuppressWarnings("rawtypes")
	public void loadFromBean() {
		try {
			for (String fieldName : bindMapping.keySet()) {
				Object obj = w.getPropertyValue(fieldName);
				Control control = bindMapping.get(fieldName);
				if (obj == null) {
					continue;
				}
				if (control instanceof Text) {
					((Text) control).setText(StringUtils.toString(obj));
				} else if (control instanceof Table) {
					Table table = (Table) control;
					if (obj instanceof List) {
						listValueToUiTable((List) obj, table);
					} else {
						System.err.println("控件" + control.getClass().getName() + "上不支持与java类型" + obj.getClass().getName() + "绑定!");
					}
				} else if (control instanceof Combo) {
					Combo co = (Combo) control;
					if (co.getData() != null) {
						Object[] options = (Object[]) co.getData();
						int index = ArrayUtils.indexOf(options, obj);
						if (index != ArrayUtils.INDEX_NOT_FOUND) {
							co.setText(obj.toString());
						}
					} else {
						String value = StringUtils.toString(obj);
						if (value.length() > 0) {
							if (!ArrayUtils.contains(co.getItems(), value)) {
								co.add(value);
							}
						}
						co.setText(value);
					}
				} else if (control instanceof Button) {
					Button b = (Button) control;
					if (obj instanceof Boolean) {
						b.setSelection((Boolean) obj);
					}
				} else if (control instanceof Tree) {
					Tree tree = (Tree) control;
					boolean isMulti = (tree.getStyle() & SWT.MULTI) > 0;
					if (isMulti) {
						List<?> list = (List<?>) obj;
						for (TreeItem item : tree.getItems()) {
							TreeNode node = (TreeNode) item.getData();
							if (list.contains(node.getValue())) {
								item.setChecked(true);
							}
						}
					} else {
						for (TreeItem item : tree.getItems()) {
							TreeNode node = (TreeNode) item.getData();
							if (obj.equals(node.getValue())) {
								tree.select(item);
								break;
							}
						}
					}
				} else if (control instanceof org.eclipse.swt.widgets.List) {
					org.eclipse.swt.widgets.List list = (org.eclipse.swt.widgets.List) control;
					Map<String, ?> data = (Map<String, ?>) list.getData();
					if (obj instanceof List) {
						for (Object v : (List) obj) {
							String key = getKeyByValue(data, v);
							int index = ArrayUtils.indexOf(list.getItems(), key);
							if (index > -1)
								list.select(index);
						}
					} else {
						System.err.println("控件" + control.getClass().getName() + "上不支持与java类型" + obj.getClass().getName() + "绑定!");
					}
				} else {
					throw new Exception("控件" + control.getClass().getName() + "上不支持!");
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	private String getKeyByValue(Map<String, ?> data, Object v) {
		for (Entry<String, ?> e : data.entrySet()) {
			if (ObjectUtils.equals(v, e.getValue())) {
				return e.getKey();
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	private void listValueToUiTable(List list, Table table) {
		for (Object o : (List) list) {
			createTableItem(o, table);
		}
	}

	public static TableItem createTableItem(Object o, Table table) {
		BeanWrapperImpl element = new BeanWrapperImpl(o);
		List<String> strs = new ArrayList<String>();
		TableColumn[] col = table.getColumns();
		for (TableColumn c : col) {
			String subF = (String) c.getData();
			if (subF.endsWith("()")) {
				String methodName = StringUtils.substringBefore(subF, "()");
				try {
					strs.add(StringUtils.toString(element.invokeMethod(methodName)));
				} catch (ReflectionException e) {
					e.printStackTrace();
					strs.add(e.getMessage());
				}
			} else {
				strs.add(StringUtils.toString(element.getPropertyValue(subF)));
			}
		}
		TableItem tableItem = new TableItem(table, SWT.NONE);
		tableItem.setText(strs.toArray(ArrayUtils.EMPTY_STRING_ARRAY));
		tableItem.setData(o);
		return tableItem;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List uiTableValueToList(Table table) {
		List list = new ArrayList();
		for (TableItem i : table.getItems()) {
			list.add(i.getData());
		}
		return list;
	}

	public void updateBean() {
		try {
			for (String fieldName : bindMapping.keySet()) {
				Control control = bindMapping.get(fieldName);
				Class<?> c = w.getFieldType(fieldName);
				if (control instanceof Text) {
					String str = ((Text) control).getText();
					if (control.getData("DateFormat") != null) {
						DateFormat df = (DateFormat) control.getData("DateFormat");
						w.setPropertyValue(fieldName, DateUtils.parse(str, df, null));
					} else {
						w.setPropertyValue(fieldName, ConvertUtils.toProperType(str, new ClassEx(c), null));
					}
				} else if (control instanceof Table) {
					Table table = (Table) control;
					if (c.isAssignableFrom(List.class)) {
						List<?> value = uiTableValueToList(table);
						w.setPropertyValue(fieldName, value);
					} else {
						System.err.println("控件" + control.getClass().getName() + "上不支持与java类型" + c.getName() + "绑定!");
					}
				} else if (control instanceof Combo) {
					Combo co = (Combo) control;
					if (co.getData() == null) {
						w.setPropertyValue(fieldName, co.getText());
					} else {
						Object[] options = (Object[]) co.getData();
						int index = ArrayUtils.indexOf(co.getItems(), co.getText());
						if (index != -1) {
							w.setPropertyValue(fieldName, options[index]);
						} else {
							w.setPropertyValue(fieldName, null);
						}
					}
				} else if (control instanceof Button) {
					w.setPropertyValue(fieldName, ((Button) control).getSelection());
				} else if (control instanceof Tree) {
					Tree tree = (Tree) control;
					boolean isMulti = (tree.getStyle() & SWT.MULTI) > 0;
					if (isMulti) {
						List<Object> result = new ArrayList<Object>();
						for (TreeItem item : tree.getItems()) {
							if (item.getChecked()) {
								TreeNode node = (TreeNode) item.getData();
								result.add(node.getValue());
							}
						}
						w.setPropertyValue(fieldName, result);
					} else {
						TreeItem[] items = tree.getSelection();
						if (items.length == 0) {
							w.setPropertyValue(fieldName, null);
						} else {
							TreeNode node = (TreeNode) items[0].getData();
							w.setPropertyValue(fieldName, node.getValue());
						}
					}
				} else if (control instanceof org.eclipse.swt.widgets.List) {
					org.eclipse.swt.widgets.List list = (org.eclipse.swt.widgets.List) control;
					Map<String, ?> data = (Map<String, ?>) list.getData();
					List l = new ArrayList<Object>();
					for (String str : list.getSelection()) {
						l.add(data.get(str));
					}
					w.setPropertyValue(fieldName, l);
				} else {
					throw new Exception("控件" + control.getClass().getName() + "上不支持!");
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	static class MyWrapper {
		private BeanWrapperImpl wrapper;

		private Map map;

		public MyWrapper(Object obj) {
			if (obj instanceof Map) {
				map = (Map) obj;
			} else {
				wrapper = new BeanWrapperImpl(obj);
			}
		}

		public Boolean isWritableProperty(String fieldName) {
			if (map != null)
				return true;
			return wrapper.isWritableProperty(fieldName);
		}

		public boolean isReadableProperty(String fieldName) {
			if (map != null)
				return true;
			return wrapper.isReadableProperty(fieldName);
		}

		public void setPropertyValue(String fieldName, Object newValue) {
			if (map != null) {
				Object old = map.get(fieldName);
				if (old != null && old instanceof InputModel) {
					((InputModel) old).set(newValue);
				} else {
					map.put(fieldName, newValue);
				}
			} else {
				wrapper.setPropertyValue(fieldName, newValue);
			}
		}

		public Class<?> getFieldType(String fieldName) {
			if (map != null) {
				return Object.class;
			}
			return wrapper.getPropertyRawType(fieldName);
		}

		public Object getPropertyValue(String fieldName) {
			if (map != null) {
				Object old = map.get(fieldName);
				if (old instanceof InputModel) {
					return ((InputModel) old).get();
				} else {
					return old;
				}
			}
			return wrapper.getPropertyValue(fieldName);
		}
	}

	static class DateDialog extends Dialog {
		DateTime time;
		DateTime calendar;
		boolean withTime;
		Date data;

		protected DateDialog(Shell parentShell, Date defaultValue, boolean withTime) {
			super(parentShell);
			this.data = defaultValue;
			this.withTime = withTime;
		}

		@Override
		protected void okPressed() {
			Calendar ca = Calendar.getInstance();
			if (withTime) {
				ca.set(calendar.getYear(), calendar.getMonth(), calendar.getDay(), time.getHours(), time.getMinutes(), time.getSeconds());
			} else {
				ca.set(calendar.getYear(), calendar.getMonth(), calendar.getDay());
			}
			this.data = ca.getTime();
			super.okPressed();
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			Composite comp = (Composite) super.createDialogArea(parent);
			comp.setLayout(new GridLayout(2, false));
			Calendar ca = Calendar.getInstance();
			ca.setTime(data);
			if (withTime) {
				time = new DateTime(comp, SWT.TIME | SWT.MEDIUM);
				time.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
				time.setTime(ca.get(Calendar.HOUR), ca.get(Calendar.MINUTE), ca.get(Calendar.SECOND));
			}
			Button cmdCurrenrTime = new Button(comp, SWT.NONE);
			cmdCurrenrTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			cmdCurrenrTime.setText("当前日期");
			cmdCurrenrTime.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					Calendar ca = Calendar.getInstance();
					if (withTime) {
						time.setTime(ca.get(Calendar.HOUR), ca.get(Calendar.MINUTE), ca.get(Calendar.SECOND));
					}
					calendar.setDate(ca.get(Calendar.YEAR), ca.get(Calendar.MONTH), ca.get(Calendar.DATE));
				}
			});
			calendar = new DateTime(comp, SWT.CALENDAR | SWT.BORDER);
			calendar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
			calendar.setDate(ca.get(Calendar.YEAR), ca.get(Calendar.MONTH), ca.get(Calendar.DATE));
			return comp;
		}
	}

	public void createDateInput(String fieldName, int colSpan, int width, final boolean isWithTime) {
		final Text data = createTextInput(fieldName, width, colSpan, false);
		ButtonListener bs = new ButtonListener() {

			@Override
			public void onClick(Button btn) {
				DateFormat format;
				if (isWithTime) {
					format = DateFormats.DATE_TIME_CS.get();
				} else {
					format = DateFormats.DATE_CS.get();
				}
				data.setData("DateFormat", format);
				String old = data.getText();
				Date d = null;
				try {
					d = DateUtils.parse(old, format);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (d == null)
					d = new Date();
				DateDialog dialog = new DateDialog(shell, d, isWithTime);
				if (dialog.open() == Dialog.OK) {
					data.setText(format.format(dialog.data));
				}

			}
		};
		createButton("...", bs);
	}
}
