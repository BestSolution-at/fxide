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

import javax.inject.Named;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.fx.ui.controls.dialog.MessageDialog.QuestionDialog;
import org.eclipse.fx.ui.controls.dialog.MessageDialog.QuestionResult;
import org.eclipse.fx.ui.controls.stage.FrameEvent;
import org.eclipse.fx.ui.services.dialog.LightWeightDialogService;
import org.eclipse.fx.ui.services.dialog.LightWeightDialogService.ModalityScope;

import at.bestsolution.fxide.jdt.JDTConstants;

@SuppressWarnings("restriction")
public class DeleteResourceHandler {
	@CanExecute
	public boolean canDelete(@Named(JDTConstants.CTX_PACKAGE_EXPLORER_SELECTION) IResource resource) {
		return resource != null;
	}

	@Execute
	public void deleteResource(LightWeightDialogService dialogService,
			@Named(JDTConstants.CTX_PACKAGE_EXPLORER_SELECTION) IResource resource) {
		QuestionDialog dialog = new QuestionDialog("Delete", "Delete file " + resource.getName() + "?");
		dialog.addEventHandler(FrameEvent.CLOSED, evt -> {
			if (dialog.getResult() == QuestionResult.YES) {
				try {
					resource.delete(true, null);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		dialogService.openDialog(dialog, ModalityScope.WINDOW);
	}

}
