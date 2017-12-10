package com.example.lockscreenview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyLockScreenViewGroup viewGroup = (MyLockScreenViewGroup) findViewById(R.id.viewGroup);
        viewGroup.setOnGestureLockViewListener(new MyLockScreenViewGroup.OnGestureLockViewListener() {
            @Override
            public void gestureResult(boolean isRight) {
                Toast.makeText(MainActivity.this, "图案" + (isRight ? "正确" : "错误") , Toast.LENGTH_LONG).show();
            }

            @Override
            public void exceedMaxTryTime(int tryTimes) {
                Toast.makeText(MainActivity.this, "失败次数达到"+tryTimes+"次" , Toast.LENGTH_LONG).show();
            }
        });


    }
}
