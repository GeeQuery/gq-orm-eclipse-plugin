package com.gc.jef.actions.popup;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import jef.database.DbUtils;
import jef.database.dialect.AbstractDialect;
import jef.database.dialect.ColumnType;
import jef.database.meta.Column;
import jef.ui.swt.PlugInProvider;
import jef.ui.swt.util.AbstractDialog;
import jef.ui.swt.util.BeanBinding;
import jef.ui.swt.util.ButtonListener;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

import com.github.geequery.codegen.EntityGenerator;
import com.github.geequery.codegen.EntityProcessorCallback;
import com.github.geequery.codegen.MetaProvider.PDMProvider;
import com.github.geequery.codegen.Metadata;
import com.github.geequery.codegen.ast.JavaField;
import com.github.geequery.codegen.ast.JavaUnit;

public class EntityPDMGenerateAction extends AbstractAction {
	@Override
	protected void run(IJavaElement obj, IAction action) {
		super.initConsole();
		String pkg = "";
		File source;
		if (obj instanceof IPackageFragmentRoot) {
			pkg = "";
			source = obj.getResource().getLocation().toFile();
		} else if (obj instanceof IPackageFragment) {
			pkg = obj.getElementName();
			IJavaElement root = obj.getParent();
			while (!(root instanceof IPackageFragmentRoot)) {
				root = root.getParent();
			}
			source = root.getResource().getLocation().toFile();
		} else {
			throw new IllegalArgumentException(obj.getClass().getName() + " is not supported!");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("dbType", "oracle");
		map.put("filename","");
		map.put("pkgName", pkg);
		map.put("isDash2", false);
		AbstractDialog dialog = new AbstractDialog(4) {
			private Text file;
			@Override
			protected void createContents(BeanBinding bind) {
				bind.createText("数据库类型：");
				bind.createCombo("dbType", new String[] { "oracle", "derby", "mysql", "sqlServer", "sqLite", "db2" }, 100);
				bind.expandToRowEnd(false);
				file=bind.createTextInput("filename", 300, 3, false);
				bind.createButton("...",  new ButtonListener(){
					@Override
					public void onClick(Button btn) {
						String filename=SWTUtils.fileOpen("*.pdm");
						if(filename!=null){
							file.setText(filename);
						}
						
					}
					
				});
				bind.createCheckBox("isDash2", "处理形如i_username格式的字段", null);
			}
		};
		if (!dialog.open(map, "JEF Entity Generator", 200, 200)) {
			return;
		}
		File file=new File((String)map.get("filename"));
		if(!file.exists() || file.isDirectory()){
			SWTUtils.messageBox("File " + file +" not exist.");
			return;
		}
		EntityGenerator g = new EntityGenerator();
		g.setProvider(new PlugInProvider(new PDMProvider(file)));
		g.setProfile(AbstractDialect.getProfile("oracle"));
		g.setMaxTables(999);
		g.setSrcFolder(source);
		g.setBasePackage((String)map.get("pkgName"));
		try {
			MyJob job = new MyJob("OBD to Java Convert", obj, g, (Boolean) map.get("isDash2"));
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
		Boolean isDash2;

		public MyJob(String name, IJavaElement java, EntityGenerator g,Boolean isDash2) {
			super(name);
			this.java = java;
			this.g = g;
			this.isDash2=isDash2;
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
				public String columnToField(String columnName) {
					if (Boolean.TRUE.equals(isDash2) && columnName.charAt(1) == '_' && columnName.length() > 2) {
						return DbUtils.underlineToUpper(columnName.substring(2).toLowerCase(), false);
					} else {
						return DbUtils.underlineToUpper(columnName.toLowerCase(), false);
					}
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
			return new Status(Status.OK, "OK", "OK");
		}
	}
}
