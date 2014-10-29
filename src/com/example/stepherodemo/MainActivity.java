package com.example.stepherodemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	int step = 0;
	int count = 0;
	TextView tv;
	Button b;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv = (TextView) findViewById(R.id.textView);
		b = (Button) findViewById(R.id.button);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				count++;
				if(count > 3){
					System.exit(0);
				}
			}
		});
				
		StepListener sl = new StepListener(this);
		sl.setOnStepListener(new StepListener.OnStepListener() {
			@Override
			public void onStep() {
				step++;
				tv.setText("当前走了：" + step + "步");
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}
}
