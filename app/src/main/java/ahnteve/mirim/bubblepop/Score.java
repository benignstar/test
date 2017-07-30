package ahnteve.mirim.bubblepop;


import android.graphics.Color;
import android.graphics.Paint;

public class Score {
    public int x, y;
    public Paint paint;
    private int loop=0;
    private int color= Color.WHITE;

    public Score(int x, int y){
        this.x=x;
        this.y=y;
        loop=0;
        paint.setColor(color);
        paint.setTextSize(50);
        paint.setAntiAlias(true);
        Move();
    }

    public boolean Move() {
        y-=4;
        if(y<-20) return false;
        loop++;
        if(loop%4 == 0){
            color = (Color.WHITE+Color.YELLOW)-color;
            paint.setColor(color);
        }
        return true;
    }
}
