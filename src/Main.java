import com.tiggerbiggo.prima.calculation.ColorTools;
import com.tiggerbiggo.prima.core.Builder;
import com.tiggerbiggo.prima.core.FileManager;
import com.tiggerbiggo.prima.core.Vector2;
import com.tiggerbiggo.prima.graphics.HueCycleGradient;
import com.tiggerbiggo.prima.processing.fragment.generate.MapGenFragment;
import com.tiggerbiggo.prima.processing.fragment.render.AnimationFragment;
import com.tiggerbiggo.prima.processing.fragment.render.RenderFragment;
import com.tiggerbiggo.prima.processing.fragment.transform.MandelFragment;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.ReconnectedEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.security.auth.login.LoginException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;


public class Main extends ListenerAdapter
{
    private static final String TOKEN = "NDM1MzczMTY2ODExMTUyMzg1.DbYAmw.wragsMYwJNVDGRceVb-YA57ddpM";
    private static final String COMMAND = "::";

    private static final String thicc = "卂乃匚刀乇下厶卄工丁长乚从\uD841口尸㔿尺丂丅凵リ山乂丫乙";
    private static final String alpha = "abcdefghijklmnopqrstuvwxyz";

    private SubstitutionSet subs = new SubstitutionSet();

    public static void main(String[] args)
            throws LoginException, RateLimitedException
    {
        JDA jda = new JDABuilder(AccountType.BOT).setToken(TOKEN).buildAsync();
        jda.addEventListener(new Main());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if (event.isFromType(ChannelType.TEXT))
        {
            Message m = event.getMessage();
            String s = m.getContentRaw();
            System.out.println(m.getAuthor() + s);
            if(s.startsWith("!"))return;
            if(m.getAuthor().isBot()){
                boolean t = false;
                for(char c : thicc.toCharArray()){
                    if(s.contains(c + "")){
                        t = true;
                    }
                }
                if(t) m.getChannel().sendMessage(convertThicc(s)).queue();
            }
            else if(s.startsWith(COMMAND)){
                handleCommand(s.substring(COMMAND.length()), m);
            }
            else if(Settings.subsEnabled){
                doSubstitutions(m);
            }
        }
    }

    private void doSubstitutions(Message m) {
        if(subs.contains(m.getContentRaw())){
            m.getChannel().sendMessage(subs.replaceAll(m.getContentRaw())).queue();
        }
    }

    public String convertThicc(String in){
        for(int i=0; i<thicc.length(); i++){
            in = in.replace(thicc.charAt(i), alpha.charAt(i));
        }
        return in;
    }

    public void handleCommand(String raw, Message m){
        int commandIndex = raw.indexOf(" ");
        String command, arg;
        String[] args;
        boolean hasArgs = false;
        if(commandIndex == -1) {
            command = raw;
            arg = "";
            args = new String[0];
        }
        else {
            command = raw.substring(0, commandIndex);
            arg = raw.substring(commandIndex+1);
            args = arg.split(" ");
            hasArgs = true;
        }



        switch(command){
            case "img":
                Color c;
                if(!hasArgs) c = Color.BLACK;
                else{
                    c = parseColor(arg);
                }
                m.getChannel().sendMessage("here's a thing:").addFile(convertImg(generateImg(c)), "img.png").queue();
                break;
            case "kick":
                if(m.getMentionedUsers().size() <=0) {
                    m.getChannel().sendMessage("You must mention a user to kick!").queue();
                }
                else{
                    MessageBuilder b = new MessageBuilder();
                    b.append("Hey ");
                    b.append(m.getMentionedUsers().get(0));
                    b.append(", catch! :athletic_shoe: :soccer:");
                    m.getChannel().sendMessage(b.build()).queue();
                }
                break;
            case "maimai":
                m.getChannel().sendMessage("in Germany we call memes MaiMais").queue();
                break;
            case "mandel":
                m.getChannel().sendMessage("Generating Fractal...").queue();
                m.getChannel()
                        .sendMessage("Here's a fractal:")
                        .addFile(convertImg(generateMandel(200, 200, Color.BLACK, Color.white)), "img.png")
                        .queue();
                break;
            case "mandelgif":
                m.getChannel().sendMessage("Generating Fractal...").queue();
                MapGenFragment f = new MapGenFragment(Vector2.MINUSTWO, Vector2.TWO);
                MandelFragment mandelFragment = new MandelFragment(f, 300, 0.1);
                AnimationFragment anim = new AnimationFragment(mandelFragment, AnimationFragment.SIMPLE);
                RenderFragment render = new RenderFragment(anim, new HueCycleGradient());

                Builder b = new Builder(render, 200, 200, 30);
                b.startBuild();
                b.joinAll();

                byte[] arr = FileManager.writeByteArray(b.getImgs());

                m.getChannel()
                        .sendMessage("Here's an animated fractal:")
                        .addFile(arr, "anim.gif")
                        .queue();
                break;
            case "markov":
                Markov mk = new Markov();

                try{
                    if(!hasArgs){
                        mk.addToIndex(readFile("markov/sentences.txt"));
                    }
                    else{
                        for(String s : args){
                            mk.addToIndex(readFile("markov/" + s + ".txt"));
                        }
                    }
                    m.getChannel().sendMessage(mk.generateSentence(1000)).queue();
                }
                catch(IOException ex){
                    m.getChannel().sendMessage("Error occured: " + ex.getMessage()).queue();
                }

                break;
            case "sub":
                m.getChannel().sendMessage("Reloading substitutions...").queue();
                subs = new SubstitutionSet(new File("substitutions.txt"));
                m.getChannel().sendMessage("Done.").queue();
                break;
            case "unsub":
                subs = new SubstitutionSet();
                m.getChannel().sendMessage("Substitutions disabled.").queue();
                break;
        }
    }

    public String readFile(String filename) throws IOException{
        FileInputStream in = new FileInputStream(filename);
        Scanner scan = new Scanner(in);

        StringBuilder bu = new StringBuilder();

        while(scan.hasNextLine()){
            bu.append(scan.nextLine());
        }
        return bu.toString();
    }

    public Color parseColor(String arg){
        if(arg.charAt(0) == '#'){
            int r, g, b;
            r = Integer.parseInt(arg.substring(1, 3), 16);
            g = Integer.parseInt(arg.substring(3, 5), 16);
            b = Integer.parseInt(arg.substring(5, 7), 16);
            return new Color(r, g, b);
        }

        return Color.BLACK;
    }

    public byte[] convertImg(BufferedImage img){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "png", baos);
            baos.flush();
            byte[] toReturn = baos.toByteArray();
            baos.close();
            return toReturn;
        }
        catch (IOException e){}
        return null;
    }

    public BufferedImage generateMandel(int w, int h, Color cA, Color cB){


        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);



        for(int i=0; i<w; i++){
            for(int j=0; j<h; j++){
                Vector2 z = Vector2.ZERO;
                Vector2 c = new Vector2((double)i/w, (double)j/h);
                c = Vector2.multiply(c, new Vector2(4));
                c = Vector2.subtract(c, new Vector2(2));
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

    public BufferedImage generateImg(Color c){
        int w = 500;
        int h = 500;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        for(int i=0; i<w; i++){
            for(int j=0; j<h; j++){
                img.setRGB(i, j, c.getRGB()*i*j);
            }
        }
        return img;
    }
}
//https://discordapp.com/oauth2/authorize?&client_id=435373166811152385&scope=bot&permissions=0