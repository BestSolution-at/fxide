package at.bestsolution.controls.demo;

import at.bestsolution.controls.patternfly.PatternFly;
import at.bestsolution.controls.patternfly.ModalDialog.QuestionDialog;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class SampleBaseDialog extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		StackPane p = new StackPane();

		QuestionDialog d = new QuestionDialog("Delete file", "Do you really want to delete 'sample.txt'", b -> {
			System.err.println("Result: " + b);
		});
		d.setMinSize(400,200);
		d.setMaxSize(400,200);

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
