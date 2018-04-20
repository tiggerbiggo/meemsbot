import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.role.GenericRoleEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A Reactable message which links behaviours to discord reactions
 */
public abstract class ReactableMessage extends ListenerAdapter{
    private List<ReactProcess> reactions;
    protected MessageChannel c;
    protected Message m;

    /**
     * Creates a new Reactable Message linked to a given channel
     * @param c The channel to post in
     */
    public ReactableMessage(MessageChannel c){
        this.c = c;
        reactions = new ArrayList<>();
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
     * Listens to reaction add events. If the event matches the correct message,
     * and was not reacted to by a bot, this method will trigger the appropriate
     * Reaction Process object.
     * @param event Event passed by JDA
     */
    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event){
        if(event.getUser().isBot())return;
        if(!event.getMessageId().equals(m.getId())) return;

        String react = event.getReactionEmote().getName();
        for(ReactProcess proc : reactions){
            if(proc.check(react))
                break;

        }
    }

    /**
     * Adds all reactions to the message
     */
    protected void doReactions(){
        for(ReactProcess p : reactions){
            m.addReaction(p.reaction).queue();
        }
    }
}
