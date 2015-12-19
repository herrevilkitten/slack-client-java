package org.evilkitten.slack.response.rtm.mesage.group;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.evilkitten.slack.response.rtm.MessageEvent;

@Data
public class GroupNameEvent extends MessageEvent {
  private String name;

  @JsonProperty("old_name")
  private String oldName;
}
