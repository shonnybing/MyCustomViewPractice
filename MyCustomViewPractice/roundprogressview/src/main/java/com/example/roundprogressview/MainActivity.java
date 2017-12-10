package com.example.roundprogressview;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private final int EMPTY_MSG = 1;
    private RoundProgressView roundProgressView;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int progress = roundProgressView.getProgress();
            roundProgressView.setProgress(++progress);
            if (progress < 100) {
                handler.sendEmptyMessageDelayed(EMPTY_MSG, 50);
            } else {
                handler.removeMessages(EMPTY_MSG);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        roundProgressView = (RoundProgressView) findViewById(R.id.progress_bar);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roundProgressView.setProgress(0);
                handler.sendEmptyMessage(EMPTY_MSG);
            }
        });
    }
}
