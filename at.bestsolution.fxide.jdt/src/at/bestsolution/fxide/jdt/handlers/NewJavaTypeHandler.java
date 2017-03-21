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

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.fx.ui.controls.dialog.TitleAreaDialog;
import org.eclipse.fx.ui.services.dialog.LightWeightDialogService;
import org.eclipse.fx.ui.services.dialog.LightWeightDialogService.ModalityScope;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import at.bestsolution.fxide.jdt.JDTConstants;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class NewJavaTypeHandler {
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
		} else {

		}
	}

	@SuppressWarnings("restriction")
	static class NewTypeDialog extends TitleAreaDialog {
		private final IPackageFragment packageFragment;
		private TextField name;
		private ChoiceBox<String> type;

		@Inject
		public NewTypeDialog(IPackageFragment packageFragment) {
			super("New Java Type", "New Java Type", "Create a new Java type");
			this.packageFragment = packageFragment;
			setMinWidth(300);
			setClientArea(createClientArea());
			addDefaultButtons();
		}

		private Node createClientArea() {
			VBox pane = new VBox(10);

			{
				VBox box = new VBox(2);
				Label l = new Label();
				l.setText("Package");

				TextField packageName = new TextField();
				packageName.setEditable(false);
				packageName.setText(packageFragment.getElementName());
				box.getChildren().addAll(l,packageName);
				pane.getChildren().add(box);
			}

			{
				VBox box = new VBox(2);
				Label l = new Label();
				l.setText("Type");

				type = new ChoiceBox<>();
				type.setItems(FXCollections.observableArrayList("Class","Interface","Enum","Annotation"));
				type.getSelectionModel().select(0);
				box.getChildren().addAll(l,type);
				pane.getChildren().add(box);
			}

			{
				VBox box = new VBox(2);
				Label l = new Label();
				l.setText("Name");

				name = new TextField();
				box.getChildren().addAll(l,name);
				pane.getChildren().add(box);
			}

			return pane;
		}

		@Override
		protected void handleOk() {
			try {
				IContainer c = (IContainer) packageFragment.getUnderlyingResource();
				IFile file = c.getFile(new Path(name.getText()+".java"));
				file.create(new ByteArrayInputStream(("package "+packageFragment.getElementName()+";\n\npublic " + type.getSelectionModel().getSelectedItem().toLowerCase() + " " + name.getText() + " {\n}").getBytes()), true, null);
				super.handleOk();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

//			ICompilationUnit unit = packageFragment.getCompilationUnit(name.getText()+".java");
//			if( ! unit.exists() ) {
//				try {
//					unit.getBuffer().append("public " + type.getSelectionModel().getSelectedItem().toLowerCase() + " {\n}");
//				} catch (JavaModelException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				super.handleOk();
//			}
		}
	}
}
