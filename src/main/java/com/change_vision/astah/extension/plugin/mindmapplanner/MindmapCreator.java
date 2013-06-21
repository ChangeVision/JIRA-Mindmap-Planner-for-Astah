package com.change_vision.astah.extension.plugin.mindmapplanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

import com.change_vision.astah.extension.plugin.mindmapplanner.model.FieldEnum;
import com.change_vision.astah.extension.plugin.mindmapplanner.usericon.UserIcon;
import com.change_vision.astah.extension.plugin.mindmapplanner.util.ModelStringUtils;
import com.change_vision.jude.api.inf.editor.MindmapEditor;
import com.change_vision.jude.api.inf.editor.TransactionManager;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.model.IMindMapDiagram;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.presentation.INodePresentation;
import com.github.jira.commons.model.Issue;

public class MindmapCreator {
	private static final Logger logger = (Logger) LoggerFactory.getLogger(MindmapCreator.class);
	private static final String ICONS_PROPERTY = "icons";
	
	private IPackage output;
	private Iterable<Issue> issues;
	private FieldEnum rootField;
	private FieldEnum subField;
	private Set<FieldEnum> outputFields;
	
	private AstahAPIHandler handler;
	private MindmapEditor editor;
	
	public MindmapCreator(IPackage output, Iterable<Issue> issues, FieldEnum rootField, FieldEnum subField, Set<FieldEnum> fields) {
		this.output = output;
		this.issues = issues;
		this.rootField = rootField;
		this.subField = subField;
		this.outputFields = fields;
	}
	
