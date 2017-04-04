package at.bestsolution.fxide.jdt.maven;

import org.eclipse.core.resources.IProject;
import org.eclipse.fx.core.Status;
import org.osgi.service.component.annotations.Component;

import at.bestsolution.fxide.jdt.services.ModuleTypeService;

@Component
public class MavenModuleTypeService implements ModuleTypeService {

	@Override
	public String getLabel() {
		return "Maven";
	}

	@Override
	public Status createModule(IProject project) {
		// TODO Auto-generated method stub
		return null;
	}

}
