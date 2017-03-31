package at.bestsolution.fxide.jdt.component;

import org.eclipse.fx.ui.controls.stage.Frame;
import org.eclipse.fx.ui.controls.stage.FrameEvent;

import at.bestsolution.controls.patternfly.ModalDialog;
import at.bestsolution.controls.patternfly.ModalDialogEvent;
import javafx.event.Event;

@SuppressWarnings("restriction")
public class LWModalDialog extends ModalDialog implements Frame {

	public LWModalDialog() {
		addEventHandler(ModalDialogEvent.CLOSED, e -> {
			Event.fireEvent(this, new FrameEvent(this, FrameEvent.CLOSED));
		});
	}

	@Override
	public void setResizeable(boolean resizable) {
	}

	@Override
	public void setMinimizable(boolean minimizable) {
	}

	@Override
	public void setMaximizable(boolean maximizable) {
	}

}
