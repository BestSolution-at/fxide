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
package at.bestsolution.fxide.base.vm.resources;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.fx.core.ObjectSerializer;
import org.eclipse.fx.core.ThreadSynchronize;
import org.eclipse.fx.core.bindings.FXBindings;
import org.eclipse.fx.core.command.Command;
import org.eclipse.fx.core.command.CommandService;
import org.eclipse.fx.core.di.ContextValue;
import org.eclipse.fx.core.log.Logger;
import org.eclipse.fx.core.log.LoggerCreator;
import org.eclipse.fx.core.observable.FXObservableUtil;

import at.bestsolution.fxide.base.BaseConstants;
import at.bestsolution.fxide.base.ModuleExplorer;
import at.bestsolution.fxide.base.vm.VM_ModuleExplorer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RS_VM_ModuleExplorer implements VM_ModuleExplorer {
	private static Logger LOGGER;

	private static Logger getLogger() {
		if( LOGGER == null ) {
			LOGGER = LoggerCreator.createLogger(ModuleExplorer.class);
		}
		return LOGGER;
	}
	
	private final ContainerNode root;
	
	private ResourceHelper resourceHelper;
	
	private ObjectProperty<VM_ExplorerNode> selectedNode = new SimpleObjectProperty<>(this,"selectedNode");
	
	private ObservableList<VM_ExplorerNode> selectedNodes = FXCollections.observableArrayList();
	
	private static final boolean PACK_PACKAGES = false;
	
	private final Map<Path, VM_ExplorerNode> resourceMap = new HashMap<>();
	
	private final ThreadSynchronize threadSync;

	private final Command<Void> openEditorForSelectedNodes;
	
	@Inject
	@ContextValue(BaseConstants.CTX_PACKAGE_EXPLORER_SELECTION)
	private Property<IResource> selection;
	
	private FXObservableUtil.Instance tracker = new FXObservableUtil.Instance();
	
	@Inject
	public RS_VM_ModuleExplorer(IWorkspace workspace, 
			ResourceHelper resourceHelper, 
			ThreadSynchronize threadSync, 
			CommandService cmdService,
			ObjectSerializer serializer) {
		this.resourceHelper = resourceHelper;
		this.threadSync = threadSync;
		
		try {
			workspace.getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //FIXME Remove that

		workspace.addResourceChangeListener(this::handleResourceChanged);
		
		root = new ContainerNode(null,ContainerType.WORKSPACE, workspace.getRoot(), resourceMap, resourceHelper);
		openEditorForSelectedNodes = cmdService.<Void>createCommand("at.bestsolution.fxide.base.vm.resources.e4.command.openfiles").orElse(null);
		
		tracker.onChange(selectedNodes, c -> {
			List<String> l;
			l = c.getList().stream()
				.filter( n -> n instanceof FileNode)
				.map( n -> (FileNode)n)
				.map( n -> "module-file:" + n.file.getProject().getName() + "/" + n.file.getProjectRelativePath())
				.collect(Collectors.toList());
			
			openEditorForSelectedNodes.parameters().put("fileURIs", serializer.serializeCollection(l, String.class));
		});
		tracker.onChange(selectedNode, v -> {
			if( v != null ) {
				if( v instanceof ContainerNode ) {
					selection.setValue(((ContainerNode) v).container);
				} else if( v instanceof FileNode ) {
					selection.setValue(((FileNode) v).file);
				}
			}
		});
	}
	
	@Override
	public ObservableList<VM_ExplorerNode> selectedNodes() {
		return selectedNodes;
	}
	
	@Override
	public Command<Void> openEditorForSelectedNodes() {
		return openEditorForSelectedNodes;
	}
	
	@Override
	public ObjectProperty<VM_ExplorerNode> selectedNode() {
		return selectedNode;
	}
	
	private void handleResourceChanged(IResourceChangeEvent event) {
		Runnable code = () -> {
			try {
				if( event.getDelta() != null ) {
					event.getDelta().accept(this::visitDelta);
				}
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
		getLogger().debugf("Visiting delta %s",delta);
		if( delta.getKind() == IResourceDelta.ADDED ) {
			if( delta.getResource() instanceof IProject ) {
				if( resourceHelper.isRootProject((IProject) delta.getResource()) ) {
					getLogger().debug("Adding a root project");
					root._folders().add(new ContainerNode(root,ContainerType.PROJECT, (IContainer) delta.getResource(), resourceMap, resourceHelper));
				} else {
					getLogger().debug("Adding a sub project");
					ContainerNode item = (ContainerNode) resourceMap.get(Paths.get(delta.getResource().getLocationURI()));
					if( item != null ) {
						ContainerNode owner = (ContainerNode) item.parent();
						ContainerNode i = new ContainerNode(owner,ContainerType.PROJECT, (IContainer) delta.getResource(), resourceMap, resourceHelper);
						owner._folders().remove(item);
						owner._folders().add(i);
					}
				}
				return true;
			}
			Optional<ContainerNode> opItem = getParentItem(delta.getResource());

			if( ! opItem.isPresent() ) {
				return true;
			}

			ContainerNode item = opItem.get();
			// Children not yet loaded
			if( item.folders == null && item.files == null ) {
				return false;
			}

			if( delta.getResource() instanceof IFile ) {
				FileNode newItem = new FileNode(item,(IFile) delta.getResource(),resourceMap);

				Comparator<FileNode> cmp = Comparator.comparing( i -> i.label().get() );
				Optional<FileNode> referenceItem = item._files()
					.stream()
					.map( i -> (FileNode)i)
					.filter( i -> i.file instanceof IFile)
					.filter( i -> cmp.compare(i, newItem) > 0)
					.findFirst();

				if( referenceItem.isPresent() ) {
					item.files().add(item.files().indexOf(referenceItem.get()),newItem);
				} else {
					item.files().add(newItem);
				}
			} else {
				ContainerNode containerItem = (ContainerNode) item;
				if( containerItem.type.get() == ContainerType.SOURCE
						|| containerItem.type.get() == ContainerType.PACKAGE
						|| containerItem.type.get() == ContainerType.TEST) {
					item._folders().add(new ContainerNode(item,ContainerType.PACKAGE, (IContainer) delta.getResource(), resourceMap, resourceHelper));
				} else {
					ContainerNode pi = Stream.of(ResourcesPlugin.getWorkspace().getRoot().getProjects())
							.filter( p -> p.getLocation().equals(delta.getResource().getLocation()))
							.findFirst()
							.map( p -> new ContainerNode(item,ContainerType.PROJECT, p, resourceMap,resourceHelper) )
							.orElseGet( () -> new ContainerNode(item,ContainerType.DEFAULT, (IContainer) delta.getResource(), resourceMap, resourceHelper));
					item._folders().add(pi);
				}
			}

			if( PACK_PACKAGES ) {
				if( item.parent() == null ) {
					System.err.println("This is a virtual node");
				}
			}

		} else if( delta.getKind() == IResourceDelta.REMOVED ) {
			if( delta.getResource() instanceof IProject ) {
				root._folders().removeIf( i -> delta.getResource().equals(((ContainerNode)i).container) );
			}
			Optional<ContainerNode> opItem = getParentItem(delta.getResource());

			if( opItem.isPresent() ) {
				VM_ExplorerNode item = (ContainerNode) resourceMap.get(Paths.get(delta.getResource().getLocationURI()));
				if( item != null ) {
					opItem.get()._folders().remove(item);
					opItem.get()._files().remove(item);
					if( item instanceof ContainerNode ) {
						cleanItemRec((ContainerNode) item);	
					}
				}
			}
		}
		return true;
	}
	
	private Optional<ContainerNode> getParentItem(IResource resource) {
		if( resource.getLocationURI() != null ) {
			ContainerNode item = (ContainerNode) resourceMap.get(Paths.get(resource.getLocationURI()).getParent());
			if( item != null ) {
				if( item.container.equals(resource.getParent()) ) {
					return Optional.of(item);
				}
			}
		}
		return Optional.empty();
	}
	
	private void cleanItemRec(ContainerNode item) {
		if( item.container != null ) {
			resourceMap.values().remove(item);
			for( VM_ContainerNode c : item._folders() ) {
				cleanItemRec((ContainerNode) c);
			}
			resourceMap.values().removeAll(item._files());
		}
	}
	
	@Override
	public VM_ContainerNode root() {
		return root;
	}
	
	static class ContainerNode implements VM_ContainerNode {
		private final ObjectProperty<ContainerType> type = new SimpleObjectProperty<>(this, "type");
		private final StringProperty label = new SimpleStringProperty(this, "label");
		private ObservableList<VM_ExplorerNode> children;
		private ObservableList<VM_ContainerNode> folders;
		private ObservableList<VM_FileNode> files;
		
		private ObservableList<ContainerNode> emptyParentContainers = FXCollections.observableArrayList();
		private Map<Path, VM_ExplorerNode> resourceMap;
		private final IContainer container;
		private final ResourceHelper resourceHelper;
		private final ContainerNode parent;
		
		public ContainerNode(ContainerNode parent, ContainerType type, IContainer container, Map<Path, VM_ExplorerNode> resourceMap, ResourceHelper resourceHelper) {
			this.type.set(type);
			this.container = container;
			this.label.set(container.getName());
			this.resourceHelper = resourceHelper;
			this.parent = parent;
			this.resourceMap = resourceMap;
			resourceMap.put(Paths.get(container.getLocationURI()), this);
		}
		
		@Override
		public VM_ContainerNode parent() {
			return parent;
		}
		
		@Override
		public Property<ContainerType> type() {
			return type;
		}

		@Override
		public StringProperty label() {
			return label;
		}
		
		ObservableList<VM_ContainerNode> _folders() {
			if( folders == null ) {
				folders = FXCollections.observableArrayList();
				folders.addAll(createChildren(this, resourceMap, resourceHelper));
			}
			return folders;
		}
		
		@Override
		public ObservableList<VM_ContainerNode> folders() {
			return _folders();
		}
		
		ObservableList<VM_FileNode> _files() {
			if( files == null ) {
				files = FXCollections.observableArrayList();
				try {
					files.addAll(Stream.of(container.members()).filter(m -> m instanceof IFile)
							.sorted(Comparator.comparing(m -> m.getName()))
							.map(i -> new FileNode(this,(IFile) i, resourceMap)).collect(Collectors.toList()));
				} catch (CoreException e) {
					getLogger().error("Failure while reading files from container '" + container + "'", e);
				}
			}
			System.err.println("FILES: " + files);
			return files;
		}
		
		@Override
		public ObservableList<VM_FileNode> files() {
			return _files();
		}
		
		@Override
		public ObservableList<VM_ExplorerNode> children() {
			if( children == null ) {
				children = FXBindings.concat(folders(),files());
			}
			return children;
		}
	}
	
	static class FileNode implements VM_FileNode {
		private final IFile file;
		private final ContainerNode parent;
		private StringProperty label = new SimpleStringProperty(this, "label");
		
		public FileNode(ContainerNode parent, IFile file, Map<Path, VM_ExplorerNode> resourceMap) {
			this.file = file;
			this.parent = parent;
			label.set(file.getName());
			resourceMap.put(Paths.get(file.getLocationURI()), this);
		}
		
		@Override
		public VM_ContainerNode parent() {
			return parent;
		}
		
		@Override
		public StringProperty label() {
			return label;
		}
	}
	
	static List<ContainerNode> createChildren(ContainerNode parentItem, Map<Path, VM_ExplorerNode> resourceMap, ResourceHelper helper) {
		ObservableList<ContainerNode> rv = FXCollections.observableArrayList();
		ContainerNode containerItem = (ContainerNode)parentItem;
		IContainer container = (IContainer) containerItem.container;

		try {
			Stream<IContainer> memberStream = Stream.of(container.members()).filter( m -> m instanceof IContainer).map( m -> (IContainer)m);
			if( containerItem.type.get() == ContainerType.WORKSPACE ) {
				memberStream = memberStream.filter( m -> helper.isRootProject((IProject) m));
			}
			rv.addAll(memberStream.sorted(Comparator.comparing( m -> m.getName())).map( i -> {
				ContainerType type = ContainerType.DEFAULT;
				List<ContainerNode> emptyContainers = new ArrayList<>();
				if( containerItem.type.get() == ContainerType.WORKSPACE ) {
					type = ContainerType.PROJECT;
				} else if( containerItem.type.get() == ContainerType.SOURCE
						|| containerItem.type.get() == ContainerType.PACKAGE
						|| containerItem.type.get() == ContainerType.TEST) {
					type = ContainerType.PACKAGE;

					if( PACK_PACKAGES ) {
						try {
							while( i.members().length == 1 && i.members()[0] instanceof IFolder ) {
								emptyContainers.add(new ContainerNode(parentItem,type, i, resourceMap, helper));
								i = (IContainer) i.members()[0];
							}
						} catch (CoreException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} else if( containerItem.type.get() == ContainerType.DEFAULT ) {
					if( "java".equals(i.getName()) && i.getParent() != null ) {
						if( i.getParent().getName().equals("main") ) {
							type = ContainerType.SOURCE;
						} else {
							type = ContainerType.TEST;
						}
					}
				} else if( containerItem.type.get() == ContainerType.PROJECT ) {
					IContainer fi = i;
					ContainerNode pi = Stream.of(i.getWorkspace().getRoot().getProjects())
						.filter( p -> p.getLocation().equals(fi.getLocation()))
						.findFirst()
						.map( p -> new ContainerNode(parentItem,ContainerType.PROJECT, p, resourceMap,helper) )
						.orElse(null);
					if( pi != null ) {
						return pi;
					}
				}
				ContainerNode item = new ContainerNode(parentItem,type, (IContainer) i, resourceMap, helper);
				item.emptyParentContainers.setAll(emptyContainers);
				return item;
			}).collect(Collectors.toList()));
		} catch (CoreException e) {
			getLogger().error("Failure while reading folders from container '"+container+"'",e);
		}

		return rv;
	}
}
