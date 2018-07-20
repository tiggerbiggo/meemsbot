package com.tiggerbiggo.meemsbot.reactlet;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A Reactable message which links behaviours to discord reactions
 */
public class ReactableMessage extends ListenerAdapter{
    private List<ReactProcess> reactions;
    protected MessageChannel c;
    protected Message control;

    private boolean add, remove;

    /**
     * Creates a new Reactable Message linked to a given channel
     * @param c The channel to post in
     */
    public ReactableMessage(MessageChannel c, boolean add, boolean remove){
        this.c = c;
        this.add = add;
        this.remove = remove;
        c.getJDA().addEventListener(this);
        reactions = new ArrayList<>();
    }

    public void setMessage(Message m){
        control = m;
    }

    /**
     * Adds a new React Process
     * @param proc The process to add
     */
    public void addReactProcess(ReactProcess proc){
        if(proc == null) return;
        reactions.add(proc);
    }

    /**
     * Removes self from JDA listeners, and deletes the associated message
     */
    public void destroy(){
        control.getJDA().removeEventListener(this);
        control.delete().complete();
    }

    /**
     * Listens to reaction add events. If the event matches the correct message,
     * and was not reacted to by a bot, this method will trigger the appropriate
     * Reaction Process object.
     * @param event Event passed by JDA
     */
    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event){
        if(!add) return;
        if(event.getUser().isBot())return;
        if(!event.getMessageId().equals(control.getId())) return;

        reactionPressed(event.getReactionEmote().getName(), event);
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        if(!remove) return;
        if(event.getUser().isBot())return;
        if(!event.getMessageId().equals(control.getId())) return;

        reactionPressed(event.getReactionEmote().getName(), event);
    }

    private void reactionPressed(String react, GenericMessageReactionEvent e){
        for(ReactProcess proc : reactions){
            if(proc.check(react, e))
                break;
        }
    }

    /**
     * Adds all reactions to the message
     */
    public void doReactions(){
        for(ReactProcess p : reactions){
            control.addReaction(p.reaction).complete();
        }
    }
}
