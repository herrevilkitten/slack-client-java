package org.evilkitten.slack.response.rtm;

import lombok.Data;
import org.evilkitten.slack.entity.User;

@Data
public class TeamJoinEvent extends RtmEvent {
  private User user;
}
