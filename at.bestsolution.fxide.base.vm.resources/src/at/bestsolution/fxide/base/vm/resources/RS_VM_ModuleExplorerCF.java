package at.bestsolution.fxide.base.vm.resources;

import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.osgi.service.component.annotations.Component;

@Component(property="service.context.key=at.bestsolution.fxide.base.vm.VM_ModuleExplorer")
public class RS_VM_ModuleExplorerCF extends ContextFunction implements IContextFunction {
	@Override
	public Object compute(IEclipseContext context) {
		return ContextInjectionFactory.make(RS_VM_ModuleExplorer.class, context);
	}
}
