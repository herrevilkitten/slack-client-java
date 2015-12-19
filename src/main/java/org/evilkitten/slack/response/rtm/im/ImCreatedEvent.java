package org.evilkitten.slack.response.rtm.im;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.evilkitten.slack.entity.Channel;
import org.evilkitten.slack.response.rtm.RtmEvent;

@Data
public class ImCreatedEvent extends RtmEvent {
  @JsonProperty("user")
  private String userId;

  private Channel channel;
}
