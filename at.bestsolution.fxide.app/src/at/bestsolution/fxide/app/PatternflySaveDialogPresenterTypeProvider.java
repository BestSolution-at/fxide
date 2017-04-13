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
package at.bestsolution.fxide.app;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.ISaveHandler.Save;
import org.eclipse.fx.ui.services.dialog.LightWeightDialogService;
import org.eclipse.fx.ui.services.dialog.LightWeightDialogService.ModalityScope;
import org.eclipse.fx.ui.workbench.renderers.fx.services.SaveDialogPresenter;
import org.eclipse.fx.ui.workbench.renderers.fx.services.SaveDialogPresenterTypeProvider;
import org.osgi.service.component.annotations.Component;

import at.bestsolution.controls.patternfly.ModalDialog;
import javafx.scene.layout.Region;

@Component
public class PatternflySaveDialogPresenterTypeProvider implements SaveDialogPresenterTypeProvider {

	@Override
	public <S extends SaveDialogPresenter> Class<S> getType() {
		return (Class<S>) SaveDialogPresenterImpl.class;
	}

	public static class SaveDialogPresenterImpl implements SaveDialogPresenter {
		@Inject
		LightWeightDialogService dialogService;

		@Override
		public CompletableFuture<List<Save>> promptToSave(SaveData data) {
			if( data.dirtyParts.size() == 1 ) {
				MPart part = data.dirtyParts.iterator().next();
				CompletableFuture<List<Save>> rv = new CompletableFuture<>();
				ModalDialog.YesNoCancelQuestionDialog d = new ModalDialog.YesNoCancelQuestionDialog("Unsaved changes", "'"+part.getLabel()+"' has been modified. Save changes?", r -> {
					switch (r) {
					case CANCEL:
						rv.complete(Collections.singletonList(Save.CANCEL));
						break;
					case YES:
						rv.complete(Collections.singletonList(Save.YES));
						break;
					case NO:
						rv.complete(Collections.singletonList(Save.NO));
						break;
					default:
						rv.complete(Collections.singletonList(Save.CANCEL));
						break;
					}
				});
				d.setMaxWidth(Region.USE_PREF_SIZE);
				d.setMaxHeight(Region.USE_PREF_SIZE);
				dialogService.openDialog(d, ModalityScope.WINDOW);

				return rv;
			}

			// TODO Auto-generated method stub
			return null;
		}
	}
}
