package com.example.test;

import java.io.FileOutputStream;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ums.AppHelper;

public class MainActivity extends Activity{
    public static final String TAG = "MainActivity";
    
	private Button start;
	private TextView textView;
	
	private EditText etAppName;
	private EditText etTransData;
	private EditText etTransType;
	private EditText etOrderNo;

	private Button btnPrint;
	
	private boolean isCallQuery = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
//		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
//		View view = getWindow().getDecorView();
//		view.getParent().recomputeViewAttributes(view);
		start = (Button) findViewById(R.id.btn_start);
		btnPrint = (Button) findViewById(R.id.btn_print);
		textView = (TextView) findViewById(R.id.txt_result);
		etAppName = (EditText) findViewById(R.id.et_app_name);
		etTransType = (EditText) findViewById(R.id.et_trans_type);
		etTransData = (EditText) findViewById(R.id.et_trans_data);
		etOrderNo = (EditText) findViewById(R.id.et_order_no);
		start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				try {
					String transApp = etAppName.getText().toString();
					String transType = etTransType.getText().toString();
					String transData = etTransData.getText().toString();
					JSONObject json = new JSONObject(transData);
					AppHelper.callTrans(MainActivity.this, transApp, transType, json);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		
		btnPrint.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				printTest();
			}
		});
	}
	
	private void printTest(){
		View view = getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		if(bitmap == null){
			Log.d(TAG, "bitmap is null");
			return;
		}
		
		String fname = "/sdcard/ddd.png";
		try {
			FileOutputStream out = new FileOutputStream(fname);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			Log.d(TAG, "file" + fname + "output done.");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		AppHelper.callPrint(this, fname);

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (Activity.RESULT_OK != resultCode) {
			Toast.makeText(this, "调用失败", Toast.LENGTH_SHORT).show();
			return;
		}
		if (data == null || data.getExtras() == null
				|| data.getExtras().getString("result") == null) {
			//走查询交易
			if (isCallQuery) {
				callQuery(requestCode);
				isCallQuery = false;
			}
			return;
		}
		
		if(AppHelper.TRANS_REQUEST_CODE == requestCode){
			Log.d(TAG, "resultCode" + resultCode);
			if (Activity.RESULT_OK == resultCode) {
				if (null != data) {
					StringBuilder result = new StringBuilder();
					Map<String,String> map = AppHelper.filterTransResult(data);
					result.append(AppHelper.TRANS_APP_NAME + ":" +map.get(AppHelper.TRANS_APP_NAME) + "\r\n");
					result.append(AppHelper.TRANS_BIZ_ID + ":" +map.get(AppHelper.TRANS_BIZ_ID) + "\r\n");
					result.append(AppHelper.RESULT_CODE + ":" +map.get(AppHelper.RESULT_CODE) + "\r\n");
					result.append(AppHelper.RESULT_MSG + ":" +map.get(AppHelper.RESULT_MSG) + "\r\n");
					result.append(AppHelper.TRANS_DATA + ":" +map.get(AppHelper.TRANS_DATA) + "\r\n");
					
					Log.d(TAG, "result" + result);
					if (null != result) {
						textView.setText(result);
					}
				}else{
					textView.setText("Intent is null");
				}
			}else{
				textView.setText("resultCode is not RESULT_OK");
			}
		} else if(AppHelper.PRINT_REQUEST_CODE == requestCode){
			Log.d(TAG, "resultCode" + resultCode);
			if (Activity.RESULT_OK == resultCode) {
				if (null != data) {
					StringBuilder result = new StringBuilder();
					String printCode = data.getStringExtra("resultCode");
					result.append("resultCode:" + printCode);
					Log.d(TAG, "result" + result);
					if (null != result) {
						textView.setText(result);
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
	
	// 交易失败，走一次查询功能
	private void callQuery(int requestCode){
		String transApp = etAppName.getText().toString();
		String transData = etTransData.getText().toString();
		String extOrderNo = etOrderNo.getText().toString();
		
		Log.d(TAG, "call query appName = " + transApp);
		if (null == transApp || transApp.isEmpty()) {
			Toast.makeText(this, "应用名字不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		Log.d(TAG, "call query extOrderNo = " + extOrderNo);
		if (null == extOrderNo || extOrderNo.isEmpty()) {
			Toast.makeText(this, "外部订单号为空", Toast.LENGTH_SHORT).show();
		}
		try {
			JSONObject transDatas = new JSONObject(transData);
			transDatas.put("extOrderNo", extOrderNo);
			AppHelper.callTrans(MainActivity.this, transApp, "交易明细", transDatas);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
