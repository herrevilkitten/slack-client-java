package org.evilkitten.slack.message.text;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.evilkitten.slack.message.TextMessage;

@Data
public class BotTextMessage extends TextMessage {
  @JsonProperty("bot_id")
  private String botId;

}
