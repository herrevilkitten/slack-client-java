package org.evilkitten.slack.response.rtm;

import lombok.Data;

@Data
public class ManualPresenceChangeEvent extends RtmEvent {
  private String presence;
}
