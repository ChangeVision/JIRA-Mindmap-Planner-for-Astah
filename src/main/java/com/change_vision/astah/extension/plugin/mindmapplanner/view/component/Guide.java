package com.change_vision.astah.extension.plugin.mindmapplanner.view.component;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.change_vision.astah.extension.plugin.mindmapplanner.Messages;


@SuppressWarnings("serial")
public class Guide extends JPanel {
	private Image image;
	
	public Guide() {
        ImageIcon icon = new ImageIcon(getClass().getResource(Messages.getMessage("ImportDialog.guide.image")));
    	image = icon.getImage();
	}
	
	@Override
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, this);
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}
}