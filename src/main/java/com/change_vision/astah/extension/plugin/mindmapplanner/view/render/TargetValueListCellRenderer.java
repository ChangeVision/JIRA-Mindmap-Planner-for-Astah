package com.change_vision.astah.extension.plugin.mindmapplanner.view.render;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

@SuppressWarnings("serial")
public class TargetValueListCellRenderer extends JLabel implements ListCellRenderer {

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
			Color bgColor = list.getBackground();
			Color darkerColor = (bgColor != null) ? bgColor.darker() : null;
			setBackground((index % 2 == 0) ? bgColor : darkerColor);
		}

		return this;
	}
}