	@SuppressWarnings({ })
	public List<IMindMapDiagram> create() throws ProjectNotFoundException {
		handler = new AstahAPIHandler();
		editor = handler.getMindmapEditor();
		output = (output != null) ? output : handler.getProjectAccessor().getProject();
		
		Map<String, Object> issueTree = createIssueTree();
		List<IMindMapDiagram> mindmaps = createMindmaps(issueTree);
		return mindmaps;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> createIssueTree() {
		long start = System.currentTimeMillis();
		Map<String, Object> issueTree = new HashMap<String, Object>();
		
		for (Issue issue : issues) {
			Object obj = ModelStringUtils.toString(issue, rootField);
			if (obj instanceof List) {
				List<String> roots = (List<String>) obj;
				for (String root : roots) {
					addIssueToRoot(issueTree, root, issue);
				}
			} else {
				addIssueToRoot(issueTree, (String) obj, issue);
			}
		}
		
		logger.debug("createIssueTree: " + (System.currentTimeMillis() - start) + "ms");
		return issueTree;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void addIssueToRoot(Map<String, Object> issueTree, String root, Issue issue) {
		Object issueTreeBranch = issueTree.get(root);
		if (FieldEnum.NONE == subField) {
			addIssueToBranch(issueTree, root, (List<Issue>) issueTreeBranch, issue);
		} else {
			if (issueTreeBranch == null) {
				issueTreeBranch = new HashMap<String, List<Issue>>();
				issueTree.put(root, issueTreeBranch);
			}
			
			Object obj = ModelStringUtils.toString(issue, subField);
			if (obj instanceof List) {
				List<String> subs = (List<String>) obj;
				for (String sub : subs) {
					addIssueToBranch((Map<String, Object>) issueTreeBranch, sub, (List<Issue>) ((Map) issueTreeBranch).get(sub), issue);
				}
			} else {
				addIssueToBranch((Map<String, Object>) issueTreeBranch, (String) obj, (List<Issue>) ((Map) issueTreeBranch).get(obj), issue);
			}
		}
	}

	private void addIssueToBranch(Map<String, Object> parent, String sub, List<Issue> issueList, Issue issue) {
		if (issueList == null) {
			issueList = new ArrayList<Issue>();
			parent.put(sub, issueList);
		}
		issueList.add(issue);
	}
	
	private List<IMindMapDiagram> createMindmaps(Map<String, Object> issueTree) throws ProjectNotFoundException {
		long start = System.currentTimeMillis();
		List<IMindMapDiagram> diagrams = new ArrayList<IMindMapDiagram>();
		
		try {
			TransactionManager.beginTransaction();
			
			Set<String> roots = issueTree.keySet();
			for (String root : roots) {
				try {
					IMindMapDiagram diagram = createMindmap(root, issueTree);
					diagrams.add(diagram);
				} catch (Exception e) {
					logger.warn(e.getMessage(), e);
				}
			}

			TransactionManager.endTransaction();
		} catch (Exception e) {
			if (TransactionManager.isInTransaction()) {
				TransactionManager.abortTransaction();
			}
			logger.error(e.getMessage(), e);
		}
		
		logger.debug("createMindmaps: " + (System.currentTimeMillis() - start) + "ms");
		return diagrams;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private IMindMapDiagram createMindmap(String root, Map<String, Object> issueTree) throws InvalidEditingException {
		long start = System.currentTimeMillis();
		IDiagram[] diagrams = output.getDiagrams();
		IMindMapDiagram diagram = editor.createMindmapDiagram(output, adjustName(diagrams, root));
		editor.setDiagram(diagram);
		
		INodePresentation rootTopic = diagram.getRoot();
		rootTopic.setProperty(ICONS_PROPERTY, UserIcon.UUID_PREFIX + rootField.getIcon());
		
		try {
			Object sub = issueTree.get(root);
			if (sub instanceof Map) {
				createSubTopics(rootTopic, (Map) sub);
			} else {
				createIssueTopics(rootTopic, (List<Issue>) sub);
			}
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}
		
		logger.debug("createMindmap: " + (System.currentTimeMillis() - start) + "ms");
		return diagram;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createSubTopics(INodePresentation rootTopic, Map subs) throws InvalidEditingException {
		Set<String> subTopicLabels = subs.keySet();
		int topicNum = subTopicLabels.size();
		int i = 0;
		for (String subTopicLabel : subTopicLabels) {
			INodePresentation subTopic = editor.createTopic(rootTopic, subTopicLabel, getPosition(topicNum, i++));
			subTopic.setProperty(ICONS_PROPERTY, UserIcon.UUID_PREFIX + subField.getIcon());
			
			createIssueTopics(subTopic, (List<Issue>) subs.get(subTopicLabel));
		}
	}

	private void createIssueTopics(INodePresentation parentTopic, List<Issue> issues) {
		if (issues == null) {
			return;
		}
		
		int issueNum = issues.size();
		int i = 0;
		for (Issue issue : issues) {
			try {
				INodePresentation topic = editor.createTopic(parentTopic, WordUtils.wrap(issue.getFields().getSummary(), 24, "\n", true), getPosition(issueNum, i++));
				IssueHyperlinkTranslator translator = new IssueHyperlinkTranslator();
				String url = translator.translate(issue);
				topic.createURLHyperlink(url, "Issue URL");
				createTopicOfIssueFields(topic, issue);
			} catch (Exception e) {
				logger.warn(e.getMessage(), e);
			}
		}
	}
	
	private String getPosition(int topicNum, int index) {
		if (topicNum == 1) {
			return "right";
		}
		
		if (index < topicNum / 2) {
			return "right";
		} else {
			return "left";
		}
	}

	private void createTopicOfIssueFields(INodePresentation parent, Issue issue) throws InvalidEditingException {
		for (FieldEnum field : FieldEnum.values()) {
			createTopicOfIssueField(parent, issue, field);
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void createTopicOfIssueField(INodePresentation parent, Issue issue, FieldEnum field) throws InvalidEditingException {
		if (rootField == field || subField == field) {
			return;
		}
		
		if (outputFields != null && !outputFields.contains(field)) {
			return;
		}
		
		Object labelObj = ModelStringUtils.toString(issue, field);
		if (labelObj == null || StringUtils.isBlank(labelObj.toString())
				|| (labelObj instanceof List && ((List) labelObj).isEmpty())) {
			return;
		}
		
		String label = labelObj.toString();
		INodePresentation topic = editor.createTopic(parent, label);
		topic.setProperty(ICONS_PROPERTY, UserIcon.UUID_PREFIX + field.getIcon());
	}

	private String adjustName(IDiagram[] diagrams, String name) {
		if (hasSameMindmap(diagrams, name)) {
			return adjustName(diagrams, name + "_0");
		}

		return name;
	}
	
	private static boolean hasSameMindmap(IDiagram[] diagrams, String name) {
		if (ArrayUtils.isEmpty(diagrams)) {
			return false;
		}
		
		for (IDiagram diagram : diagrams) {
			if (diagram instanceof IMindMapDiagram 
					&& ((IMindMapDiagram) diagram).getRoot().getLabel().equals(name)) {
				return true;
			}
		}
		return false;
	}
}
