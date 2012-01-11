package com.change_vision.astah.extension.plugin.mindmapplanner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

import com.change_vision.jude.api.inf.model.IMindMapDiagram;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

// ProGuardによる難読化で一部リフレクションが機能しない
@Deprecated
public class MindmapCreatorUtils {
	private static final Logger logger = (Logger) LoggerFactory.getLogger(MindmapCreatorUtils.class);
	
	public static boolean canSetSuppressReize() {
		boolean canSetSuppressReize = false;
		if (setSuppressResizeClassLoader == null) {
			try {
				initSetSuppressResize();
				canSetSuppressReize = true;
			} catch (Exception e) {
				logger.debug("Can't invoke setSuppressReize().");
			}
		} else {
			if (setSuppressResize != null) {
				canSetSuppressReize = true;
			}
		}

		return canSetSuppressReize;
	}

	public static boolean topicResizable() {
		boolean resizable = false;
		if (commandClassLoader == null) {
			try {
				initCommand();
				resizable = true;
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("Can't invoke resize().");
			}
		} else {
			if (command != null) {
				resizable = true;
			}
		}
		return resizable;
	}

	public static boolean setSuppressResizeMethod(boolean suppressResize) {
		if (!canSetSuppressReize()) {
			return false;
		}
		
		boolean result = false;
		try {
			initSetSuppressResize();
			setSuppressResize.invoke(null, suppressResize);
			result = true;
		} catch (Exception e) {
			logger.debug("Can't invoke setSuppressResizeMethod().");
		}
		return result;
	}
	
//	public static boolean resize2(IMindMapDiagram diagram) {
//		boolean result = false;
//		try {
//			initCommand();
//
//			Object o = diagramField.get(diagram);
//			UMindMapDiagram uDiagram = (UMindMapDiagram) o;
//			List<IMMTopicPresentation> topics = new ArrayList<IMMTopicPresentation>();
//			IMMTopicPresentation root = uDiagram.getRoot();
//			topics.add(root);
//			topics.addAll(root.getAllChildren());
//			for (IMMTopicPresentation topic : topics) {
//				topic.resize();
//			}
//			setLayouted(root);
//			uDiagram.startLayout();
//			result = true;
//		} catch (Exception e) {
//			logger.debug("Can't invoke applySelectedTemplate().");
//		}
//		return result;
//	}
//	
//	private static void setLayouted(IMMTopicPresentation topic) {
//		List children = topic.getChildren();
//		for (int i = 0; i < children.size(); i++) {
//			IMMTopicPresentation child = (IMMTopicPresentation) children.get(i);
//			child.setFixHorizontalPosition(false);
//			setLayouted(child);
//		}
//	}
	
	public static boolean resize(IMindMapDiagram diagram) {
		if (!topicResizable()) {
			return false;
		}
		
		boolean result = false;
		try {
			initCommand();

			Object uDiagram = diagramField.get(diagram);
			setArgumentString.invoke(command, "2");
			applySelectedTemplate.invoke(command, uDiagram);
			result = true;
		} catch (Exception e) {
			logger.debug("Can't invoke applySelectedTemplate().");
		}
		return result;
	}

	private static void initSetSuppressResize() throws ClassNotFoundException, NoSuchMethodException {
		if (setSuppressResize == null) {
			setSuppressResizeClassLoader = ProjectAccessor.class.getClassLoader();
			Class<?> JomtPresentation = Class.forName("JP.co.esm.caddies.jomt.jmodel.JomtPresentation", false, setSuppressResizeClassLoader);
			setSuppressResize = JomtPresentation.getMethod("setSuppressResize", new Class[]{ boolean.class });
		}
	}

	private static void initCommand() throws ClassNotFoundException,
			NoSuchMethodException, NoSuchFieldException,
			InstantiationException, IllegalAccessException {
		if (command == null) {
			commandClassLoader = ProjectAccessor.class.getClassLoader();
			Class<?> diagramClass = Class.forName("com.change_vision.jude.api.imp.model.IMindMapDiagramImp", false, commandClassLoader);
			Class<?> uDiagramClass = Class.forName("JP.co.esm.caddies.uml.mindmap.UMindMapDiagram", false, commandClassLoader);
			Class<?> applyMindmapTemplateCommandClass = Class.forName("JP.co.esm.caddies.jomt.jcontrol.ApplyMindmapTemplateCommand", false, commandClassLoader);
			Class<?> applyTemplateFromPrjCommandClass = Class.forName("JP.co.esm.caddies.jomt.jcontrol.ApplyTemplateFromPrjCommand", false, commandClassLoader);
	
			setArgumentString = applyMindmapTemplateCommandClass.getMethod("setArgumentString",  new Class[]{ String.class });
			applySelectedTemplate = applyMindmapTemplateCommandClass.getDeclaredMethod("applySelectedTemplate",  new Class[]{ uDiagramClass });
			applySelectedTemplate.setAccessible(true);
			
			diagramField = diagramClass.getDeclaredField("uMindMapDiagram");
			diagramField.setAccessible(true);
			command = applyTemplateFromPrjCommandClass.newInstance();
		}
	}

	private static ClassLoader setSuppressResizeClassLoader;
	private static ClassLoader commandClassLoader;
	private static Method setSuppressResize;
	private static Method setArgumentString;
	private static Method applySelectedTemplate;
	private static Object command;
	private static Field diagramField;
}
