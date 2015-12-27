package org.evilkitten.slack.response.rtm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PinAddedEvent extends RtmEvent {
  @JsonProperty("user")
  private String userId;

  @JsonProperty("channel_id")
  private String channelId;

  @JsonProperty("event_ts")
  private double eventTimeStamp;

  private String item;
}
