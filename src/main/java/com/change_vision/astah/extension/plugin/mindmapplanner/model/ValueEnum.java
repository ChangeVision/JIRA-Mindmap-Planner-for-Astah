package com.change_vision.astah.extension.plugin.mindmapplanner.model;

import com.change_vision.astah.extension.plugin.mindmapplanner.Messages;

public enum ValueEnum {
	NONE("none");
	
	private String key;

	private ValueEnum(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
	
	@Override
	public String toString() {
		return Messages.getMessage("ValueEnum.none");
	}
}
