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
package at.bestsolution.fxide.jdt.services;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.fx.core.Status;
import org.eclipse.fx.core.Status.State;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.osgi.service.component.annotations.Component;

@Component
public class JDTModuleTypeService implements ModuleTypeService {

	@Override
	public String getLabel() {
		return "Plain";
	}

	@Override
	public String getId() {
		return "module.plain";
	}

	@Override
	public Status createModule(IProject project, IResource resource) {
		IProjectDescription description = project.getWorkspace().newProjectDescription(project.getName());
		description.setNatureIds( new String[] {
			JavaCore.NATURE_ID
		});
		ICommand cmd = description.newCommand();
		cmd.setBuilderName(JavaCore.BUILDER_ID);
		description.setBuildSpec(new ICommand[] { cmd });

		try {
			project.create(description, null);
			project.open(null);

			project.getFolder(new Path("target")).create(true, true, null);
			project.getFolder(new Path("target").append("classes")).create(true, true, null);

			project.getFolder(new Path("target")).setDerived(true,null);
			project.getFolder(new Path("target").append("classes")).setDerived(true,null);

			project.getFolder(new Path("src")).create(true, true, null);

			project.getFolder(new Path("src").append("main")).create(true, true, null);
			project.getFolder(new Path("src").append("main").append("java")).create(true, true, null);
			project.getFolder(new Path("src").append("main").append("resources")).create(true, true, null);

			project.getFolder(new Path("src").append("test")).create(true, true, null);
			project.getFolder(new Path("src").append("test").append("java")).create(true, true, null);
			project.getFolder(new Path("src").append("test").append("resources")).create(true, true, null);

//			IExecutionEnvironmentsManager executionEnvironmentsManager = JavaRuntime.getExecutionEnvironmentsManager();
//			IExecutionEnvironment[] executionEnvironments = executionEnvironmentsManager.getExecutionEnvironments();
//			System.err.println(JavaRuntime.getDefaultVMInstall().getInstallLocation());

			IJavaProject jProject = JavaCore.create(project);
			jProject.setOutputLocation(project.getFolder(new Path("target").append("classes")).getFullPath(), null);

			List<IClasspathEntry> entries = new ArrayList<>();
			entries.add(JavaCore.newSourceEntry(project.getProject().getFullPath().append("src").append("main").append("java")));
			entries.add(JavaCore.newSourceEntry(project.getProject().getFullPath().append("src").append("main").append("resources")));
			entries.add(JavaCore.newSourceEntry(project.getProject().getFullPath().append("src").append("test").append("java")));
			entries.add(JavaCore.newSourceEntry(project.getProject().getFullPath().append("src").append("test").append("resources")));
			entries.add(JavaCore.newContainerEntry(JavaRuntime.newDefaultJREContainerPath()));

			jProject.setRawClasspath(entries.toArray(new IClasspathEntry[0]), null);
			project.getWorkspace().save(true, null);
			return Status.ok();
		} catch (CoreException ex) {
			return Status.status(State.ERROR, -1, "Failed to create project", ex);
		}
	}

}
