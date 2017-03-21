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
package at.bestsolution.fxide.jdt.editor;

import org.eclipse.fx.code.editor.Input;
import org.eclipse.fx.code.editor.services.InputTypeProvider;
import org.osgi.service.component.annotations.Component;

@Component(property="service.ranking:Integer=1")
@SuppressWarnings("restriction")
public class JDTSourceFileInputTypeProvider implements InputTypeProvider {

	public JDTSourceFileInputTypeProvider() {
		System.err.println("CREATED!!!!!");
	}

	@Override
	public Class<? extends Input<?>> getType(String s) {
		return JDTSourceFileInput.class;
	}

	@Override
	public boolean test(String t) {
		System.err.println("TESTING: " +t);
		if( t.endsWith(".java") ) {
			if( t.startsWith("module-file:") ) {
				return true;
			}
		}
		return false;
	}

}