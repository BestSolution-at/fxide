package at.bestsolution.fxide.app;

import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.IWindowCloseHandler;
import org.osgi.service.component.annotations.Component;

@Component
public class DirtyWindowCloseHandler implements IWindowCloseHandler {

	@Override
	public boolean close(MWindow window) {
		EPartService partService = window.getContext().get(EPartService.class);
		return partService.saveAll(true);
	}
}