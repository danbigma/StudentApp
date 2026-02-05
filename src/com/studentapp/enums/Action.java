package com.studentapp.enums;

public enum Action {
    
    LIST("list"), ADD("add"), UPDATE("update"), DELETE("delete"), DASHBOARD("dashboard");
	
	private String action;
	
	Action(String action) {
		this.action = action;
	}
	
    public String getAction() {
        return action;
    }

}
