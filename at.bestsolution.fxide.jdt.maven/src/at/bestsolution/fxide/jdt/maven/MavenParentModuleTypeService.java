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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.maven.model.Model;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.fx.core.IOUtils;
import org.eclipse.fx.core.Status;
import org.eclipse.fx.core.Status.State;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.MavenPlugin;
import org.osgi.service.component.annotations.Component;

import at.bestsolution.fxide.jdt.services.ModuleTypeService;

@Component
public class MavenParentModuleTypeService implements ModuleTypeService  {

	@Override
	public String getLabel() {
		return "Maven Parent";
	}

	@Override
	public String getId() {
		return "module.maven.parent";
	}

	@Override
	public Status createModule(IProject project, IResource resource) {
		IProjectDescription description = project.getWorkspace().newProjectDescription(project.getName());
		description.setNatureIds( new String[] {
			JavaCore.NATURE_ID,
			"org.eclipse.m2e.core.maven2Nature"
		});

		ICommand[] commands = new ICommand[1];

		{
			ICommand cmd = description.newCommand();
			cmd.setBuilderName("org.eclipse.m2e.core.maven2Builder");
			commands[0] = cmd;
		}

		// If we get a parent path we create a nested project
		if( resource != null ) {
			try {
				if( resource.getProject().getNature("org.eclipse.m2e.core.maven2Nature") != null ) {
					description.setLocation(resource.getProject().getFullPath().append(project.getName()));
				}
			} catch (CoreException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		description.setBuildSpec(commands);

		try {
			project.create(description, null);
			project.open(null);

			project.getFolder(new Path("target")).create(true, true, null);
			project.getFolder(new Path("target")).setDerived(true,null);

			project.getFolder(new Path("src")).create(true, true, null);
			project.getFolder(new Path("src").append("site")).create(true, true, null);

			{
				IFile file = project.getFile(new Path("pom.xml"));
				try( InputStream templateStream = getClass().getResourceAsStream("template-parent-pom.xml") ) {
					String pomContent = IOUtils.readToString(templateStream, Charset.forName("UTF-8"));
					Map<String, String> map = new HashMap<>();
					map.put("groupId", project.getName());
					map.put("artifactId", project.getName());
					map.put("version", "1.0.0");

					try ( ByteArrayInputStream in = new ByteArrayInputStream(StrSubstitutor.replace(pomContent, map).getBytes());
							ByteArrayOutputStream out = new ByteArrayOutputStream();) {
						Model model = MavenPlugin.getMaven().readModel(in);
						MavenPlugin.getMaven().writeModel(model, out);
						try( ByteArrayInputStream in2 = new ByteArrayInputStream(out.toByteArray()) ) {
							file.create(in2, true, null);
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			project.getWorkspace().save(true, null);
		} catch (CoreException ex) {
			return Status.status(State.ERROR, -1, "Failed to create project", ex);
		}

		return Status.ok();
	}

}
