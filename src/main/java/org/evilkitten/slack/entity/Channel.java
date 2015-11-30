package org.evilkitten.slack.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.evilkitten.slack.Bottable;
import org.evilkitten.slack.SlackBot;
import org.evilkitten.slack.request.chat.PostMessage;
import org.evilkitten.slack.response.rtm.MessageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class Channel implements Bottable {
  @JsonIgnore
  private SlackBot slackBot;

  private String id;
  private String name;
  private long created;

  @JsonProperty("creator")
  private String creatorId;

  @JsonProperty("is_archived")
  private boolean archived;

  @JsonProperty("is_general")
  private boolean general;

  private List<String> members = new ArrayList<>();

  private Value topic;
  private Value purpose;

  @JsonProperty("is_member")
  private boolean member;

  @JsonProperty("last_read")
  private double lastRead;

  @JsonProperty("unread_count")
  private int unreadCount;

  @JsonProperty("unread_count_display")
  private int unreadCountDisplay;

  public void send(MessageEvent messageEvent) {
    Objects.requireNonNull(slackBot);
    slackBot.send(messageEvent);
  }

  public void send(String text) {
    MessageEvent messageEvent = new MessageEvent();
    messageEvent.setChannelId(id);
    messageEvent.setType("message");
    messageEvent.setText(text);
    send(messageEvent);
  }

  @Data
  public class Value {
    private String value;

    @JsonProperty("creator")
    private String creatorId;

    @JsonProperty("last_set")
    private long lastSet;
  }
}
