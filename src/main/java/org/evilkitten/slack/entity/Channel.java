package org.evilkitten.slack.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.evilkitten.slack.SlackBot;

import java.util.ArrayList;
import java.util.List;

@Data
public class Channel {
  private final SlackBot slackBot;

  public Channel(SlackBot slackBot) {
    this.slackBot = slackBot;
  }

  private String id;
  private String name;
  private long created;

  @JsonProperty("creator")
  private String creatorId;
  private User creator;

  @JsonProperty("is_archived")
  private boolean archived;

  @JsonProperty("is_general")
  private boolean general;

  private List<String> members = new ArrayList<>();

  private Topic topic;
  private Topic purpose;

  @JsonProperty("is_member")
  private boolean member;

  @JsonProperty("last_read")
  private double lastRead;

  @JsonProperty("unread_count")
  private int unreadCount;

  @JsonProperty("unread_count_display")
  private int unreadCountDisplay;

  @Data
  public class Topic {
    private String value;

    @JsonProperty("creator")
    private String creatorId;

    private User creator;

    @JsonProperty("last_set")
    private long lastSet;
  }

  public void postMessage(String text) {
  }
}
