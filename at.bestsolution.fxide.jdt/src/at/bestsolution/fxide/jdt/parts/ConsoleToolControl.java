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
package at.bestsolution.fxide.jdt.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.fx.core.preferences.Preference;

import at.bestsolution.controls.patternfly.PatternFly;
import javafx.beans.property.Property;
import javafx.geometry.Insets;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class ConsoleToolControl {
	@Inject
	@Preference(nodePath="at.bestsolution.fxide.app.part.console-view",key="visible")
	private Property<Boolean> consoleView;
	
	@PostConstruct
	void init(HBox b) {
		b.setPadding(new Insets(5, 5, 5, 0));
		Region r = new Region();
		HBox.setHgrow(r, Priority.ALWAYS);
		b.getChildren().add(r);
		ToggleButton button = PatternFly.defaultButton(new ToggleButton("Console"));
		button.selectedProperty().bindBidirectional(consoleView);
		b.getChildren().add(button);
	}
}
