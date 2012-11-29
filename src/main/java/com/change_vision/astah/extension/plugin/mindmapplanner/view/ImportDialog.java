package com.change_vision.astah.extension.plugin.mindmapplanner.view;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.LayoutStyle;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.astah.extension.plugin.mindmapplanner.Messages;
import com.change_vision.astah.extension.plugin.mindmapplanner.MindmapCreator;
import com.change_vision.astah.extension.plugin.mindmapplanner.model.FieldEnum;
import com.change_vision.astah.extension.plugin.mindmapplanner.model.ValueEnum;
import com.change_vision.astah.extension.plugin.mindmapplanner.util.AstahProjectUtils;
import com.change_vision.astah.extension.plugin.mindmapplanner.util.AstahProjectUtils.UserObject;
import com.change_vision.astah.extension.plugin.mindmapplanner.util.ConfigurationUtils;
import com.change_vision.astah.extension.plugin.mindmapplanner.util.JiraClientUtils;
import com.change_vision.astah.extension.plugin.mindmapplanner.util.RequestUtils;
import com.change_vision.astah.extension.plugin.mindmapplanner.view.component.Guide;
import com.change_vision.astah.extension.plugin.mindmapplanner.view.model.RootComboBoxModel;
import com.change_vision.astah.extension.plugin.mindmapplanner.view.model.SubComboBoxModel;
import com.change_vision.astah.extension.plugin.mindmapplanner.view.model.TargetValueListModel;
import com.change_vision.astah.extension.plugin.mindmapplanner.view.render.TargetValueListCellRenderer;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IPackage;
import com.github.jira.commons.client.JiraClient;
import com.github.jira.commons.exception.JiraRequestException;
import com.github.jira.commons.model.IssueFields;
import com.github.jira.commons.model.Project;
import com.github.jira.commons.model.SearchResults;

// TODO 取り込むIssueの事前確認
@SuppressWarnings("serial")
public class ImportDialog extends JDialog {
	private static final Logger logger = LoggerFactory.getLogger(ImportDialog.class);
	private JiraClient jira;

    public ImportDialog(Frame parent, boolean modal, JiraClient jira) {
        super(parent, modal);
        this.jira = jira;
        
        initComponents();
        loadConfiguration();
    }
    
    private int getValue(Map<String, String> options, String configKey) {
    	try {
    		return Integer.parseInt(options.get(configKey));
    	} catch (Exception e) {
    		return 0;
    	}
    }
    
    private void loadConfiguration() {
    	Map<String, String> options = ConfigurationUtils.load();
    	jira.setContext(options.get(ConfigurationUtils.JIRA_URL));
    	jira.setUserName(options.get(ConfigurationUtils.USER_NAME));
    	jira.setPassword(options.get(ConfigurationUtils.PASSWORD));
    	
    	jiraUrlField.setText(jira.getContext());

    	setSelectedProject(options);
    	setSelectedRoot(options);
    	setSelectedSub(options);
    	setSelectedFields(options);
    }
    
	private void setSelectedProject(Map<String, String> options) {
		List<Project> cachedProjects = ConfigurationUtils.loadProjectsFromCache(options);

		if (!cachedProjects.isEmpty()) {
			addItemsTo(projectBox, cachedProjects);
		} else {
			try {
				Iterable<Project> projects = jira.getProjects();
				addItemsTo(projectBox, projects);

				ConfigurationUtils.writeProjectsToCache(projects);
			} catch (JiraRequestException e) {
				JOptionPane.showMessageDialog(getParent(),
						Messages.getMessage("alert.jira_request_error"), "Alert",
						JOptionPane.ERROR_MESSAGE);
				logger.warn(e.getMessage(), e);
			}
		}
		
		int projectIndex = getValue(options, ConfigurationUtils.PROJECT);
		if (projectBox.getModel().getSize() > projectIndex) projectBox.setSelectedIndex(projectIndex);
	}

	@SuppressWarnings("rawtypes")
	private void addItemsTo(JComboBox comboBox, Iterable items) {
		ComboBoxModel comboBoxModel = comboBox.getModel();
		for (Object item : items) {
			if (contains(comboBoxModel, (Project) item)) {
				continue;
			}
			comboBox.addItem(item);
		}
	}
	
