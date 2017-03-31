package at.bestsolution.fxide.jdt.services;

import org.eclipse.core.resources.IProject;
import org.eclipse.fx.core.Status;

public interface ModuleTypeService {
	public String getLabel();
	public Status createModule(IProject project);
}
