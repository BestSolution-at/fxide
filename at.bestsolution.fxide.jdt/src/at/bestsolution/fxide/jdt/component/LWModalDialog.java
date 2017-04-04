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
package at.bestsolution.fxide.jdt.component;

import java.util.function.Consumer;

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

	public static class LWQuestionDialog extends QuestionDialog implements Frame {

		public LWQuestionDialog(String title, String question, Consumer<Boolean> resultConsumer) {
			super(title, question, resultConsumer);
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
}
