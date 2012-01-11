package com.change_vision.astah.extension.plugin.mindmapplanner;

import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.astah.extension.plugin.mindmapplanner.view.AlertEditionDialog;

public class EditionChecker {
	private AstahAPIHandler handler = new AstahAPIHandler();
	private static final Set<String> targetEditon = new HashSet<String>();

	static {
		targetEditon.add("professional");
		targetEditon.add("uml");
	}

	private static final Logger logger = LoggerFactory.getLogger(EditionChecker.class);

	public boolean hasError() {
		String edition = handler.getAstahEdition();
		if (targetEditon.contains(edition)) {
			return false;
		}
		logger.info(edition);
		showEndOfPeriodDialog();
		return true;
	}

	private void showEndOfPeriodDialog() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = handler.getMainFrame();
				AlertEditionDialog dialog = new AlertEditionDialog(frame);
				dialog.setModal(true);
				dialog.setVisible(true);
			}
		});
	}
}