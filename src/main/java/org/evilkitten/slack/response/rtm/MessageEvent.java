package org.evilkitten.slack.response.rtm;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import org.evilkitten.slack.response.rtm.text.BotMessageEvent;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "subtype")
@JsonSubTypes({
    @JsonSubTypes.Type(value = BotMessageEvent.class, name = "bot_message")
})
@Data
public class MessageEvent extends RtmEvent {
  @JsonProperty("channel")
  private String channelId;

  @JsonProperty("user")
  private String userId;

  @JsonProperty("team")
  private String teamId;

  private String text;

  @JsonProperty("ts")
  private double timestamp;

  private String subtype;

  @JsonProperty("is_starred")
  private boolean starred;

  @JsonProperty("pinned_to")
  private List<String> pinnedTo;

  private List<Reaction> reactions;

  @Data
  public class Reaction {
    private String name;
    private int count;
    private List<String> users;
  }
}
