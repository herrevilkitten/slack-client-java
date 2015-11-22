package org.evilkitten.slack.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TextMessage extends Message {
  @JsonProperty("channel")
  private String channelId;

  @JsonProperty("user")
  private String userId;

  @JsonProperty("team")
  private String teamId;

  private String text;

  @JsonProperty("ts")
  private double timestamp;
}
