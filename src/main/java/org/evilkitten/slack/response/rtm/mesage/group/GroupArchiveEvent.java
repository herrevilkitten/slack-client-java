package org.evilkitten.slack.response.rtm.mesage.group;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.evilkitten.slack.response.rtm.MessageEvent;

import java.util.ArrayList;
import java.util.List;

@Data
public class GroupArchiveEvent extends MessageEvent {
  @JsonProperty("members")
  private List<String> memberIds = new ArrayList<>();
}
