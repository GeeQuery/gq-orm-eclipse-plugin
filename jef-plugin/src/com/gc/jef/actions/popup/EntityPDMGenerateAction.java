package com.gc.jef.actions.popup;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import jef.codegen.EntityGenerator;
import jef.codegen.EntityProcessorCallback;
import jef.codegen.MetaProvider.PDMProvider;
import jef.codegen.Metadata;
import jef.codegen.ast.JavaField;
import jef.codegen.ast.JavaUnit;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

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
		Map<String, String> map = new HashMap<String, String>();
		map.put("dbType", "oracle");
		map.put("filename","");
		map.put("pkgName", pkg);
	
		AbstractDialog dialog = new AbstractDialog(4) {
			private Text file;
			@Override
			protected void createContents(BeanBinding bind) {
				bind.createText("数据库类型：");
				bind.createCombo("dbType", new String[] { "oracle", "derby", "mysql", "sqlServer", "sqLite", "db2" }, 100);
				bind.expandToRowEnd(false);
				file=bind.createTextInput("filename", 300, 3, false);
				bind.createButton("...", "browseFile", this);
			}
			
			@SuppressWarnings("unused")
			protected void browseFile(Button button) {
				String filename=SWTUtils.fileOpen("*.pdm");
				if(filename!=null){
					file.setText(filename);
				}
			}
		};
		if (!dialog.open(map, "JEF Entity Generator", 200, 200)) {
			return;
		}
		File file=new File(map.get("filename"));
		if(!file.exists() || file.isDirectory()){
			SWTUtils.messageBox("File " + file +" not exist.");
			return;
		}
		EntityGenerator g = new EntityGenerator();
		g.setProvider(new PlugInProvider(new PDMProvider(file)));
		g.setProfile(DbmsProfile.getProfile("oracle"));
		g.setMaxTables(999);
		g.setSrcFolder(source);
		g.setBasePackage(map.get("pkgName"));
		try {
			MyJob job = new MyJob("OBD to Java Convert", obj, g);
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

		public MyJob(String name, IJavaElement java, EntityGenerator g) {
			super(name);
			this.java = java;
			this.g = g;
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
			return new Status(Status.OK, "OK", "OK");
		}
	}
}
