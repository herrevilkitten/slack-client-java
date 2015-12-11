package org.evilkitten.slack.response.rtm.mesage;

import lombok.Data;
import org.evilkitten.slack.entity.File;
import org.evilkitten.slack.response.rtm.MessageEvent;

@Data
public class FileShareEvent extends MessageEvent {
  private boolean upload;
  private File file;
}
