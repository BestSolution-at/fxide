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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.ISaveHandler.Save;
import org.eclipse.fx.code.editor.Constants;
import org.eclipse.fx.ui.controls.stage.FrameEvent;
import org.eclipse.fx.ui.services.dialog.LightWeightDialogService;
import org.eclipse.fx.ui.services.dialog.LightWeightDialogService.ModalityScope;
import org.eclipse.fx.ui.workbench.renderers.fx.services.SaveDialogPresenter;
import org.eclipse.fx.ui.workbench.renderers.fx.services.SaveDialogPresenter.SaveData;
import org.eclipse.fx.ui.workbench.renderers.fx.services.SaveDialogPresenterTypeProvider;
import org.osgi.service.component.annotations.Component;

import at.bestsolution.controls.patternfly.ModalDialog;
import at.bestsolution.controls.patternfly.PatternFly;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

@SuppressWarnings("restriction")
@Component
public class PatternflySaveDialogPresenterTypeProvider implements SaveDialogPresenterTypeProvider {

	@Override
	public Class<SaveDialogPresenterImpl> getType() {
		return SaveDialogPresenterImpl.class;
	}

	public static class SaveDialogPresenterImpl implements SaveDialogPresenter {
		@Inject
		LightWeightDialogService dialogService;

		@Override
		public CompletableFuture<List<Save>> promptToSave(SaveData data) {
			if( data.dirtyParts.size() == 1 ) {
				MPart part = data.dirtyParts.iterator().next();
				CompletableFuture<List<Save>> rv = new CompletableFuture<>();
				ModalDialog.YesNoCancelQuestionDialog d = new ModalDialog.YesNoCancelQuestionDialog("Unsaved changes", "'"+part.getLabel()+"' has been modified. Save changes?", r -> {
					switch (r) {
					case CANCEL:
						rv.complete(Collections.singletonList(Save.CANCEL));
						break;
					case YES:
						rv.complete(Collections.singletonList(Save.YES));
						break;
					case NO:
						rv.complete(Collections.singletonList(Save.NO));
						break;
					default:
						rv.complete(Collections.singletonList(Save.CANCEL));
						break;
					}
				});
				d.setMaxWidth(Region.USE_PREF_SIZE);
				d.setMaxHeight(Region.USE_PREF_SIZE);
				dialogService.openDialog(d, ModalityScope.WINDOW);

				return rv;
			} else {
				CompletableFuture<List<Save>> rv = new CompletableFuture<>();
				MultiSelectionList d = new MultiSelectionList(data, rv::complete);
				d.setMinWidth(500);
				d.setMaxWidth(Region.USE_PREF_SIZE);
				d.setMaxHeight(400);
				dialogService.openDialog(d, ModalityScope.WINDOW);

				return rv;
			}
		}
	}

	static class MultiSelectionList extends ModalDialog {
		private SaveData data;
		private List<Save> saveList;
		private final List<Save> defaultList;
		private ListView<SaveItem> listView;

		public MultiSelectionList(SaveData data, Consumer<List<Save>> resultConsumer) {
			this.data = data;
			this.defaultList = new ArrayList<>(data.dirtyParts.stream().map( m -> Save.CANCEL).collect(Collectors.toList()));
			this.saveList = new ArrayList<>(defaultList);

			setTitle("Save Resources");
			setClientArea(createClientArea());

			Button cancel = new Button("Cancel");
			PatternFly.defaultButton(cancel);
			cancel.setCancelButton(true);
			getButtons().add(cancel);

			Button ok = new Button("Ok");
			ok.setOnAction( e -> {
				this.saveList = listView.getItems().stream()
					.map( i -> i.selected.get() ? Save.YES : Save.NO)
					.collect(Collectors.toList());
				close();
				this.saveList = new ArrayList<>(defaultList);
			});
			ok.setDefaultButton(true);
			PatternFly.primaryButton(ok);
			getButtons().add(ok);
			addEventHandler(FrameEvent.CLOSED, e -> resultConsumer.accept(saveList));
		}

		private Node createClientArea() {
			BorderPane parent = new BorderPane();

			listView = new ListView<>();
			listView.setStyle("-fx-padding: 15");
			listView.getStyleClass().add("single-colored");
			listView.setCellFactory( DirtyPartCell::new );
			listView.setItems(FXCollections.observableArrayList(data.dirtyParts.stream().map(SaveItem::new).collect(Collectors.toList())));
			parent.setCenter(listView);

			return parent;
		}
	}

	static class SaveItem {
		final BooleanProperty selected = new SimpleBooleanProperty(this, "selected", true);
		final MPart part;

		public SaveItem(MPart part) {
			this.part = part;
		}
	}

	static class DirtyPartCell extends ListCell<SaveItem> {
		private CheckBox cb;

		public DirtyPartCell(ListView<SaveItem> view) {

		}

		@Override
		protected void updateItem(SaveItem item, boolean empty) {
			if( cb != null && getItem() != null ) {
				cb.selectedProperty().unbindBidirectional(getItem().selected);
			}

			super.updateItem(item, empty);

			if( item != null && ! empty ) {
				HBox box = new HBox();

				cb = new CheckBox();
				cb.selectedProperty().bindBidirectional(item.selected);

				box.getChildren().add(cb);

				Separator separator = new Separator(Orientation.VERTICAL);
				separator.setMaxHeight(20);
				box.getChildren().add(separator);

				VBox vb = new VBox();

				Label l = new Label(item.part.getLabel());
				l.getStyleClass().add("row-header");
				vb.getChildren().add(l);

				l = new Label(item.part.getPersistedState().get(Constants.DOCUMENT_URL).substring("module-file:".length()));
				vb.getChildren().add(l);

				box.getChildren().add(vb);

				setGraphic(box);
			} else {
				setGraphic(null);
				cb = null;
			}
		}
	}
}
