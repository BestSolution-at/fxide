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
package at.bestsolution.fxide.base.vm.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.osgi.service.component.annotations.Component;

@Component(service=ResourceHelper.class)
public class ResourceHelper {
	private Set<IProject> rootProjects;

	public ResourceHelper() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(this::handleResourceChanged);
		IWorkspaceRoot root = workspace.getRoot();
		List<IProject> rootPaths = new ArrayList<>(Arrays.asList(root.getProjects()));

		List<IProject> subprojects = new ArrayList<>();

		for (IProject p : rootPaths) {
			String check = p.getLocationURI().toASCIIString();
			for (IProject cp : rootPaths) {
				if (cp == p) {
					continue;
				}

				if (cp.getLocationURI().toASCIIString().startsWith(check)) {
					subprojects.add(cp);
				}
			}
		}

		rootPaths.removeAll(subprojects);
		this.rootProjects = new HashSet<>(rootPaths);
	}

	private void handleResourceChanged(IResourceChangeEvent event) {
		try {
			IResourceDelta delta = event.getDelta();
			if( delta != null ) {
				delta.accept(this::visitDelta);
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean visitDelta(IResourceDelta delta) throws CoreException {
		if( delta.getKind() == IResourceDelta.ADDED ) {
			if( delta.getResource() instanceof IProject ) {
				if( isRootProject((IProject) delta.getResource()) ) {
					rootProjects.add((IProject) delta.getResource());
				}
			}
		} else if( delta.getKind() == IResourceDelta.REMOVED ) {
			if( delta.getResource() instanceof IProject ) {
				rootProjects.remove(delta.getResource());
			}
		}
		return false;
	}

	public boolean isRootProject(IProject project) {
		return rootProjects.contains(project);
	}
}
