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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.fx.ui.services.dialog.LightWeightDialogService;
import org.eclipse.fx.ui.services.dialog.LightWeightDialogService.ModalityScope;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;

import at.bestsolution.controls.patternfly.PatternFly;
import at.bestsolution.fxide.jdt.JDTConstants;
import at.bestsolution.fxide.jdt.component.LWModalDialog;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class NewJavaTypeHandler {
	@CanExecute
	public boolean isEnabled(@Named(JDTConstants.CTX_PACKAGE_EXPLORER_SELECTION) IResource resource) {
		if( resource == null ) {
			return false;
		}

		IJavaElement element;
		if( resource instanceof IFile ) {
			element = JavaCore.create(resource.getParent());
		} else {
			element = JavaCore.create(resource);
		}

		return element instanceof IPackageFragment;
	}

	@Execute
	public void createType(LightWeightDialogService dialogService, @Named(JDTConstants.CTX_PACKAGE_EXPLORER_SELECTION) IResource resource) {
		IJavaElement element;
		if( resource instanceof IFile ) {
			element = JavaCore.create(resource.getParent());
		} else {
			element = JavaCore.create(resource);
		}

		if( element != null && element instanceof IPackageFragment ) {
			dialogService.openDialog(new NewTypeDialog((IPackageFragment) element), ModalityScope.WINDOW);
		}
	}

	static class NewTypeDialog extends LWModalDialog {
		private final IPackageFragment packageFragment;
		private TextField name;
		private ChoiceBox<String> type;

		@Inject
		public NewTypeDialog(IPackageFragment packageFragment) {
			setTitle("New Java Type");
			getStyleClass().add("jdt-new-type-dialog");
			this.packageFragment = packageFragment;
			setMinWidth(500);
			setClientArea(createClientArea());

			Button cancel = PatternFly.defaultButton(new Button("Cancel"));
			cancel.setOnAction( evt -> close());
			cancel.setCancelButton(true);

			Button ok = PatternFly.primaryButton(new Button("Ok"));
			ok.setOnAction( this::okPressed );
			ok.setDefaultButton(true);

			getButtons().addAll(cancel,ok);
		}

		@Override
		protected void opened() {
			super.opened();
			Platform.runLater( () -> {
				name.requestFocus();
			});
		}

		private Node createClientArea() {
			VBox pane = new VBox(10);
			pane.getStyleClass().add("dialog-content");

			{
				VBox box = new VBox(2);
				Label l = PatternFly.defaultLabel(new Label());
				l.setText("Package");

				TextField packageName = PatternFly.defaultTextField(new TextField());
				packageName.setEditable(false);
				packageName.setText(packageFragment.getElementName());
				box.getChildren().addAll(l,packageName);
				pane.getChildren().add(box);
			}

			{
				VBox box = new VBox(2);

				Label l = PatternFly.defaultLabel(new Label());
				l.setText("Type");
				box.getChildren().add(l);

				type = PatternFly.defaultChoiceBox(new ChoiceBox<>());
				type.setItems(FXCollections.observableArrayList("Class","Interface","Enum","Annotation"));
				type.getSelectionModel().select(0);

				name = PatternFly.defaultTextField(new TextField());
				name.requestFocus();
				HBox.setHgrow(name, Priority.ALWAYS);

				HBox hBox = new HBox(type, name);
				hBox.setSpacing(25);
				box.getChildren().add(hBox);

				pane.getChildren().add(box);
			}

			return pane;
		}

		protected void okPressed(ActionEvent evt) {
			try {
				IContainer c = (IContainer) packageFragment.getUnderlyingResource();
				IFile file = c.getFile(new Path(name.getText()+".java"));

				String fileContent;
				if( "Annotation".equals(type.getSelectionModel().getSelectedItem()) ) {
					fileContent = "package "+packageFragment.getElementName()+";\n\npublic @interface " + name.getText() + " {\n}";
				} else {
					fileContent = "package "+packageFragment.getElementName()+";\n\npublic " + type.getSelectionModel().getSelectedItem().toLowerCase() + " " + name.getText() + " {\n}";
				}

				try( ByteArrayInputStream in = new ByteArrayInputStream(fileContent.getBytes()) ) {
					file.create(in, true, null);
				}
				close();
			} catch (IOException | CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
