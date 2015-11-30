package org.evilkitten.slack.response.rtm;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import org.evilkitten.slack.response.Response;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
    defaultImpl = RtmEvent.class)
@JsonSubTypes({
    @JsonSubTypes.Type(value = RtmEvent.class, name = "event"),
    @JsonSubTypes.Type(value = PresenceChangeEvent.class, name = "presence_change"),
    @JsonSubTypes.Type(value = HelloEvent.class, name = "hello"),
    @JsonSubTypes.Type(value = PongEvent.class, name = "pong"),
    @JsonSubTypes.Type(value = MessageEvent.class, name = "message"),
    @JsonSubTypes.Type(value = UserTypingEvent.class, name = "user_typing"),
})
@Data
public class RtmEvent extends Response {
  private long id;
  private String type;
}
