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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public class FormLayout extends Region {
	public enum LabelAlignment {
		TOP, RIGHT, LEFT
	}

	private ObjectProperty<LabelAlignment> labelAlignment = new SimpleObjectProperty<>(this, "labelAlignment",
			LabelAlignment.LEFT);

	private ObservableList<FormGroup> formGroupList = FXCollections.observableArrayList();

	public final ObjectProperty<LabelAlignment> labelAlignmentProperty() {
		return this.labelAlignment;
	}

	public final LabelAlignment getLabelAlignment() {
		return this.labelAlignmentProperty().get();
	}

	public final void setLabelAlignment(final LabelAlignment labelAlignment) {
		this.labelAlignmentProperty().set(labelAlignment);
	}

	public ObservableList<FormGroup> getFormGroupList() {
		return formGroupList;
	}

	private GridPane grid;

	private DoubleProperty columnWidth = new SimpleDoubleProperty(this, "columnWidth", 81);

	public FormLayout() {
		grid = new GridPane();
//		for(int i = 0; i < 12; i++) {
//			ColumnConstraints c = new ColumnConstraints();
//			c.minWidthProperty().bind(columnWidth);
//			c.prefWidthProperty().bind(columnWidth);
//			c.maxWidthProperty().bind(columnWidth);
//			grid.getColumnConstraints().add(c);
//		}

		formGroupList.addListener(this::handleChange);
	}

	private void handleChange(Change<? extends FormGroup> c) {
		while( c.next() ) {

		}
	}
}
