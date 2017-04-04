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

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;

public class FormGroup {
	private StringProperty label = new SimpleStringProperty(this, "label");
	private ObjectProperty<Node> node = new SimpleObjectProperty<>(this, "node");

	private IntegerProperty xsColIdx = new SimpleIntegerProperty(this, "xsColIdx");
	private IntegerProperty xsColSpan = new SimpleIntegerProperty(this, "xsColSpan");
	private IntegerProperty xsRowIdx = new SimpleIntegerProperty(this, "xsRowIdx");
	private IntegerProperty xsRowSpan = new SimpleIntegerProperty(this, "xsRowSpan");

	private IntegerProperty smColIdx = new SimpleIntegerProperty(this, "smColIdx");
	private IntegerProperty smColSpan = new SimpleIntegerProperty(this, "smColSpan");
	private IntegerProperty smRowIdx = new SimpleIntegerProperty(this, "smRowIdx");
	private IntegerProperty smRowSpan = new SimpleIntegerProperty(this, "smRowSpan");

	private IntegerProperty mdColIdx = new SimpleIntegerProperty(this, "mdColIdx");
	private IntegerProperty mdColSpan = new SimpleIntegerProperty(this, "mdColSpan");
	private IntegerProperty mdRowIdx = new SimpleIntegerProperty(this, "mdRowIdx");
	private IntegerProperty mdRowSpan = new SimpleIntegerProperty(this, "mdRowSpan");

	private IntegerProperty lgColIdx = new SimpleIntegerProperty(this, "lgColIdx");
	private IntegerProperty lgColSpan = new SimpleIntegerProperty(this, "lgColSpan");
	private IntegerProperty lgRowIdx = new SimpleIntegerProperty(this, "lgRowIdx");
	private IntegerProperty lgRowSpan = new SimpleIntegerProperty(this, "lgRowSpan");

	public final StringProperty labelProperty() {
		return this.label;
	}

	public final String getLabel() {
		return this.labelProperty().get();
	}

	public final void setLabel(final String label) {
		this.labelProperty().set(label);
	}

	public final ObjectProperty<Node> nodeProperty() {
		return this.node;
	}

	public final Node getNode() {
		return this.nodeProperty().get();
	}

	public final void setNode(final Node node) {
		this.nodeProperty().set(node);
	}

	public final IntegerProperty xsColIdxProperty() {
		return this.xsColIdx;
	}


	public final int getXsColIdx() {
		return this.xsColIdxProperty().get();
	}


	public final void setXsColIdx(final int xsColIdx) {
		this.xsColIdxProperty().set(xsColIdx);
	}


	public final IntegerProperty xsColSpanProperty() {
		return this.xsColSpan;
	}


	public final int getXsColSpan() {
		return this.xsColSpanProperty().get();
	}


	public final void setXsColSpan(final int xsColSpan) {
		this.xsColSpanProperty().set(xsColSpan);
	}


	public final IntegerProperty xsRowIdxProperty() {
		return this.xsRowIdx;
	}


	public final int getXsRowIdx() {
		return this.xsRowIdxProperty().get();
	}


	public final void setXsRowIdx(final int xsRowIdx) {
		this.xsRowIdxProperty().set(xsRowIdx);
	}


	public final IntegerProperty xsRowSpanProperty() {
		return this.xsRowSpan;
	}


	public final int getXsRowSpan() {
		return this.xsRowSpanProperty().get();
	}


	public final void setXsRowSpan(final int xsRowSpan) {
		this.xsRowSpanProperty().set(xsRowSpan);
	}


	public final IntegerProperty smColIdxProperty() {
		return this.smColIdx;
	}


	public final int getSmColIdx() {
		return this.smColIdxProperty().get();
	}


	public final void setSmColIdx(final int smColIdx) {
		this.smColIdxProperty().set(smColIdx);
	}


	public final IntegerProperty smColSpanProperty() {
		return this.smColSpan;
	}


	public final int getSmColSpan() {
		return this.smColSpanProperty().get();
	}


	public final void setSmColSpan(final int smColSpan) {
		this.smColSpanProperty().set(smColSpan);
	}


	public final IntegerProperty smRowIdxProperty() {
		return this.smRowIdx;
	}


	public final int getSmRowIdx() {
		return this.smRowIdxProperty().get();
	}


	public final void setSmRowIdx(final int smRowIdx) {
		this.smRowIdxProperty().set(smRowIdx);
	}


	public final IntegerProperty smRowSpanProperty() {
		return this.smRowSpan;
	}


	public final int getSmRowSpan() {
		return this.smRowSpanProperty().get();
	}


	public final void setSmRowSpan(final int smRowSpan) {
		this.smRowSpanProperty().set(smRowSpan);
	}


	public final IntegerProperty mdColIdxProperty() {
		return this.mdColIdx;
	}


	public final int getMdColIdx() {
		return this.mdColIdxProperty().get();
	}


	public final void setMdColIdx(final int mdColIdx) {
		this.mdColIdxProperty().set(mdColIdx);
	}


	public final IntegerProperty mdColSpanProperty() {
		return this.mdColSpan;
	}


	public final int getMdColSpan() {
		return this.mdColSpanProperty().get();
	}


	public final void setMdColSpan(final int mdColSpan) {
		this.mdColSpanProperty().set(mdColSpan);
	}


	public final IntegerProperty mdRowIdxProperty() {
		return this.mdRowIdx;
	}


	public final int getMdRowIdx() {
		return this.mdRowIdxProperty().get();
	}


	public final void setMdRowIdx(final int mdRowIdx) {
		this.mdRowIdxProperty().set(mdRowIdx);
	}


	public final IntegerProperty mdRowSpanProperty() {
		return this.mdRowSpan;
	}


	public final int getMdRowSpan() {
		return this.mdRowSpanProperty().get();
	}


	public final void setMdRowSpan(final int mdRowSpan) {
		this.mdRowSpanProperty().set(mdRowSpan);
	}


	public final IntegerProperty lgColIdxProperty() {
		return this.lgColIdx;
	}


	public final int getLgColIdx() {
		return this.lgColIdxProperty().get();
	}


	public final void setLgColIdx(final int lgColIdx) {
		this.lgColIdxProperty().set(lgColIdx);
	}


	public final IntegerProperty lgColSpanProperty() {
		return this.lgColSpan;
	}


	public final int getLgColSpan() {
		return this.lgColSpanProperty().get();
	}


	public final void setLgColSpan(final int lgColSpan) {
		this.lgColSpanProperty().set(lgColSpan);
	}


	public final IntegerProperty lgRowIdxProperty() {
		return this.lgRowIdx;
	}


	public final int getLgRowIdx() {
		return this.lgRowIdxProperty().get();
	}


	public final void setLgRowIdx(final int lgRowIdx) {
		this.lgRowIdxProperty().set(lgRowIdx);
	}


	public final IntegerProperty lgRowSpanProperty() {
		return this.lgRowSpan;
	}


	public final int getLgRowSpan() {
		return this.lgRowSpanProperty().get();
	}


	public final void setLgRowSpan(final int lgRowSpan) {
		this.lgRowSpanProperty().set(lgRowSpan);
	}
}
