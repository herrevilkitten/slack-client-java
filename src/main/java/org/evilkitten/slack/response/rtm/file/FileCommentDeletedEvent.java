package org.evilkitten.slack.response.rtm.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.evilkitten.slack.entity.File;

@Data
public class FileCommentDeletedEvent {
  private File file;

  @JsonProperty("comment")
  private String commentId;
}
