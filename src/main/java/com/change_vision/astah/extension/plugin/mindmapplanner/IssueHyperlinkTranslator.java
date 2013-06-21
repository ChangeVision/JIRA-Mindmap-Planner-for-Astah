package com.change_vision.astah.extension.plugin.mindmapplanner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.jira.commons.model.Issue;

public class IssueHyperlinkTranslator {

	public String translate(Issue issue) {
		if (issue == null) {
			throw new IllegalArgumentException("issue is null");
		}
		String self = issue.getSelf();
		String key = issue.getKey();
		if (self == null || key == null){
			throw new IllegalStateException("Illegal state of the issue " + issue);
		}
		Pattern pattern = Pattern.compile("(.*)/rest/api/2/issue/.*");
		Matcher matcher = pattern.matcher(self);
		if (matcher.matches() == false) {
			String message = String.format("It may be changed the URL pattern.'%s'\nPlease contact astah-sales@change-vision.com to fix this issue.",self);
			throw new IllegalStateException(message);
		}
		String base = matcher.group(1);
		String url = String.format("%s/browse/%s",base,key);
		return url;
		
	}

}
