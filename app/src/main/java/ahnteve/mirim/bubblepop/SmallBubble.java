package ahnteve.mirim.bubblepop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Random;

public class SmallBubble {
    public int x, y, rad;
    public boolean dead=false;
    public Bitmap imgBubble;

    private int width, height;
    private int cx, cy;
    private int cr;
    private double r;
    private int speed;
    private int num;
    private int life;

    public SmallBubble(Context context, int x, int y, int ang, int width, int height){
        cx=x;
        cy=y;
        this.width=width;
        this.height=height;
        r=ang*Math.PI/180;

        Random rnd=new Random();
        speed=rnd.nextInt(5)+2;
        rad=rnd.nextInt(10)+10;
        num=rnd.nextInt(6);
        life=rnd.nextInt(31)+20;

        imgBubble=BitmapFactory.decodeResource(context.getResources(), R.drawable.bubble1);
        imgBubble=Bitmap.createScaledBitmap(imgBubble, rad*2, rad*2, false);
        cr=10;
        MoveBubble();
    }

    public void MoveBubble() {
        life--;
        cr+=speed;
        x=(int)(cx+Math.cos(r)*cr);
        y=(int)(cy-Math.sin(r)*cr);
        if(x<-rad || x>width+rad || y<-rad || y>height+rad || life<=0)
            dead=true;
    }
}
