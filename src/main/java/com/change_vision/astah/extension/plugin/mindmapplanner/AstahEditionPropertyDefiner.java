package com.change_vision.astah.extension.plugin.mindmapplanner;

import ch.qos.logback.core.PropertyDefinerBase;

public class AstahEditionPropertyDefiner extends PropertyDefinerBase {
	private AstahAPIHandler handler = new AstahAPIHandler();

	@Override
	public String getPropertyValue() {
		String edition = handler.getAstahEdition();
		return edition;
	}
}