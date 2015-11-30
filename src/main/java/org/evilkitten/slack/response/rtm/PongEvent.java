package org.evilkitten.slack.response.rtm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PongEvent extends RtmEvent {
  @JsonProperty("reply_to")
  public String replyTo;
}
