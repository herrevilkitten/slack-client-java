package org.evilkitten.slack.response.rtm;

import lombok.Data;

@Data
public class PreferenceChangeEvent extends RtmEvent {
  private String name;
  private String value;
}
