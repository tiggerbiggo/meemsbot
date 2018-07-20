package com.tiggerbiggo.meemsbot;

import com.tiggerbiggo.meemsbot.reactlet.fourinarow.FourRowLoader;
import com.tiggerbiggo.primaplay.calculation.Vector2;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import old.core.MainOld;
import old.core.Settings;

public class Main extends ListenerAdapter {
  private static final String TOKEN = Settings.getToken();
  private static final String COMMAND = "::";

  public static JDA jda;

  public static void main(String[] args) throws LoginException {
    jda = new JDABuilder(AccountType.BOT).setToken(TOKEN).buildAsync();
    jda.addEventListener(new Main());
  }

  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    if(event.getAuthor().isBot()) return;
    if(event.getMessage().getContentRaw().startsWith(COMMAND)){
      new FourRowLoader(event.getChannel());
    }
  }
}
