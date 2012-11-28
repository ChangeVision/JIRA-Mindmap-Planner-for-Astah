package com.change_vision.astah.extension.plugin.mindmapplanner.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JList;
import javax.swing.ListModel;

import org.apache.commons.lang.ArrayUtils;

import com.change_vision.astah.extension.plugin.mindmapplanner.model.FieldEnum;
import com.change_vision.astah.extension.plugin.mindmapplanner.model.ValueEnum;

public class RequestUtils {
	public static final String UTF8 = "utf-8";

	public static String buildQueryParameters(Map<String, Object> params) {
		StringBuilder queryParameters = new StringBuilder();

		Iterator<String> keys = params.keySet().iterator();
		while (keys.hasNext()) {
			if (queryParameters.length() > 0) {
				queryParameters.append("&");
			}

			String key = keys.next();
			Object value = params.get(key);
			queryParameters.append(key);
			queryParameters.append("=");

			if (value instanceof String) {
				queryParameters.append("'");
				queryParameters.append(encode((String) value));
				queryParameters.append("'");
			} else {
				queryParameters.append(encode((String) value));
			}
		}
		return queryParameters.toString();
	}

	public static String encode(String value) {
		try {
			return URLEncoder.encode(value, UTF8);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	// 全選択と選択なしの場合は絞り込みは行わない
	@SuppressWarnings("unchecked")
	public static String buildQueryPart(FieldEnum field, JList valueList) {
		StringBuilder jqlBuilder = new StringBuilder();
		ListModel valueListModel = valueList.getModel();
		int[] selectedIndices = valueList.getSelectedIndices();
		 // 選択されてないはずのvalueListModel.getSize()の値が入ってしまうため削除する
		selectedIndices = ArrayUtils.removeElement(selectedIndices, valueListModel.getSize());
		
		if (field == FieldEnum.NONE || selectedIndices == null || selectedIndices.length == 0) {
			return "";
		}
		
		Object firstValue = valueListModel.getElementAt(selectedIndices[0]);
		if (firstValue == ValueEnum.NONE) {
			jqlBuilder.append(field.getQuery());
			jqlBuilder.append(" is null");
			if (selectedIndices.length > 1) {
				jqlBuilder.append(" or ");
			}
		}
		
		if ((firstValue == ValueEnum.NONE) ? selectedIndices.length > 1 : selectedIndices.length > 0) {
			jqlBuilder.append(field.getQuery());
			jqlBuilder.append(" in (");
			int startIndex = (firstValue == ValueEnum.NONE) ? 1 : 0;
			for (int i = startIndex; i < selectedIndices.length; i++) {
				Object value = valueListModel.getElementAt(selectedIndices[i]);
				if (!(value instanceof Map)) {
					continue;
				}
				
				if (i != startIndex) {
					jqlBuilder.append(", ");
				}

				String paramKey = field.getParamKey();
				String name = (String) ((Map<String, Object>) value).get(paramKey);
				jqlBuilder.append("'");
				jqlBuilder.append(name.replace("'", "\\'"));
				jqlBuilder.append("'");
			}
			jqlBuilder.append(")");
		}
		
		return jqlBuilder.toString();
	}
}
