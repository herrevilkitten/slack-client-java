package org.evilkitten.slack.response.rtm.im;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.evilkitten.slack.response.rtm.RtmEvent;

@Data
public class ImMarkedEvent extends RtmEvent {
  @JsonProperty("channel")
  private String channelId;

  @JsonProperty("ts")
  private double timeStamp;
}
