package org.evilkitten.slack.response;

import org.evilkitten.slack.SlackBot;

public interface PostProcessing {
  public void postProcess(SlackBot slackBot);
}
