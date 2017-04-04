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
package at.bestsolution.controls.patternfly;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;

public class PatternFly {
	public static final String COLOR_DEFINITIONS = PatternFly.class.getResource("css/colors.css").toExternalForm();
	public static final String CONTROL_CSS = PatternFly.class.getResource("css/controls.css").toExternalForm();

	static {
		init();
	}

	public static <N extends Button> N primaryButton(N node) {
		node.getStyleClass().add("btn-primary");
		return node;
	}

	public static <N extends Button> N defaultButton(N node) {
		node.getStyleClass().add("btn-default");
		return node;
	}

	public static <N extends Label> N defaultLabel(N node) {
		node.getStyleClass().add("lb-default");
		return node;
	}

	public static <N extends TextField> N defaultTextField(N node) {
		node.getStyleClass().add("tf-default");
		return node;
	}

	public static <N extends ChoiceBox<?>> N defaultChoiceBox(N node) {
		node.getStyleClass().add("cb-default");
		return node;
	}

	public static void init() {
		Font.loadFont(PatternFly.class.getResource("css/OpenSans-Bold.ttf").toExternalForm(), 12);
		Font.loadFont(PatternFly.class.getResource("css/OpenSans-BoldItalic.ttf").toExternalForm(), 12);
//		Font.loadFont(PatternFly.class.getResource("css/OpenSans-ExtraBold.ttf").toExternalForm(), 12);
//		Font.loadFont(PatternFly.class.getResource("css/OpenSans-ExtraBoldItalic.ttf").toExternalForm(), 12);
		Font.loadFont(PatternFly.class.getResource("css/OpenSans-Italic.ttf").toExternalForm(), 12);
//		Font.loadFont(PatternFly.class.getResource("css/OpenSans-Light.ttf").toExternalForm(), 12);
//		Font.loadFont(PatternFly.class.getResource("css/OpenSans-LightItalic.ttf").toExternalForm(), 12);
		Font.loadFont(PatternFly.class.getResource("css/OpenSans-Regular.ttf").toExternalForm(), 12);
//		Font.loadFont(PatternFly.class.getResource("css/OpenSans-Semibold.ttf").toExternalForm(), 12);
//		Font.loadFont(PatternFly.class.getResource("css/OpenSans-SemiboldItalic.ttf").toExternalForm(), 12);
	}
}
