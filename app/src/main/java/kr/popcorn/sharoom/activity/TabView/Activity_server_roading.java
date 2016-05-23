package kr.popcorn.sharoom.activity.TabView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import kr.popcorn.sharoom.R;
import kr.popcorn.sharoom.activity.Fragment.User.Activity_user_view;
import kr.popcorn.sharoom.helper.Helper_room;

import static android.support.v4.app.ActivityCompat.startActivity;

public class Activity_server_roading extends Activity {


    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_roading);

        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        progressBar.incrementProgressBy(1);

        Handler handler = new Handler();
        handler.postDelayed(new splashhandler(), 3000);
    }

    private class splashhandler implements Runnable{
        public void run() {
            Activity_server_roading.this.finish(); // 로딩페이지 Activity Stack에서 제거

        }
    }
}


