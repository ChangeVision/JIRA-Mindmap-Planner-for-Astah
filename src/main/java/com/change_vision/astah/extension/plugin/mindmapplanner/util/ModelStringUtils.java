package com.change_vision.astah.extension.plugin.mindmapplanner.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.change_vision.astah.extension.plugin.mindmapplanner.model.FieldEnum;
import com.github.jira.commons.model.Component;
import com.github.jira.commons.model.Issue;
import com.github.jira.commons.model.Version;


public class ModelStringUtils {
	public static Iterable<String> versionsToString(Iterable<Version> versions) {
		List<String> ret = new ArrayList<String>();
		for (Version version : versions) {
			ret.add(version.getName());
		}
		return ret;
	}
	
	public static Iterable<String> componentsToString(Iterable<Component> components) {
		List<String> ret = new ArrayList<String>();
		for (Component component : components) {
			ret.add(component.getName());
		}
		return ret;
	}
	
	public static Iterable<String> labelsToString(Iterable<String> labels) {
		List<String> ret = new ArrayList<String>();
		for (String label : labels) {
			ret.add(label);
		}
		return ret;
	}
	
	public static Object toString(Issue issue, FieldEnum field) {
		switch (field) {
		case KEY:
			return issue.getKey();
		case FIX_VERSION:
			Iterable<String> versions = ModelStringUtils.versionsToString(issue.getFields().getFixVersions());
			return versions.iterator().hasNext() ? versions : "None";
		case ASSIGNEE:
			String name = issue.getFields().getAssignee().getName();
			return StringUtils.isNotBlank(name) ? name : "Unassigned";
		case PRIORITY:
			return issue.getFields().getPriority().getName();
		case COMPONENT:
			Iterable<String> components = ModelStringUtils.componentsToString(issue.getFields().getComponents());
			return components.iterator().hasNext() ? components : "None";
		case STATUS:
			return issue.getFields().getStatus().getName();
		case DESCRIPTION:
			return issue.getFields().getDescription();
		case LABEL:
			Iterable<String> labels = ModelStringUtils.labelsToString(issue.getFields().getLabels());
			return labels.iterator().hasNext() ? labels : "None";
		case DUE_DATE:
			Date duedate = issue.getFields().getDuedate();
			return ObjectUtils.defaultIfNull(duedate, "").toString();
		case RESOLUTION_DATE:
			Date resolutionDate = issue.getFields().getResolutionDate();
			return ObjectUtils.defaultIfNull(resolutionDate, "").toString();
		case REPORTER:
			return issue.getFields().getReporter().getName();
		case ISSUE_TYPE:
			return issue.getFields().getIssueType().getName();
		default:
			break;
		}
		
		return "";
	}
}
