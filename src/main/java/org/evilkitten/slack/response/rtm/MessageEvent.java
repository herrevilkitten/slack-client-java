package org.evilkitten.slack.response.rtm;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import org.evilkitten.slack.response.rtm.mesage.*;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "subtype",
    defaultImpl = MessageEvent.class)
@JsonSubTypes({
    @JsonSubTypes.Type(value = BotMessageEvent.class, name = "bot_message"),
    @JsonSubTypes.Type(value = MessageDeletedEvent.class, name = "message_deleted"),
    @JsonSubTypes.Type(value = MessageChangedEvent.class, name = "message_changed"),
    @JsonSubTypes.Type(value = ChannelJoinMessageEvent.class, name = "channel_join"),
    @JsonSubTypes.Type(value = MeMessageEvent.class, name = "me_message")
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

  private EditInfo edited;

  @Data
  public class EditInfo {
    @JsonProperty("user")
    private String userId;

    @JsonProperty("ts")
    private double timeStamp;
  }

  @Data
  public class Reaction {
    private String name;
    private int count;
    private List<String> users;
  }
}
