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
package at.bestsolution.fxide.base;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.fx.core.bindings.FXBindings;
import org.eclipse.fx.core.observable.FXObservableUtil;
import org.eclipse.fx.ui.controls.tree.LazyTreeItem;

import at.bestsolution.fxide.base.vm.VM_ModuleExplorer;
import at.bestsolution.fxide.base.vm.VM_ModuleExplorer.VM_ContainerNode;
import at.bestsolution.fxide.base.vm.VM_ModuleExplorer.VM_ExplorerNode;
import at.bestsolution.fxide.base.vm.VM_ModuleExplorer.VM_FileNode;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

public class ModuleExplorer {
	@Inject
	VM_ModuleExplorer vm;
	private TreeView<VM_ExplorerNode> viewer;
	
	private FXObservableUtil.Instance tracker = new FXObservableUtil.Instance();
	
	@PostConstruct
	void init(BorderPane p) {
		viewer = new TreeView<>();
		viewer.setCellFactory(ModuleExplorerTreeCell::new);
		viewer.setShowRoot(false);
		viewer.setRoot(new ContainerTreeItem(vm.root()));
		viewer.setOnMouseClicked(this::handleClick);
		
		
		ObservableList<TreeItem<VM_ExplorerNode>> selectedItems = viewer.getSelectionModel().getSelectedItems();
		tracker.onChange(selectedItems, this::handleSelectedItemsChange);
		
		ReadOnlyObjectProperty<TreeItem<VM_ExplorerNode>> focusedItem = viewer.getSelectionModel().selectedItemProperty();
		tracker.onChange(focusedItem, this::handleSelectedItemChange);
		
		p.setCenter(viewer);
	}
	
	@PreDestroy
	void cleanup() {
		tracker.dispose();
	}
	
	private void handleClick(MouseEvent e) {
		if( e.getClickCount() > 1) {
			vm.openEditorForSelectedNodes().execute();
		}
	}
	
	private void handleSelectedItemChange(TreeItem<VM_ExplorerNode> v) {
		vm.selectedNode().set( v != null ? v.getValue() : null);
	}
	
	private void handleSelectedItemsChange(Change<? extends TreeItem<VM_ExplorerNode>> c) {
		List<VM_ExplorerNode> list = c.getList().stream()
			.map( t -> t.getValue()).collect(Collectors.toList());
		vm.selectedNodes().setAll(list);
	}
	
	static class ContainerTreeItem extends LazyTreeItem<VM_ExplorerNode> {

		public ContainerTreeItem(VM_ContainerNode value) {
			super(value, ModuleExplorer::createChildren);
		}
	}
	
	static class FileTreeItem extends TreeItem<VM_ExplorerNode> {
		public FileTreeItem(VM_FileNode value) {
			super(value);
		}
	}
	
	static class ModuleExplorerTreeCell extends TreeCell<VM_ExplorerNode> {
		private final List<String> currentStyleClass = new ArrayList<String>();
		
		public ModuleExplorerTreeCell(TreeView<VM_ExplorerNode> v) {
			
		}
		
		@Override
		protected void updateItem(VM_ExplorerNode item, boolean empty) {
			super.updateItem(item, empty);
			
			textProperty().unbind();
			
			if( currentStyleClass != null ) {
				getStyleClass().removeAll(currentStyleClass);
				currentStyleClass.clear();
			}
			
			if( item instanceof VM_ContainerNode ) {
				currentStyleClass.add("folder-"+((VM_ContainerNode) item).type().getValue().name().toLowerCase());
			} else if( item instanceof VM_FileNode ) {
				currentStyleClass.add("file");
			}
			
			if( item != null && ! empty ) {
				textProperty().bind(item.label());
				ImageView view = new ImageView();
				setGraphic(view);
				
				if( ! currentStyleClass.isEmpty() ) {
					getStyleClass().addAll(currentStyleClass);
				}
			} else {
				setText(null);
				setGraphic(null);
			}
		}
	}
	
	private static ObservableList<TreeItem<VM_ExplorerNode>> createChildren(TreeItem<VM_ExplorerNode> parent) {
		ObservableList<TreeItem<VM_ExplorerNode>> rv = FXCollections.observableArrayList();
		VM_ContainerNode c = (VM_ContainerNode) parent.getValue();
		FXBindings.bindContent(rv, c.children(), n -> {
			if( n instanceof VM_FileNode ) {
				return new FileTreeItem((VM_FileNode) n);
			} else if( n instanceof VM_ContainerNode ) {
				return new ContainerTreeItem((VM_ContainerNode)n);
			} else {
				return new TreeItem<>(null);
			}
		});
		
		return rv;
	}
}
