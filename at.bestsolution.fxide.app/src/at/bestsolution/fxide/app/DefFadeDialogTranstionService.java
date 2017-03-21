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

import org.eclipse.fx.ui.workbench.renderers.fx.services.FadeDialogTranstionService;
import org.eclipse.fx.ui.workbench.renderers.fx.services.LightweightDialogTransitionService;
import org.osgi.service.component.annotations.Component;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.util.Duration;

@Component
public class DefFadeDialogTranstionService extends FadeDialogTranstionService implements LightweightDialogTransitionService {
	@Override
	protected void configureFadeIn(FadeTransition transition) {
		super.configureFadeIn(transition);
		transition.setDuration(Duration.millis(1000));
		transition.setInterpolator(Interpolator.EASE_IN);
	}
}
