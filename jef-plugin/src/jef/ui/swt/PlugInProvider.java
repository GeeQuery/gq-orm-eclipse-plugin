package jef.ui.swt;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jef.common.wrapper.Holder;
import jef.database.meta.TableInfo;
import jef.ui.model.TreeNode;
import jef.ui.swt.util.AbstractDialog;
import jef.ui.swt.util.BeanBinding;
import jef.ui.swt.util.ButtonListener;
import jef.ui.swt.util.UIOper;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Tree;

import com.github.geequery.codegen.MetaProvider;
import com.github.geequery.codegen.Metadata;

public class PlugInProvider implements MetaProvider,ButtonListener {
	private Tree tree;
	private MetaProvider parentProvider;
	
	public PlugInProvider(MetaProvider arg0) {
		this.parentProvider=arg0;
	}

	@SuppressWarnings("unchecked")
	public List<TableInfo> getTables() throws SQLException {
		final List<TableInfo> tables=parentProvider.getTables();
		final Map<String,Object> data=new HashMap<String,Object>();
		data.put("list", tables);
		final Holder<Boolean> isOk=new Holder<Boolean>(true);
		
		new UIOper(true){
			@Override
			protected void execute() {
				AbstractDialog abd=new AbstractDialog(2){
					@Override
					protected void createContents(BeanBinding bind) {
						TreeNode node=new TreeNode("root");
						List<TreeNode> childre= new ArrayList<TreeNode>();
						for(TableInfo t: tables){
							childre.add(new TreeNode(t));
						}
						node.setChildren(childre.toArray(new TreeNode[childre.size()]));
						tree=bind.createTree("list", node, 480, 2, true);
						GridLayoutHelper.setHeight(tree, 500);
						Button b=bind.createCheckBox(null,"Check All", PlugInProvider.this);
						b.setSelection(true);
					}
				};
				isOk.set(abd.open(data, "Choose tables"));
			}
		};
		if(isOk.get()){
			return (List<TableInfo>) data.get("list");	
		}else{
			return new ArrayList<TableInfo>();
		}
	}
	
	
	public String getSchema() {
		return parentProvider.getSchema();
	}

	public Metadata getTableMetadata(String arg0) throws SQLException {
		return parentProvider.getTableMetadata(arg0);
	}

	@Override
	public void onClick(Button button) {
		if(button.getSelection()){
			for(int i=0; i<tree.getItemCount();i++){
				tree.getItem(i).setChecked(true);
			}
		}else{
			for(int i=0; i<tree.getItemCount();i++){
				tree.getItem(i).setChecked(false);
			}
		}		
	}
}
