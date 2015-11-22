package org.evilkitten.slack.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = PresenceChangeMessage.class, name = "presence_change"),
    @JsonSubTypes.Type(value = HelloMessage.class, name = "hello"),
    @JsonSubTypes.Type(value = PongMessage.class, name = "pong"),
    @JsonSubTypes.Type(value = TextMessage.class, name = "message"),
    @JsonSubTypes.Type(value = UserTypingMessage.class, name = "user_typing"),
})
@Data
public class Message {
  private String type;
}
