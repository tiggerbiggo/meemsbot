package old.reactlet.mandel;

import static com.tiggerbiggo.primaplay.core.Main.chain;

import com.tiggerbiggo.primaplay.calculation.Vector2;
import com.tiggerbiggo.primaplay.graphics.SimpleGradient;
import com.tiggerbiggo.primaplay.node.core.NodeHasOutput;
import com.tiggerbiggo.primaplay.node.core.RenderNode;
import com.tiggerbiggo.primaplay.node.implemented.*;
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent;
import old.core.MessageTools;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.function.Function;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import com.tiggerbiggo.meemsbot.reactlet.ReactProcess;
import com.tiggerbiggo.meemsbot.reactlet.ReactableMessage;

public class MandelMessage extends ReactableMessage {

  private Vector2 offset;
  private double zoom;

  private RenderNode r;
  private MapGenNode gen;

  Message m;

  private static final int W = 200;
  private static final int H = 200;

  public static final String UL = "↖";
  public static final String UR = "↗";
  public static final String BL = "↙";
  public static final String BR = "↘";

  Vector2 A, B;

  public MandelMessage(MessageChannel c) {
    super(c, true, true);

    A = B = Vector2.ZERO;

    gen = new MapGenNode(Vector2.MINUSTWO, Vector2.TWO);
    NodeHasOutput o = chain(gen, new MandelNode(500, 0.03));
    o = chain(o, new AnimationNode(new Function<Double, Vector2>() {
      @Override
      public Vector2 apply(Double aDouble) {
        return new Vector2(1-aDouble);
      }
    }));
    o = chain(o, new GradientNode(new SimpleGradient(Color.green, Color.blue, true)));
    //o = chain(o, new GradientNode(new HueCycleGradient()));
    o = chain(o, new SuperSampleNode(3));

    r = new BasicRenderNode();
    r.link(o);

    offset = new Vector2(-2);
    zoom = 0.25;

    addReactProcess(new ReactProcess(BL) {
      @Override
      protected void onReact(
          GenericMessageReactionEvent e) {
        double delt = 1 / (2 * zoom);
        offset = Vector2.add(offset, new Vector2(0, delt));
        zoom *= 2;
        sendNextMessage();
      }
    });

    addReactProcess(new ReactProcess(BR) {
      @Override
      protected void onReact(
          GenericMessageReactionEvent e) {
        double delt = 1 / (2 * zoom);
        offset = Vector2.add(offset, new Vector2(delt));
        zoom *= 2;
        sendNextMessage();
      }
    });

    addReactProcess(new ReactProcess(UL) {
      @Override
      protected void onReact(
          GenericMessageReactionEvent e) {
        offset = Vector2.add(offset, new Vector2(0));
        zoom *= 2;
        sendNextMessage();
      }
    });

    addReactProcess(new ReactProcess(UR) {
      @Override
      protected void onReact(
          GenericMessageReactionEvent e) {
        double delt = 1 / (2 * zoom);
        offset = Vector2.add(offset, new Vector2(delt, 0));
        zoom *= 2;
        sendNextMessage();
      }
    });

    addReactProcess(new ReactProcess("\uD83D\uDCF7") { //render
      @Override
      protected void onReact(
          GenericMessageReactionEvent e) {
        MessageTools.sendMessageAsync("Rendering...", m.getChannel());
        m.getJDA().removeEventListener(this);
        m.delete().complete();
        control.delete().complete();
        MessageTools.sendMessageWithGif(
            "Rendered:",
            "full",
            r.render(450, 450, 30),
            m.getChannel());
        MessageTools.sendMessage("A: " + A.toString(), m.getChannel());
        MessageTools.sendMessage("B: " + B.toString(), m.getChannel());
      }
    });

    addReactProcess(new ReactProcess("\uD83D\uDD01") { //reset
      @Override
      protected void onReact(
          GenericMessageReactionEvent e) {
        offset = new Vector2(-2);
        zoom = 0.25;
        sendNextMessage();
      }
    });

    addReactProcess(new ReactProcess("❎") {
      @Override
      protected void onReact(
          GenericMessageReactionEvent e) {
        m.delete().complete();
        control.delete().complete();
        m.getJDA().removeEventListener(this);
      }
    });

    m = generateFractalMessage();
    control = MessageTools.sendMessage("Controls:", c);
    doReactions();
  }

  private Message generateFractalMessage() {
    BufferedImage img = generate(W, H, Color.BLACK, Color.WHITE);
    return MessageTools.sendMessageWithImage("Here's a Mandelbrot", "mandel", img, c);
  }

  public BufferedImage generate(int w, int h, Color cA, Color cB) {
    BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);


    A = Vector2.ZERO;
    B = Vector2.ONE;

    A = Vector2.divide(A, new Vector2(zoom));
    A = Vector2.add(A, offset);

    B = Vector2.divide(B, new Vector2(zoom));
    B = Vector2.add(B, offset);

    gen.set(A, B);

    return r.renderSingle(w, h);

    /*for (int i = 0; i < w; i++) {
      for (int j = 0; j < h; j++) {
        Vector2 z = Vector2.ZERO;

        Vector2 c = new Vector2((double) i / w, (double) j / h);
        //Vector2 c = new Vector2((double)j/h, (double)i/w);

        c = Vector2.divide(c, new Vector2(zoom));
        c = Vector2.add(c, offset);

        img.setRGB(i, j, cA.getRGB());
        for (int iter = 0; iter < 300; iter++) {
          double a, b;
          a = z.X();
          b = z.Y();

          //perform calculation for this iteration
          z = new Vector2(
              (a * a) - (b * b) + c.Y(),
              (2 * a * b) + c.X()
          );

          //check for out of bounds
          if (z.magnitude() > 2) {
            //img.setRGB(i, j, ColorTools.colorLerp(cA, cB, iter*0.1).getRGB());
            img.setRGB(i, j,
                ColorTools.colorLerp(cA, cB, Calculation.modLoop(iter * 0.1, true)).getRGB());
            break;
          }
        }
      }
    }
    return img;*/
  }

  public void sendNextMessage() {
    m.delete().queue();
    m = generateFractalMessage();
  }
}
