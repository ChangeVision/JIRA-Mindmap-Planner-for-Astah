package com.change_vision.astah.extension.plugin.mindmapplanner.action;

import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Window;

import javax.swing.JOptionPane;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.astah.extension.plugin.mindmapplanner.Messages;
import com.change_vision.astah.extension.plugin.mindmapplanner.view.ImportDialog;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;
import com.github.jira.commons.client.JiraClient;
import com.github.jira.commons.exception.JiraRequestException;

public class ImportAction implements IPluginActionDelegate {
	private static final Logger logger = LoggerFactory.getLogger(ImportAction.class);

	public Object run(IWindow window) throws UnExpectedException {
		final Window parent = window.getParent();
		
		parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {	
			checkProjectState();
			showDialog(parent);
		} catch (ProjectNotFoundException e) {
			JOptionPane.showMessageDialog(parent,
					Messages.getMessage("warning.project_is_not_opened"),
					"Warning", JOptionPane.WARNING_MESSAGE);
		} catch (JiraRequestException e) {
			logger.error(Messages.getMessage("alert.jira_request_error"), e);
			JOptionPane.showMessageDialog(parent,
					Messages.getMessage("alert.jira_request_error"), "Alert",
					JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			logger.error(Messages.getMessage("alert.unexpected_error"), e);
			JOptionPane.showMessageDialog(parent,
					Messages.getMessage("alert.unexpected_error"), "Alert",
					JOptionPane.ERROR_MESSAGE);
			throw new UnExpectedException();
		} finally {
			parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		
		return null;
	}

	private void checkProjectState() throws ClassNotFoundException, ProjectNotFoundException {
		ProjectAccessor projectAccessor = ProjectAccessorFactory.getProjectAccessor();
		projectAccessor.getProject();
	}

	private void showDialog(final Window parent) {
		ImportDialog dialog = new ImportDialog((Frame) parent, true, new JiraClient());
		dialog.setLocationByPlatform(true);
		dialog.setVisible(true);
	}
}
