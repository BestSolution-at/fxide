package at.bestsolution.fxide.app;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class SaveHandler {
	@CanExecute
	public boolean canSave(MPart part) {
		System.err.println("CHECK");
		return part.isDirty();
	}

	@Execute
	public void save(MPart part, EPartService partService) {
		partService.savePart(part, false);
	}
}
