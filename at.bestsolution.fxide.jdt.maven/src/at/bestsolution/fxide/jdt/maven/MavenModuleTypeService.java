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
package at.bestsolution.fxide.jdt.maven;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.text.StrSubstitutor;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.fx.core.IOUtils;
import org.eclipse.fx.core.Status;
import org.eclipse.fx.core.Status.State;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.osgi.service.component.annotations.Component;

import at.bestsolution.fxide.jdt.services.ModuleTypeService;

@Component
public class MavenModuleTypeService implements ModuleTypeService {

	@Override
	public String getLabel() {
		return "Maven";
	}

	@Override
	public String getId() {
		return "module.maven";
	}

	@Override
	public Status createModule(IProject project, IResource resource) {
		IProjectDescription description = project.getWorkspace().newProjectDescription(project.getName());
		description.setNatureIds( new String[] {
			JavaCore.NATURE_ID,
			"org.eclipse.m2e.core.maven2Nature"
		});

		ICommand[] commands = new ICommand[2];

		{
			ICommand cmd = description.newCommand();
			cmd.setBuilderName(JavaCore.BUILDER_ID);
			commands[0] = cmd;
		}

		{
			ICommand cmd = description.newCommand();
			cmd.setBuilderName("org.eclipse.m2e.core.maven2Builder");
			commands[1] = cmd;
		}

		if( resource != null ) {
			// If we get a parent path we create a nested project
			try {
				if( resource.getProject().getNature("org.eclipse.m2e.core.maven2Nature") != null ) {
					IFolder folder = resource.getProject().getFolder(project.getName());
					if( folder.exists()) {
						return Status.status(State.ERROR, -1, "Folder already exists",null);
					}
					folder.create(true, true, null);

					description.setLocation(folder.getLocation());
				}
			} catch (CoreException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return Status.status(State.ERROR, -1, "Could not create parent relation", e1);
			}
		}

		description.setBuildSpec(commands);

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

			{
				IFile file = project.getFile(new Path("pom.xml"));
				try( InputStream templateStream = getClass().getResourceAsStream("template-pom.xml") ) {
					String pomContent = IOUtils.readToString(templateStream, Charset.forName("UTF-8"));
					Map<String, String> map = new HashMap<>();
					map.put("groupId", project.getName());
					map.put("artifactId", project.getName());
					map.put("version", "1.0.0");

					file.create(new ByteArrayInputStream(StrSubstitutor.replace(pomContent, map).getBytes()), true, null);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			IJavaProject jProject = JavaCore.create(project);
			jProject.setOutputLocation(project.getFolder(new Path("target").append("classes")).getFullPath(), null);

			List<IClasspathEntry> entries = new ArrayList<>();

			{
				IClasspathAttribute[] attributes = new IClasspathAttribute[2];
				attributes[0] = JavaCore.newClasspathAttribute("optional", "true");
				attributes[1] = JavaCore.newClasspathAttribute("maven.pomderived", "true");
				IPath path = new Path("src").append("main").append("java");
				IPath output = new Path("target").append("classes");
				IClasspathEntry sourceEntry = JavaCore.newSourceEntry(project.getProject().getFullPath().append(path), null, null, project.getProject().getFullPath().append(output), attributes);
				entries.add(sourceEntry);
			}

			{
				IClasspathAttribute[] attributes = new IClasspathAttribute[1];
				attributes[0] = JavaCore.newClasspathAttribute("maven.pomderived", "true");
				IPath path = new Path("src").append("main").append("resources");
				IPath output = new Path("target").append("classes");
				IPath[] exclusions = new IPath[] { new Path("**") };
				entries.add(JavaCore.newSourceEntry(project.getProject().getFullPath().append(path),null,exclusions,project.getProject().getFullPath().append(output),attributes));
			}

			{
				IClasspathAttribute[] attributes = new IClasspathAttribute[2];
				attributes[0] = JavaCore.newClasspathAttribute("optional", "true");
				attributes[1] = JavaCore.newClasspathAttribute("maven.pomderived", "true");
				IPath path = new Path("src").append("test").append("java");
				IPath output = new Path("target").append("test-classes");
				IClasspathEntry sourceEntry = JavaCore.newSourceEntry(project.getProject().getFullPath().append(path), null, null, project.getProject().getFullPath().append(output), attributes);
				entries.add(sourceEntry);
			}

			{
				IClasspathAttribute[] attributes = new IClasspathAttribute[1];
				attributes[0] = JavaCore.newClasspathAttribute("maven.pomderived", "true");
				IPath path = new Path("src").append("test").append("resources");
				IPath output = new Path("target").append("test-classes");
				IPath[] exclusions = new IPath[] { new Path("**") };
				entries.add(JavaCore.newSourceEntry(project.getProject().getFullPath().append(path),null,exclusions,project.getProject().getFullPath().append(output),attributes));
			}

			{
				IClasspathAttribute[] attributes = new IClasspathAttribute[1];
				attributes[0] = JavaCore.newClasspathAttribute("maven.pomderived", "true");

				IPath path = JavaRuntime.newDefaultJREContainerPath();
				entries.add(JavaCore.newContainerEntry(path,null,attributes,false));
			}

			{
				IClasspathAttribute[] attributes = new IClasspathAttribute[1];
				attributes[0] = JavaCore.newClasspathAttribute("maven.pomderived", "true");

				IPath path = new Path("org.eclipse.m2e.MAVEN2_CLASSPATH_CONTAINER");
				entries.add(JavaCore.newContainerEntry(path,null,attributes,false));
			}

			jProject.setRawClasspath(entries.toArray(new IClasspathEntry[0]), null);
			project.getWorkspace().save(true, null);
			return Status.ok();
		} catch (CoreException ex) {
			//TODO
			ex.printStackTrace();
			return Status.status(State.ERROR, -1, "Failed to create project", ex);
		}
	}
}
