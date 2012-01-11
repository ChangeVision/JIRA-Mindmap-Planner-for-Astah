package com.change_vision.astah.extension.plugin.mindmapplanner.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.astah.extension.plugin.mindmapplanner.model.FieldEnum;
import com.github.jira.commons.client.IssueSearchParameter;
import com.github.jira.commons.client.JiraClient;
import com.github.jira.commons.exception.JiraRequestException;
import com.github.jira.commons.model.Issue;
import com.github.jira.commons.model.IssueFields;
import com.github.jira.commons.model.Priority;
import com.github.jira.commons.model.Project;
import com.github.jira.commons.model.SearchResults;
import com.github.jira.commons.model.Status;
import com.github.jira.commons.model.User;

public class JiraClientUtils {
	private static final Logger logger = LoggerFactory.getLogger(JiraClientUtils.class);
	private static final int DEFAULT_START_AT = 0;
	private static final int DEFAULT_MAX_RESULTS = 255;

	public static SearchResults searchIssues(JiraClient jira, String jql, Set<FieldEnum> fields, int startAt, int maxResults) throws JSONException, JiraRequestException {
		if (jql == null) {
			throw new IllegalArgumentException("JQL must not be null.");
		}
		
		long start = System.currentTimeMillis();
		JSONObject requestParams = new JSONObject();
		requestParams.put(IssueSearchParameter.JQL, jql);
		requestParams.put(IssueSearchParameter.START_AT, startAt);
		requestParams.put(IssueSearchParameter.MAX_RESULTS, maxResults);
		requestParams.put(IssueSearchParameter.FIELDS, convert(fields));
		SearchResults result = jira.search(requestParams);
		
		logger.debug("searchIssues:" + (System.currentTimeMillis() - start) + "ms");
		return result;
	}

	public static Iterable<?> getFieldValues(JiraClient jira, Project project, FieldEnum field) throws JiraRequestException {
		Iterable<?> values = new ArrayList<Object>();
		switch (field) {
		case FIX_VERSION:
			values = getFixVersions(jira, project);
			break;
		case ASSIGNEE:
			values = getAssignees(jira, project);
			break;
		case PRIORITY:
			values = getPriorities(jira, project);
			break;
		case COMPONENT:
			values = getComponents(jira, project);
			break;
		case STATUS:
			values = getStatuses(jira, project);
			break;
		default:
			break;
		}
		return values;
	}

	private static Set<String> convert(Set<FieldEnum> fields) {
    	Set<String> converted = new HashSet<String>();
    	for (FieldEnum field : fields) {
    		converted.add(field.getName());
    	}
    	return converted;
    }

    public static Iterable<?> getFixVersions(JiraClient jira, Project project) {
		if (!project.getVersions().iterator().hasNext()) {
			Project fullProject = jira.getProject(project.getKey());
			project.put(Project.VERSIONS, fullProject.getVersions());
		}
		
		return project.getVersions();
	}

	public static Iterable<?> getAssignees(JiraClient jira, Project project) {
		String key = "assignees";
		if (!project.containsKey(key)) {
			HashSet<FieldEnum> fields = new HashSet<FieldEnum>();
			fields.add(FieldEnum.ASSIGNEE);
			Set<User> users = new HashSet<User>();
			SearchResults issues;
			try {
				String jql = IssueFields.PROJECT + "=" + project.getKey();
				issues = JiraClientUtils.searchIssues(jira, jql, fields, DEFAULT_START_AT, DEFAULT_MAX_RESULTS);
				for (Issue issue : issues.getIssues()) {
					User assignee = issue.getFields().getAssignee();
					if (assignee != null && StringUtils.isNotBlank(assignee.getName())) {
						users.add(assignee);
					}
				}
				project.put(key, users);
			} catch (JiraRequestException e) {
				throw e;
			} catch (JSONException e) {
				logger.warn(e.getMessage(), e);
			}
		}
		
		return (Iterable<?>) project.get("assignees");
	}
	
	public static Iterable<?> getComponents(JiraClient jira, Project project) {
		if (!project.getComponents().iterator().hasNext()) {
			Project fullProject = jira.getProject(project.getKey());
			project.put(Project.COMPONENTS, fullProject.getComponents());
		}

		return project.getComponents();
	}
	
	public static Iterable<?> getPriorities(JiraClient jira, Project project) {
		String key = "priorities";
		if (!project.containsKey(key)) {
			Iterable<Priority> priorities = jira.getPriorities();
			project.put(key, priorities);
		}

		return (Iterable<?>) project.get(key);
	}
	
	public static Iterable<?> getStatuses(JiraClient jira, Project project) {
		String key = "statuses";
		if (!project.containsKey(key)) {
			Iterable<Status> statuses = jira.getStatuses();
			project.put(key, statuses);
		}

		return (Iterable<?>) project.get(key);
	}
}
