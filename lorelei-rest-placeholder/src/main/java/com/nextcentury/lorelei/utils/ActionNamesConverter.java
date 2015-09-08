package com.nextcentury.lorelei.utils;

import java.beans.PropertyEditorSupport;

public class ActionNamesConverter extends PropertyEditorSupport {

	
	@Override
    public void setAsText(String text) throws IllegalArgumentException {
		setValue(ActionNames.fromString(text));
	}
}
