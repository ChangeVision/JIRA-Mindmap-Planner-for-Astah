package com.change_vision.astah.extension.plugin.mindmapplanner.util;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IERModel;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;

public class AstahProjectUtils {
	public static TreeModel getCurrentProjectTreeModel() throws ProjectNotFoundException, ClassNotFoundException {
		IModel project = ProjectAccessorFactory.getProjectAccessor().getProject();
		return getProjectTreeModel(project);
	}
	
	public static TreeModel getProjectTreeModel(IModel project) {
		DefaultMutableTreeNode rootNode = getTreeNode(project);
		sort(rootNode);
		
		DefaultTreeModel model = new DefaultTreeModel(rootNode);
		return model;
	}
	
	private static void sort(DefaultMutableTreeNode root) {
		for (int i = 0; i < root.getChildCount(); i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
			UserObject userObject = (UserObject) child.getUserObject();
			String label = userObject.toString();
			
			for (int j = 0; j < i; j++) {
				DefaultMutableTreeNode prev = (DefaultMutableTreeNode) root.getChildAt(j);
				Object prevUserObject = prev.getUserObject();
				String prevLabel = prevUserObject.toString();
				
				if (label.compareToIgnoreCase(prevLabel) < 0) {
					root.insert(child, j);
					root.insert(prev, i);
				}
			}
			
			if(child.getChildCount() > 0) { 
				sort(child);
			}
		}
	}
	
	private static DefaultMutableTreeNode getTreeNode(IPackage target) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(new UserObject(target, target.getName()));
		INamedElement[] elements = target.getOwnedElements();
		for (INamedElement element : elements) {
			if (!(element instanceof IERModel) && element instanceof IPackage) {
				DefaultMutableTreeNode child = getTreeNode((IPackage) element);
				node.add(child);
			}
		}
		return node;
	}
	
//	public static void main(String[] args) throws Throwable {
//		ProjectAccessor accessor = ProjectAccessorFactory.getProjectAccessor();
//		accessor.open("mm.asta");
//		IModel project = accessor.getProject();
//		TreeModel projectTreeModel = getProjectTreeModel(project);
//		accessor.close();
//	}
	
	public static class UserObject {
		IPackage model;
		String label;
		
		UserObject(IPackage model, String label) {
			this.model = model;
			this.label = label;
		}
		
		public IPackage getModel() {
			return model;
		}
		
		@Override public String toString() {
			return label;
		}
	}
}
