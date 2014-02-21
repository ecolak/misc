package self.ec.btcbots.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "accounts")
public class Account extends BaseEntity {

	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;
	
	@Column(name = "usd_balance")
	private Float usdBalance;
	
	@Column(name = "btc_balance")
	private Float btcBalance;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Float getUsdBalance() {
		return usdBalance;
	}

	public void setUsdBalance(Float usdBalance) {
		this.usdBalance = usdBalance;
	}

	public Float getBtcBalance() {
		return btcBalance;
	}

	public void setBtcBalance(Float btcBalance) {
		this.btcBalance = btcBalance;
	}
	
}