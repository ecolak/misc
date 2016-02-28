package self.ec.btcbots.model;

import java.util.HashMap;
import java.util.Map;

public class BotConfig {

  public static final String PARAM_BUDGET = "budget";
  public static final String PARAM_START_PRICE = "start_price";
  public static final String PARAM_TRADE_DIFF_PCT = "trade_diff_pct";
  public static final String PARAM_MIN_NUM_BTC = "min_num_btc";

  public static final float DEFAULT_MIN_NUM_BTC = 0.01f;
  public static final float DEFAULT_TRADE_DIFF_PCT = 5;

  private String name;
  private BotType botType;
  private Schedule schedule;
  private Map<String, Object> params = new HashMap<>();

  public BotConfig() {}

  public BotConfig(String name, BotType botType, Schedule schedule, Map<String, Object> params) {
    this.name = name;
    this.botType = botType;
    this.schedule = schedule;
    this.params = params;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BotType getBotType() {
    return botType;
  }

  public void setBotType(BotType botType) {
    this.botType = botType;
  }

  public Schedule getSchedule() {
    return schedule;
  }

  public void setSchedule(Schedule schedule) {
    this.schedule = schedule;
  }

  public Map<String, Object> getParams() {
    return params;
  }

  public void setParams(Map<String, Object> params) {
    this.params = params;
  }

  public void addParam(String key, Object value) {
    params.put(key, value);
  }
}
