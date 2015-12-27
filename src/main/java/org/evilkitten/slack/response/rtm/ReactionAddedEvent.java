package org.evilkitten.slack.response.rtm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReactionAddedEvent extends RtmEvent {
  @JsonProperty("user")
  private String userId;

  private String item;

  @JsonProperty("event_ts")
  private double eventTimeStamp;

  private String name;
}
