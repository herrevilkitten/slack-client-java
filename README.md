# slack-java-rtm
## A Java client for Slack's Real-Time Messaging API

WORK IN PROGRESS

### Example

    // Build a bot with the default options, listen for text messages
    SlackBot bot = new SlackBot.Builder("API TOKEN")
      .addHandler((RtmMessageHandler<TextMessage>) message -> System.err.println("Message: " + message.getText()))
      .build();

    // Connect to Slack -- the bot will run in its own thread
    bot.connect();

### Usage