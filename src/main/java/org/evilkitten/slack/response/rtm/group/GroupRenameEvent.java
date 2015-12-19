package org.evilkitten.slack.response.rtm.group;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.evilkitten.slack.entity.Channel;
import org.evilkitten.slack.response.rtm.RtmEvent;

@Data
public class GroupRenameEvent extends RtmEvent {
  @JsonProperty("channel")
  private Channel channel;
}
