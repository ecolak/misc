package com.abe.olasihaber.model;

import java.util.List;

public class Arguments {

	private List<Argument> arguments_for;
	
	private List<Argument> arguments_against;

	public Arguments() {}
	
	public Arguments(List<Argument> arguments_for,
			List<Argument> arguments_against) {
		this.arguments_for = arguments_for;
		this.arguments_against = arguments_against;
	}

	public List<Argument> getArguments_for() {
		return arguments_for;
	}

	public void setArguments_for(List<Argument> arguments_for) {
		this.arguments_for = arguments_for;
	}

	public List<Argument> getArguments_against() {
		return arguments_against;
	}

	public void setArguments_against(List<Argument> arguments_against) {
		this.arguments_against = arguments_against;
	}
	
}
