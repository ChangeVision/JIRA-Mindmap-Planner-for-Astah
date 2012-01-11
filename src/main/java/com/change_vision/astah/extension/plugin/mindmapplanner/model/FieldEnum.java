package com.change_vision.astah.extension.plugin.mindmapplanner.model;

import com.change_vision.astah.extension.plugin.mindmapplanner.Messages;

public enum FieldEnum {
	NONE("none", "none"), KEY("key", "key"), SUMMARY("summary", "summary"), FIX_VERSION("fixVersions", "fixVersion"), 
	ASSIGNEE("assignee", "assignee"), ISSUE_TYPE("issuetype", "issuetype"), PRIORITY("priority", "priority"), 
	COMPONENT("components", "component"), STATUS("status", "status"),
	DESCRIPTION("description", "description"), LABEL("labels", "labels"), 
	DUE_DATE("duedate", "duedate"), RESOLUTION_DATE("resolutiondate", "resolutiondate"), 
	REPORTER("reporter", "reporter");

	private String key;
	private String query;

	private FieldEnum(String key, String query) {
		this.key = key;
		this.query = query;
	}

	public String getName() {
		return this.key;
	}
	
	public String getQuery() {
		return this.query;
	}

	@Override
	public String toString() {
		return Messages.getMessage("FieldComboBoxModel." + key);
	}
	
	public String getIcon() {
		return Messages.getMessage("MM_Icon." + key);
	}
}
