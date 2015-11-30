package org.evilkitten.slack.response.rtm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PresenceChangeEvent extends RtmEvent {
  @JsonProperty("user")
  private String userId;
  private String presence;
}
