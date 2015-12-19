package org.evilkitten.slack.response.rtm.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.evilkitten.slack.entity.File;
import org.evilkitten.slack.response.rtm.RtmEvent;

@Data
public class FilePrivateEvent extends RtmEvent {
  @JsonProperty("file")
  private String fileId;
}