	private boolean contains(ComboBoxModel comboBoxModel, Project project) {
		int comboBoxSize = comboBoxModel.getSize();
		for (int i = 0; i < comboBoxSize; i++) {
			Object element = comboBoxModel.getElementAt(i);
			if (project.equals(((Project) element))) {
				return true;
			}
		}
		return false;
	}
    
	private void setSelectedRoot(Map<String, String> options) {
		int rootIndex = getValue(options, ConfigurationUtils.ROOT);
		if (rootBox.getModel().getSize() > rootIndex) { 
			int selectedIndex = rootBox.getSelectedIndex();
			if (rootIndex == selectedIndex) {
				rootBoxItemStateChanged((FieldEnum) rootBox.getSelectedItem());
			}
			rootBox.setSelectedIndex(rootIndex);
		}
	}

	private void setSelectedSub(Map<String, String> options) {
		int subIndex = getValue(options, ConfigurationUtils.SUB);
		if (subBox.getModel().getSize() > subIndex) {
			int selectedIndex = subBox.getSelectedIndex();
			if (subIndex == selectedIndex) {
				subBoxItemStateChanged((FieldEnum) subBox.getSelectedItem());
			}
			subBox.setSelectedIndex(subIndex);
		}
	}

	private void setSelectedFields(Map<String, String> options) {
		if (!options.containsKey(ConfigurationUtils.FIELDS)) {
			return;
		}
		
		String fields = options.get(ConfigurationUtils.FIELDS);
    	Component[] checks = importPanel.getComponents();
		for (Component check : checks) {
			((JCheckBox) check).setSelected(false);
			for (String field : fields.split(",")) {
				if (StringUtils.equalsIgnoreCase(check.getName(), field)) {
					((JCheckBox) check).setSelected(true);
					break;
				}
			}
		}
	}

    private void saveConfiguration() {
    	int projectIndex = projectBox.getSelectedIndex();
    	int rootIndex = rootBox.getSelectedIndex();
    	int subIndex = subBox.getSelectedIndex();
    	Set<FieldEnum> fields = getSelectedFields();
    	StringBuilder fieldsBuilder = new StringBuilder();
    	for (FieldEnum field : fields) {
    		if (fieldsBuilder.length() != 0) {
    			fieldsBuilder.append(",");
    		}
    		fieldsBuilder.append(field.getName());
    	}
    	
    	Map<String, String> options = ConfigurationUtils.load();
    	options.put(ConfigurationUtils.PROJECT, String.valueOf(projectIndex));
    	options.put(ConfigurationUtils.ROOT, String.valueOf(rootIndex));
    	options.put(ConfigurationUtils.SUB, String.valueOf(subIndex));
    	options.put(ConfigurationUtils.FIELDS, fieldsBuilder.toString());
    	ConfigurationUtils.save(options);
    }

    private void cancelButtonActionPerformed(ActionEvent evt) {
        dispose();
    }

