package com.example.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class TestActivity extends Activity{
	public static final String PAY_ACTION = "com.myaction";
	private Button start;
	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		start = (Button) findViewById(R.id.btn_start);
		textView = (TextView) findViewById(R.id.txt_result);
		
		textView.setText("ddddd");
		
		start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			}
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (100 == requestCode) {
			Log.d("WebMainActivity", "resultCode" + resultCode);
			if (Activity.RESULT_OK == resultCode) {
				if (null != data) {
					String result = data.getStringExtra("result");
					Log.d("WebMainActivity", "result" + result);
					if (null != result && !result.isEmpty()) {
						textView.setText(result);
					}else{
						textView.setText("result is null");
					}
				}else{
					textView.setText("Intent is null");
				}
			}else{
				textView.setText("resultCode is not RESULT_OK");
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
