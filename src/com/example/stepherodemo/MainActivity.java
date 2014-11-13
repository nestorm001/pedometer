package com.example.stepherodemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {
	int step = 0;
	int count = 0;
	TextView tv;
	Button b;
	ProgressBar progressBar = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv = (TextView) findViewById(R.id.textView);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		b = (Button) findViewById(R.id.button);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				count++;
				if (count > 3) {
					System.exit(0);
				}
			}
		});
		

		final StepListener sl = new StepListener(this);
		sl.setOnStepListener(new StepListener.OnStepListener() {
			@Override
			public void onStep() {
				step++;
				tv.setText("当前走了：" + step + "步");
				int progressBarMax = progressBar.getMax();
				int stepProgress = step * progressBarMax / 100;
				Log.d("step", "" + progressBarMax);
				if (progressBarMax != progressBar.getProgress()) {
					progressBar.setProgress(stepProgress);
				} else {
					new AlertDialog.Builder(MainActivity.this)
					.setTitle("Message")
					.setMessage("恭喜你到达了小村庄！！")
					.setPositiveButton("确定", null).show();
					sl.pause();
				}
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
