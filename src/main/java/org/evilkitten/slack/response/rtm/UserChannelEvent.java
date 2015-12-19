package org.evilkitten.slack.response.rtm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserChannelEvent extends RtmEvent {
  @JsonProperty("channel")
  private String channelId;

  @JsonProperty("user")
  private String userId;
}
