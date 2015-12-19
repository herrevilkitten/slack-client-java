package org.evilkitten.slack.response.rtm.group;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GroupUnarchiveEvent {
  @JsonProperty("channel")
  private String channelId;
}