	private void okButtonActionPerformed(ActionEvent evt) {    	
    	Project project = (Project) projectBox.getSelectedItem();
    	if (project == null) {
			JOptionPane.showMessageDialog(this, Messages.getMessage("ImportDialog.projectNotSelected.error"), "Warning", JOptionPane.WARNING_MESSAGE);
			return;
    	}
    	
    	FieldEnum root = (FieldEnum) rootBox.getSelectedItem();
    	FieldEnum sub = (FieldEnum) subBox.getSelectedItem();
    	Set<FieldEnum> fields = getSelectedFields();
		fields.add(FieldEnum.SUMMARY); // rootとsub, summaryはトピックを作るために必須
		fields.add(root);
		if (sub != FieldEnum.NONE) {
			fields.add(sub);
		}
		
		IPackage outputDirection = null;
		TreeSelectionModel selectionModel = astahTree.getSelectionModel();
		if (!selectionModel.isSelectionEmpty()) {
			DefaultMutableTreeNode astahTreePathComponent = (DefaultMutableTreeNode) selectionModel.getSelectionPath().getLastPathComponent();
			outputDirection = ((UserObject) astahTreePathComponent.getUserObject()).getModel();
		}
        
		try {
			getParent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    	saveConfiguration();
	        dispose();
	        
			String jqlQuery = buildQuery();
			SearchResults result = JiraClientUtils.searchIssues(jira, jqlQuery, fields, 0, 1000);
			new MindmapCreator(outputDirection, result.getIssues(), root, sub, fields).create();
		} catch (JSONException e) {
			JOptionPane.showMessageDialog(getParent(),
					Messages.getMessage("alert.json_error"), "Alert",
					JOptionPane.ERROR_MESSAGE);
			logger.error(e.getMessage(), e);
		} catch (JiraRequestException e) {
			JOptionPane.showMessageDialog(getParent(),
					Messages.getMessage("alert.jira_request_error"), "Alert",
					JOptionPane.ERROR_MESSAGE);
			logger.error(e.getMessage(), e);
		} catch (ProjectNotFoundException e) {
			JOptionPane.showMessageDialog(getParent(),
					Messages.getMessage("warning.project_is_not_opened"), "Alert",
					JOptionPane.ERROR_MESSAGE);
			logger.error(e.getMessage(), e);
		} finally {
			getParent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
    }

	private String buildQuery() {
		Project project = (Project) projectBox.getSelectedItem();
		
		StringBuilder jqlBuilder = new StringBuilder();
		jqlBuilder.append(IssueFields.PROJECT);
		jqlBuilder.append("=");
		jqlBuilder.append("'");
		jqlBuilder.append(project.getKey());
		jqlBuilder.append("'");
		
		String rootQueryPart = RequestUtils.buildQueryPart((FieldEnum) rootBox.getSelectedItem(), rootValueList);
		if (StringUtils.isNotBlank(rootQueryPart)) {
			jqlBuilder.append(" and(");
			jqlBuilder.append(rootQueryPart);
			jqlBuilder.append(") ");
		}
		
		String subQueryPart = RequestUtils.buildQueryPart((FieldEnum) subBox.getSelectedItem(), subValueList);
		if (StringUtils.isNotBlank(subQueryPart)) {
			jqlBuilder.append(" and(");
			jqlBuilder.append(subQueryPart);
			jqlBuilder.append(") ");
		}
		return jqlBuilder.toString();
	}

    private void addJiraButtonActionPerformed(ActionEvent evt) {
    	setVisible(false);
    	
    	ConnectionSettingDialog dialog = new ConnectionSettingDialog((Frame) getParent(), true, jira);
    	dialog.setLocationByPlatform(true);
    	dialog.setVisible(true);
    	
    	dialog.addWindowListener(new WindowAdapter() {
    		@Override
    		public void windowClosed(WindowEvent event) {
    			connectionSettingDialogWindowClosed();
    		}
		});
    }
    
	private void filterButtonActionPerformed(java.awt.event.ActionEvent evt) {
		CardLayout layout = (CardLayout) cardPanel.getLayout();
		if (filterButton.isSelected()) {
			layout.last(cardPanel);
		} else {
			layout.first(cardPanel);
		}
	}
    
    private void connectionSettingDialogWindowClosed() {
    	loadConfiguration();
    	setVisible(true);
    }
    
    private Set<FieldEnum> getSelectedFields() {
    	Set<FieldEnum> fields = new HashSet<FieldEnum>();
    	if (keyCheck.isSelected()) fields.add(FieldEnum.KEY);
        if (descCheck.isSelected()) fields.add(FieldEnum.DESCRIPTION);
        if (componentCheck.isSelected()) fields.add(FieldEnum.COMPONENT);
        if (priorityCheck.isSelected()) fields.add(FieldEnum.PRIORITY);
        if (fixVersionCheck.isSelected()) fields.add(FieldEnum.FIX_VERSION);
        if (assigneeCheck.isSelected()) fields.add(FieldEnum.ASSIGNEE);
        if (dueDateCheck.isSelected()) fields.add(FieldEnum.DUE_DATE);
        if (labelCheck.isSelected()) fields.add(FieldEnum.LABEL);
        if (resolutionDateCheck.isSelected()) fields.add(FieldEnum.RESOLUTION_DATE);
        if (reporterCheck.isSelected()) fields.add(FieldEnum.REPORTER);
        if (issueTypeCheck.isSelected()) fields.add(FieldEnum.ISSUE_TYPE);
        if (statusCheck.isSelected()) fields.add(FieldEnum.STATUS);
    	return fields;
    }
    
	private void rootBoxItemStateChanged(FieldEnum field) {
		rootValueListModel.clear();
		rootModel.adjustSubModel();
		Project project = (Project) projectBox.getSelectedItem();
		if (project == null) {
			return;
		}
		
		if (field == FieldEnum.FIX_VERSION || field == FieldEnum.ASSIGNEE || field == FieldEnum.COMPONENT) {
			rootValueListModel.addElement(ValueEnum.NONE);
		}
		
		try {
			Iterable<?> values = JiraClientUtils.getFieldValues(jira, project, field);
			for (Object value : values) {
				rootValueListModel.addElement(value);
			}
			rootValueList.setSelectionInterval(0, rootValueListModel.getSize());
		} catch (JiraRequestException e) {
			logger.warn(e.getMessage(), e);
		}
	}

	private void subBoxItemStateChanged(FieldEnum field) {
		subValueListModel.clear();
		Project project = (Project) projectBox.getSelectedItem();
		if (project == null || field == FieldEnum.NONE) {
			return;
		}
		
		if (field == FieldEnum.FIX_VERSION || field == FieldEnum.ASSIGNEE || field == FieldEnum.COMPONENT) {
			subValueListModel.addElement(ValueEnum.NONE);
		}
		
		try {
			Iterable<?> values = JiraClientUtils.getFieldValues(jira, project, field);
			for (Object value : values) {
				subValueListModel.addElement(value);
			}
			subValueList.setSelectionInterval(0, subValueListModel.getSize());
		} catch (JiraRequestException e) {
			logger.warn(e.getMessage(), e);
		}
	}
	
    private void initComponents() {
        rootBox.addItemListener(new ItemListener() {
        	@Override public void itemStateChanged(ItemEvent e) {
        		if (e.getStateChange() == ItemEvent.SELECTED) {
        			rootBoxItemStateChanged((FieldEnum) e.getItem());
        		}
        	}
        });
        rootModel.setSubModel(subModel);
        
        subBox.addItemListener(new ItemListener() {
        	@Override public void itemStateChanged(ItemEvent e) {
        		if (e.getStateChange() == ItemEvent.SELECTED) {
        			subBoxItemStateChanged((FieldEnum) e.getItem());
        		}
        	}
        });
        
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Messages.getMessage("ImportDialog.Form.title"));
        setName("Form");
        setResizable(false);

        jiraUrlField.setName("jiraUrlField");
        jiraUrlField.setEditable(false);

        jiraLabel.setText(Messages.getMessage("ImportDialog.jiraLabel.text"));
        jiraLabel.setName("jiraLabel");

        projectBox.setName("projectBox");
        projectBox.addItemListener(new ItemListener() {
        	@Override public void itemStateChanged(ItemEvent e) {
        		if (e.getStateChange() == ItemEvent.SELECTED) {
        			rootBoxItemStateChanged((FieldEnum) rootBox.getSelectedItem());
        			subBoxItemStateChanged((FieldEnum) subBox.getSelectedItem());
        		}
        	}
        });
        projectBox.addPopupMenuListener(new MinWidthPopupMenuListener());
        projectBox.setPreferredSize(new Dimension(159, 25));

        projectLabel.setText(Messages.getMessage("ImportDialog.projectLabel.text"));
        projectLabel.setName("projectLabel");

        rootLabel.setText(Messages.getMessage("ImportDialog.rootLabel.text"));
        rootLabel.setName("rootLabel");

        rootBox.setName("rootBox");

        subLabel.setText(Messages.getMessage("ImportDialog.subLabel.text"));
        subLabel.setName("subLabel");

        subBox.setName("subBox");
        
        cardPanel.setName("cardPanel");
        cardPanel.setLayout(new java.awt.CardLayout());

        cardFirstPanel.setName("cardFirstPanel");

        cancelButton.setText(Messages.getMessage("cancel"));
        cancelButton.setName("cancelButton");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        okButton.setText(Messages.getMessage("ok"));
        okButton.setName("okButton");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        importScrollPane.setBorder(null);
        importScrollPane.setBorder(BorderFactory.createEtchedBorder());
        importScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        importScrollPane.setName("importScrollPane");

        importPanel.setName("importPanel");
        importPanel.setLayout(new GridLayout(8, 2));

        keyCheck.setText(Messages.getMessage("ImportDialog.keyCheck.text"));
        keyCheck.setName("key");
        keyCheck.setSelected(false);
        importPanel.add(keyCheck);

        fixVersionCheck.setText(Messages.getMessage("ImportDialog.fixVersionCheck.text"));
        fixVersionCheck.setName("fixVersions");
        fixVersionCheck.setSelected(true);
        importPanel.add(fixVersionCheck);

        assigneeCheck.setText(Messages.getMessage("ImportDialog.assigneeCheck.text"));
        assigneeCheck.setName("assignee");
        assigneeCheck.setSelected(true);
        importPanel.add(assigneeCheck);

        issueTypeCheck.setText(Messages.getMessage("ImportDialog.issueTypeCheck.text"));
        issueTypeCheck.setName("issueType");
        issueTypeCheck.setSelected(true);
        importPanel.add(issueTypeCheck);

        priorityCheck.setText(Messages.getMessage("ImportDialog.priorityCheck.text"));
        priorityCheck.setName("priority");
        priorityCheck.setSelected(true);
        importPanel.add(priorityCheck);

        statusCheck.setText(Messages.getMessage("ImportDialog.statusCheck.text"));
        statusCheck.setName("status");
        statusCheck.setSelected(true);
        importPanel.add(statusCheck);

        componentCheck.setText(Messages.getMessage("ImportDialog.componentCheck.text"));
        componentCheck.setName("components");
        componentCheck.setSelected(true);
        importPanel.add(componentCheck);

        labelCheck.setText(Messages.getMessage("ImportDialog.labelCheck.text"));
        labelCheck.setName("labels");
        labelCheck.setSelected(false);
        importPanel.add(labelCheck);

        descCheck.setText(Messages.getMessage("ImportDialog.descCheck.text"));
        descCheck.setName("description");
        descCheck.setSelected(false);
        importPanel.add(descCheck);

        reporterCheck.setText(Messages.getMessage("ImportDialog.reporterCheck.text"));
        reporterCheck.setName("reporter");
        reporterCheck.setSelected(false);
        importPanel.add(reporterCheck);

        dueDateCheck.setText(Messages.getMessage("ImportDialog.dueDateCheck.text"));
        dueDateCheck.setName("dueDate");
        dueDateCheck.setSelected(false);
        importPanel.add(dueDateCheck);

        resolutionDateCheck.setText(Messages.getMessage("ImportDialog.resolutionDateCheck.text"));
        resolutionDateCheck.setName("resolutionDate");
        resolutionDateCheck.setSelected(false);
        importPanel.add(resolutionDateCheck);

        importScrollPane.setViewportView(importPanel);

        addJiraButton.setText(Messages.getMessage("ImportDialog.addJiraButton.text"));
        addJiraButton.setName("addJiraButton");
        addJiraButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                addJiraButtonActionPerformed(evt);
            }
        });

        astahTreePane.setName("astahTreePane");
        astahTreePane.setBorder(BorderFactory.createEtchedBorder());
        astahTree.setName("astahTree");
        try {
			astahTree.setModel(AstahProjectUtils.getCurrentProjectTreeModel());
			DefaultTreeCellRenderer cellRenderer = (DefaultTreeCellRenderer) astahTree.getCellRenderer();
			cellRenderer.setLeafIcon(cellRenderer.getDefaultClosedIcon());
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}
        astahTreePane.setViewportView(astahTree);

        TitledBorder border = BorderFactory.createTitledBorder(Messages.getMessage("ImportDialog.guide.title"));
        border.setTitleJustification(TitledBorder.CENTER);
        imagePanel.setBorder(border);
        imagePanel.setName("imagePanel");
        
        GroupLayout imagePanelLayout = new GroupLayout(imagePanel);
        imagePanel.setLayout(imagePanelLayout);
        imagePanelLayout.setHorizontalGroup(
            imagePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 320, Short.MAX_VALUE)
            .addComponent(guide)
        );
        imagePanelLayout.setVerticalGroup(
            imagePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 270, Short.MAX_VALUE)
            .addComponent(guide)
        );
        
        tabbedPane.setName("tabbedPane");
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.addTab(Messages.getMessage("ImportDialog.astahTree.border.title"), astahTreePane);
        tabbedPane.addTab(Messages.getMessage("ImportDialog.importPanel.border.title"), importScrollPane);

        GroupLayout cardFirstPanelLayout = new GroupLayout(cardFirstPanel);
        cardFirstPanel.setLayout(cardFirstPanelLayout);
        cardFirstPanelLayout.setHorizontalGroup(
            cardFirstPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, cardFirstPanelLayout.createSequentialGroup()
                .addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(imagePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        cardFirstPanelLayout.setVerticalGroup(
            cardFirstPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(cardFirstPanelLayout.createSequentialGroup()
                .addGroup(cardFirstPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 293, GroupLayout.PREFERRED_SIZE)
                    .addGroup(cardFirstPanelLayout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(imagePanel, GroupLayout.PREFERRED_SIZE, 274, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cardPanel.add(cardFirstPanel, "first");
        cardSecondPanel.setName("cardSecondPanel");

        rootListScroll.setBackground(cardPanel.getBackground());
        rootListScroll.setBorder(BorderFactory.createTitledBorder(null, Messages.getMessage("ImportDialog.rootListScroll.border.title"), TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        rootListScroll.setName("rootListScroll");

        rootValueList.setName("rootValueList");
        rootValueList.setCellRenderer(new TargetValueListCellRenderer());
        rootListScroll.setViewportView(rootValueList);

        subListScroll.setBackground(cardPanel.getBackground());
        subListScroll.setBorder(BorderFactory.createTitledBorder(null, Messages.getMessage("ImportDialog.subListScroll.border.title"), TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        subListScroll.setName("subListScroll");

        subValueList.setName("subValueList");
        subValueList.setCellRenderer(new TargetValueListCellRenderer());
        subListScroll.setViewportView(subValueList);

        GroupLayout cardSecondPanelLayout = new GroupLayout(cardSecondPanel);
        cardSecondPanel.setLayout(cardSecondPanelLayout);
        cardSecondPanelLayout.setHorizontalGroup(
                cardSecondPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(cardSecondPanelLayout.createSequentialGroup()
                    .addGap(45, 45, 45)
                    .addComponent(rootListScroll, GroupLayout.PREFERRED_SIZE, 226, GroupLayout.PREFERRED_SIZE)
                    .addGap(35, 35, 35)
                    .addComponent(subListScroll, GroupLayout.PREFERRED_SIZE, 218, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(74, Short.MAX_VALUE))
            );
            cardSecondPanelLayout.setVerticalGroup(
                cardSecondPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(cardSecondPanelLayout.createSequentialGroup()
                    .addGap(9, 9, 9)
                    .addGroup(cardSecondPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                        .addComponent(subListScroll, GroupLayout.Alignment.LEADING)
                        .addComponent(rootListScroll, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE))
                    .addContainerGap(7, Short.MAX_VALUE))
            );
        
        cardPanel.add(cardSecondPanel, "second");

        filterButton.setText(Messages.getMessage("ImportDialog.filterButton.text"));
        filterButton.setName("filterButton");
        filterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterButtonActionPerformed(evt);
            }
        });
        
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(cardPanel, GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE))
                    .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(projectLabel)
                            .addComponent(rootLabel)
                            .addComponent(jiraLabel))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(jiraUrlField, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(projectBox, GroupLayout.Alignment.LEADING, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(rootBox, GroupLayout.Alignment.LEADING, 0, 159, Short.MAX_VALUE))
                                .addGap(13, 13, 13)
                                .addComponent(subLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(subBox, 0, 138, Short.MAX_VALUE)))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(addJiraButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(filterButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(443, Short.MAX_VALUE)
                        .addComponent(cancelButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(okButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(jiraUrlField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jiraLabel))
                        .addGap(8, 8, 8)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(projectBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(projectLabel)))
                    .addComponent(addJiraButton))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(rootBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(rootLabel)
                    .addComponent(subBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(filterButton)
                    .addComponent(subLabel))
                .addGap(18, 18, 18)
                .addComponent(cardPanel, GroupLayout.PREFERRED_SIZE, 289, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }

    public static void main(String args[]) throws Throwable {
//		for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
//			if ("Nimbus".equals(info.getName())) {
//				UIManager.setLookAndFeel(info.getClassName());
//				break;
//			}
//		}

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                ImportDialog dialog = new ImportDialog(new JFrame(), true, new JiraClient());
                dialog.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    private JPanel cardFirstPanel = new JPanel();
    private JPanel cardPanel = new JPanel();
    private JPanel cardSecondPanel = new JPanel();
    private JToggleButton filterButton = new JToggleButton();
    private JScrollPane importScrollPane = new JScrollPane();
    private JPanel importPanel = new JPanel();
    private JCheckBox keyCheck = new JCheckBox();
    private JCheckBox fixVersionCheck = new JCheckBox();
    private JCheckBox assigneeCheck = new JCheckBox();
    private JCheckBox issueTypeCheck = new JCheckBox();
    private JCheckBox priorityCheck = new JCheckBox();
    private JCheckBox statusCheck = new JCheckBox();
    private JCheckBox componentCheck = new JCheckBox();
    private JCheckBox labelCheck = new JCheckBox();
    private JCheckBox descCheck = new JCheckBox();
    private JCheckBox reporterCheck = new JCheckBox();
    private JCheckBox dueDateCheck = new JCheckBox();
    private JCheckBox resolutionDateCheck = new JCheckBox();
    
    private JTextField jiraUrlField = new JTextField();
    private JLabel jiraLabel = new JLabel();
    private JButton addJiraButton = new JButton();
    private JComboBox projectBox = new JComboBox();
    private JLabel projectLabel = new JLabel();
    private JLabel rootLabel = new JLabel();
    private RootComboBoxModel rootModel = new RootComboBoxModel();
    private JComboBox rootBox = new JComboBox(rootModel);
    private JScrollPane rootListScroll = new JScrollPane();
    private TargetValueListModel rootValueListModel = new TargetValueListModel();
    private JList rootValueList = new JList(rootValueListModel);
    private JLabel subLabel = new JLabel();
    private SubComboBoxModel subModel = new SubComboBoxModel();
    private JComboBox subBox = new JComboBox(subModel);
    private JScrollPane subListScroll = new JScrollPane();
    private TargetValueListModel subValueListModel = new TargetValueListModel();
    private JList subValueList = new JList(subValueListModel);
    private JTree astahTree = new JTree();
    private JScrollPane astahTreePane = new JScrollPane();
    private JButton okButton = new JButton();
    private JButton cancelButton = new JButton();
    private JPanel imagePanel = new JPanel();
    private JTabbedPane tabbedPane = new JTabbedPane();
    private Guide guide = new Guide();
    
    class MinWidthPopupMenuListener implements PopupMenuListener {
        private static final int POPUP_MIN_WIDTH = 320;
        private boolean adjusting = false;

        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            JComboBox<Object> combo = (JComboBox<Object>) e.getSource();
            Dimension size = combo.getSize();
            if (size.width >= POPUP_MIN_WIDTH) return;
            if (!adjusting) {
                adjusting = true;
                combo.setSize(POPUP_MIN_WIDTH, size.height);
                combo.showPopup();
            }
            combo.setSize(size);
            adjusting = false;
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
        }
      }
}
