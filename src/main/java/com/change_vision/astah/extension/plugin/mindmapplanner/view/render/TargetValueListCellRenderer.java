package com.change_vision.astah.extension.plugin.mindmapplanner.view.render;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

@SuppressWarnings("serial")
public class TargetValueListCellRenderer extends JLabel implements ListCellRenderer {
	private static final Color evenColor = new Color(240, 240, 255);
	
	public TargetValueListCellRenderer() {
		setOpaque(true);
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		setText(value.toString());
		if (isSelected) {
			setForeground(list.getSelectionForeground());
			setBackground(list.getSelectionBackground());
		} else {
			setForeground(list.getForeground());
			setBackground((index % 2 == 0) ? evenColor : list.getBackground());
		}

		return this;
	}
}
