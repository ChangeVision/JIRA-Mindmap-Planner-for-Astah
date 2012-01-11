package com.change_vision.astah.extension.plugin.mindmapplanner;

import java.util.HashMap;

import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.model.IMindMapDiagram;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.presentation.INodePresentation;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;

public class MindmapWalker {
	public static void main(String[] args) throws Throwable {
		ProjectAccessor pa = ProjectAccessorFactory.getProjectAccessor();
		pa.open("mm2.asta");
		
		IModel project = pa.getProject();
		IDiagram[] diagrams = project.getDiagrams();
		for (IDiagram diagram : diagrams) {
			IMindMapDiagram mindmap = ((IMindMapDiagram) diagram);
			INodePresentation root = mindmap.getRoot();
			print(root);
			
			INodePresentation[] children = root.getChildren();
			for (INodePresentation child : children) {
				print(child);
			}
		}
		pa.close();
	}
	
	@SuppressWarnings({ "rawtypes" })
	private static void print(INodePresentation topic) {
		HashMap properties = topic.getProperties();
		String icons = (String) properties.get("icons");
		System.out.println(topic.getLabel() + " : " + icons + ", " + properties);
	}
}
