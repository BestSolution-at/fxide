package at.bestsolution.fxide.base.vm;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import org.eclipse.fx.core.command.Command;

public interface VM_ModuleExplorer {
	enum ContainerType {
		WORKSPACE,
		PROJECT,
		SOURCE,
		PACKAGE,
		TEST,
		DEFAULT
	}
	
	public VM_ContainerNode root();
	public ObjectProperty<VM_ExplorerNode> selectedNode();
	public ObservableList<VM_ExplorerNode> selectedNodes();
	public Command<Void> openEditorForSelectedNodes();
	
	public interface VM_ExplorerNode {
		public VM_ContainerNode parent();
		public StringProperty label();
	}
	
	public interface VM_ContainerNode extends VM_ExplorerNode {
		public Property<ContainerType> type();
		public ObservableList<VM_ExplorerNode> children();
		public ObservableList<VM_ContainerNode> folders();
		public ObservableList<VM_FileNode> files();
	}
	
	public interface VM_FileNode extends VM_ExplorerNode {
		
	}
}
