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
