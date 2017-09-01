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
package at.bestsolution.fxide.jdt.handlers;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.fx.core.Status;
import org.eclipse.fx.core.Status.State;
import org.eclipse.fx.core.di.Service;
import at.bestsolution.fxide.base.BaseConstants;
import at.bestsolution.fxide.jdt.services.ModuleTypeService;

public class CreateJavaModuleHandler {
	@Inject
	@Service
	private List<ModuleTypeService> moduleTypeServiceList;

	@Execute
	public Status createModule(@Named("projectName") String projectName,
			@Named("moduleType") String moduleType,
			@Optional @Named(BaseConstants.CTX_PACKAGE_EXPLORER_SELECTION) IResource resource) {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		return moduleTypeServiceList.stream()
					.filter( m -> m.getId().equals(moduleType))
					.findFirst()
					.map( s -> s.createModule(project,resource))
					.orElse(Status.status(State.ERROR, -1, "Could not find service for moduleType='"+moduleType+"'", null));
	}
}
