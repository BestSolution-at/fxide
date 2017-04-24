/**
 * FX-IDE - JavaFX and Eclipse based IDE
 *
 * Copyright (C) 2017 - BestSoltion.at
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */
package at.bestsolution.fxide.jdt.editor;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.fx.code.editor.Constants;
import org.eclipse.fx.code.editor.SourceFileChange;
import org.eclipse.fx.code.editor.SourceFileInput;
import org.eclipse.fx.core.event.EventBus;
import org.eclipse.fx.core.function.ExExecutor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IProblemRequestor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.IProblem;

@SuppressWarnings("restriction")
public class JDTSourceFileInput implements SourceFileInput, IProblemRequestor {
	private final String url;
	private IFile file;
	private final EventBus eventBus;

	private JDTWorkingCopyOwner owner;
	private ICompilationUnit compilationUnit;

	private Map<String, Object> map;

	@Inject
	public JDTSourceFileInput(IWorkspace workspace, @Named(Constants.DOCUMENT_URL) String url, @Optional EventBus eventBus) {
		this.url = url;
		this.eventBus = eventBus;
		String path = url.substring("module-file:".length());
		String project = path.substring(0,path.indexOf('/'));

		IProject p = workspace.getRoot().getProject(project);
		file = p.getFile(new Path(path.substring(project.length()+1)));

		this.owner = new JDTWorkingCopyOwner(this);
		try {
			this.compilationUnit = JavaCore.createCompilationUnitFrom(file).getWorkingCopy(owner, null);
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public JDTWorkingCopyOwner getOwner() {
		return owner;
	}

	@PostConstruct
	protected void init() {
		if( eventBus != null ) {
			eventBus.publish(Constants.TOPIC_SOURCE_FILE_INPUT_CREATED, this, true);
		}
	}

	@PreDestroy
	@Override
	public final void dispose() {
		map = null;

		if( compilationUnit != null && compilationUnit.getResource().exists() ) {
			ExExecutor.executeRunnable(compilationUnit::restore);
			ExExecutor.executeRunnable(compilationUnit::discardWorkingCopy);
		}

		if( eventBus != null ) {
			eventBus.publish(Constants.TOPIC_SOURCE_FILE_INPUT_DISPOSED, this, true);
		}
	}

	public ICompilationUnit getCompilationUnit() {
		return compilationUnit;
	}

	@Override
	public void updateData(int offset, int length, String replacement) {
		ExExecutor.executeRunnable(() -> compilationUnit.getBuffer().replace(offset, length, replacement),"Updateing buffer failed");

		if( eventBus != null ) {
			SourceFileChange sourceChange = new SourceFileChange(this, offset, length, replacement);
			eventBus.publish(Constants.TOPIC_SOURCE_FILE_INPUT_MODIFIED, sourceChange, true);
		}
	}

	@Override
	public String getData() {
		return ExExecutor.executeSupplier( () -> compilationUnit.getBuffer().getContents(),"").orElse(null);
	}

	@Override
	public void setData(String data) {
		ExExecutor.executeConsumer(data, d -> {compilationUnit.getBuffer().setContents(data);}, "Unable to set contents");
	}

	@Override
	public void persist() {
		try {
			compilationUnit.commitWorkingCopy(true, null);
			if( eventBus != null ) {
				eventBus.publish(Constants.TOPIC_SOURCE_FILE_INPUT_SAVED, this, true);
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Map<String, Object> getTransientData() {
		if( map == null ) {
			map = new HashMap<>();
		}
		return map;
	}

	@Override
	public String getURI() {
		return url;
	}

	@Override
	public void reload() {
		// TODO Auto-generated method stub
	}


	@Override
	public void acceptProblem(IProblem problem) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beginReporting() {
		// TODO Auto-generated method stub

	}

	@Override
	public void endReporting() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isActive() {
		return true;
	}
}
