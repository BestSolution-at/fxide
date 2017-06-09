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

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.fx.ui.controls.dialog.TitleAreaDialog;
import org.eclipse.fx.ui.services.dialog.LightWeightDialogService;

import at.bestsolution.fxide.base.BaseConstants;


public class NewFileHandler {
	@CanExecute
	public boolean canCreate(@Named(BaseConstants.CTX_PACKAGE_EXPLORER_SELECTION) IResource resource) {
		return resource instanceof IFolder;
	}

	@Execute
	public void createNewFile(LightWeightDialogService dialogService, @Named(BaseConstants.CTX_PACKAGE_EXPLORER_SELECTION) IFolder resource) {

	}

//	static class NewFileDialog extends TitleAreaDialog {
//
//		public NewFileDialog() {
//			super(frameTitle, title, message);
//			// TODO Auto-generated constructor stub
//		}
//
//	}
}
