package ahnteve.mirim.bubblepop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by 안성현 on 2017-07-30.
 */

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    GameThread mThread;
    SurfaceHolder mHolder;
    Context mContext;
    final int LEFT=1;
    final int RIGHT=2;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        SurfaceHolder holder=getHolder();
        holder.addCallback(this);
        mHolder=holder;
        mThread=new GameThread(holder, context);
        mContext=context;
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        StopGame();
    }

    public void StopGame(){
        mThread.StopThread();
    }

    public void PauseGame(){
        mThread.PauseResume(true);
    }

    public void ResumeGame(){
        mThread.PauseResume(false);
    }

    public void RestartGame(){
        mThread.StopThread();

        mThread=null;
        mThread=new GameThread(mHolder, mContext);
        mThread.start();
    }


    class GameThread extends Thread{
        SurfaceHolder mHolder;
        Context mContext;

        int width, height;
        Bitmap background;
        Bitmap car;
        int cw, ch;
        int mx, my;
        long lastTime;

        int Tot=0;
        Paint paint=new Paint();
        ArrayList<Bubble> mBubble=new ArrayList<>();
        ArrayList<SmallBubble> sBubble=new ArrayList<>();
        ArrayList<WaterBubble> wBubble=new ArrayList<>();
        ArrayList<Score> mScore=new ArrayList<>();

        boolean canRun=true;
        boolean isWait=false;

        public GameThread(SurfaceHolder holder, Context context){
            mHolder=holder;
            mContext=context;

            Display display=((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            Point point=new Point();
            display.getSize(point);

            width=point.x;
            height=point.y;

            background= BitmapFactory.decodeResource(getResources(), R.drawable.road);
            background=Bitmap.createScaledBitmap(background, width, height, false);

            car=BitmapFactory.decodeResource(getResources(), R.drawable.red_car);
            car=Bitmap.createScaledBitmap(car, width/6, width/3, false);
            cw=car.getWidth()/2;
            ch=car.getHeight()/2;

            mx=width/2;
            my=height/6*5;

            paint.setTextSize(40);
            paint.setColor(Color.WHITE);
            paint.setAntiAlias(true);
        }

        public void StopThread(){
            canRun=false;
            synchronized (this){
                this.notify();
            }
        }


        private void MakeSmallBubble(int x, int y){
            Random rnd=new Random();
            int count=rnd.nextInt(9)+7;
            for(int i=1; i<=count; i++){
                int ang=rnd.nextInt(360);
                sBubble.add(new SmallBubble(mContext, x, y, ang, width, height));
            }
        }

        public void MakeBubble(){
            Random rnd=new Random();
            if(mBubble.size()>9 || rnd.nextInt(40) < 38) return;
            int x=rnd.nextInt(width);
            int y=rnd.nextInt(height-(height-my));
            mBubble.add(new Bubble(mContext, x, y, width, height));
        }

        public void MoveCharacters(){

            for(int i=mBubble.size()-1 ; i>=0; i--){
                mBubble.get(i).MoveBubble();
                if(mBubble.get(i).dead) {
                    //MakeSmallBubble(mBubble.get(i).x, mBubble.get(i).y);
                    mBubble.remove(i);
                }
            }

            for(int i=sBubble.size()-1; i>=0; i++) {
                sBubble.get(i).MoveBubble();
                if(sBubble.get(i).dead)
                    sBubble.remove(i);
            }

            for(int i=wBubble.size()-1; i>=0; i--){
                wBubble.get(i).MoveBubble();
                if(wBubble.get(i).dead)
                    wBubble.remove(i);
            }

            for(int i=mScore.size()-1; i>=0; i--)
                if(mScore.get(i).Move() == false)
                    mScore.remove(i);

        }

        private void MoveCar(int n){
            int sx=4;
            if(n==LEFT) sx=-4;
            mx+=sx;
            if(mx<cw) mx=cw;
            if(mx>width-cw) mx=width-cw;
        }

        private void MakeWaterBubble(){
            long thisTime=System.currentTimeMillis();
            if(thisTime-lastTime>=300)
                wBubble.add(new WaterBubble(mContext, mx, my-20, width, height));
            lastTime=thisTime;
        }

        public void CheckCollision(){
            int x1, y1, x2, y2;

            for(WaterBubble water : wBubble){
                x1=water.x;
                y1=water.y;
                for(Bubble tmp : mBubble){
                    x2=tmp.x;
                    y2=tmp.y;
                    if(Math.abs(x1-x2)<tmp.rad && Math.abs(y1-y2)<tmp.rad){
                   //     MakeSmallBubble(tmp.x, tmp.y);
                        mScore.add(new Score(tmp.x, tmp.y));
                        tmp.dead=true;
                        water.dead=true;
                        Tot+=100;
                        break;
                    }
                }
            }
        }

        public void DrawCharacters(Canvas canvas){
            canvas.drawBitmap(background, 0, 0, null);

            for(Bubble tmp : mBubble)
                canvas.drawBitmap(tmp.imgBubble, tmp.x-tmp.rad, tmp.y-tmp.rad, null);

            for(SmallBubble tmp : sBubble)
                canvas.drawBitmap(tmp.imgBubble, tmp.x-tmp.rad, tmp.y-tmp.rad, null);

            for(WaterBubble tmp : wBubble)
                canvas.drawBitmap(tmp.imgBubble, tmp.x-tmp.rad, tmp.y-tmp.rad, null);

            for(Score tmp : mScore){
                canvas.drawText("+100", tmp.x-20, tmp.y-10, tmp.paint);
                canvas.drawText("총점 : "+Tot, 10, 30, paint);
            }
            canvas.drawBitmap(car, mx-cw, my-ch, null);
        }



        public void run(){
            Canvas canvas=null;

            while(canRun){
                canvas=mHolder.lockCanvas();
                try {
                    synchronized (mHolder){
                        MakeBubble();
                        MoveCharacters();
                        CheckCollision();
                        DrawCharacters(canvas);
                    }
                }finally {
                    if(canvas!=null)
                        mHolder.unlockCanvasAndPost(canvas);
                }
                synchronized (this) {
                    if (isWait) {
                        try {
                            wait();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }




        public void PauseResume(boolean wait) {
            isWait=wait;
            synchronized (this){
                this.notify();
            }
        }
    }
    /*@Override
    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            synchronized (mHolder){
                int x=(int)event.getX();
                int y=(int)event.getY();
                mThread.MakeBubble(x,y);
            }
        }
        return true;
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        synchronized (mHolder){
            if(event.getAction()==KeyEvent.ACTION_DOWN){
                switch (keyCode){
                    case KeyEvent.KEYCODE_DPAD_LEFT :
                        mThread.MoveCar(LEFT);
                        break;
                    case KeyEvent.KEYCODE_DPAD_RIGHT :
                        mThread.MoveCar(RIGHT);
                        break;
                    case KeyEvent.KEYCODE_DPAD_UP :
                        mThread.MakeWaterBubble();
                        break;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
