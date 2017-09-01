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
package at.bestsolution.fxide.base.vm.resources.e4;

import java.util.List;
import javax.inject.Named;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.fx.code.editor.services.EditorOpener;
import org.eclipse.fx.core.adapter.Adapt;

@SuppressWarnings("restriction")
public class OpenFilesHandler {
	@Execute
	public void openFiles(@Named("fileURIs") @Adapt List<String> fileURIs, EditorOpener editorOpener) {
		for( String f : fileURIs ) {
			editorOpener.openEditor(f);
		}
	}
}
