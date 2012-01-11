package com.change_vision.astah.extension.plugin.mindmapplanner.view.model;

import javax.swing.DefaultComboBoxModel;

import com.change_vision.astah.extension.plugin.mindmapplanner.model.FieldEnum;


@SuppressWarnings("serial")
public class RootComboBoxModel extends DefaultComboBoxModel {
	public static final FieldEnum[] fields = {
		FieldEnum.FIX_VERSION, FieldEnum.ASSIGNEE, FieldEnum.PRIORITY, FieldEnum.COMPONENT, FieldEnum.STATUS };
	private SubComboBoxModel sub;

	public RootComboBoxModel() {
		super(fields);
	}
	
	public void setSubModel(SubComboBoxModel sub) {
		this.sub = sub;
		adjustSubModel();
	}
	
	public void adjustSubModel() {
		if (sub == null) {
			return;
		}
		
		FieldEnum selectedRoot = (FieldEnum) getSelectedItem();
		FieldEnum selectedSub = (FieldEnum) sub.getSelectedItem();
		
		sub.removeAllElements();
		for (FieldEnum field : SubComboBoxModel.fields) {
			if (field != selectedRoot) {
				sub.addElement(field);
			}
		}
		
		if (sub.getIndexOf(selectedSub) > 0) {
			sub.setSelectedItem(selectedSub);
		} else {
			sub.setSelectedItem(SubComboBoxModel.fields[0]);
		}
	}
}
