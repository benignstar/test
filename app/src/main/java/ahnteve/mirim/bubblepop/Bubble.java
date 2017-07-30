package ahnteve.mirim.bubblepop;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Random;

public class Bubble {
    public int x, y, rad;               // 좌표, 반지름
    public Bitmap imgBubble;            // 비트맵 이미지
    public boolean dead=false;          // 터뜨림 여부

    private int _rad;                   // 원래의 반지름
    private int sx, sy;                 // 이동 방향 및 속도
    private int width, height;          // View의 크기
    private Bitmap Bubbles[]=new Bitmap[6]; //  풍선 애니매이션용 이미지
    private int imgNum=0;                   // 이미지 번호
    private int loop =0;                    // 애니매이션용 루프 카운터
    private int counter=0;                  // 벽과 충돌 횟수

    public Bubble(Context context, int x, int y, int width, int height){
        this.width=width;
        this.height=height;
        this.x=x;
        this.y=y;

        imgBubble= BitmapFactory.decodeResource(context.getResources(), R.drawable.bubble);
        imgBubble=Bitmap.createScaledBitmap(imgBubble, width/6, width/6, true);

        Random rnd=new Random();
        _rad=rnd.nextInt(100)+imgBubble.getWidth()/2;   // 반지름 : 물방울의 원래 크기 ~ +100
        rad=_rad;

        // 반지름이 5간격으로 커졌다 작아지는 물방울 6개 만들기
        for(int i=0; i<=3; i++)
            Bubbles[i]=Bitmap.createScaledBitmap(imgBubble, _rad*2+i*5, _rad*2+i*5, false);
        Bubbles[4]=Bubbles[2];
        Bubbles[5]=Bubbles[1];
        imgBubble=Bubbles[0];

        sx=2;
        sy=rnd.nextInt(2) == 0 ? -2 : 2;
        MoveBubble();
    }

    public void MoveBubble() {
        loop++;
        if(loop%3==0) {
            imgNum++;
            if (imgNum > 5) imgNum = 0;
            imgBubble = Bubbles[imgNum];

            rad = _rad + (imgNum <= 3 ? imgNum : 6 - imgNum) * 2;
        }
        x+=sx;
        y+=sy;
        if(x>=width+rad) {
            x=-rad;
        }
        if(y<=rad||y>=height-rad){
            sy=-sy;
            y+=sy;
        }
    }
}
