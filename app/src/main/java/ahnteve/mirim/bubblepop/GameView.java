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

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    GameThread mThread;
    SurfaceHolder mHolder;
    Context mContext;

    final int LEFT = 1;       // 차 이동 방향
    final int RIGHT = 8;

    private Rect[] ArrowRect = new Rect[2];
    private Integer ArrowX[] = new Integer[2];
    private int counter=0;
    private int x1, y1;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

        int width = point.x;
        int height = point.y;

        ArrowRect[0] = new Rect(0, 0, width / 2, height);
        ArrowRect[1] = new Rect(width / 2 + 1, 0, width, height);

        ArrowX[0] = -(width / 30);
        ArrowX[1] = -ArrowX[0];

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        mHolder = holder;
        mContext = context;       // holder와 Context 보존
        mThread = new GameThread(holder, context);

        setFocusable(true);
    }

    // Surface가 생성될 때 실행되는 부분
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mThread.start();
    }

    // Surface가 바뀔 때 실행되는 부분
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    // SurfaceView가 해제될 때 실행되는 부분
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean done = true;
        while (done) {
            try {
                mThread.join();        // 스레드가 현재 step을 끝낼 때까지 대기
                done = false;
            } catch (InterruptedException e) {
            }
        }
    }

    // 스레드 완전 정지
    public void StopGame() {
        mThread.StopThread();
    }

    // 스레드 일시 정지
    public void PauseGame() {
        mThread.PauseResume(true);
    }

    // 스레드 재기동
    public void ResumeGame() {
        mThread.PauseResume(false);
    }

    // 게임 초기화
    public void RestartGame() {
        mThread.StopThread(); // 스레드 중지

        // 현재의 스레드를 비우고 다시 생성
        mThread = null;
        mThread = new GameThread(mHolder, mContext);
        mThread.start();
    }


    // --- GameThread Class

    class GameThread extends Thread {
        SurfaceHolder mHolder;
        Context mContext;


        int width, height;
        Bitmap background;  // 배경
        Bitmap car;         // 차
        int cw, ch;         // 차의 크기
        int mx, my;         // 차의 좌표
        long lastTime;      // 시간 계산용 변수
        int Tot = 0;          // 득점 합계
        int CarCnt;
        Paint paint = new Paint(); // 점수 표시용

        ArrayList<Bubble> mBubble = new ArrayList<Bubble>(); // 큰 방울
        ArrayList<SmallBubble> sBubble = new ArrayList<SmallBubble>(); // 작은 방울
        ArrayList<WaterBubble> wBubble = new ArrayList<WaterBubble>(); // 총알
        ArrayList<Car> mCar = new ArrayList<Car>();
        ArrayList<Score> mScore = new ArrayList<Score>(); // 점수
        Score totScore;

        boolean canRun = true;    // 스레드 제어용
        boolean isWait = false;
        private int sx1, sy1;
        private int cx, cy;

        public GameThread(SurfaceHolder holder, Context context) {
            mHolder = holder; // SurfaceHolder 보존
            mContext = context;

            Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);

            width = point.x;
            height = point.y;

            background = BitmapFactory.decodeResource(getResources(), R.drawable.road);
            background = Bitmap.createScaledBitmap(background, width, height, false);

            cx=width/2;
            cy=height/2;

            x1=0;          // Viewport의 시작 위치는 이미지의 한가운데
            y1=cy;
            sx1=0;         // Viewport를 1회에 이동시킬 거리
            sy1=-1;

            car = BitmapFactory.decodeResource(getResources(), R.drawable.red_car);
            car = Bitmap.createScaledBitmap(car, width / 10, width / 5, false);

            mx = width / 2;
            my = height / 6 * 5;

            totScore = new Score(mContext, 0, 0, 0);

            InitGame();
        }

        public void InitGame() {
            CarCnt = 3;
            for (int i = 0; i <= CarCnt; i++) {
                mCar.add(new Car(mContext, width / 2, height / 6 * 5, width));
            }
            Tot = 0;
        }

        // 큰 방울 만들기
        public void MakeBubble() {
            Random rnd = new Random();
            if (mBubble.size() > 9 || rnd.nextInt(40) < 38) return;
            int x = rnd.nextInt(width);
            int y = 0;
            mBubble.add(new Bubble(mContext, x, y, width, height));
        }


        // 작은 방울 만들기
        private void MakeSmallBubble(int x, int y) {
            Random rnd = new Random();
            int count = rnd.nextInt(9) + 7; // 7~15개
            for (int i = 1; i <= count; i++) {
                int ang = rnd.nextInt(360);
                sBubble.add(new SmallBubble(mContext, x, y, ang, width, height));
            }
        }

        // 모든 캐릭터 이동
        public void MoveCharacters() {

            for (int i = mBubble.size() - 1; i >= 0; i--) {
                mBubble.get(i).MoveBubble();
                if(mBubble.get(i).y>height+mBubble.get(i).rad) mBubble.get(i).dead=true;
                if (mBubble.get(i).dead) mBubble.remove(i);
            }

            for (int i = sBubble.size() - 1; i >= 0; i--) {
                sBubble.get(i).MoveBubble();
                if (sBubble.get(i).dead)
                    sBubble.remove(i);
            }

            for (int i = wBubble.size() - 1; i >= 0; i--) {
                wBubble.get(i).MoveBubble();
                if (wBubble.get(i).dead)
                    wBubble.remove(i);
            }

            for (int i = mScore.size() - 1; i >= 0; i--)
                if (mScore.get(i).Move() == false)
                    mScore.remove(i);

            mCar.get(CarCnt).UndeadMode();

        }

        // 캐릭터 이동
        private void MoveCar(int n) {
            mCar.get(CarCnt).speed = n;

        }


        // 충돌 판정
        public void CheckCollision() {
            int x1, y1, x2, y2;
            int score = new Random().nextInt(101) + 100;
            // 방울과 거미의 충돌
            Car c = mCar.get(CarCnt);
            for (Bubble tmp : mBubble) {
                x1 = Math.abs(c.x - tmp.x);
                y1 = Math.abs(c.y - tmp.y);
                if (x1 < c.cw + tmp.rad && y1 < c.ch + tmp.rad) {
                    MakeSmallBubble(tmp.x, tmp.y);
                    tmp.dead = true;
                    if (c.undead == false) {
                        mCar.remove(CarCnt);
                        CarCnt--;
                        if (CarCnt < 0) GameOver();
                    }
                }
            }
            // 총알과 비눗방울의 충돌
            for (WaterBubble water : wBubble) {
                x1 = water.x;
                y1 = water.y;
                for (Bubble tmp : mBubble) {
                    x2 = tmp.x;
                    y2 = tmp.y;
                    if (Math.abs(x1 - x2) < tmp.rad && Math.abs(y1 - y2) < tmp.rad) {
                        MakeSmallBubble(tmp.x, tmp.y);
                        mScore.add(new Score(mContext, tmp.x, tmp.y, score));
                        tmp.dead = true;
                        water.dead = true;
                        Tot += score;
                        break;
                    }
                } // for
            } // for
        }

        private void GameOver() {
            InitGame();
        }

        // Canvas에 그리기
        public void DrawCharacters(Canvas canvas) {
            Rect src=new Rect(); // Viewport의 좌표
            Rect dst=new Rect(); // View(화면)의 좌표
            dst.set(0, 0, width, height); // View는 화면 전체 크기

            ScrollImage();
            src.set(x1, y1, width, y1+cy);
            canvas.drawBitmap(background, src, dst, null);

            for (int i = 0; i <= CarCnt; i++)
                canvas.drawBitmap(car, width / 12 * i + 20, height - 400, null);

            for (Bubble tmp : mBubble)
                canvas.drawBitmap(tmp.imgBubble, tmp.x - tmp.rad, tmp.y - tmp.rad, null);

            for (SmallBubble tmp : sBubble)
                canvas.drawBitmap(tmp.imgBubble, tmp.x - tmp.rad, tmp.y - tmp.rad, null);

            for (WaterBubble tmp : wBubble)
                canvas.drawBitmap(tmp.imgBubble, tmp.x - tmp.rad, tmp.y - tmp.rad, null);

            for (Score tmp : mScore)
                canvas.drawBitmap(tmp.imgScore, tmp.x - tmp.sw, tmp.y - tmp.sh, null);
            totScore.MakeScore(Tot);
            canvas.drawBitmap(totScore.imgScore, 10, 10, null);


            Car tmp = mCar.get(CarCnt);
            tmp.MoveRight();
            canvas.drawBitmap(tmp.imgCar, tmp.x - tmp.cw, tmp.y - tmp.ch, null);
        }

        public void StopThread() {
            canRun = false;
            synchronized (this) {
                this.notify();
            }
        }


        public void run() {

            Canvas canvas = null;

            while (canRun) {
                canvas = mHolder.lockCanvas();
                try {
                    synchronized (mHolder) {
                        MakeBubble();
                        MoveCharacters();
                        CheckCollision();
                        DrawCharacters(canvas);
                    }
                } finally {
                    if (canvas != null)
                        mHolder.unlockCanvasAndPost(canvas);
                }

                synchronized (this) {
                    if (isWait)
                        try {
                            wait();
                        } catch (Exception e) {
                        }
                }
            }
        }

        private void ScrollImage() {

            counter++;
            if(counter%2==0){
                x1+=sx1;
                y1+=sy1;
                if(x1<0) x1=cx;
                if(y1<0) y1=cy;
            }
        }



        public void PauseResume(boolean wait) {
            isWait = wait;
            synchronized (this) {
                this.notify();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int move = 0;
            int x = (int) event.getX();
            int y = (int) event.getY();
            synchronized (mHolder) {
                for (int i = 0; i < 2; i++) {
                    if (ArrowRect[i].contains(x, y)) {
                        move=ArrowX[i];
                        break;
                    }
                }
                mThread.MoveCar(move);
            }
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            synchronized (mHolder) {
                mThread.MoveCar(0);
            }
        }
        return true;
    }
}
