package org.evilkitten.slack.response.rtm.file;

import lombok.Data;
import org.evilkitten.slack.entity.File;

@Data
public class FileCommentAddedEvent {
  private File file;
  private String comment;
}
