package com.change_vision.astah.extension.plugin.mindmapplanner;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXB;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

import com.change_vision.astah.extension.plugin.mindmapplanner.usericon.MmUserIcon;
import com.change_vision.jude.api.inf.ui.IMessageDialogHandler;
import com.change_vision.jude.api.inf.ui.IMessageDialogHandlerFactory;

public class Activator implements BundleActivator {
	private static final Logger logger = (Logger) LoggerFactory.getLogger(Activator.class);
	
	private static IMessageDialogHandler messageHandler;
	private static ServiceTracker tracker;
	private ServiceReference reference;
	private AstahAPIHandler handler;

	public void start(BundleContext context) {
		EditionChecker checker = new EditionChecker();
		if (checker.hasError()) {
			return;
		}
		
		tracker = new ServiceTracker(context, IMessageDialogHandlerFactory.class.getName(), null);
		tracker.open();
		
		initializeMessageDialogHandler(context);
		
		try {
			handler = new AstahAPIHandler();
			File userIconXmlFile = new File(System.getProperty("user.home")
					+ File.separator + ".astah" + File.separator + handler.getAstahEdition(), MmUserIcon.FILE_NAME);
			
			initializeUserIcon(userIconXmlFile);
		} catch (Exception e) {
			logger.error("Initialization error: " + MmUserIcon.FILE_NAME);
			return;
		}
	}

	public void stop(BundleContext context) {
		if (tracker != null) {
			tracker.close();
		}
	}

	private void initializeUserIcon(File userIconXmlFile) throws IOException, FileNotFoundException {
		InputStream inputStreamForPlugin = MmUserIcon.class.getResourceAsStream(MmUserIcon.FILE_NAME);
		MmUserIcon mmUserIconForPlugin = MmUserIcon.unmarshall(inputStreamForPlugin);
		MmUserIcon mmUserIcon = (userIconXmlFile.canRead()) ? 
				MmUserIcon.unmarshall(new FileInputStream(userIconXmlFile))	: new MmUserIcon();
		if (!mmUserIcon.containsAll(mmUserIconForPlugin)) {
			mmUserIcon.merge(mmUserIconForPlugin);
			JAXB.marshal(mmUserIcon, new FileWriter(userIconXmlFile));
		}
	}

	public static IMessageDialogHandler getMessageHandler() {
		return messageHandler;
	}
	
	public static ServiceTracker getServiceTracker() {
		return tracker;
	}

	private void initializeMessageDialogHandler(BundleContext context) {
		reference = context.getServiceReference(IMessageDialogHandlerFactory.class.getName());
		IMessageDialogHandlerFactory factory = (IMessageDialogHandlerFactory)context.getService(reference);
		if (factory != null) {
			messageHandler = factory.createMessageDialogHandler(new Messages(), ".astah\\plugins\\mindmap-planner");
		}
		context.ungetService(reference);
	}
}
