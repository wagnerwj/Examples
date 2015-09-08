package com.nextcentury.lorelei.utils;

public enum ServiceNames {
	SERVICE1("service1"), SERVICE2("service2"), SERVICE3("service3"), SERVICE4("service4");
	
	private String shortName;

	public String getShortName() {
		return shortName;
	}
	
	private ServiceNames(String shortName){
		this.shortName=shortName;
	}
	
	
	public static ServiceNames fromString(String text){
		for(ServiceNames service:ServiceNames.values()){
			if(text.equals(service.getShortName())){
				return service;
			}
		}
		return null;
	}
}
