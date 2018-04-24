package reactlet.mandel;

import com.tiggerbiggo.prima.calculation.Calculation;
import com.tiggerbiggo.prima.calculation.ColorTools;
import com.tiggerbiggo.prima.core.Vector2;
import core.MessageTools;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import reactlet.ReactProcess;
import reactlet.ReactableMessage;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MandelMessage extends ReactableMessage {
    private Vector2 offset;
    private double zoom;

    Message m;

    private static final int W = 200;
    private static final int H = 200;

    public static final String UL = "↖";
    public static final String UR = "↗";
    public static final String BL = "↙";
    public static final String BR = "↘";

    public MandelMessage(MessageChannel c){
        super(c, true, true);

        offset = new Vector2(-2);
        zoom = 0.25;

        addReactProcess(new ReactProcess(BL) {
            @Override
            protected void onReact() {
                double delt = 1/(2*zoom);
                offset = Vector2.add(offset, new Vector2(0,delt));
                zoom *= 2;
                sendNextMessage();
            }
        });

        addReactProcess(new ReactProcess(BR) {
            @Override
            protected void onReact() {
                double delt = 1/(2*zoom);
                offset = Vector2.add(offset, new Vector2(delt));
                zoom *= 2;
                sendNextMessage();
            }
        });

        addReactProcess(new ReactProcess(UL) {
            @Override
            protected void onReact() {
                offset = Vector2.add(offset, new Vector2(0));
                zoom *= 2;
                sendNextMessage();
            }
        });

        addReactProcess(new ReactProcess(UR) {
            @Override
            protected void onReact() {
                double delt = 1/(2*zoom);
                offset = Vector2.add(offset, new Vector2(delt, 0));
                zoom *= 2;
                sendNextMessage();
            }
        });

        addReactProcess(new ReactProcess("\uD83D\uDCF7") { //render
            @Override
            protected void onReact() {
                MessageTools.sendMessageAsync("Rendering...", m.getChannel());
                m.getJDA().removeEventListener(this);
                m.delete().complete();
                control.delete().complete();
                MessageTools.sendMessageWithImage(
                        "Rendered:",
                        "full",
                        generate(1000,1000,Color.BLACK, Color.WHITE),
                        m.getChannel());
            }
        });

        addReactProcess(new ReactProcess("\uD83D\uDD01") { //reset
            @Override
            protected void onReact() {
                offset = new Vector2(-2);
                zoom = 0.25;
                sendNextMessage();
            }
        });

        addReactProcess(new ReactProcess("❎") {
            @Override
            protected void onReact() {
                m.delete().complete();
                control.delete().complete();
                m.getJDA().removeEventListener(this);
            }
        });

        m = generateFractalMessage();
        control = MessageTools.sendMessage("Controls:", c);
        doReactions();
    }

    private Message generateFractalMessage(){
        BufferedImage img = generate(W, H, Color.BLACK, Color.WHITE);
        return MessageTools.sendMessageWithImage("Here's a Mandelbrot", "mandel", img, c);
    }

    public BufferedImage generate(int w, int h, Color cA, Color cB){
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        for(int i=0; i<w; i++){
            for(int j=0; j<h; j++){
                Vector2 z = Vector2.ZERO;

                Vector2 c = new Vector2((double)i/w, (double)j/h);
                //Vector2 c = new Vector2((double)j/h, (double)i/w);

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
                        //img.setRGB(i, j, ColorTools.colorLerp(cA, cB, iter*0.1).getRGB());
                        img.setRGB(i, j, ColorTools.colorLerp(cA, cB, Calculation.modLoop(iter*0.1, true)).getRGB());
                        break;
                    }
                }
            }
        }
        return img;
    }

    public void sendNextMessage() {
        m.delete().queue();
        m = generateFractalMessage();
    }
}
