package jef.ui.swt.util;

import jef.ui.model.TreeNode;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;



/**
 * created by @author dht on 2006-8-1
 *
 */
public class TreeContentProvider implements ITreeContentProvider {


	public Object[] getChildren(Object parentElement){
		if (parentElement != null && parentElement instanceof TreeNode) {
			TreeNode node = (TreeNode) parentElement;

			try {
				return node.getChildren();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return new TreeNode[0];
	}

	public Object getParent(Object element) {
		if (element instanceof TreeNode) {
			return ((TreeNode) element).getParent();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof TreeNode) {
			TreeNode node = (TreeNode) element;
			try {
				return node.hasChildren();
			} catch (Exception e) {
				SWTUtils.showError(e.getMessage());
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public void dispose() {

	}

	public void inputChanged(Viewer viewer, Object oldInput,
			Object newInput) {
	}

}
