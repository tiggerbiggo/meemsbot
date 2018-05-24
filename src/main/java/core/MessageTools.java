package core;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MessageTools {
    public static Message sendMessage(String message, MessageChannel c){
        return c.sendMessage(message).complete();
    }

    public static void sendMessageAsync(String message, MessageChannel c){
        c.sendMessage(message).queue();
    }
    public static Message sendMessageWithImage(String message, String imgTitle, BufferedImage img, MessageChannel c){
        byte[] converted = convertImg(img);
        if(converted == null) return c.sendMessage("Error: Attempted to send image message, convertImg returned null.").complete();
        return c.sendMessage(message).addFile(converted, imgTitle + ".png").complete();
    }

    public static byte[] convertImg(BufferedImage img){
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
}
