package org.evilkitten.slack.response.rtm.group;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GroupArchiveEvent {
  @JsonProperty("channel")
  private String channelId;
}
