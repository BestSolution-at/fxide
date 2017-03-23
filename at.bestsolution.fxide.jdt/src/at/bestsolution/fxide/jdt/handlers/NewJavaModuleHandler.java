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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.fx.ui.controls.dialog.TitleAreaDialog;
import org.eclipse.fx.ui.services.dialog.LightWeightDialogService;
import org.eclipse.fx.ui.services.dialog.LightWeightDialogService.ModalityScope;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

@SuppressWarnings("restriction")
public class NewJavaModuleHandler {

	@Execute
	public void createJavaModule(LightWeightDialogService dialogService) {
		dialogService.openDialog(NewPojectDialog.class, ModalityScope.WINDOW);
	}

	static class NewPojectDialog extends TitleAreaDialog {
		private final IWorkspace workspace;
		private TextField projectName;

		@Inject
		public NewPojectDialog(IWorkspace workspace) {
			super("New Java Module", "New Java Module", "Create a new Java module");
			getStyleClass().add("jdt-new-module-dialog");
			this.workspace = workspace;
			setClientArea(createClientArea());
			setMinWidth(500);
			addDefaultButtons();
		}

		private Node createClientArea() {
			HBox box = new HBox(25);

			ChoiceBox<String> f = new ChoiceBox<>();
			f.setItems(FXCollections.observableArrayList("Maven","Gradle"));
			f.getSelectionModel().select(0);

			projectName = new TextField();
			HBox.setHgrow(projectName, Priority.ALWAYS);
			box.getChildren().addAll(f,projectName);

			return box;
		}

		@Override
		protected void handleOk() {
			IProjectDescription description = workspace.newProjectDescription(projectName.getText());
			description.setNatureIds( new String[] {
				JavaCore.NATURE_ID
			});
			ICommand cmd = description.newCommand();
			cmd.setBuilderName(JavaCore.BUILDER_ID);
			description.setBuildSpec(new ICommand[] { cmd });

			IProject project = workspace.getRoot().getProject(projectName.getText());
			try {
				project.create(description, null);
				project.open(null);

				project.getFolder(new Path("target")).create(true, true, null);
				project.getFolder(new Path("target").append("classes")).create(true, true, null);

				project.getFolder(new Path("target")).setDerived(true,null);
				project.getFolder(new Path("target").append("classes")).setDerived(true,null);

				project.getFolder(new Path("src")).create(true, true, null);

				project.getFolder(new Path("src").append("main")).create(true, true, null);
				project.getFolder(new Path("src").append("main").append("java")).create(true, true, null);
				project.getFolder(new Path("src").append("main").append("resources")).create(true, true, null);

				project.getFolder(new Path("src").append("test")).create(true, true, null);
				project.getFolder(new Path("src").append("test").append("java")).create(true, true, null);
				project.getFolder(new Path("src").append("test").append("resources")).create(true, true, null);

//				IExecutionEnvironmentsManager executionEnvironmentsManager = JavaRuntime.getExecutionEnvironmentsManager();
//				IExecutionEnvironment[] executionEnvironments = executionEnvironmentsManager.getExecutionEnvironments();
//				System.err.println(JavaRuntime.getDefaultVMInstall().getInstallLocation());

				IJavaProject jProject = JavaCore.create(project);
				jProject.setOutputLocation(project.getFolder(new Path("target").append("classes")).getFullPath(), null);

				List<IClasspathEntry> entries = new ArrayList<>();
				entries.add(JavaCore.newSourceEntry(project.getProject().getFullPath().append("src").append("main").append("java")));
				entries.add(JavaCore.newSourceEntry(project.getProject().getFullPath().append("src").append("main").append("resources")));
				entries.add(JavaCore.newSourceEntry(project.getProject().getFullPath().append("src").append("test").append("java")));
				entries.add(JavaCore.newSourceEntry(project.getProject().getFullPath().append("src").append("test").append("resources")));
				entries.add(JavaCore.newContainerEntry(JavaRuntime.newDefaultJREContainerPath()));

				jProject.setRawClasspath(entries.toArray(new IClasspathEntry[0]), null);
				workspace.save(true, null);
				super.handleOk();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
