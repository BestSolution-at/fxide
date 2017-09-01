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
