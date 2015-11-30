package org.evilkitten.slack.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.evilkitten.slack.Bottable;
import org.evilkitten.slack.SlackBot;

import java.net.URL;
import java.util.Map;

@Data
public class Bot implements Bottable {
  @JsonIgnore
  private SlackBot slackBot;

  private String id;
  private boolean deleted;
  private String name;
  private Map<String, URL> icons;

  @Data
  public static class Icon {
    @JsonProperty("image_48")
    private URL image48;
  }
}
