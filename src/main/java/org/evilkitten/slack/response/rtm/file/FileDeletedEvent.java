package org.evilkitten.slack.response.rtm.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.evilkitten.slack.response.rtm.RtmEvent;

@Data
public class FileDeletedEvent extends RtmEvent {
  @JsonProperty("file_id")
  private String fileId;

  @JsonProperty("event_ts")
  private double eventTimeStamp;
}
