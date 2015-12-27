package org.evilkitten.slack.response.rtm;

import lombok.Data;

import java.net.URL;

@Data
public class TeamDomainChangeEvent extends RtmEvent {
  private URL url;
  private String domain;
}
