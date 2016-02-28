package self.ec.btcbots.api;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

import self.ec.btcbots.api.TransactionResponse.Transfer;
import self.ec.btcbots.simu.Constants;

public class CoinbaseRestClient {

  public static final String BASE_URL = "https://coinbase.com/api/v1";
  public static final SimpleDateFormat tsFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

  private static final String USD = "USD";
  private static final Client client;
  static {
    ClientConfig config = new ClientConfig();
    config.register(JacksonFeature.class);
    client = ClientBuilder.newClient(config);
  }

  public static PriceCheckResponse getBuyPrice(float quantity) {
    return getBuyOrSellPrice(quantity, "prices/buy");
  }

  public static PriceCheckResponse getSellPrice(float quantity) {
    return getBuyOrSellPrice(quantity, "prices/sell");
  }

  private static PriceCheckResponse getBuyOrSellPrice(float quantity, String path) {
    return client.target(BASE_URL).path(path).queryParam("qty", quantity)
        .request(MediaType.APPLICATION_JSON).get(PriceCheckResponse.class);
  }

  public static TransactionResponse simulateBuy(float quantity, float buyPrice) {
    return simulateBuyOrSell(quantity, buyPrice, "buys");
  }

  public static TransactionResponse simulateSell(float quantity, float sellPrice) {
    return simulateBuyOrSell(quantity, sellPrice, "sells");
  }

  private static TransactionResponse simulateBuyOrSell(float quantity, float price, String path) {
    TransactionResponse response = new TransactionResponse();
    response.success = true;
    response.transfer = new Transfer();
    response.transfer.btc = new Price(String.valueOf(quantity), USD);
    response.transfer.created_at = tsFormat.format(new Date());
    response.transfer.status = "created";
    float coinbaseFee = price * Constants.DEFAULT_TRANSACTION_PCT / 100;
    float bankFee = 0.15f;
    float subtotal = quantity * price;
    float total = subtotal + coinbaseFee + bankFee;
    response.transfer.fees =
        new Fee[] {new Fee(new Price(String.valueOf(coinbaseFee), USD), new Price(
            String.valueOf(bankFee), USD))};
    response.transfer.subtotal = new Price(String.valueOf(subtotal), USD);
    response.transfer.total = new Price(String.valueOf(total), USD);

    if ("sells".equals(path))
      response.transfer.type = "Sell";
    else if ("buys".equals(path))
      response.transfer.type = "Buy";

    return response;
  }

  public static TransactionResponse buy(String apiKey, float quantity, float buyPrice) {
    return buyOrSell(apiKey, quantity, buyPrice, "buys");
  }

  public static TransactionResponse sell(String apiKey, float quantity, float sellPrice) {
    return buyOrSell(apiKey, quantity, sellPrice, "sells");
  }

  private static TransactionResponse buyOrSell(String apiKey, float quantity, float price,
      String path) {
    return client.target(BASE_URL).path(path).request(MediaType.APPLICATION_JSON)
        .post(Entity.json(new Quantity(quantity)), TransactionResponse.class);
  }

}
