package com.example.horizontalprogressbar;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private MyHorizontalProgressBar myHorizontalProgressBar;
    private final int EMPTY_MESSAGE = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int progress = myHorizontalProgressBar.getProgress();
            myHorizontalProgressBar.setProgress(++progress);
            if (progress >= 100) {
                handler.removeMessages(EMPTY_MESSAGE);
            } else {
                handler.sendEmptyMessageDelayed(EMPTY_MESSAGE, 50);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myHorizontalProgressBar = (MyHorizontalProgressBar) findViewById(R.id.progress_bar);
        myHorizontalProgressBar.setProgress(100);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myHorizontalProgressBar.getProgress() == 100) {
                    myHorizontalProgressBar.setProgress(0);
                    handler.sendEmptyMessage(EMPTY_MESSAGE);
                }
            }
        });
    }
}
