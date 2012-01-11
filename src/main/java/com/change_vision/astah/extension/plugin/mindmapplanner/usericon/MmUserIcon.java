package com.change_vision.astah.extension.plugin.mindmapplanner.usericon;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="mmUserIcon")
public class MmUserIcon {
	public static final String FILE_NAME = "JudeMMUserIconP.xml";
	
	@XmlElement(name="mmUserIconInfo")
	private List<MmUserIconInfo> mmUserIconInfos;

	public MmUserIcon() {
		mmUserIconInfos = new ArrayList<MmUserIconInfo>();
	}
	
	public List<MmUserIconInfo> getMmUserIconInfos() {
		if (mmUserIconInfos == null) {
			return new ArrayList<MmUserIconInfo>();
		}
		return mmUserIconInfos;
	}

	public void setMmUserIconInfos(List<MmUserIconInfo> mmUserIconInfos) {
		this.mmUserIconInfos = mmUserIconInfos;
	}
	
	public boolean containsAll(MmUserIcon valueToFind) {
		if (valueToFind == null) {
			return false;
		}
		
		try {
			List<UserIcon> userIcons = getMmUserIconInfos().get(0).getUserIcons();
			List<UserIcon> userIconsToFind = valueToFind.getMmUserIconInfos().get(0).getUserIcons();
			if (userIcons.containsAll(userIconsToFind)) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}
	
	public static MmUserIcon unmarshall(InputStream xmlInputStream) throws IOException {
		MmUserIcon root = null;
		try {
			root = JAXB.unmarshal(xmlInputStream, MmUserIcon.class);
		} catch (DataBindingException e) {
			throw new IOException(e);
		}
		return root;
	}
	
	public static MmUserIcon create(List<File> iconFiles) throws IOException {
		List<UserIcon> userIcons = new ArrayList<UserIcon>();
		for (File file : iconFiles) {
			UserIcon icon = UserIcon.create(file);
			
			if (icon != null) {
				userIcons.add(icon);
			}
		}
		
		MmUserIcon root = new MmUserIcon();
		MmUserIconInfo iconInfo = new MmUserIconInfo();
		iconInfo.setUserIcons(userIcons);
		root.setMmUserIconInfos(Arrays.asList(iconInfo));
		return root;
	}
	
	public void merge(MmUserIcon from) {
		if (from == null) {
			return;
		}

		List<MmUserIconInfo> fromIconInfos = from.getMmUserIconInfos();
		if (fromIconInfos == null || fromIconInfos.isEmpty()) {
			return;
		}
		
		List<MmUserIconInfo> mmUserIconInfos = getMmUserIconInfos();
		if (mmUserIconInfos.isEmpty()) {
			mmUserIconInfos.add(new MmUserIconInfo());
		}
		
		MmUserIconInfo newUserIconInfo = mmUserIconInfos.get(0);
		List<UserIcon> newUserIcons = newUserIconInfo.getUserIcons();
		
		MmUserIconInfo fromUserIconInfo = fromIconInfos.get(0);
		List<UserIcon> fromUserIcons = fromUserIconInfo.getUserIcons();
		
		if (fromUserIcons == null || fromUserIcons.isEmpty()) {
			return;
		}
		
		for (UserIcon fromUserIcon : fromUserIcons) {
			if (!newUserIcons.contains(fromUserIcon)) {
				newUserIcons.add(fromUserIcon);
			}
		}

		newUserIconInfo.setUserIcons(newUserIcons);
	}
}
/*
<mmUserIcon>
<mmUserIconInfo>
 <name>jude.usericons</name>
 <userIcon>
  <uuid>zhf-g73xgzao-p95sux--35ll3t-73ed29994297df888d0899a5b073b90e</uuid>
  <iconContent>/9j/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAAUCAAQABAEASIAAhEBAxEBBCIA/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADgQBAAIRAxEEAAA/APWvEOviL7RploZRd7ADInGwnBxk/wCyScjpx+C+H9e8wwaXeGQ3YQ4kfB8zqRyOvy4Oe/P4s8V2d3Lc2FzZWzyyqWjLRjkE425I7Dk5PA79aPC1rdreahdX9s8U77E3SLgkjO7HseOnHTHArO8ufyPY5aH1K9td99b3t93W3/Dnv9FFFf/Z</iconContent>
  <iconExtension>jpg</iconExtension>
  <iconLabel>梅</iconLabel>
 </userIcon>
 <userIcon>
  <uuid>zhg-g73xgzao-p95sux--35ll3t-73ed29994297df888d0899a5b073b90e</uuid>
  <iconContent>/9j/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAAUCAAQABAEASIAAhEBAxEBBCIA/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADgQBAAIRAxEEAAA/APYtb1k2cphhkwVQhygDFWYYTOewOCfqvrgu0XV7m9uXt7tYxJ5YlXyxgAHBweSejKenr6VNc6R5sepSMRNLdJhEKgBMDjGe+QCT7Djilg0gJJp1w74uLWIRuFJKthSP0JPPv+XCoYj2vNfS+3lt+WplafNc9/ooor//2Q==</iconContent>
  <iconExtension>jpg</iconExtension>
  <iconLabel>竹</iconLabel>
 </userIcon>
 <userIcon>
  <uuid>2pv-gd84p7et-fe6nch--35ll3t-73ed29994297df888d0899a5b073b90e</uuid>
  <iconContent>iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABVElEQVR42mNgoAWwCPP7T5EBhn5e/1ENDCbewI3rNv7X9bD9D6IRBrriN8AwzOs/yFZdoEINoGbdghowBhmi4WqJYhhh24Ean3++/f/47fVwg0AYJK9gafRfw9ESv2HaeTX/j99Z/18vS+n/7ddn/l99duj/sTvr/uvkV4NdArbEz/Y/yNUogQQLKJACrdzq/7dfnQG7BGTI6ftb/u+4NBcoXoPfO6DAAvlZPav6/9Xnh8CaJx2s/3/s9rr/2y7N/L/sZPt/9ewq/F5QtjP6r5JRBXY2yObjQM3FyyP+rz838f/8441gOVBYgLyB1QBYqCumVwKdPef/VqDN64Cag3tt/k87XPlfKa0K7g309AIGoKgEKZBPrfyvAMQgZ4Nsnnqo8r9VjfZ/+ZSK/3jTAzIf5FTZpMr/sskVQFwJZpOUpJUdjeBRR1aeAKVK8jNUmBdRmgGWYPGsjrOhhgAAAABJRU5ErkJggg==</iconContent>
  <iconExtension>png</iconExtension>
  <iconLabel>デバッグパートナー</iconLabel>
 </userIcon>
 <userIcon>
  <uuid>26bj-gd9e6k3h-5jjoe8--35ll3t-73ed29994297df888d0899a5b073b90e</uuid>
  <iconContent>iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAkElEQVR42mNgwAEuvZj/f/55AzB+9fXyfwZSAUUG3Hi9Cq6ZLEPIMuA/FIDY6JphGF0d9QzApeH006T/c8/p/cdnIOUG4JL8/Xfu/4/fJ/y/864SzMamZskl6/+0MwCEZ5/V+b/yiv1/fGooNwAUJcQkmkcfDmCoA0cnxQYgKzjwoIJgegepAallwGYDOQYAAHdl1kriahREAAAAAElFTkSuQmCC</iconContent>
  <iconExtension>png</iconExtension>
  <iconLabel>松</iconLabel>
 </userIcon>
</mmUserIconInfo>
</mmUserIcon>
*/