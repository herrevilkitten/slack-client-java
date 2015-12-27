package org.evilkitten.slack.response.rtm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EmailDomainChangedEvent extends RtmEvent {
  @JsonProperty("email_domain")
  private String emailDomain;

  @JsonProperty("event_ts")
  private double eventTimeStamp;
}
