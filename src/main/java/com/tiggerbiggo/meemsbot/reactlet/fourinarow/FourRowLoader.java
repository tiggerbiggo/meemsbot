package com.tiggerbiggo.meemsbot.reactlet.fourinarow;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import old.core.MessageTools;

public class FourRowLoader extends ListenerAdapter {
  User P1, P2;
  String P1Emote, P2Emote;
  boolean playerFlag = false;

  private MessageChannel c;
  private JDA jda;
  private Message m;

  public FourRowLoader(MessageChannel _c) {
    c = _c;
    jda = c.getJDA();
    jda.addEventListener(this);
    m = MessageTools.sendMessage("React with the emote you would like to use.", c);
  }

  @Override
  public void onMessageReactionAdd(MessageReactionAddEvent event) {
    //User player = event.getUser();
//    /String emote
    if(!playerFlag){
      P1 = event.getUser();
      P1Emote = event.getReactionEmote().getName();
      playerFlag = true;
      m.editMessage("Player 1 has selected emote: " + P1Emote).complete();
    }
    else{
      P2 = event.getUser();
      P2Emote = event.getReactionEmote().getName();
      new FourInARow(c, 10, 10, P1Emote, P2Emote, P1, P2);
      jda.removeEventListener(this);
    }
  }
}
