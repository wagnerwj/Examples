package com.nextcentury.lorelei.utils;

public enum ActionNames {
	ACTION1("action1"), ACTION2("action2"),ACTION3("action3");
	
	private String shortName;

	public String getShortName() {
		return shortName;
	}
	
	private ActionNames(String shortName){
		this.shortName=shortName;
	}
	
	public static ActionNames fromString(String text){
		for(ActionNames action:ActionNames.values()){
			if(text.equals(action.getShortName())){
				return action;
			}
		}
		return null;
	}
}
