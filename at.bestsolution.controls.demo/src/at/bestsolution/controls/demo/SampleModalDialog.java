package at.bestsolution.controls.demo;

import at.bestsolution.controls.patternfly.ModalDialog;
import at.bestsolution.controls.patternfly.PatternFly;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class SampleModalDialog extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		StackPane p = new StackPane();

		ModalDialog d = new ModalDialog();
		d.setTitle("Hello Patternfly");
		d.setMinSize(500, 300);
		d.setMaxSize(500, 300);

		GridPane g = new GridPane();
		g.setPadding(new Insets(15));
		g.setHgap(20);
		g.setVgap(20);

		{
			Label l = PatternFly.defaultLabel(new Label("Default"));
			GridPane.setHalignment(l, HPos.RIGHT);
			g.add(l, 0, 0);

			TextField t = PatternFly.defaultTextField(new TextField());
			GridPane.setHgrow(t, Priority.ALWAYS);
			g.add(t, 1, 0);
		}

		{
			Label l = PatternFly.defaultLabel(new Label("Disabled"));
			GridPane.setHalignment(l, HPos.RIGHT);
			g.add(l, 0, 1);

			TextField t = PatternFly.defaultTextField(new TextField());
			GridPane.setHgrow(t, Priority.ALWAYS);
			g.add(t, 1, 1);
		}

		{
			Label l = PatternFly.defaultLabel(new Label("No error"));
			GridPane.setHalignment(l, HPos.RIGHT);
			g.add(l, 0, 2);

			TextField t = PatternFly.defaultTextField(new TextField());
			GridPane.setHgrow(t, Priority.ALWAYS);
			g.add(t, 1, 2);
		}

		d.setClientArea(g);

		d.getButtons().addAll(PatternFly.defaultButton(new Button("Close")), PatternFly.primaryButton(new Button("Save changes")));

		p.getChildren().add(d);

		Scene s = new Scene(p, 800, 600);
		s.getStylesheets().addAll(PatternFly.COLOR_DEFINITIONS, PatternFly.CONTROL_CSS);
		primaryStage.setScene(s);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
