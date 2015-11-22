package org.evilkitten.slack.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PongMessage extends Message {
  @JsonProperty("reply_to")
  public String replyTo;
}
