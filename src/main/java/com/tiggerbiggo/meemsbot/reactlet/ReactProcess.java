package com.tiggerbiggo.meemsbot.reactlet;

import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent;

public abstract class ReactProcess {
    public String reaction;

    public ReactProcess(String reaction) {
        this.reaction = reaction;
    }

    protected abstract void onReact(GenericMessageReactionEvent e);

    public boolean check(String toCheck, GenericMessageReactionEvent e){
        if(toCheck.equals(reaction)){
            onReact(e);
            return true;
        }
        return false;
    }
}
