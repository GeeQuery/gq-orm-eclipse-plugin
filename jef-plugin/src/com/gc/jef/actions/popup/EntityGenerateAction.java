package com.gc.jef.actions.popup;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import jef.codegen.EntityGenerator;
import jef.codegen.EntityProcessorCallback;
import jef.codegen.MetaProvider.DbClientProvider;
import jef.codegen.Metadata;
import jef.codegen.ast.JavaField;
import jef.codegen.ast.JavaUnit;
import jef.database.DbClient;
import jef.database.DbClientFactory;
import jef.database.dialect.ColumnType;
import jef.database.dialect.DbmsProfile;
import jef.database.meta.Column;
import jef.ui.swt.PlugInProvider;
import jef.ui.swt.util.AbstractDialog;
import jef.ui.swt.util.BeanBinding;
import jef.ui.swt.util.SWTUtils;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.action.IAction;

import com.gc.jef.PluginHelper;

public class EntityGenerateAction extends AbstractAction {
	
	@Override
	protected void run(IJavaElement obj, IAction action) {
		super.initConsole();
		String pkg = "";
		File source;
		if(obj instanceof IPackageFragmentRoot){
			pkg="";
			source=obj.getResource().getLocation().toFile();
		}else if(obj instanceof IPackageFragment){
			pkg=obj.getElementName();
			IJavaElement root=obj.getParent();
			while(!(root instanceof IPackageFragmentRoot)){
				root=root.getParent();
			}
			source=root.getResource().getLocation().toFile();
		}else{
			throw new IllegalArgumentException(obj.getClass().getName()+" is not supported!");
		}
		
        //获取可选项的存储  
		
    
		Map<String,String> map=new HashMap<String,String>();
		map.put("dbType", PluginHelper.getString("jef.import.dbtype", "oracle"));
		map.put("pkgName", pkg);
		map.put("sid", PluginHelper.getString("jef.import.sid", "db-name"));
		map.put("host",PluginHelper.getString("jef.import.host", "db-host"));
		map.put("user", PluginHelper.getString("jef.import.user", ""));
		map.put("password", PluginHelper.getString("jef.import.password", ""));
		
		AbstractDialog dialog=new AbstractDialog(4){
			@Override
			protected void createContents(BeanBinding bind) {
				bind.createText("数据库类型：");
				bind.createCombo("dbType",  new String[]{"oracle","derby","mysql","postgresql","sqLite","db2"}, 100);
				bind.createText("数据库地址：");
				bind.createTextInput("host", 160, 1, false);
				bind.createText("服务名：");
				bind.createTextInput("sid", 160, 1, false);
				bind.createText("用户：");
				bind.createTextInput("user", 160, 1, false);
				bind.createText("密码：");
				bind.createTextInput("password", 160, 1, true);
				bind.createText("实体类包名：");
				bind.createTextInput("pkgName", 160, 1, false);
			}
		};
		if(!dialog.open(map, "JEF Entity Generator",200,200)){
			return;
		}
		PluginHelper.setString("jef.import.dbtype", map.get("dbType"));
		PluginHelper.setString("jef.import.sid", map.get("sid"));
		PluginHelper.setString("jef.import.host", map.get("host"));
		PluginHelper.setString("jef.import.user", map.get("user"));
		PluginHelper.setString("jef.import.password", map.get("password"));
		
		
		
		EntityGenerator g=new EntityGenerator();
		
		try {
			DbClient db=DbClientFactory.getDbClient(map.get("dbType"), map.get("host"), 0, map.get("sid"),map.get("user"), map.get("password"));
			g.setProvider(new PlugInProvider(new DbClientProvider(db)));
		} catch (Exception e1) {
			e1.printStackTrace();
			SWTUtils.messageBox(e1.getMessage());
			return;
		}
		g.setProfile(DbmsProfile.getProfile(map.get("dbType")));
		g.addExcludePatter(".*_\\d+$"); //防止出现分表
		g.addExcludePatter("AAA");      //排除表
		g.setMaxTables(999);
		g.setSrcFolder(source);
		g.setBasePackage(map.get("pkgName"));
		
		try {
			MyJob job = new MyJob("Database to Jef entity...",obj, g);  
            job.addJobChangeListener(new JobChangeAdapter());  
            SWTUtils.perform(job);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	class MyJob extends Job {  
		EntityGenerator g;
		IJavaElement java;
        public MyJob(String name,IJavaElement java,EntityGenerator g) {  
            super(name);  
            this.java=java;
            this.g=g;
        }  

        @Override  
        protected IStatus run(final IProgressMonitor monitor) {
        	g.setCallback(new EntityProcessorCallback() {
				public void setTotal(int arg0) {
					monitor.beginTask("Generating Entity...", arg0);
				}

				public void addField(JavaUnit arg0, JavaField arg1, Column arg2, ColumnType arg3) {
					monitor.worked(1);
				}
				public void finish(JavaUnit arg0) {
					monitor.done();
				}
				public void init(Metadata arg0, String arg1, String arg2, String arg3, JavaUnit arg4) {
				}
			});
    		try {
				g.generateSchema();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			monitor.done();
			try {
				java.getResource().refreshLocal(IResource.DEPTH_INFINITE, monitor);
			} catch (CoreException e) {
				e.printStackTrace();
			}
			return  new Status(Status.OK, "OK", "OK");  
        }  
    }  
}
