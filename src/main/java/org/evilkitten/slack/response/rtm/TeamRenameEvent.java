package org.evilkitten.slack.response.rtm;

import lombok.Data;

@Data
public class TeamRenameEvent extends RtmEvent {
  private String name;
}
