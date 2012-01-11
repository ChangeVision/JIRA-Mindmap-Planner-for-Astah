package com.change_vision.astah.extension.plugin.mindmapplanner.view.component;

import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JProgressBar;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.change_vision.astah.extension.plugin.mindmapplanner.Activator;
import com.change_vision.jude.api.inf.ui.IMessageDialogHandler;

@SuppressWarnings("serial")
public class JProgressBarDialog extends JDialog implements IProgress {
	private static final Logger logger = LoggerFactory.getLogger(JProgressBarDialog.class);
	private static final Marker marker = MarkerFactory.getMarker("dialog");
	
	private JProgressBar bar;
	private IMessageDialogHandler dialogHandler;

	public JProgressBarDialog(Frame parent) {
		super(parent,true);
		bar = new JProgressBar();
		bar.setIndeterminate(true);
		bar.setStringPainted(true);
		bar.setString("test");
		getContentPane().add(bar);
		setLocationRelativeTo(parent);
		pack();
	}
	
	public void setMessage(String message) {
		bar.setString(message);
	}
	
	@Override
	public void showErrorMessage(Exception e, String message) {
		if(dialogHandler == null) {
			dialogHandler = Activator.getMessageHandler();
		}
		if(logger.isErrorEnabled(marker)) {
			logger.error("error has occured.",e);
		}
		dialogHandler.showErrorMessage(getParent(), message);
	}

}