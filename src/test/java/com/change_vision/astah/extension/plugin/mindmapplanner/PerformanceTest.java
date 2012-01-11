package com.change_vision.astah.extension.plugin.mindmapplanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.change_vision.astah.extension.plugin.mindmapplanner.model.FieldEnum;
import com.change_vision.astah.extension.plugin.mindmapplanner.usericon.UserIcon;
import com.change_vision.jude.api.inf.editor.MindmapEditor;
import com.change_vision.jude.api.inf.editor.TransactionManager;
import com.change_vision.jude.api.inf.model.IMindMapDiagram;
import com.change_vision.jude.api.inf.presentation.INodePresentation;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;

public class PerformanceTest {
	public static void main(String[] args) throws Throwable {
		ProjectAccessor pa = ProjectAccessorFactory.getProjectAccessor();
		MindmapEditor editor = pa.getDiagramEditorFactory().getMindmapEditor();
		
		pa.create("performance.asta");
		
		try {
//			Thread.sleep(8000);
			
			long start = System.currentTimeMillis();
			TransactionManager.beginTransaction();
			
			List<IMindMapDiagram> diagrams = new ArrayList<IMindMapDiagram>();
			Map<String, INodePresentation> topicMap = new HashMap<String, INodePresentation>();
			for (int x = 0; x < 10; x++) {
				String diagramName = "mmxxxxxxxxxxxxxxxxx" + x;
				IMindMapDiagram diagram = editor.createMindmapDiagram(pa.getProject(), diagramName);
				diagrams.add(diagram);
				
//				MindmapCreatorUtils.setSuppressResizeMethod(true);
				
				INodePresentation root = diagram.getRoot();
				topicMap.put(root.getID(), root);
				for (int i = 0; i < 6; i++) {
					String label = "childxxxxxxxxxxx\nxxxxxxxxxxxxxxx" + i;
					INodePresentation child = editor.createTopic(root, label, (i % 2 == 0) ? "right" : "left");
					child.createURLHyperlink("http://www.google.com", "Google");
					topicMap.put(child.getID(), child);
					for (int j = 0; j < 10; j++) {
						label = "grandChildxxxxxxxxxxx\nxxxxxxxxxxxxxxx" + i + j;
						INodePresentation grandChild = editor.createTopic(child, label);
						grandChild.createURLHyperlink("http://www.google.com", "Google");
						grandChild.setProperty("icons", UserIcon.UUID_PREFIX + FieldEnum.ASSIGNEE.getIcon());
						topicMap.put(grandChild.getID(), grandChild);
						for (int k = 0; k < 5; k++) {
							label = "endxxxxxxxxxxx\nxxxxxxxxxxxxxxx" + i + j + k;
							INodePresentation theEnd = editor.createTopic(grandChild, label);
							theEnd.createURLHyperlink("http://www.google.com", "Google");
							theEnd.setProperty("icons", UserIcon.UUID_PREFIX + FieldEnum.STATUS.getIcon());
							topicMap.put(theEnd.getID(), theEnd);
						}
					}
				}
				
//				MindmapCreatorUtils.setSuppressResizeMethod(false);
//				MindmapCreatorUtils.resize(diagram);
			}
			
			TransactionManager.endTransaction();
	
			System.out.println((System.currentTimeMillis() - start) + "ms");
			
			pa.save();
			pa.close();
		} catch (Throwable e) {
			if (TransactionManager.isInTransaction()) {
				TransactionManager.abortTransaction();
			}
			
			e.printStackTrace();
		}
	}
}
