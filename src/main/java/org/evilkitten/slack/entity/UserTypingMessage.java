package org.evilkitten.slack.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserTypingMessage extends Message {
  @JsonProperty("channel")
  public String channelId;

  @JsonProperty("user")
  public String userId;
}
