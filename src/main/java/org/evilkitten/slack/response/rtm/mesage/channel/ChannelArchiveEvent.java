package org.evilkitten.slack.response.rtm.mesage.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.evilkitten.slack.response.rtm.MessageEvent;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChannelArchiveEvent extends MessageEvent {
  @JsonProperty("members")
  private List<String> memberIds = new ArrayList<>();
}
