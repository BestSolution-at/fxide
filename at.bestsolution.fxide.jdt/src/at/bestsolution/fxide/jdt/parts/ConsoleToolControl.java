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
