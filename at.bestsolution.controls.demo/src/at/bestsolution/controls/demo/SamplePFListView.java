package at.bestsolution.controls.demo;

import at.bestsolution.controls.patternfly.PatternFly;
import at.bestsolution.controls.patternfly.list.PFListGroupItem;
import at.bestsolution.controls.patternfly.list.PFListView;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class SamplePFListView extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane root = new BorderPane();

		PFListView<Event> v = new PFListView<>();
		v.setGroupItemFactory( e -> {
			PFListGroupItem i = new PFListGroupItem();
			i.headingProperty().bind(e.name);
			i.textProperty().bind(e.description);
			return i;
		});
		v.setElements(FXCollections.observableArrayList(
				new Event("Event One", "The following snippet of text is rendered as link text."),
				new Event("Event Two", "The following snippet of text is rendered as link text."),
				new Event("Event Three", "The following snippet of text is rendered as link text."),
				new Event("Event Four", "The following snippet of text is rendered as link text.")));
		root.setCenter(v);

		Scene s = new Scene(root, 800, 600);
		s.getStylesheets().addAll(PatternFly.COLOR_DEFINITIONS, PatternFly.CONTROL_CSS);

		primaryStage.setScene(s);
		primaryStage.show();
	}

	static class Event {
		private StringProperty name = new SimpleStringProperty(this, "name");
		private StringProperty description = new SimpleStringProperty(this, "description");

		public Event(String name, String description) {
			this.name.set(name);
			this.description.set(description);
		}

		public final StringProperty nameProperty() {
			return this.name;
		}

		public final String getName() {
			return this.nameProperty().get();
		}

		public final void setName(final String name) {
			this.nameProperty().set(name);
		}

		public final StringProperty descriptionProperty() {
			return this.description;
		}

		public final String getDescription() {
			return this.descriptionProperty().get();
		}

		public final void setDescription(final String description) {
			this.descriptionProperty().set(description);
		}
	}
}
