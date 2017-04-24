package at.bestsolution.controls.patternfly.list;

import java.util.function.Function;

import org.eclipse.fx.core.Subscription;
import org.eclipse.fx.core.observable.FXObservableUtil;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;

public class PFListView<E> extends Region {
	private ObjectProperty<Function<E, PFListGroupItem>> groupItemFactory = new SimpleObjectProperty<Function<E, PFListGroupItem>>(this,
			"groupItemFactory");
	private final ListView<E> listView;
	private ObservableList<E> selectedElements = FXCollections.observableArrayList();

	public PFListView() {
		getStyleClass().add("pf-list-view");
		listView = new ListView<>();
		listView.setCellFactory(PFListCell::new);
		getChildren().add(listView);
	}

	@Override
	protected void layoutChildren() {
		Insets i = getInsets();
		listView.resizeRelocate(i.getLeft(), i.getTop(), getWidth() - i.getLeft() - i.getRight(), getHeight() - i.getTop() - i.getBottom());
	}

	public void setElements(ObservableList<E> elements) {
		listView.setItems(elements);
	}

	public ObservableList<E> getElements() {
		return listView.getItems();
	}

	public ObservableList<E> getSelectedElements() {
		return selectedElements;
	}

	public final ObjectProperty<Function<E, PFListGroupItem>> groupItemFactoryProperty() {
		return this.groupItemFactory;
	}

	public final Function<E, PFListGroupItem> getGroupItemFactory() {
		return this.groupItemFactoryProperty().get();
	}

	public final void setGroupItemFactory(final Function<E, PFListGroupItem> rowFactory) {
		this.groupItemFactoryProperty().set(rowFactory);
	}

	class PFListCell extends ListCell<E> {
		private Subscription selectedElementsSub;
		private Subscription selectedSub;

		public PFListCell(ListView<E> v) {

		}

		@Override
		protected void updateItem(E item, boolean empty) {
			super.updateItem(item, empty);

			if( selectedElementsSub != null ) {
				selectedElementsSub.dispose();
			}

			if( item != null && ! empty ) {
				PFListGroupItem nodeItem = groupItemFactory.get().apply(item);
				nodeItem.setSelected(selectedElements.contains(item));
				selectedElementsSub = FXObservableUtil.onChange(selectedElements, e -> {
					if( selectedElements.contains(item) ) {
						nodeItem.selectedProperty().set(true);
					}
				});
				selectedSub = FXObservableUtil.onChange(nodeItem.selectedProperty(), e -> {
					if( e ) {
						selectedElements.add(item);
					} else {
						selectedElements.remove(item);
					}
				});
				setGraphic(nodeItem);
			} else {
				setGraphic(null);
			}
		}
	}
}
