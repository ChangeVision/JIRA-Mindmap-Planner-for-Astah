package com.change_vision.astah.extension.plugin.mindmapplanner.view;

import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.astah.extension.plugin.mindmapplanner.Messages;
import com.change_vision.astah.extension.plugin.mindmapplanner.util.ConfigurationUtils;
import com.change_vision.astah.extension.plugin.mindmapplanner.validator.JiraUrlVerifier;
import com.change_vision.astah.extension.plugin.mindmapplanner.validator.PasswordVerifier;
import com.change_vision.astah.extension.plugin.mindmapplanner.validator.UserNameVerifier;
import com.github.jira.commons.client.JiraClient;

@SuppressWarnings("serial")
public class ConnectionSettingDialog extends JDialog {
	private static final Logger logger = LoggerFactory.getLogger(ConnectionSettingDialog.class);
	private JiraClient jira;
	
	public ConnectionSettingDialog(Frame parent, boolean modal, JiraClient jira) {
        super(parent, modal);
        this.jira = jira;
        
        initComponents();
        loadConfiguration();
        
        httpAuthCheck.setEnabled(false); // TODO HTTP AUTH
    }

    private void httpAuthCheckStateChanged(ChangeEvent evt) {
    	httpUserNameField.setEnabled(httpAuthCheck.isSelected());
    	httpPasswordField.setEnabled(httpAuthCheck.isSelected());
    }

    private void cancelButtonActionPerformed(ActionEvent evt) {
        dispose();
    }

    private void okButtonActionPerformed(ActionEvent evt) {
    	if (verifyForm() && verifyConnection()) {
    		jira.setContext(urlField.getText());
    		jira.setUserName(userNameField.getText());
    		jira.setPassword(new String(passwordField.getPassword()));
    		saveConfiguration();
    		dispose();
    	} else {
			JOptionPane.showMessageDialog(getParent(),
					Messages.getMessage("ConnectionSettingDialog.validate.error"),
					"Warning", JOptionPane.WARNING_MESSAGE);
    	}
    }
    
    private boolean verifyForm() {
    	boolean verifyUrl = urlField.getInputVerifier().verify(urlField);
    	boolean verifyUserName = userNameField.getInputVerifier().verify(userNameField);
    	boolean verifyPassword = passwordField.getInputVerifier().verify(passwordField);
    	
    	if (verifyUrl && verifyUserName && verifyPassword) {
    		if (httpAuthCheck.isSelected()) {
    			boolean verifyHttpUserName = httpUserNameField.getInputVerifier().verify(httpUserNameField);
    			boolean verifyHttpPassword = httpPasswordField.getInputVerifier().verify(httpPasswordField);
    			
        		return (verifyHttpUserName && verifyHttpPassword);
    		} else {
    			return true;
    		}
    	}
		return false;
    }
    
    private void loadConfiguration() {
    	Map<String, String> options = ConfigurationUtils.load();
    	urlField.setText(options.get(ConfigurationUtils.JIRA_URL));
    	userNameField.setText(options.get(ConfigurationUtils.USER_NAME));
    	passwordField.setText(options.get(ConfigurationUtils.PASSWORD));
    	
    	boolean httpAuth = Boolean.valueOf(options.get(ConfigurationUtils.HTTP_AUTH));
    	httpAuthCheck.setSelected(httpAuth);
    	httpUserNameField.setEnabled(httpAuth);
    	httpPasswordField.setEnabled(httpAuth);
    	httpUserNameField.setText(options.get(ConfigurationUtils.HTTP_USER_NAME));
    	httpPasswordField.setText(options.get(ConfigurationUtils.HTTP_PASSWORD));
    }
    
    private void saveConfiguration() {
    	Map<String, String> options = new HashMap<String, String>();
    	options.put(ConfigurationUtils.JIRA_URL, urlField.getText());
    	options.put(ConfigurationUtils.USER_NAME, userNameField.getText());
    	options.put(ConfigurationUtils.PASSWORD, new String(passwordField.getPassword()));
    	
    	if (httpAuthCheck.isSelected()) {
    		options.put(ConfigurationUtils.HTTP_AUTH, Boolean.TRUE.toString());
    		options.put(ConfigurationUtils.HTTP_USER_NAME, httpUserNameField.getText());
    		options.put(ConfigurationUtils.HTTP_PASSWORD, new String(httpPasswordField.getPassword()));
    	} else {
    		options.put(ConfigurationUtils.HTTP_AUTH, Boolean.FALSE.toString());
    		options.put(ConfigurationUtils.HTTP_USER_NAME, "");
    		options.put(ConfigurationUtils.HTTP_PASSWORD, "");
    	}
    	ConfigurationUtils.save(options);
    }
    
