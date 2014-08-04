package jef.ui.swt.util;

import org.eclipse.core.runtime.IProgressMonitor;

public class MonitorWrapper{
	private IProgressMonitor monitor;
	private int currentProgress=0; 
	
	//private Step[] steps;
	public MonitorWrapper(IProgressMonitor monitor,String name){
		this.monitor=monitor;
		monitor.beginTask(name, 100);
	}
	public void setAbsoulteProgress(int n){
		if(n>currentProgress){
			monitor.worked(n-currentProgress);
			currentProgress=n;
		}
	}
	public void setWorked(int n){
		monitor.worked(n);
		currentProgress+=n;
	}
	public void subTask(String name){
		monitor.subTask(name);
	}
	public void done() {
		monitor.done();
	}
	public void setTaskName(String name) {
		monitor.setTaskName(name);
	}
	public void setStepProgress(int stepBegin, int stepEnd, int total, int current){
		setAbsoulteProgress(calculateProgress(stepBegin,stepEnd,total,current));
	}
	/**
	 * 根据当前步骤的位置、总数等信息计算进度百分比
	 * @param stepBegin 当前步骤开始时的百分比
	 * @param setOver   当前步骤完成时的百分比
	 * @param total		当前步骤要处理的总量
	 * @param current   当前步骤已经处理的数量
	 */
	public static int calculateProgress(int stepBegin, int setOver, int total, int current){
		int width=setOver-stepBegin;
		int progress=(int)((float)current/total*width);
		return stepBegin+progress;
	}
	
}
