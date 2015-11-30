package org.evilkitten.slack.request.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;
import org.evilkitten.slack.entity.Attachment;

import java.util.ArrayList;
import java.util.List;

@Data
public class PostMessage {
  @NonNull
  private String token;

  @NonNull
  private String channel;

  @NonNull
  private String text;

  private String username;

  @JsonProperty("as_user")
  private Boolean asUser;

  private String parse;

  @JsonProperty("link_names")
  private Integer linkNames;

  private List<Attachment> attachments = new ArrayList<>();

  @JsonProperty("unfurl_links")
  private Boolean unfurlLinks;

  @JsonProperty("unfurl_media")
  private Boolean unfurlMedia;
}
