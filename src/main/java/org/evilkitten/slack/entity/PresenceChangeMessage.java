package org.evilkitten.slack.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PresenceChangeMessage extends Message {
  @JsonProperty("user")
  private String userId;
  private String presence;
}
