package com.nextcentury.lorelei.utils;

import java.beans.PropertyEditorSupport;


public class ServiceNamesConverter extends PropertyEditorSupport {
	

	@Override
    public void setAsText(String text) throws IllegalArgumentException {
		setValue(ServiceNames.fromString(text));
	}

}
