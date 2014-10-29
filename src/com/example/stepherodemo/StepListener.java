package com.example.stepherodemo;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class StepListener implements SensorEventListener {
	float[] preCoordinate;
	double currentTime = 0, lastTime = 0; // ��¼ʱ��
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
			if (preCoordinate == null) {// ��δ�������
				preCoordinate = new float[3];
				for (int i = 0; i < 3; i++) {
					preCoordinate[i] = event.values[i];
				}
			} else { // ��¼��ԭʼ����Ļ��ͽ��бȽ�
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
			lastTime = currentTime;// ���¼�ʱ
		}
	}

	public int calculateAngle(float[] newPoints, float[] oldPoints) {
		int angle = 0;
		float vectorProduct = 0; // ������
		float newMold = 0; // ��������ģ
		float oldMold = 0; // ��������ģ
		for (int i = 0; i < 3; i++) {
			vectorProduct += newPoints[i] * oldPoints[i];
			newMold += newPoints[i] * newPoints[i];
			oldMold += oldPoints[i] * oldPoints[i];
		}
		newMold = (float) Math.sqrt(newMold);
		oldMold = (float) Math.sqrt(oldMold);
		// ����нǵ�����
		float cosineAngle = (float) (vectorProduct / (newMold * oldMold));
		// ͨ������ֵ��Ƕ�
		float fangle = (float) Math.toDegrees(Math.acos(cosineAngle));
		angle = (int) fangle;
		return angle; // ���������ļн�
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

}
