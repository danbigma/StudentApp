package com.studentapp.enums;

public enum Action {
	
	LIST("list"), ADD("add"), LOAD("load"), UPDATE("update"), DELETE("delete");
	
	private String action;
	
	Action(String action) {
		this.action = action;
	}
	
	public String getAction() {
		return action;
	}

}
