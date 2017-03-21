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
package at.bestsolution.fxide.jdt.parts;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.fx.code.editor.services.EditorOpener;
import org.eclipse.fx.core.Subscription;
import org.eclipse.fx.core.ThreadSynchronize;
import org.eclipse.fx.core.di.ContextValue;
import org.eclipse.fx.core.log.Logger;
import org.eclipse.fx.core.log.LoggerCreator;
import org.eclipse.fx.core.observable.FXObservableUtil;
import org.eclipse.fx.ui.controls.tree.LazyTreeItem;

import at.bestsolution.fxide.jdt.JDTConstants;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

@SuppressWarnings("restriction")
public class PackageExplorer {
	private static Logger LOGGER;

	private static Logger getLogger() {
		if( LOGGER == null ) {
			LOGGER = LoggerCreator.createLogger(PackageExplorer.class);
		}
		return LOGGER;
	}

	private final Property<IResource> packageExplorerSelection;
	private List<Subscription> subscriptions = new ArrayList<>();
	private TreeView<IResource> viewer;
	private final EditorOpener editorOpener;

	@Inject
	private ThreadSynchronize threadSync;

	@Inject
	public PackageExplorer(@ContextValue(JDTConstants.CTX_PACKAGE_EXPLORER_SELECTION) Property<IResource> packageExplorerSelection, EditorOpener editorOpener) {
		this.packageExplorerSelection = packageExplorerSelection;
		this.editorOpener = editorOpener;
	}

	@PreDestroy
	void cleanup() {
		subscriptions.forEach( Subscription::dispose );
	}

