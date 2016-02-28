package self.ec.btcbots.model;

public class MonetaryAmount {

  private float value;
  private Currency currency;

  public MonetaryAmount() {}

  public MonetaryAmount(float value, Currency currency) {
    this.value = value;
    this.currency = currency;
  }

  public float getValue() {
    return value;
  }

  public void setValue(float value) {
    this.value = value;
  }

  public Currency getCurrency() {
    return currency;
  }

  public void setCurrency(Currency currency) {
    this.currency = currency;
  }

}
