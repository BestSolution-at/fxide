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

import javax.annotation.PostConstruct;

import javafx.scene.text.Font;

public class FontLoader {
	@PostConstruct
	void init() {
		System.err.println(Font.loadFont(getClass().getResourceAsStream("fonts/Hack-Bold.ttf"), 12).getFamily());
		Font.loadFont(getClass().getResourceAsStream("fonts/Hack-BoldItalic.ttf"), 12);
		Font.loadFont(getClass().getResourceAsStream("fonts/Hack-Italic.ttf"), 12);
		Font.loadFont(getClass().getResourceAsStream("fonts/Hack-Regular.ttf"), 12);

		System.err.println(Font.loadFont(getClass().getResourceAsStream("fonts/SourceCodePro-Bold.ttf"), 12).getFamily());
		Font.loadFont(getClass().getResourceAsStream("fonts/SourceCodePro-BoldIt.ttf"), 12);
		Font.loadFont(getClass().getResourceAsStream("fonts/SourceCodePro-It.ttf"), 12);
		Font.loadFont(getClass().getResourceAsStream("fonts/SourceCodePro-Regular.ttf"), 12);

		Font.loadFont(getClass().getResourceAsStream("fonts/SourceSansPro-Bold.ttf"), 12);
		Font.loadFont(getClass().getResourceAsStream("fonts/SourceSansPro-BoldIt.ttf"), 12);
		Font.loadFont(getClass().getResourceAsStream("fonts/SourceSansPro-It.ttf"), 12);
		Font.loadFont(getClass().getResourceAsStream("fonts/SourceSansPro-Regular.ttf"), 12).getFamily();
	}
}
