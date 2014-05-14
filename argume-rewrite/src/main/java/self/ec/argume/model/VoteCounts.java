package self.ec.argume.model;

public class VoteCounts {

	private int favorable;
	private int against;
	
	public VoteCounts() {}
	
	public VoteCounts(int favorable, int against) {
		this.favorable = favorable;
		this.against = against;
	}

	public int getFavorable() {
		return favorable;
	}
	public void setFavorable(int favorable) {
		this.favorable = favorable;
	}
	public int getAgainst() {
		return against;
	}
	public void setAgainst(int against) {
		this.against = against;
	}
}