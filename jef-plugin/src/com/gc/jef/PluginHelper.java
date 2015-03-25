package com.gc.jef;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.ReflectionException;

import jef.tools.Assert;
import jef.tools.IOUtils;
import jef.tools.StringUtils;
import jef.tools.XMLUtils;
import jef.tools.reflect.BeanUtils;
import jef.tools.reflect.ClassLoaderUtil;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.internal.core.LaunchConfigurationInfo;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.preference.IPreferenceStore;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

@SuppressWarnings({ "unchecked", "rawtypes", "restriction" })
public final class PluginHelper implements IJavaLaunchConfigurationConstants {
	public static final String JEF_APPLICATION = "com.gc.jef.launchConfigurations.JefConfiguration";
	public static final String JEF_WEB_APPLICATION = "com.gc.jef.launchConfigurations.JefWebConfiguration";
	public static final String JEF_EJB_APPLICATION = "com.gc.jef.launchConfigurations.JefEJBConfiguration";

	public static void addClassPath(IJavaProject javaProject) {
		initJefHome();
		try {
			IClasspathEntry[] jef = getJefEntries();
			List<IClasspathEntry> list = new ArrayList<IClasspathEntry>();
			for (IClasspathEntry e : javaProject.getRawClasspath()) {
				list.add(e);
			}
			boolean flag = false;
			for (IClasspathEntry e : jef) {
				if (!list.contains(e)) {
					list.add(e);
					flag = true;
				}
			}
			if (flag) {
				javaProject.setRawClasspath(list.toArray(new IClasspathEntry[list.size()]), null);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	private static void initJefHome(){
		File file = IOUtils.urlToFile(ClassLoaderUtil.getCodeSource(IOUtils.class));
		final String s = file.getParent();
		IPath path = new Path(s);
		try {
			IPath pp = JavaCore.getClasspathVariable("JEF_HOME");
			if (pp == null || !pp.equals(path)) {
				JavaCore.setClasspathVariable("JEF_HOME", path, null);
			}
		}catch(CoreException e){
			e.printStackTrace();
		}
	}

	private static IClasspathEntry[] getJefEntries() {
		return new IClasspathEntry[] { 
				JavaCore.newVariableEntry(new Path("JEF_HOME/common-core-2.0.0-SNAPSHOT.jar"), null, null), 
				JavaCore.newVariableEntry(new Path("JEF_HOME/common-orm-2.0.0-SNAPSHOT.jar"), null, null),
				JavaCore.newVariableEntry(new Path("JEF_HOME/commons-lang-2.5.jar"), null, null),
				JavaCore.newVariableEntry(new Path("JEF_HOME/geronimo-jpa_2.0_spec-1.1.jar"), null, null),
				JavaCore.newVariableEntry(new Path("JEF_HOME/javaparser-1.0.8.jar"), null, null),
				JavaCore.newVariableEntry(new Path("JEF_HOME/slf4j-api-1.7.5.jar"), null, null)
		 };
	}

	private static IClasspathEntry[] getWebEntries() {
		return new IClasspathEntry[] {
				JavaCore.newVariableEntry(new Path("JEF_HOME/commons-lang-2.5.jar"), null, null),
				JavaCore.newVariableEntry(new Path("JEF_HOME/slf4j-api-1.7.5.jar"), null, null),
				JavaCore.newVariableEntry(new Path("JEF_HOME/slf4j-simple-1.7.5.jar"), null, null),
				JavaCore.newVariableEntry(new Path("JEF_HOME/servlet-api-2.5.jar"), null, null),
				JavaCore.newVariableEntry(new Path("JEF_HOME/jsp-api-2.1.jar"), null, null),
				JavaCore.newVariableEntry(new Path("JEF_HOME/ecj-4.2.2.jar"), null, null),
				///////////////////////////////////////////////////////////////////
				JavaCore.newVariableEntry(new Path("JEF_HOME/common-core-1.9.5.RELEASE.jar"), null, null),
				JavaCore.newVariableEntry(new Path("JEF_HOME/common-net-1.9.5.RELEASE.jar"), null, null),
				JavaCore.newVariableEntry(new Path("JEF_HOME/common-orm-1.9.5.RELEASE.jar"), null, null),
				JavaCore.newVariableEntry(new Path("JEF_HOME/jef-jetty-731.jar"), null, null),
				JavaCore.newVariableEntry(new Path("JEF_HOME/xercesImpl-2.9.1.jar"), null, null),
				JavaCore.newVariableEntry(new Path("JEF_HOME/jef-jasper.jar"), null, null)
				};
	}

	public static void fix(ILaunchConfiguration configuration) {
		try {
			LaunchConfigurationInfo info = (LaunchConfigurationInfo) BeanUtils.invokeMethod(configuration, "getInfo");
			Map map = (Map) BeanUtils.getFieldValue(info, "fAttributes");
			String className = (String) map.get(ATTR_MAIN_TYPE_NAME);
			if (!className.equals("jef.database.JefClassLoader")) {
				map.put(ATTR_MAIN_TYPE_NAME, "jef.database.JefClassLoader");
				String args = (String) map.get(ATTR_PROGRAM_ARGUMENTS);
				if (args == null) {
					map.put(ATTR_PROGRAM_ARGUMENTS, className);
				} else {
					map.put(ATTR_PROGRAM_ARGUMENTS, StringUtils.join(new String[] { className, args }, " "));
				}
			}
		} catch (ReflectionException e) {
			e.printStackTrace();
		}
	}

	public static void fixAsWeb(ILaunchConfiguration config, IJavaProject jp) {
		try {
			LaunchConfigurationInfo info = (LaunchConfigurationInfo) BeanUtils.invokeMethod(config, "getInfo");
			Map map = (Map) BeanUtils.getFieldValue(info, "fAttributes");
			String projectName = (String) map.get(ATTR_PROJECT_NAME);
			List<String> l = (List<String>) map.get(ATTR_CLASSPATH);
			Object obj=map.get(ATTR_CLASSPATH);
			if(obj!=null){
				Object[] exists=null;
				if(obj.getClass().isArray()){
					exists=(Object[])obj;
				}else if(obj instanceof Collection){
					exists=((Collection)obj).toArray();
				}
				IClasspathEntry[] toAdd=getWebEntries();
				boolean flag1=false,flag2=false;
				for(Object o:exists){
					String s=o.toString();
					if(s.indexOf("JEF_HOME/jef-jasper.jar")>-1){
						flag1=true;
					}else if(s.indexOf("JEF_HOME/jef-jetty-731.jar")>-1){
						flag2=true;
					}
				}
				if(!flag1 || !flag2){
					map.put(ATTR_CLASSPATH,appendLaunchClasspath(l, projectName, config,toAdd,jp,0));//指定添加类路径
				}
			}else{
				map.put(ATTR_CLASSPATH,appendLaunchClasspath(l, projectName, config,getWebEntries(),jp,0));//指定添加类路径
			}
			map.put(ATTR_DEFAULT_CLASSPATH, false);//指定不采用缺省类路径
//			IFile f = jp.getProject().getFile("pom.xml");
			File root = jp.getProject().getLocation().toFile();
			String arg = (String) map.get(ATTR_PROGRAM_ARGUMENTS);
			map.put(ATTR_MAIN_TYPE_NAME, "jef.database.JefClassLoader");
			if (arg == null) {
				map.put(ATTR_PROGRAM_ARGUMENTS, "jef.http.server.JettyConsole -j \"" + root.getAbsolutePath() + "\"" + " -n " + jp.getElementName());
			} else if (arg.startsWith("jef.http.server.JettyConsole ")) {
			} else {
				map.put(ATTR_PROGRAM_ARGUMENTS, "jef.http.server.JettyConsole -j \"" + root.getAbsolutePath() + "\"" + arg);
			}
		} catch (ReflectionException e) {
			e.printStackTrace();
		}
	}

	private static List<String> appendLaunchClasspath(List<String> l, String projectName, ILaunchConfiguration config,IClasspathEntry[] adding,IJavaProject jp,int high) {
		initJefHome();
		Set<IClasspathEntry> set=new HashSet<IClasspathEntry>();
		try {
			if (l == null) {
				l = new ArrayList<String>();
				l.add(JavaRuntime.computeJREEntry(jp).getMemento());
				l.add(String.format(
						"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n<runtimeClasspathEntry id=\"org.eclipse.jdt.launching.classpathentry.defaultClasspath\">\r\n<memento exportedEntriesOnly=\"false\" project=\"%s\"/></runtimeClasspathEntry>\r\n", projectName));
			} else {
				for (String s : l) {
					Document doc = XMLUtils.loadDocumentByString(s);
					if (doc.getDocumentElement().hasAttribute("type")) {
						IRuntimeClasspathEntry rcp=new org.eclipse.jdt.internal.launching.RuntimeClasspathEntry(doc.getDocumentElement());
						set.add(rcp.getClasspathEntry());
					}
				}
			}
			int i=0;
			for(IClasspathEntry cp:adding){
				if(!set.contains(cp)){
					IRuntimeClasspathEntry rcp=new org.eclipse.jdt.internal.launching.RuntimeClasspathEntry(cp);
					if(i<high){
						rcp.setClasspathProperty(2);
					}else{
						rcp.setClasspathProperty(3);
					}
					l.add(rcp.getMemento());
					set.add(cp);
					i++;
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return l;
	}

	public static void fixAsEJB(ILaunchConfiguration config) {
		try {
			LaunchConfigurationInfo info = (LaunchConfigurationInfo) BeanUtils.invokeMethod(config, "getInfo");
			Map map = (Map) BeanUtils.getFieldValue(info, "fAttributes");

			String projectName = (String) map.get(ATTR_PROJECT_NAME);
			IJavaModel jModel = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
			IJavaProject jp = jModel.getJavaProject(projectName);
			Assert.notNull(jp);

			File root = jp.getProject().getLocation().toFile();
			map.put("org.eclipse.jdt.launching.MAIN_TYPE", "jef.database.JefClassLoader");

			String arg = (String) map.get(ATTR_PROGRAM_ARGUMENTS);
			if (arg == null) {
				File openEjbFolder = findOpenEjbFolder();
				String projectPath = root.getAbsolutePath();
				String openEjbPath = openEjbFolder.getAbsolutePath();
				map.put(ATTR_PROGRAM_ARGUMENTS, "jef.ejb.server.OpenejbServer " + projectPath + " " + openEjbPath);
			}

		} catch (ReflectionException e) {
			e.printStackTrace();
		}
	}

	public static String[] getEjbStartParams(ILaunchConfiguration config) throws Exception {
		String[] result = new String[2];
		LaunchConfigurationInfo info = (LaunchConfigurationInfo) BeanUtils.invokeMethod(config, "getInfo");
		Map map = (Map) BeanUtils.getFieldValue(info, "fAttributes");

		String projectName = (String) map.get(ATTR_PROJECT_NAME);
		IJavaModel jModel = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
		IJavaProject jp = jModel.getJavaProject(projectName);

		File root = jp.getProject().getLocation().toFile();
		File openEjbFolder = findOpenEjbFolder();
		String projectPath = root.getAbsolutePath();
		String openEjbPath = openEjbFolder.getAbsolutePath();

		result[0] = projectPath;
		result[1] = openEjbPath;

		return result;
	}

	private static File findOpenEjbFolder() {
		File file = IOUtils.urlToFile(ClassLoaderUtil.getCodeSource(PluginHelper.class));
		File openEjbFolder = new File(file, "apache-openejb");
		return openEjbFolder;
	}

	private static IPreferenceStore store = Activator.getDefault().getPreferenceStore();

	public static String getString(String name, String defaultValue) {
		String s = store.getString(name);
		if (s.length() == 0)
			return defaultValue;
		return s;
	}

	public static int getInt(String name, int defaultValue) {
		int s = store.getInt(name);
		if (s == 0)
			return defaultValue;
		return s;
	}

	public static void seInt(String name, int value) {
		store.setValue(name, value);
	}

	public static void setString(String name, String value) {
		store.setValue(name, value);
	}
}
