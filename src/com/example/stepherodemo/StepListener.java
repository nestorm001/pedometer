package com.example.stepherodemo;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class StepListener implements SensorEventListener {
	float[] preCoordinate;
	double currentTime = 0, lastTime = 0; // 记录时间
	final static int WALKING_THRESHOLD = 20;
	final static int UP_THRESHOLD = 50;
	final static float DURATION = 250;
	int mLastDiff = WALKING_THRESHOLD;
	public final static String TAG = "step";

	private SensorManager mSensorMgr;
	private OnStepListener mStepListener;
	private Context mContext;

	public interface OnStepListener {
		public void onStep();
	}

	public StepListener(Context context) {
		mContext = context;
		resume();
	}

	public void setOnStepListener(OnStepListener listener) {
		mStepListener = listener;
	}

	public void resume() {
		mSensorMgr = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);
		if (mSensorMgr == null) {
			throw new UnsupportedOperationException("Sensors not supported");
		}

		boolean supported = mSensorMgr.registerListener(this,
				mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
		if (!supported) {
			mSensorMgr.unregisterListener(this);
			throw new UnsupportedOperationException(
					"Accelerometer not supported");
		}
	}

	public void pause() {
		if (mSensorMgr != null) {
			mSensorMgr.unregisterListener(this);
			mSensorMgr = null;
		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
			return;
		}

		long currentTime = System.currentTimeMillis();
		if (currentTime - lastTime > DURATION) {
			if (preCoordinate == null) {// 还未存过数据
				preCoordinate = new float[3];
				for (int i = 0; i < 3; i++) {
					preCoordinate[i] = event.values[i];
				}
			} else { // 记录了原始坐标的话就进行比较
				int angle = calculateAngle(event.values, preCoordinate);
				if (angle >= WALKING_THRESHOLD) {
					if (angle >= UP_THRESHOLD) {
						lastTime = currentTime;
					} else {
						mStepListener.onStep();
						Log.d(TAG, "" + angle + " " + mLastDiff);
						mLastDiff = angle;
					}
				}
				for (int i = 0; i < 3; i++) {
					preCoordinate[i] = event.values[i];
				}
			}
			lastTime = currentTime;// 重新计时
		}
	}

	public int calculateAngle(float[] newPoints, float[] oldPoints) {
		int angle = 0;
		float vectorProduct = 0; // 向量积
		float newMold = 0; // 新向量的模
		float oldMold = 0; // 旧向量的模
		for (int i = 0; i < 3; i++) {
			vectorProduct += newPoints[i] * oldPoints[i];
			newMold += newPoints[i] * newPoints[i];
			oldMold += oldPoints[i] * oldPoints[i];
		}
		newMold = (float) Math.sqrt(newMold);
		oldMold = (float) Math.sqrt(oldMold);
		// 计算夹角的余弦
		float cosineAngle = (float) (vectorProduct / (newMold * oldMold));
		// 通过余弦值求角度
		float fangle = (float) Math.toDegrees(Math.acos(cosineAngle));
		angle = (int) fangle;
		return angle; // 返回向量的夹角
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

}
