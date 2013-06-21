package com.change_vision.astah.extension.plugin.mindmapplanner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.github.jira.commons.model.Issue;

public class IssueHyperlinkTranslatorTest {
	
	private IssueHyperlinkTranslator translator;

	@Mock
	private Issue issue;

	@Mock
	private Issue illegalSelfIssue;

	@Mock
	private Issue illegalKeyIssue;

	@Mock
	private Issue illegalSelfPatternIssue;

	@Before
	public void before() throws Exception {
		MockitoAnnotations.initMocks(this);
		translator = new IssueHyperlinkTranslator();
		when(issue.getSelf()).thenReturn("http://localhost:2990/jira/rest/api/2/issue/10001");
		when(issue.getKey()).thenReturn("DEMO-2");
		when(illegalSelfIssue.getKey()).thenReturn("DEMO-2");
		when(illegalKeyIssue.getSelf()).thenReturn("http://localhost:2990/jira/rest/api/2/issue/10001");
		when(illegalSelfPatternIssue.getSelf()).thenReturn("http://localhost:2990/jira/rest/api/3/issue/10001");
		when(illegalSelfPatternIssue.getKey()).thenReturn("DEMO-2");
	}

	@Test(expected=IllegalArgumentException.class)
	public void withNull() {
		translator.translate(null);
	}
	
	@Test(expected=IllegalStateException.class)
	public void withIllegalSelfIssue() throws Exception {
		translator.translate(illegalSelfIssue);
	}

	@Test(expected=IllegalStateException.class)
	public void withIllegalKeyIssue() throws Exception {
		translator.translate(illegalKeyIssue);
	}

	@Test
	public void translate() throws Exception {
		String actual = translator.translate(issue);
		assertThat(actual,is("http://localhost:2990/jira/browse/DEMO-2"));
	}
	
	@Test(expected=IllegalStateException.class)
	public void translateIllegalSelfPattern() throws Exception {
		translator.translate(illegalSelfPatternIssue);
	}

}
