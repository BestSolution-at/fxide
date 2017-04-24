package at.bestsolution.controls.patternfly.list;

import org.eclipse.fx.core.Subscription;

import at.bestsolution.controls.patternfly.PatternFly;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public class PFListGroupItem extends Region {
	private StringProperty heading = new SimpleStringProperty(this, "heading"); //TODO Make CharSequence
	private StringProperty text = new SimpleStringProperty(this,"text");  //TODO Make CharSequence
	private ObjectProperty<Node> graphic = new SimpleObjectProperty<>(this,"graphic");
	private BooleanProperty selected = new SimpleBooleanProperty(this,"selected",false);

	private ObservableList<Button> primaryButtons = FXCollections.observableArrayList();
	private GridPane pane;

	public PFListGroupItem() {
		getStyleClass().add("pf-list-group-item");
		pane = new GridPane();

		{
			CheckBox cb = new CheckBox();
			pane.add(cb, 0, 0);

			pane.add(new Separator(Orientation.VERTICAL), 1, 0);

			Label l = new Label();
			l.textProperty().bind(heading);
			l.getStyleClass().add("pf-list-group-item-heading");
			pane.add(l, 2, 0);

			// TODO Add Actions here
		}

		{
			Label l = new Label();
			l.textProperty().bind(text);
			l.getStyleClass().add("pf-list-group-item-text");
			pane.add(l, 2, 1);
		}

		getChildren().add(pane);
	}

	@Override
	protected void layoutChildren() {
		Insets i = getInsets();
		pane.resizeRelocate(i.getLeft(), i.getTop(), getWidth() - i.getLeft() - i.getRight(), getHeight() - i.getTop() - i.getBottom());
	}

	public Subscription registerAction(Runnable r, boolean primary, String label, String... styleClasses ) {
		if( primary ) {
			Button b = new Button(label);
			PatternFly.defaultButton(b);
			b.getStyleClass().addAll(styleClasses);
			b.setOnAction( e -> r.run());
			primaryButtons.add(b);
			return () -> {};
		} else {
			return () -> {};
		}
	}

	public final StringProperty headingProperty() {
		return this.heading;
	}


	public final String getHeading() {
		return this.headingProperty().get();
	}


	public final void setHeading(final String heading) {
		this.headingProperty().set(heading);
	}


	public final StringProperty textProperty() {
		return this.text;
	}


	public final String getText() {
		return this.textProperty().get();
	}


	public final void setText(final String text) {
		this.textProperty().set(text);
	}


	public final ObjectProperty<Node> graphicProperty() {
		return this.graphic;
	}


	public final Node getGraphic() {
		return this.graphicProperty().get();
	}


	public final void setGraphic(final Node graphic) {
		this.graphicProperty().set(graphic);
	}


	public final BooleanProperty selectedProperty() {
		return this.selected;
	}


	public final boolean isSelected() {
		return this.selectedProperty().get();
	}


	public final void setSelected(final boolean selected) {
		this.selectedProperty().set(selected);
	}
}
