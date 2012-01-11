package com.change_vision.astah.extension.plugin.mindmapplanner.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.github.jira.commons.model.Project;

public class ConfigurationUtils {
	private static final Logger logger = LoggerFactory.getLogger(ConfigurationUtils.class);
	
	private static final String ENCRYPT_KEY = "MINDMAP_planner";
	private static final String ENCODING = "iso-8859-1";
	public static final String JIRA_URL = "jiraUrl";
	public static final String USER_NAME = "userName";
	public static final String PASSWORD = "password";
	public static final String HTTP_AUTH = "httpAuth";
	public static final String HTTP_USER_NAME = "httpUserName";
	public static final String HTTP_PASSWORD = "httpPassword";
	
	public static final String PROJECTS_CACHE = "projects";
	public static final String PROJECT = "project";
	public static final String ROOT = "root";
	public static final String SUB = "sub";
	public static final String FIELDS = "fields";
	
	public static String EDITION;
	public static File CONFIG_FILE;
	
	static {
		try {
			EDITION = ProjectAccessorFactory.getProjectAccessor().getAstahEdition();
		} catch (ClassNotFoundException e) {
			EDITION = "professional";
		}
		
		CONFIG_FILE = new File(
				System.getProperty("user.home") + File.separator + ".astah" + File.separator + EDITION,
				"mindmap-planner.properties");
	}
	
	public static Map<String, String> load() {
		Map<String, String> options = new HashMap<String, String>();
		
    	Properties config = null;
    	try {
    		CONFIG_FILE.createNewFile();
			config = new Properties();
			config.load(new FileInputStream(CONFIG_FILE));
			if (!config.isEmpty()) {
				String encryptedPassword = StringUtils.defaultString(config.getProperty(PASSWORD));
				String encryptedHttpPassword = StringUtils.defaultString(config.getProperty(HTTP_PASSWORD));
				
				setOptionFromConfig(options, JIRA_URL, config);
				setOptionFromConfig(options, USER_NAME, config);
				setOptionFromConfig(options, PASSWORD, config);
				setOptionFromConfig(options, HTTP_AUTH, config);
				setOptionFromConfig(options, HTTP_USER_NAME, config);
				setOptionFromConfig(options, HTTP_PASSWORD, config);
				setOptionFromConfig(options, PROJECTS_CACHE, config);
				setOptionFromConfig(options, PROJECT, config);
				setOptionFromConfig(options, ROOT, config);
				setOptionFromConfig(options, SUB, config);
				setOptionFromConfig(options, FIELDS, config);

				if (config.containsKey(PASSWORD)) {
					options.put(PASSWORD, PasswordUtils.decrypt(ENCRYPT_KEY, encryptedPassword.getBytes(ENCODING)));
				}
				
				if (config.containsKey(HTTP_PASSWORD)) {
					options.put(HTTP_PASSWORD, PasswordUtils.decrypt(ENCRYPT_KEY, encryptedHttpPassword.getBytes(ENCODING)));
				}
			}
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}
    	
    	return options;
	}

	private static void setOptionFromConfig(Map<String, String> options, String key, Properties config) {
		if (config.containsKey(key)) {
			options.put(key, config.getProperty(key));
		}
	}
	
	private static void setConfigFromOption(Properties config, String key, Map<String, String> options) {
		if (options.containsKey(key)) {
			config.setProperty(key, options.get(key));
		}
	}
	
	public static void save(Map<String, String> options) {
		Properties config = null;
    	try {
			config = new Properties();
			setConfigFromOption(config, JIRA_URL, options);
			setConfigFromOption(config, USER_NAME, options);
			setConfigFromOption(config, HTTP_AUTH, options);
			setConfigFromOption(config, HTTP_USER_NAME, options);
			setConfigFromOption(config, PROJECTS_CACHE, options);
			setConfigFromOption(config, PROJECT, options);
			setConfigFromOption(config, ROOT, options);
			setConfigFromOption(config, SUB, options);
			setConfigFromOption(config, FIELDS, options);
			
			if (options.containsKey(PASSWORD)) {
				config.setProperty(PASSWORD, new String(PasswordUtils.encrypt(ENCRYPT_KEY, options.get(PASSWORD)), ENCODING));
			}
			
			if (options.containsKey(HTTP_PASSWORD)) {
				config.setProperty(HTTP_PASSWORD, new String(PasswordUtils.encrypt(ENCRYPT_KEY, options.get(HTTP_PASSWORD)), ENCODING));
			}

			config.store(new FileOutputStream(CONFIG_FILE), "");
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}
	}
	
	public static void save(String key, String value) {
		Map<String, String> config = load();
		config.put(key, value);
		save(config);
	}

   @SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<Project> loadProjectsFromCache(Map<String, String> options) {
    	List<Project> cachedProjects = new ArrayList<Project>();
		ObjectMapper objectMapper = new ObjectMapper();
		String projectsJson = options.get(PROJECTS_CACHE);
		if (!StringUtils.isEmpty(projectsJson)) {
			try {
				List projectMaps = objectMapper.readValue(projectsJson, List.class);
				for (Object projectMap : projectMaps) {
					Project project = new Project();
					project.putAll((Map<String, Object>) projectMap);
					cachedProjects.add(project);
				}
			} catch (Exception e) {
				cachedProjects.clear();
				logger.warn("Can't parse Projects JSON");
			}
		}
		return cachedProjects;
    }
   
	public static void writeProjectsToCache(Iterable<Project> projects) {
		try {
			String projectsJson = new ObjectMapper().writeValueAsString(projects);
			save(PROJECTS_CACHE, projectsJson);
		} catch (IOException e) {
			logger.warn(e.getMessage(), e);
		}
	}
}
