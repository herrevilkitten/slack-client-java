package org.evilkitten.slack.response.api;

import lombok.Data;
import org.evilkitten.slack.SlackBot;
import org.evilkitten.slack.entity.*;
import org.evilkitten.slack.response.PostProcessing;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Data
public class RtmStartResponse extends RtmResponse implements PostProcessing {
  private boolean ok;
  private Self self;
  private Team team;
  private List<Bot> bots = new ArrayList<>();
  private List<Channel> channels = new ArrayList<>();
  private List<User> users = new ArrayList<>();
  private URI url;
  private String error;

  @Override
  public void postProcess(SlackBot slackBot) {
    Stream
        .of(bots.stream(), channels.stream(), users.stream())
        .reduce(Stream::concat)
        .orElseGet(Stream::empty)
        .forEach((bot) -> bot.setSlackBot(slackBot));

    bots.stream()
        .forEach((bot) -> slackBot.getBots().put(bot.getId(), bot));

    channels.stream()
        .forEach((channel) -> slackBot.getChannels().put(channel.getId(), channel));

    users.stream()
        .forEach((user) -> slackBot.getUsers().put(user.getId(), user));
  }
}
