package self.ec.btcbots.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "transactions")
public class Transaction {

	public static enum Type {
		BUY, SELL
	}
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
	private Long id;

	@Column(name = "bot_key")
	private String botKey;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	private Type type;
	
	@Column(name = "btc_amount")
	private Float btcAmount;
	
	@Column(name = "btc_price")
	private Float btcPrice;
	
	@Column(name = "total_fees")
	private Float totalFees;
	
	@Column(name = "ts")
	private Long timestamp;
	
	public Transaction() {}
	
	public Transaction(String botKey, Type type, Float btcAmount,
			Float btcPrice, Float totalFees, Long timestamp) {
		this.botKey = botKey;
		this.type = type;
		this.btcAmount = btcAmount;
		this.btcPrice = btcPrice;
		this.totalFees = totalFees;
		this.timestamp = timestamp;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBotKey() {
		return botKey;
	}

	public void setBotKey(String botKey) {
		this.botKey = botKey;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Float getBtcAmount() {
		return btcAmount;
	}

	public void setBtcAmount(Float btcAmount) {
		this.btcAmount = btcAmount;
	}

	public Float getBtcPrice() {
		return btcPrice;
	}

	public void setBtcPrice(Float btcPrice) {
		this.btcPrice = btcPrice;
	}

	public Float getTotalFees() {
		return totalFees;
	}

	public void setTotalFees(Float totalFees) {
		this.totalFees = totalFees;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

}