package at.bestsolution.fxide.jdt.parts;

import javax.annotation.PostConstruct;

import org.eclipse.fx.ui.controls.styledtext.StyledTextArea;

import javafx.scene.layout.BorderPane;

public class Console {
	@PostConstruct
	void init(BorderPane p) {
		StyledTextArea t = new StyledTextArea();
		p.setCenter(t);
	}
}
