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

import java.util.function.Consumer;

import org.eclipse.fx.ui.controls.JavaFXCompatUtil;
import org.eclipse.fx.ui.controls.image.GraphicNode;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public class ModalDialog extends StackPane {
	private Label headerLabel;
	private BorderPane box;
	private ObservableList<Button> buttons = FXCollections.observableArrayList();

	public ModalDialog() {
		getStyleClass().add("modal-dialog");
		box = new BorderPane();

		{
			HBox header = new HBox();
			header.setAlignment(Pos.CENTER_LEFT);
			header.getStyleClass().add("header-area");
			headerLabel = new Label();


			Button b = new Button("x");
			b.setOnAction( e -> {
				close();
			});

			Region spacer = new Region();
			HBox.setHgrow(spacer, Priority.ALWAYS);
			header.getChildren().addAll(headerLabel,spacer,b);
			box.setTop(header);
		}

		{
			HBox footer = new HBox();
			footer.getStyleClass().add("footer-area");
			footer.setAlignment(Pos.CENTER_RIGHT);
			Bindings.bindContent(footer.getChildren(), buttons);
			box.setBottom(footer);
		}

		getChildren().add(box);

		parentProperty().addListener((o, ol, ne) -> {
			if( ol == null && ne != null ) {
				opened();
			}
		});
	}

	protected void opened() {
		// Fix regression introduced in Java8u102
		Platform.runLater( () -> {
			setInitialFocus();
			JavaFXCompatUtil.reapplyCSS(this);
		});
	}

	protected void setInitialFocus() {

	}

	public void setTitle(String title) {
		headerLabel.setText(title);
	}

	public String getTitle() {
		return headerLabel.getText();
	}

	public StringProperty titleProperty() {
		return headerLabel.textProperty();
	}

	public void setClientArea(Node node) {
		box.setCenter(node);
	}

	public Node getClientArea() {
		return box.getCenter();
	}

	public ObjectProperty<Node> clientAreaProperty() {
		return box.centerProperty();
	}

	public ObservableList<Button> getButtons() {
		return buttons;
	}

	public void close() {
		ModalDialogEvent closeRequest = new ModalDialogEvent(ModalDialogEvent.CLOSE_REQUEST);
		Event.fireEvent(this, closeRequest);
		if( closeRequest.isConsumed() ) {
			return;
		}

		ModalDialogEvent close = new ModalDialogEvent(ModalDialogEvent.CLOSED);
		Event.fireEvent(this, close);
	}

	public static class QuestionDialog extends ModalDialog {
		private Boolean result = Boolean.FALSE;

		public QuestionDialog(String title, String question, Consumer<Boolean> resultConsumer) {
			getStyleClass().add("question");
			setTitle(title);
			setClientArea(new Label(question, new GraphicNode()));

			Button noButton = PatternFly.defaultButton(new Button("No"));
			noButton.setCancelButton(true);
			noButton.setOnAction( e -> {
				result = Boolean.FALSE;
				close();
				result = Boolean.FALSE;
			} );

			Button yesButton = PatternFly.primaryButton(new Button("Yes"));
			yesButton.setDefaultButton(true);
			yesButton.setOnAction( e -> {
				result = Boolean.TRUE;
				close();
				result = Boolean.FALSE;
			});

			getButtons().addAll(noButton,yesButton);
			addEventHandler(ModalDialogEvent.CLOSED, evt -> resultConsumer.accept(result));
		}
	}
}