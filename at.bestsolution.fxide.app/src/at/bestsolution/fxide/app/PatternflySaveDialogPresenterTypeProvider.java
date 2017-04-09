package at.bestsolution.fxide.app;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.ISaveHandler.Save;
import org.eclipse.fx.core.ThreadSynchronize.BlockCondition;
import org.eclipse.fx.ui.controls.Util;
import org.eclipse.fx.ui.services.dialog.LightWeightDialogService;
import org.eclipse.fx.ui.services.dialog.LightWeightDialogService.ModalityScope;
import org.eclipse.fx.ui.workbench.renderers.fx.services.SaveDialogPresenter;
import org.eclipse.fx.ui.workbench.renderers.fx.services.SaveDialogPresenterTypeProvider;
import org.osgi.service.component.annotations.Component;

import at.bestsolution.controls.patternfly.ModalDialog.YesNoCancel;
import at.bestsolution.fxide.jdt.component.LWModalDialog.LWYesNoCancelQuestionDialog;
import javafx.scene.layout.Region;

//@Component
public class PatternflySaveDialogPresenterTypeProvider implements SaveDialogPresenterTypeProvider {

	@Override
	public <S extends SaveDialogPresenter> Class<S> getType() {
		return (Class<S>) SaveDialogPresenterImpl.class;
	}

	public static class SaveDialogPresenterImpl implements SaveDialogPresenter {
//		@Inject
//		LightWeightDialogService dialogService;

		@Override
		public List<Save> promptToSave(SaveData data) {
			if( data.dirtyParts.size() == 1 ) {
				MPart part = data.dirtyParts.iterator().next();
				BlockCondition<YesNoCancel> b = new BlockCondition<>();
				LWYesNoCancelQuestionDialog d = new LWYesNoCancelQuestionDialog("Unsaved changes", "'"+part.getLabel()+"' has been modified. Save changes?", b::release);
//				d.setMinWidth(500);
				d.setMaxWidth(Region.USE_PREF_SIZE);
				d.setMaxHeight(Region.USE_PREF_SIZE);
				part.getContext().get(LightWeightDialogService.class).openDialog(d, ModalityScope.WINDOW);
				List<Save> rv = new ArrayList<>();
				b.subscribeUnblockedCallback( r -> {
					switch (r) {
					case CANCEL:
						rv.add(Save.CANCEL);
						break;
					case YES:
						rv.add(Save.YES);
						break;
					case NO:
						rv.add(Save.NO);
						break;
					default:
						rv.add(Save.CANCEL);
						break;
					}
				});
				Util.waitUntil(b);
				return rv;
			}

			// TODO Auto-generated method stub
			return null;
		}

	}
}
