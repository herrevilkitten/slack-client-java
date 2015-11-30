package org.evilkitten.slack.response.rtm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserTypingEvent extends RtmEvent {
  @JsonProperty("channel")
  public String channelId;

  @JsonProperty("user")
  public String userId;
}
