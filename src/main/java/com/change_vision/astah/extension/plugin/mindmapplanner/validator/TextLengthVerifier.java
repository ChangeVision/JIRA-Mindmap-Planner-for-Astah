package com.change_vision.astah.extension.plugin.mindmapplanner.validator;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.UIManager;

import com.change_vision.astah.extension.plugin.mindmapplanner.Messages;


public class TextLengthVerifier extends InputVerifier {
	protected static final int MIN_LENGTH = 1;
	protected static final int MAX_LENGTH = 255;
	protected static final String MESSAGE_KEY = "validator.text.invalid";

	@Override
	public boolean verify(JComponent c) {
		boolean verified = false;
		JTextField textField = (JTextField) c;
		String text = textField.getText();
		
		int length = text.length();
		if (MIN_LENGTH <= length && length <= MAX_LENGTH) {
			verified = true;
			textField.setBackground(UIManager.getColor("TextField.background").brighter());
			textField.setToolTipText("");
		} else {
			textField.setBackground(UIManager.getColor("TextField.selectionBackground"));
			textField.setToolTipText(Messages.getMessage(MESSAGE_KEY, MIN_LENGTH, MAX_LENGTH));
			UIManager.getLookAndFeel().provideErrorFeedback(c);
		}
		
		return verified;
	}

//	public static void main(String[] args) {
//		JTextField textField = new JTextField("hoge");
//		textField.setInputVerifier(new ConnectionNameVerifier());
//		JFrame jFrame = new JFrame();
//		jFrame.setLayout(new FlowLayout());
//		jFrame.add(textField);
//		jFrame.add(new JTextField("          "));
//		jFrame.add(new JButton("hoge"));
//		jFrame.pack();
//		jFrame.setVisible(true);
//	}
}
