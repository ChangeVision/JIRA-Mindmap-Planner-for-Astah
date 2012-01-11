package com.change_vision.astah.extension.plugin.mindmapplanner.validator;

import java.awt.Color;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.UIManager;

import com.change_vision.astah.extension.plugin.mindmapplanner.Messages;


public class JiraUrlVerifier extends InputVerifier {

	@Override
	public boolean verify(JComponent c) {
		boolean verified = false;
		JTextField textField = (JTextField) c;
		String text = textField.getText();
		
		if (text.matches("^http://.*|^https://.*")) {
			verified = true;
			textField.setBackground(Color.WHITE);
			textField.setToolTipText("");
		} else {
			textField.setBackground(Color.PINK);
			textField.setToolTipText(Messages.getMessage("validator.jira_url.invalid"));
			UIManager.getLookAndFeel().provideErrorFeedback(c);
		}
		
		return verified;
	}
	
//	public static void main(String[] args) {
//		System.out.println("http://hoge.com/".matches("^http://.*|^https://.*"));
//		System.out.println("https://hoge.com/".matches("^http://.*|^https://.*"));
//		System.out.println("ftp://hoge.com/".matches("^http://.*|^https://.*"));
//		System.out.println("scp://hoge.com/".matches("^http://.*|^https://.*"));
//		
//		JTextField textField = new JTextField("http://hoge.com/");
//		textField.setInputVerifier(new JiraUrlVerifier());
//		JFrame jFrame = new JFrame();
//		jFrame.setLayout(new FlowLayout());
//		jFrame.add(textField);
//		jFrame.add(new JTextField("          "));
//		jFrame.add(new JButton("hoge"));
//		jFrame.pack();
//		jFrame.setVisible(true);
//	}
}
