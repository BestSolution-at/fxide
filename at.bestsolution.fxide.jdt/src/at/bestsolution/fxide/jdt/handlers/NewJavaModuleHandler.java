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

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.fx.core.Status;
import org.eclipse.fx.core.di.Service;
import org.eclipse.fx.ui.services.dialog.LightWeightDialogService;
import org.eclipse.fx.ui.services.dialog.LightWeightDialogService.ModalityScope;

import at.bestsolution.controls.patternfly.ModalDialog;
import at.bestsolution.controls.patternfly.PatternFly;
import at.bestsolution.fxide.jdt.JDTConstants;
import at.bestsolution.fxide.jdt.services.ModuleTypeService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;

public class NewJavaModuleHandler {

	@Execute
	public void createJavaModule(LightWeightDialogService dialogService) {
		dialogService.openDialog(NewProjectDialog.class, ModalityScope.WINDOW);
	}

	static class NewProjectDialog extends ModalDialog {
		private final IWorkspace workspace;
		private TextField projectName;
		private final List<ModuleTypeService> moduleTypes;
		private ChoiceBox<ModuleTypeService> moduleType;
		private final IResource resource;

		@Inject
		public NewProjectDialog(IWorkspace workspace,
				@Service List<ModuleTypeService> moduleTypes,
				@Optional @Named(JDTConstants.CTX_PACKAGE_EXPLORER_SELECTION) IResource resource) {
			this.workspace = workspace;
			this.moduleTypes = moduleTypes;
			this.resource = resource;
			setTitle("New Java Module");
			setMinWidth(500);
			setMaxWidth(500);
			setClientArea(createClientArea());

			Button cancel = PatternFly.defaultButton(new Button("Cancel"));
			cancel.setOnAction( evt -> close());

			Button ok = PatternFly.primaryButton(new Button("Ok"));
			ok.setOnAction( this::okPressed );

			getButtons().addAll(cancel,ok);
		}

		private Node createClientArea() {
			HBox box = new HBox(10);
			box.getStyleClass().add("dialog-content");

			moduleType = PatternFly.defaultChoiceBox(new ChoiceBox<>());
			moduleType.setConverter( new StringConverter<ModuleTypeService>() {

				@Override
				public String toString(ModuleTypeService object) {
					return object.getLabel();
				}

				@Override
				public ModuleTypeService fromString(String string) {
					return null;
				}
			});
			moduleType.setItems(FXCollections.observableArrayList(moduleTypes));
			moduleType.getSelectionModel().select(0);

			projectName = PatternFly.defaultTextField(new TextField());
			HBox.setHgrow(projectName, Priority.ALWAYS);
			box.getChildren().addAll(moduleType,projectName);

			return box;
		}

		private void okPressed(ActionEvent e) {
			if( projectName.getText() == null || projectName.getText().isEmpty() ) {
				//TODO Show error
				return;
			}

			IProject project = workspace.getRoot().getProject(projectName.getText());
			ModuleTypeService moduleTypeService = moduleType.getSelectionModel().getSelectedItem();
			Status status = moduleTypeService.createModule(project,resource);

			if( status.isOk() ) {
				close();
			} else {
				//TODO Show error
				return;
			}
		}
	}
}
