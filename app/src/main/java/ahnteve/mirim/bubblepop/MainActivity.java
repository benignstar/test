package ahnteve.mirim.bubblepop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {
    GameView mGameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mGameView=(GameView)findViewById(R.id.mGameView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGameView.PauseGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGameView.ResumeGame();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGameView.PauseGame();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,1,0, "게임종료");
        menu.add(0,2,0, "일시정지");
        menu.add(0,3,0, "계속진행");
        menu.add(0,4,0, "게임초기화");
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 1:
                mGameView.StopGame();
                finish();
                break;
            case 2:
                mGameView.PauseGame();
                break;
            case 3:
                mGameView.ResumeGame();
                break;
            case 4:
                mGameView.RestartGame();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mGameView.StopGame();
    }
}
