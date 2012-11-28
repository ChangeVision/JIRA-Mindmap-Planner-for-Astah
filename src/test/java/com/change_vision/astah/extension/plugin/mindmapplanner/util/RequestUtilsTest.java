package com.change_vision.astah.extension.plugin.mindmapplanner.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Map;

import javax.swing.JList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.change_vision.astah.extension.plugin.mindmapplanner.model.FieldEnum;
import com.change_vision.astah.extension.plugin.mindmapplanner.view.model.TargetValueListModel;
import com.github.jira.commons.model.Component;
import com.github.jira.commons.model.Priority;
import com.github.jira.commons.model.Status;
import com.github.jira.commons.model.User;

public class RequestUtilsTest {
    
    @Mock
    private JList list;
    
    @Mock
    private TargetValueListModel model;
    
    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        when(list.getSelectedIndices()).thenReturn(new int[]{0});
        when(model.getSize()).thenReturn(1);
        when(list.getModel()).thenReturn(model);
    }
    
    @Test
    public void whenListHasNoValuesTheQueryIsEmpty() throws Exception {
        when(list.getSelectedValues()).thenReturn(new Object[]{});
        when(list.getSelectedIndices()).thenReturn(new int[]{});
        when(list.getModel()).thenReturn(new TargetValueListModel());
        String jql = RequestUtils.buildQueryPart(FieldEnum.STATUS, list);
        assertThat(jql,is(""));        
    }

    @Test
    public void buildQueryPartNeedsToUseIdWhenStatus() {
        Status status = new Status();
        setValue(status);
        setSelectedValues(status);
        when(model.getElementAt(0)).thenReturn(status);
        
        String jql = RequestUtils.buildQueryPart(FieldEnum.STATUS, list);
        assertThat(jql,is("status in ('1')"));        
    }
    
    @Test
    public void buildQueryPartNeedsToUseIdWhenComponent() {
        Component component = new Component();
        setValue(component);
        setSelectedValues(component);
        when(model.getElementAt(0)).thenReturn(component);
        
        String jql = RequestUtils.buildQueryPart(FieldEnum.COMPONENT, list);
        assertThat(jql,is("component in ('1')"));
    }

    @Test
    public void buildQueryPartNeedsToUseIdWhenPriority() {
        Priority priority = new Priority();
        setValue(priority);
        setSelectedValues(priority);
        when(model.getElementAt(0)).thenReturn(priority);
        
        String jql = RequestUtils.buildQueryPart(FieldEnum.PRIORITY, list);
        assertThat(jql,is("priority in ('1')"));        
    }

    @Test
    public void buildQueryPartNeedsToUseNameWhenUser() {
        User user = new User();
        setValue(user);
        setSelectedValues(user);
        when(model.getElementAt(0)).thenReturn(user);
        
        String jql = RequestUtils.buildQueryPart(FieldEnum.ASSIGNEE, list);
        assertThat(jql,is("assignee in ('HogeFuga')"));        
    }

    private void setSelectedValues(Object... values) {
        Object[] selectedValues = createSelectedValues(values);
        when(list.getSelectedValues()).thenReturn(selectedValues);
    }

    private Object[] createSelectedValues(Object... values) {
        return values;
    }

    private void setValue(Map<String,Object> target) {
        target.put("id", "1");
        target.put("name","HogeFuga");
    }

}
