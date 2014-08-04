package jef.ui.swt.util;

import org.eclipse.swt.widgets.TreeItem;


/**
 * created by @author dht on 2006-8-1
 *
 */
public class WidgetUtil {
	public static void checkChildrenTreeItem(TreeItem item, boolean b){
		for(int i=0;i<item.getItems().length;i++){
			item.getItems()[i].setChecked(b);
			item.getItems()[i].setGrayed(false);
			checkChildrenTreeItem(item.getItems()[i],b);
		}
	}
	public static void setAllParentGray(TreeItem item){
		if(item == null) return;
		if(item.getParentItem() == null) return ;
		boolean allChildrenNoSelected = true;
		for(int i=0;i<item.getParentItem().getItems().length;i++){
			if(item.getParentItem().getItems()[i].getChecked()){
				allChildrenNoSelected=false;
				break;
			}
		}
		if(allChildrenNoSelected){
			item.getParentItem().setChecked(false);
		}else{
			item.getParentItem().setGrayed(true);
		}
		setAllParentGray(item.getParentItem());
	}
	public static void checkParentTreeItem(TreeItem item) {
		if (item == null)
			return;
		if (item.getParentItem() == null)
			return;
		if(item.getParentItem().getChecked()&& item.getParentItem().getGrayed()){
			return;
		}
		item.getParentItem().setChecked(true);
		item.getParentItem().setGrayed(true);
		checkParentTreeItem(item.getParentItem());
	}
}
