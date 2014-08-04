package com.gc.jef.view;

import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;



public class OBDclipseView extends ViewPart{
	public OBDclipseView() {
	}
	Table table;

	
//	class ViewContentProvider implements IStructuredContentProvider {
//		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
//		}
//		public void dispose() {
//		}
//		public Object[] getElements(Object parent) {
//			return new String[] { "One", "Two", "Three" };
//		}
//	}
//	
//	
//	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
//		public String getColumnText(Object obj, int index) {
//			return getText(obj);
//		}
//		public Image getColumnImage(Object obj, int index) {
//			return getImage(obj);
//		}
//		public Image getImage(Object obj) {
//			return PlatformUI.getWorkbench().
//					getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
//		}
//	}
//	
	class NameSorter extends ViewerSorter {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION| SWT.H_SCROLL | SWT.V_SCROLL);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(100);
		tableColumn.setText("File");
		
		TableColumn tableColumn_1 = new TableColumn(table, SWT.NONE);
		tableColumn_1.setWidth(300);
		tableColumn_1.setText("Imported OBD Class");
	}


	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		table.setFocus();
	}

//	public void addItem(JavaObdReference javaObdReference) {
//		TableItem tableItem = new TableItem(table, SWT.NONE);
//		tableItem.setText(javaObdReference.toTabletext());
//	}
	public void clearAll(){
		table.clearAll();
	}
	public void redraw(){
		table.redraw();
	}
}
