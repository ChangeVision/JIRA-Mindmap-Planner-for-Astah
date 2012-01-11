package com.change_vision.astah.extension.plugin.mindmapplanner.view.model;

import javax.swing.DefaultComboBoxModel;

import com.change_vision.astah.extension.plugin.mindmapplanner.model.FieldEnum;


@SuppressWarnings("serial")
public class SubComboBoxModel extends DefaultComboBoxModel {
	public static final FieldEnum[] fields = {
		FieldEnum.NONE, FieldEnum.FIX_VERSION, FieldEnum.ASSIGNEE, FieldEnum.PRIORITY, FieldEnum.COMPONENT, FieldEnum.STATUS };

	public SubComboBoxModel() {
		super(fields);
	}
}
