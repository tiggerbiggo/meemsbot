import com.tiggerbiggo.prima.calculation.ColorTools;
import com.tiggerbiggo.prima.core.Vector2;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MandelMessage {
    Vector2 BL, TR;

    public BufferedImage generate(int w, int h, Color cA, Color cB){
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
}
