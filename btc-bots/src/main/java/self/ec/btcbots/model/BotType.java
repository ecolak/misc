package self.ec.btcbots.model;

import org.quartz.Job;

import self.ec.btcbots.bot.SimpleBot;

public enum BotType {

  SIMPLE(SimpleBot.class), PREDICTIVE(SimpleBot.class);

  private Class<? extends Job> clazz;

  BotType(Class<? extends Job> clazz) {
    this.clazz = clazz;
  }

  public Class<? extends Job> getClazz() {
    return clazz;
  }
}
