package ahnteve.mirim.bubblepop;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class Score {
    public int x, y, sw, sh;
    public Bitmap imgScore;

    private Bitmap fonts[]=new Bitmap[10];
    private int loop=0;

    public Score(Context context, int x, int y, int _score){
        this.x=x;
        this.y=y;

        Display display=((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point=new Point();
        display.getSize(point);

        for(int i=0; i<10; i++){
            fonts[i]= BitmapFactory.decodeResource(context.getResources(), R.drawable.f0+i);
            fonts[i]=Bitmap.createScaledBitmap(fonts[i], point.x/13, point.x/13, false);

        }

        MakeScore(_score);
        Move();
    }

    public void MakeScore(int _score) {
        String score=""+_score;
        int L=score.length();

        imgScore=Bitmap.createBitmap(fonts[0].getWidth()*L, fonts[0].getHeight(), fonts[0].getConfig());

        Canvas canvas=new Canvas();
        canvas.setBitmap(imgScore);

        int w=fonts[0].getWidth();
        for(int i=0; i<L; i++){
            int n=(int)score.charAt(i)-48;
            canvas.drawBitmap(fonts[n], w*i, 0, null);
        }
        sw=imgScore.getWidth()/2;
        sh=imgScore.getHeight()/2;
    }

    public boolean Move() {
        y-=4;
        if(loop>20) return false;
        loop++;
        return true;
    }
}
