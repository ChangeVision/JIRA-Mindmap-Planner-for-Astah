package com.change_vision.astah.extension.plugin.mindmapplanner.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.github.jira.commons.client.JiraClient;
import com.github.jira.commons.model.Issue;
import com.github.jira.commons.model.Project;
import com.github.jira.commons.model.SearchResults;

public class JiraClientUtilsTest {

    @Test
    public void getAssignees() throws JSONException {
        JiraClient jira = mock(JiraClient.class);
        Project project = new Project();
        project.put(Project.KEY, "IN");
        SearchResults searchResults = mock(SearchResults.class);
        when(searchResults.getIssues()).thenReturn(new ArrayList<Issue>());
        when(jira.search(any(JSONObject.class))).thenReturn(searchResults);
        JiraClientUtils.getAssignees(jira , project);
        ArgumentCaptor<JSONObject> captor = ArgumentCaptor.forClass(JSONObject.class);
        verify(jira).search(captor.capture());
        JSONObject actual = captor.getValue();
        assertThat(actual.getString("jql"),is("project='IN'"));
    }

}
