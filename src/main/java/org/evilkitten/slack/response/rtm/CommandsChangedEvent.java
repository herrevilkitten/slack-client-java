package org.evilkitten.slack.response.rtm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CommandsChangedEvent extends RtmEvent {
  @JsonProperty("event_ts")
  private double eventTimeStamp;
}
