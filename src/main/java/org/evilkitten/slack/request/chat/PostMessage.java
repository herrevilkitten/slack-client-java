package org.evilkitten.slack.request.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.evilkitten.slack.entity.Attachment;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public class PostMessage {
  private String token;
  private String channel;
  private String text;

  private Optional<String> username = Optional.empty();

  @JsonProperty("as_user")
  private Optional<Boolean> asUser = Optional.empty();

  private Optional<String> parse;

  @JsonProperty("link_names")
  private Optional<Integer> linkNames;

  private Optional<List<Attachment>> attachments;

  @JsonProperty("unfurl_links")
  private Optional<Boolean> unfurlLinks;

  @JsonProperty("unfurl_media")
  private Optional<Boolean> unfurlMedia;
}
