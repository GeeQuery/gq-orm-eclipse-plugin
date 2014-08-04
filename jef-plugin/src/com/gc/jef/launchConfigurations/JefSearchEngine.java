package com.gc.jef.launchConfigurations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;

public class JefSearchEngine {
	private class MethodCollector extends SearchRequestor {
		private List<IType> fResult=new ArrayList<IType>();
		public List<IType> getResult() {
			return fResult;
		}

		public void acceptSearchMatch(SearchMatch match) throws CoreException {
			fResult.add((IType)match.getElement());
		}
	}

	public JefSearchEngine() {
	}

	public IType searchJefClassLoader(IJavaSearchScope scope) throws CoreException {
		SearchPattern pattern = SearchPattern.createPattern("jef.database.JefClassLoader",IJavaSearchConstants.TYPE,IJavaSearchConstants.DECLARATIONS,SearchPattern.R_FULL_MATCH);
		SearchParticipant participants[] = { SearchEngine.getDefaultSearchParticipant() };
		MethodCollector collector = new MethodCollector();
		new SearchEngine().search(pattern, participants, scope, collector, null);
		List<IType> result = collector.getResult();
		if(result.isEmpty())return null;
		return (IType) result.get(0);
	}

}
