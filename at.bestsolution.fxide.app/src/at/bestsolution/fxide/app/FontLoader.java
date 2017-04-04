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
		Font.loadFont(getClass().getResource("fonts/Hack-Bold.ttf").toExternalForm(), 12);
		Font.loadFont(getClass().getResource("fonts/Hack-BoldItalic.ttf").toExternalForm(), 12);
		Font.loadFont(getClass().getResource("fonts/Hack-Italic.ttf").toExternalForm(), 12);
		Font.loadFont(getClass().getResource("fonts/Hack-Regular.ttf").toExternalForm(), 12);

		Font.loadFont(getClass().getResource("fonts/SourceCodePro-Bold.ttf").toExternalForm(), 12);
		Font.loadFont(getClass().getResource("fonts/SourceCodePro-BoldIt.ttf").toExternalForm(), 12);
		Font.loadFont(getClass().getResource("fonts/SourceCodePro-It.ttf").toExternalForm(), 12);
		Font.loadFont(getClass().getResource("fonts/SourceCodePro-Regular.ttf").toExternalForm(), 12);

		Font.loadFont(getClass().getResource("fonts/SourceSansPro-Bold.ttf").toExternalForm(), 12);
		Font.loadFont(getClass().getResource("fonts/SourceSansPro-BoldIt.ttf").toExternalForm(), 12);
		Font.loadFont(getClass().getResource("fonts/SourceSansPro-It.ttf").toExternalForm(), 12);
		Font.loadFont(getClass().getResource("fonts/SourceSansPro-Regular.ttf").toExternalForm(), 12);

		Font.loadFont(getClass().getResource("fonts/OpenSans-Bold.ttf").toExternalForm(), 12);
		Font.loadFont(getClass().getResource("fonts/OpenSans-BoldItalic.ttf").toExternalForm(), 12);
		Font.loadFont(getClass().getResource("fonts/OpenSans-Italic.ttf").toExternalForm(), 12);
		Font.loadFont(getClass().getResource("fonts/OpenSans-Regular.ttf").toExternalForm(), 12);
	}
}
