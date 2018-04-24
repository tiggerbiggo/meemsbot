import com.tiggerbiggo.prima.calculation.ColorTools;
import com.tiggerbiggo.prima.core.Vector2;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MandelMessageOld extends ListenerAdapter{
    /*public static final int W = 200;
    public static final int H = 200;

    public static final String UL = "↖";
    public static final String UR = "↗";
    public static final String BL = "↙";
    public static final String BR = "↘";

    private Vector2 offset;
    private double zoom;
    private Message control;

    public MandelMessageOld(Vector2 offset, double zoom, Message control) {
        this.offset = offset;
        this.zoom = zoom;
        this.control = control;

        control.addReaction(UL).queue();
        control.addReaction(UR).queue();
        control.addReaction(BL).queue();
        control.addReaction(BR).queue();
    }

    public MandelMessageOld(MessageChannel channel){
        this(
                new Vector2(-2),
                0.25,
                core.MessageTools.sendMessageWithImage(
                        "Mandel: ",
                        "mandel.gif",
                        generate(W, H, Color.BLACK, Color.WHITE), channel));
    }

    public static BufferedImage generate(int w, int h, Color cA, Color cB){
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        for(int i=0; i<w; i++){
            for(int j=0; j<h; j++){
                Vector2 z = Vector2.ZERO;

                Vector2 c = new Vector2((double)i/w, (double)j/h);
                c = Vector2.divide(c, new Vector2(zoom));
                c = Vector2.add(c, offset);

                img.setRGB(i, j, cA.getRGB());
                for(int iter=0; iter<300; iter++){
                    double a, b;
                    a=z.X();
                    b=z.Y();

                    //perform calculation for this iteration
                    z = new Vector2(
                            (a*a)-(b*b)+c.Y(),
                            (2*a*b) + c.X()
                    );

                    //check for out of bounds
                    if(z.magnitude() >2) {
                        img.setRGB(i, j, ColorTools.colorLerp(cA, cB, iter*0.1).getRGB());
                        break;
                    }
                }
            }
        }
        return img;
    }



    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        System.out.println("Reaction Added: " + event.getReactionEmote().getName());
        if(!event.getUser().isBot()){
            switch(event.getReactionEmote().getName()){
                case UL:
                    control.getChannel().sendMessage("UL").queue();
                    break;
                case UR:
                    control.getChannel().sendMessage("UR").queue();
                    break;
                case BL:
                    control.getChannel().sendMessage("BL").queue();
                    break;
                case BR:
                    control.getChannel().sendMessage("BR").queue();
                    break;
            }
        }
    }*/
}