    private boolean verifyConnection() {
        final String context = urlField.getText();
        final String userName = userNameField.getText();
        final String password = new String(passwordField.getPassword());
        
        getParent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
		JiraClient target = new JiraClient(context, userName, password);
		boolean connectable = target.isConnectable();
		
		getParent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		
        return connectable;
    }

    private void validateButtonActionPerformed(ActionEvent evt) {
    	if (verifyForm() && verifyConnection()) {
			JOptionPane.showMessageDialog(getParent(),
					Messages.getMessage("ConnectionSettingDialog.validate.success"),
					"Information", JOptionPane.INFORMATION_MESSAGE);
        } else {
			JOptionPane.showMessageDialog(getParent(),
					Messages.getMessage("ConnectionSettingDialog.validate.error"),
					"Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Messages.getMessage("ConnectionSettingDialog.Form.title"));
        setName("Form");
        setResizable(false);

        urlLabel.setText(Messages.getMessage("ConnectionSettingDialog.urlLabel.text"));
        urlLabel.setName("urlLabel");

        urlField.setName("urlField");
        urlField.setInputVerifier(new JiraUrlVerifier());

        userNameLabel.setText(Messages.getMessage("ConnectionSettingDialog.userNameLabel.text"));
        userNameLabel.setName("userNameLabel");

        userNameField.setName("userNameField");
        userNameField.setInputVerifier(new UserNameVerifier());

        passwordLabel.setText(Messages.getMessage("ConnectionSettingDialog.passwordLabel.text"));
        passwordLabel.setName("passwordLabel");

        passwordField.setName("passwordField");
        passwordField.setInputVerifier(new PasswordVerifier());

        httpAuthCheck.setText(Messages.getMessage("ConnectionSettingDialog.httpAuthCheck.text"));
        httpAuthCheck.setName("httpAuthCheck");
        httpAuthCheck.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				httpAuthCheckStateChanged(e);
			}
		});

        httpUserNameLabel.setText(Messages.getMessage("ConnectionSettingDialog.httpUserNameLabel.text"));
        httpUserNameLabel.setName("httpUserNameLabel");

        httpUserNameField.setName("httpUserNameField");
        httpUserNameField.setInputVerifier(new UserNameVerifier());

        httpPasswordLabel.setText(Messages.getMessage("ConnectionSettingDialog.httpPasswordLabel.text"));
        httpPasswordLabel.setName("httpPasswordLabel");

        httpPasswordField.setName("httpPasswordField");
        httpPasswordField.setInputVerifier(new PasswordVerifier());

        validateButton.setText(Messages.getMessage("ConnectionSettingDialog.validateButton.text"));
        validateButton.setName("validateButton");
        validateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                validateButtonActionPerformed(evt);
            }
        });

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

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(10, 10, 10)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(passwordLabel)
                                .addComponent(userNameLabel)
                                .addComponent(urlLabel))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(urlField, GroupLayout.PREFERRED_SIZE, 280, GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(userNameField, GroupLayout.Alignment.LEADING)
                                    .addComponent(passwordField, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE))))
                        .addComponent(httpAuthCheck)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(29, 29, 29)
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(httpUserNameLabel)
                                .addComponent(httpPasswordLabel))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(httpPasswordField)
                                .addComponent(httpUserNameField, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(validateButton)
                            .addGap(67, 67, 67)
                            .addComponent(cancelButton)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(okButton)))
                    .addContainerGap())
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(8, 8, 8)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(urlField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(urlLabel))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(userNameLabel)
                        .addComponent(userNameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(passwordLabel)
                        .addComponent(passwordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addComponent(httpAuthCheck)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(11, 11, 11)
                            .addComponent(httpUserNameLabel))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(5, 5, 5)
                            .addComponent(httpUserNameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                    .addGap(8, 8, 8)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(httpPasswordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(httpPasswordLabel))
                    .addGap(18, 18, 18)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(validateButton)
                        .addComponent(cancelButton)
                        .addComponent(okButton))
                    .addContainerGap())
            );

        pack();
    }

    public static void main(String args[]) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(ConnectionSettingDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                ConnectionSettingDialog dialog = new ConnectionSettingDialog(new JFrame(), true, new JiraClient());
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

    private JButton validateButton = new JButton();
    private JButton okButton = new JButton();
    private JButton cancelButton = new JButton();
    private JCheckBox httpAuthCheck = new JCheckBox();
    private JPasswordField httpPasswordField = new JPasswordField();
    private JLabel httpPasswordLabel = new JLabel();
    private JTextField httpUserNameField = new JTextField();
    private JLabel httpUserNameLabel = new JLabel();
    private JPasswordField passwordField = new JPasswordField();
    private JLabel passwordLabel = new JLabel();
    private JTextField urlField = new JTextField();
    private JLabel urlLabel = new JLabel();
    private JTextField userNameField = new JTextField();
    private JLabel userNameLabel = new JLabel();
}
