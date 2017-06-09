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
package at.bestsolution.fxide.jdt.handlers;

import java.util.Optional;

import javax.inject.Named;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.fx.ui.services.dialog.LightWeightDialogService;
import org.eclipse.fx.ui.services.dialog.LightWeightDialogService.ModalityScope;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import at.bestsolution.controls.patternfly.ModalDialog;
import at.bestsolution.controls.patternfly.PatternFly;
import at.bestsolution.fxide.base.BaseConstants;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class NewPackageHandler {

	private static Optional<IPackageFragmentRoot> findRoot(IJavaElement e) {
		do {
			if( e instanceof IPackageFragmentRoot ) {
				return Optional.of((IPackageFragmentRoot) e);
			}
		} while( (e = e.getParent()) != null );

		return Optional.empty();
	}

	@CanExecute
	public boolean canCreatePackage(@Named(BaseConstants.CTX_PACKAGE_EXPLORER_SELECTION) IResource resource) {
		IJavaElement element;
		if( resource instanceof IFile ) {
			element = JavaCore.create(resource.getParent());
		} else {
			element = JavaCore.create(resource);
		}

		return element != null && !(element instanceof IJavaProject);
	}

	@Execute
	public void createPackage(LightWeightDialogService dialogService, @Named(BaseConstants.CTX_PACKAGE_EXPLORER_SELECTION) IResource resource) {
		IJavaElement element;
		if( resource instanceof IFile ) {
			element = JavaCore.create(resource.getParent());
		} else {
			element = JavaCore.create(resource);
		}

		Optional<IPackageFragmentRoot> rootOp = findRoot(element);
		if( !rootOp.isPresent() ) {
			return;
		}

		if( element != null ) {
			dialogService.openDialog(new NewPackageDialog(rootOp.get(),element), ModalityScope.WINDOW);
		}
	}

	static class NewPackageDialog extends ModalDialog {
		private final IPackageFragmentRoot root;
		private final IJavaElement element;
		private TextField packageName;

		public NewPackageDialog(IPackageFragmentRoot root, IJavaElement element) {
			this.root = root;
			this.element = element;
			setTitle("New package");
			setClientArea(createClientArea());
			setMinWidth(500);

			Button cancel = PatternFly.defaultButton(new Button("Cancel"));
			cancel.setOnAction( evt -> close());
			cancel.setCancelButton(true);

			Button ok = PatternFly.primaryButton(new Button("Ok"));
			ok.setOnAction( this::okPressed );
			ok.setDefaultButton(true);

			getButtons().addAll(cancel,ok);
		}

		private Node createClientArea() {
			VBox pane = new VBox(10);
			pane.getStyleClass().add("dialog-content");

			{
				VBox box = new VBox(2);
				Label l = PatternFly.defaultLabel(new Label());
				l.setText("Name");

				packageName = PatternFly.defaultTextField(new TextField());
				packageName.setText( element instanceof IPackageFragmentRoot ? "" : element.getElementName());
				box.getChildren().addAll(l,packageName);
				pane.getChildren().add(box);
			}

			return pane;
		}

		protected void okPressed(ActionEvent evt) {
			try {
				root.createPackageFragment(packageName.getText(), true, null);
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			close();
		}
	}
}
