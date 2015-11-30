package org.evilkitten.slack.response.rtm.text;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.evilkitten.slack.response.rtm.MessageEvent;

@Data
public class BotMessageEvent extends MessageEvent {
  @JsonProperty("bot_id")
  private String botId;

}
