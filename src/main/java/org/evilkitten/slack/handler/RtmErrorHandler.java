package org.evilkitten.slack.handler;

public interface RtmErrorHandler extends RtmHandler {
  void onError();
}
