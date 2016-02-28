package self.ec.btcbots.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "accounts")
public class Account extends BaseEntity {

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "usd_balance")
  private Float usdBalance;

  @Column(name = "btc_balance")
  private Float btcBalance;

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
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