	@PostConstruct
	public void init(BorderPane parent, IWorkspace workspace) {
		try {
			workspace.getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //FIXME Remove that

		workspace.addResourceChangeListener(this::handleResourceChanged);

		viewer = new TreeView<>();
		viewer.setCellFactory( (v) -> new ResourceTreeCell());
		viewer.setRoot(new ContainerItem(ContainerType.WORKSPACE, workspace.getRoot()));
		viewer.setShowRoot(false);
		viewer.setOnMouseClicked(this::handleClick);
		FXObservableUtil.onChange(packageExplorerSelection, this::setTreeSelection);
		FXObservableUtil.onChange(viewer.getSelectionModel().selectedItemProperty(), o -> {
			if( o != null ) {
				packageExplorerSelection.setValue(o.getValue());
			} else {
				packageExplorerSelection.setValue(null);
			}
		});

		parent.setCenter(viewer);
	}

	private void handleResourceChanged(IResourceChangeEvent event) {
		Runnable code = () -> {
			try {
				event.getDelta().accept(this::visitDelta);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};

		if( threadSync.isCurrent() ) {
			code.run();
		} else {
			threadSync.asyncExec( code );
		}
	}

	public boolean visitDelta(IResourceDelta delta) throws CoreException {
		if( delta.getKind() == IResourceDelta.ADDED ) {
			Optional<TreeItem<IResource>> opItem = getParentItem(delta.getResource());

			if( ! opItem.isPresent() ) {
				return true;
			}

			TreeItem<IResource> item = opItem.get();

			if( delta.getResource() instanceof IFile ) {
				TreeItem<IResource> newItem = new TreeItem<IResource>(delta.getResource());

				Comparator<TreeItem<IResource>> cmp = Comparator.comparing( i -> i.getValue().getName() );
				Optional<TreeItem<IResource>> referenceItem = item.getChildren()
					.stream()
					.filter( i -> i.getValue() instanceof IFile)
					.filter( i -> cmp.compare(i, newItem) > 0)
					.findFirst();

				if( referenceItem.isPresent() ) {
					item.getChildren().add(item.getChildren().indexOf(referenceItem.get()),newItem);
				} else {
					item.getChildren().add(newItem);
				}
			} else {
				ContainerItem containerItem = (ContainerItem) item;
				if( containerItem.type == ContainerType.SOURCE
						|| containerItem.type == ContainerType.PACKAGE
						|| containerItem.type == ContainerType.TEST) {
					item.getChildren().add(new ContainerItem(ContainerType.PACKAGE, (IContainer) delta.getResource()));
				}
			}
		} else if( delta.getKind() == IResourceDelta.REMOVED ) {
			Optional<TreeItem<IResource>> opItem = getParentItem(delta.getResource());

			if( opItem.isPresent() ) {
				opItem.get().getChildren().removeIf( i -> i.getValue().equals(delta.getResource()));
			} else {
				System.err.println("COULD NOT FIND PARENT");
			}
		}
		return true;
	}

	private void handleClick(MouseEvent e) {
		if( e.getClickCount() > 1
				&& viewer.getSelectionModel().getSelectedItem() != null
				&& viewer.getSelectionModel().getSelectedItem().getValue() instanceof IFile) {
			IFile f = (IFile) viewer.getSelectionModel().getSelectedItem().getValue();
			editorOpener.openEditor("module-file:" + f.getProject().getName() + "/" + f.getProjectRelativePath());
		}
	}

	private Optional<TreeItem<IResource>> getParentItem(IResource resource) {
		TreeItem<IResource> item = viewer.getRoot();
		List<IContainer> path = new ArrayList<>();
		IContainer c = resource.getParent();
		while( c != null && ! (c instanceof IProject) ) {
			path.add(c);
			c = c.getParent();
		}
		path.add(c);

		for( int i = path.size() - 1; i >= 0; i-- ) {
			IContainer r = path.get(i);
			Optional<TreeItem<IResource>> first = item.getChildren()
					.stream()
					.filter( it -> it.getValue() != null)
					.filter( it -> it.getValue().equals(r) ).findFirst();
			if( ! first.isPresent() ) {
				return Optional.empty();
			}
			item = first.get();
		}
		return Optional.of(item);
	}

	private TreeItem<IResource> expandParentPath(IResource resource) {
		TreeItem<IResource> item = viewer.getRoot();

		List<IContainer> path = new ArrayList<>();
		IContainer c = resource.getParent();
		while( c != null && ! (c instanceof IProject) ) {
			path.add(c);
			c = c.getParent();
		}
		path.add(c);

		for( int i = path.size() - 1; i >= 0; i-- ) {
			IContainer r = path.get(i);
			Optional<TreeItem<IResource>> first = item.getChildren().stream().filter( it -> it.getValue().equals(r) ).findFirst();
			if( ! first.isPresent() ) {
				break;
			}
			item = first.get();
			item.setExpanded(true);
		}
		return item;
	}

	private void setTreeSelection(IResource resource) {
		TreeItem<IResource> item = expandParentPath(resource);

		item.getChildren().stream().filter( it -> it.getValue() == resource).findFirst().ifPresent( it -> {
			viewer.getSelectionModel().select(it);
		} );
	}

	static class ResourceTreeCell extends TreeCell<IResource> {
		private List<String> currentStyleClass = new ArrayList<String>();

		@Override
		protected void updateItem(IResource item, boolean empty) {
			super.updateItem(item, empty);

			if( currentStyleClass != null ) {
				getStyleClass().removeAll(currentStyleClass);
				currentStyleClass.clear();
			}

			if( item != null && ! empty ) {
				if( getTreeItem() instanceof ContainerItem ) {
					ContainerItem c = (ContainerItem) getTreeItem();
					setText(c.getValue().getName());
					currentStyleClass.add("folder-"+c.type.name().toLowerCase());
				} else {
					setText(item.getName());
					currentStyleClass.add("file");
					if( item.getFileExtension() != null ) {
						currentStyleClass.add(item.getFileExtension());
					}

				}

				if( item.getName().startsWith(".") || item.isDerived() ) {
					currentStyleClass.add("hidden");
				}

				if( ! currentStyleClass.isEmpty() ) {
					getStyleClass().addAll(currentStyleClass);
				}
				ImageView view = new ImageView();
				setGraphic(view);
			} else {
				setText(null);
				setGraphic(null);
			}
		}
	}

	static class ContainerItem extends LazyTreeItem<IResource> {
		private final ContainerType type;

		public ContainerItem(ContainerType type, IContainer value) {
			super(value,  self -> createChildren(self));
			this.type = type;
		}
	}

	static List<TreeItem<IResource>> createChildren(TreeItem<IResource> parentItem) {
		ObservableList<TreeItem<IResource>> rv = FXCollections.observableArrayList();
		ContainerItem containerItem = (ContainerItem)parentItem;
		IContainer container = (IContainer) containerItem.getValue();

		try {
			rv.addAll(Stream.of(container.members()).filter( m -> m instanceof IContainer).sorted(Comparator.comparing( m -> m.getName())).map( i -> {
				ContainerType type = ContainerType.DEFAULT;

				if( containerItem.type == ContainerType.WORKSPACE ) {
					type = ContainerType.PROJECT;
				} else if( containerItem.type == ContainerType.SOURCE
						|| containerItem.type == ContainerType.PACKAGE
						|| containerItem.type == ContainerType.TEST) {
					type = ContainerType.PACKAGE;
				} else if( containerItem.type == ContainerType.DEFAULT ) {
					if( "java".equals(i.getName()) && i.getParent() != null ) {
						if( i.getParent().getName().equals("main") ) {
							type = ContainerType.SOURCE;
						} else {
							type = ContainerType.TEST;
						}
					}
				}
				return new ContainerItem(type, (IContainer) i);
			}).collect(Collectors.toList()));
		} catch (CoreException e) {
			getLogger().error("Failure while reading folders from container '"+container+"'",e);
		}

		try {
			rv.addAll(Stream.of(container.members()).filter( m -> m instanceof IFile).sorted(Comparator.comparing( m -> m.getName())).map(i -> new TreeItem<>(i)).collect(Collectors.toList()));
		} catch (CoreException e) {
			getLogger().error("Failure while reading files from container '"+container+"'",e);
		}

		return rv;
	}

	enum ContainerType {
		WORKSPACE,
		PROJECT,
		SOURCE,
		PACKAGE,
		TEST,
		DEFAULT
	}
}
