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
package at.bestsolution.fxide.app;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.fx.code.editor.Constants;
import org.eclipse.fx.core.ThreadSynchronize;
import org.osgi.service.prefs.BackingStoreException;

@SuppressWarnings("restriction")
public class WorkspaceTrackerAddon {
	private final ThreadSynchronize threadSync;
	private final IWorkspace workspace;
	private final MApplication application;
	private final EModelService modelService;

	@Inject
	public WorkspaceTrackerAddon(ThreadSynchronize threadSync, IWorkspace workspace, MApplication application, EModelService modelService) {
		this.threadSync = threadSync;
		this.workspace = workspace;
		this.application = application;
		this.modelService = modelService;

		IEclipsePreferences node = InstanceScope.INSTANCE.getNode(ResourcesPlugin.PI_RESOURCES);
		if( node.get(ResourcesPlugin.PREF_ENCODING, "").equals("UTF-8") ) {
			node.put(ResourcesPlugin.PREF_ENCODING, "UTF-8");
			try {
				node.flush();
			} catch (BackingStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		workspace.addResourceChangeListener(this::handleResourceChanged);
	}

	private void handleResourceChanged(IResourceChangeEvent event) {
		Runnable code = () -> {
			try {
				if( event.getDelta() != null ) {
					event.getDelta().accept(this::visitDelta);
				}
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};

		if( threadSync.isCurrent() ) {
			code.run();
		} else {
			threadSync.asyncExec( code );
		}
	}

	public boolean visitDelta(IResourceDelta delta) throws CoreException {
		if( delta.getKind() == IResourceDelta.REMOVED && delta.getResource() instanceof IFile ) {
			IFile f = (IFile) delta.getResource();

			String path = "module-file:" + f.getProject().getName() + "/" + f.getProjectRelativePath();
			modelService.findElements(application, null, MPart.class, null).stream()
				.filter( m -> path.equals(m.getPersistedState().get(Constants.DOCUMENT_URL)))
				.forEach( m -> m.setToBeRendered(false));
		}

		return true;
	}

	@PreDestroy
	void close() {
		try {
			workspace.save(true, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
