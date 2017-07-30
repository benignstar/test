package ahnteve.mirim.bubblepop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by 안성현 on 2017-07-30.
 */

public class WaterBubble {
    public int x, y, rad;
    public boolean dead=false;
    public Bitmap imgBubble;

    private int width, height;
    private int speed;

    public WaterBubble(Context context, int x, int y, int width, int height){
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
        imgBubble= BitmapFactory.decodeResource(context.getResources(), R.drawable.bubble1);
        imgBubble=Bitmap.createScaledBitmap(imgBubble, width/30, width/30, false);

        rad=imgBubble.getWidth()/2;
        speed=8;
        MoveBubble();
    }

    public void MoveBubble() {
        y-=speed;
        if(y<0) dead=true;
    }
}
