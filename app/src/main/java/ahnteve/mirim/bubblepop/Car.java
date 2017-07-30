package ahnteve.mirim.bubblepop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by 안성현 on 2017-07-30.
 */

public class Car {
    int x, y, cw, ch;
    public boolean dead=false;
    public boolean undead=true;
    public Bitmap imgCar;

    private Bitmap Car[]= new Bitmap[2];
    private int width;
    private int speed;

    private int loop;
    private int undeadCnt=0;
    private int imgNum=1;

    public Car(Context context, int x, int y, int width){
        this.x=x;
        this.y=y;
        this.width=width;

        Car[0]= BitmapFactory.decodeResource(context.getResources(), R.drawable.red_car);
        Car[0]=Bitmap.createScaledBitmap(Car[0], width/6, width/3, false);
        Car[1]= BitmapFactory.decodeResource(context.getResources(), R.drawable.red_car2);
        Car[1]=Bitmap.createScaledBitmap(Car[1], width/6, width/3, false);

        imgCar=Car[0];
        cw=imgCar.getWidth()/2;
        ch=imgCar.getHeight()/2;
        speed=10;
    }

    public void UndeadMode(){
        if(undeadCnt>=5) return;
        loop++;
        if(loop%5==0){
            imgNum=1-imgNum;
            imgCar=Car[imgNum];
            undeadCnt++;
        }
        if(undeadCnt>=5) undead=false;
    }

    public void MoveLeft(){
        if(x>cw) x-=speed;
        if(x<cw) x=cw;
    }

    public void MoveRight(){
        if(x<width-cw) x+=speed;
        if(x>width-cw) x=width-cw;
    }

}
