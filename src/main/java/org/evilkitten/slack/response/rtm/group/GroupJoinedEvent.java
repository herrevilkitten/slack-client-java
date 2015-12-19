package org.evilkitten.slack.response.rtm.group;

import lombok.Data;
import org.evilkitten.slack.entity.Channel;
import org.evilkitten.slack.response.rtm.RtmEvent;

@Data
public class GroupJoinedEvent extends RtmEvent {
  private Channel channel;
}
