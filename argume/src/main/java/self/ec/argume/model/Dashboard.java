package self.ec.argume.model;

import java.util.ArrayList;
import java.util.List;

public class Dashboard {

	private int argumeScore;
	private List<Argument> arguments = new ArrayList<Argument>();
	
	public Dashboard() {}
	
	public Dashboard(int argumeScore, List<Argument> arguments) {
		this.argumeScore = argumeScore;
		this.arguments = arguments;
	}
	public int getArgumeScore() {
		return argumeScore;
	}
	public void setArgumeScore(int argumeScore) {
		this.argumeScore = argumeScore;
	}
	public List<Argument> getArguments() {
		return arguments;
	}
	public void setArguments(List<Argument> arguments) {
		this.arguments = arguments;
	}
	
	
}
