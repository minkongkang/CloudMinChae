package com.example.android_resapi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android_resapi.R;
import com.example.android_resapi.ui.apicall.GetThingShadow;
import com.example.android_resapi.ui.apicall.UpdateShadow;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Timer;
import java.util.TimerTask;

public class DeviceActivity extends AppCompatActivity {
    String urlStr;
    final static String TAG = "AndroidAPITest";
    Timer timer;
    Button startGetBtn;
    Button stopGetBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        Intent intent = getIntent();
        urlStr = intent.getStringExtra("thingShadowURL");

        startGetBtn = findViewById(R.id.startGetBtn);
        startGetBtn.setEnabled(true);
        startGetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer = new Timer();
                timer.schedule(new TimerTask() {
                                   @Override
                                   public void run() {
                                       new GetThingShadow(DeviceActivity.this, urlStr).execute();
                                   }
                               },
                        0,2000);

                startGetBtn.setEnabled(false);
                stopGetBtn.setEnabled(true);
            }
        });

        stopGetBtn = findViewById(R.id.stopGetBtn);
        stopGetBtn.setEnabled(false);
        stopGetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timer != null)
                    timer.cancel();
                clearTextView();
                startGetBtn.setEnabled(true);
                stopGetBtn.setEnabled(false);
            }
        });

        /////////////////////내부, 외부 상태입력 : 자동화 /////////////////////////////////
       /* Button updateBtn = findViewById(R.id.updateBtn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText edit_dustin = findViewById(R.id.edit_dustin);
                EditText edit_dustout = findViewById(R.id.edit_dustout);

                JSONObject payload = new JSONObject();

                try {
                    JSONArray jsonArray = new JSONArray();
                    String dustin_input = edit_dustin.getText().toString();
                    if (dustin_input != null && !dustin_input.equals("")) {
                        JSONObject tag1 = new JSONObject();
                        tag1.put("tagName", "temperature");
                        tag1.put("tagValue", dustin_input);

                        jsonArray.put(tag1);
                    }

                    String dustout_input = edit_dustout.getText().toString();
                    if (dustout_input != null && !dustout_input.equals("")) {
                        JSONObject tag2 = new JSONObject();
                        tag2.put("tagName", "LED");
                        tag2.put("tagValue", dustout_input);

                        jsonArray.put(tag2);
                    }

                    if (jsonArray.length() > 0)
                        payload.put("tags", jsonArray);
                } catch (JSONException e) {
                    Log.e(TAG, "JSONEXception");
                }
                Log.i(TAG,"payload="+payload);
                if (payload.length() >0 )
                    new UpdateShadow(DeviceActivity.this,urlStr).execute(payload);
                else
                    Toast.makeText(DeviceActivity.this,"변경할 상태 정보 입력이 필요합니다", Toast.LENGTH_SHORT).show();
            }
        }); */

        // /////////////////////////팬 세기 :0- 수동 ///////////////////////////////////////////////////
        Button fanspeedBtn0 = findViewById(R.id.fanspeed_0);
        fanspeedBtn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject payload = new JSONObject();

                try {
                    JSONArray jsonArray = new JSONArray();
                    String fanspeed0_input = "0";
                    if (fanspeed0_input != null && !fanspeed0_input.equals("")) {
                        JSONObject tag1 = new JSONObject();
                        tag1.put("tagName", "fanled");
                        tag1.put("tagValue", fanspeed0_input);

                        jsonArray.put(tag1);
                    }
                    if (jsonArray.length() > 0)
                        payload.put("tags", jsonArray);
                } catch (JSONException e) {
                    Log.e(TAG, "JSONEXception");
                }
                Log.i(TAG,"payload="+payload);
                if (payload.length() >0 ) {
                    Toast.makeText(DeviceActivity.this,"세기: 0", Toast.LENGTH_SHORT).show();
                    new UpdateShadow(DeviceActivity.this, urlStr).execute(payload);
                }
                else
                    Toast.makeText(DeviceActivity.this,"변경할 상태 정보를 선택하세요", Toast.LENGTH_SHORT).show();
            }
        });
        Button fanspeedBtn1 = findViewById(R.id.fanspeed_1);
        fanspeedBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject payload = new JSONObject();

                try {
                    JSONArray jsonArray = new JSONArray();
                    String fanspeed1_input = "1";
                    if (fanspeed1_input != null && !fanspeed1_input.equals("")) {
                        JSONObject tag1 = new JSONObject();
                        tag1.put("tagName", "fanled");
                        tag1.put("tagValue", fanspeed1_input);

                        jsonArray.put(tag1);
                    }
                    if (jsonArray.length() > 0)
                        payload.put("tags", jsonArray);
                } catch (JSONException e) {
                    Log.e(TAG, "JSONEXception");
                }
                Log.i(TAG,"payload="+payload);
                if (payload.length() >0 ) {
                    Toast.makeText(DeviceActivity.this,"세기: 1", Toast.LENGTH_SHORT).show();
                    new UpdateShadow(DeviceActivity.this, urlStr).execute(payload);
                }
                else
                    Toast.makeText(DeviceActivity.this,"변경할 상태 정보를 선택하세요", Toast.LENGTH_SHORT).show();
            }
        });
        Button fanspeedBtn2 = findViewById(R.id.fanspeed_2);
        fanspeedBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject payload = new JSONObject();

                try {
                    JSONArray jsonArray = new JSONArray();
                    String fanspeed2_input = "2";
                    if (fanspeed2_input != null && !fanspeed2_input.equals("")) {
                        JSONObject tag1 = new JSONObject();
                        tag1.put("tagName", "fanled");
                        tag1.put("tagValue", fanspeed2_input);

                        jsonArray.put(tag1);
                    }
                    if (jsonArray.length() > 0)
                        payload.put("tags", jsonArray);
                } catch (JSONException e) {
                    Log.e(TAG, "JSONEXception");
                }
                Log.i(TAG,"payload="+payload);
                if (payload.length() >0 ) {
                    Toast.makeText(DeviceActivity.this,"세기: 2", Toast.LENGTH_SHORT).show();
                    new UpdateShadow(DeviceActivity.this, urlStr).execute(payload);
                }
                else
                    Toast.makeText(DeviceActivity.this,"변경할 상태 정보를 선택하세요", Toast.LENGTH_SHORT).show();
            }
        });
        Button fanspeedBtn3 = findViewById(R.id.fanspeed_3);
        fanspeedBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject payload = new JSONObject();

                try {
                    JSONArray jsonArray = new JSONArray();
                    String fanspeed3_input = "3";
                    if (fanspeed3_input != null && !fanspeed3_input.equals("")) {
                        JSONObject tag1 = new JSONObject();
                        tag1.put("tagName", "fanled");
                        tag1.put("tagValue", fanspeed3_input);

                        jsonArray.put(tag1);
                    }
                    if (jsonArray.length() > 0)
                        payload.put("tags", jsonArray);
                } catch (JSONException e) {
                    Log.e(TAG, "JSONEXception");
                }
                Log.i(TAG,"payload="+payload);
                if (payload.length() >0 ) {
                    Toast.makeText(DeviceActivity.this,"세기: 3", Toast.LENGTH_SHORT).show();
                    new UpdateShadow(DeviceActivity.this, urlStr).execute(payload);
                }
                else
                    Toast.makeText(DeviceActivity.this,"변경할 상태 정보를 선택하세요", Toast.LENGTH_SHORT).show();
            }
        });

        ///////////////////////////////////팬방향 : 수동 ////////////////////////////////////////////////////
        Button fanBtnB = findViewById(R.id.fandirect_back);
        fanBtnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject payload = new JSONObject();

                try {
                    JSONArray jsonArray = new JSONArray();
                    String fanB_input = "Back";
                    if (fanB_input != null && !fanB_input.equals("")) {
                        JSONObject tag1 = new JSONObject();
                        tag1.put("tagName", "fan");
                        tag1.put("tagValue", fanB_input);

                        jsonArray.put(tag1);
                    }
                    if (jsonArray.length() > 0)
                        payload.put("tags", jsonArray);
                } catch (JSONException e) {
                    Log.e(TAG, "JSONEXception");
                }
                Log.i(TAG,"payload="+payload);
                if (payload.length() >0 ) {
                    Toast.makeText(DeviceActivity.this,"fan방향: BACK", Toast.LENGTH_SHORT).show();
                    new UpdateShadow(DeviceActivity.this, urlStr).execute(payload);
                }
                else
                    Toast.makeText(DeviceActivity.this,"변경할 상태 정보를 선택하세요", Toast.LENGTH_SHORT).show();
            }
        });
        Button fanBtnS = findViewById(R.id.fandirect_stop);
        fanBtnS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject payload = new JSONObject();

                try {
                    JSONArray jsonArray = new JSONArray();
                    String fanS_input = "Stop";
                    if (fanS_input != null && !fanS_input.equals("")) {
                        JSONObject tag1 = new JSONObject();
                        tag1.put("tagName", "fan");
                        tag1.put("tagValue", fanS_input);

                        jsonArray.put(tag1);
                    }
                    if (jsonArray.length() > 0)
                        payload.put("tags", jsonArray);
                } catch (JSONException e) {
                    Log.e(TAG, "JSONEXception");
                }
                Log.i(TAG,"payload="+payload);
                if (payload.length() >0 ) {
                    Toast.makeText(DeviceActivity.this,"fan방향: STOP", Toast.LENGTH_SHORT).show();
                    new UpdateShadow(DeviceActivity.this, urlStr).execute(payload);
                }
                else
                    Toast.makeText(DeviceActivity.this,"변경할 상태 정보를 선택하세요", Toast.LENGTH_SHORT).show();
            }
        });
        Button fanBtnF = findViewById(R.id.fandirect_front);
        fanBtnF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject payload = new JSONObject();

                try {
                    JSONArray jsonArray = new JSONArray();
                    String fanF_input = "Front";
                    if (fanF_input != null && !fanF_input.equals("")) {
                        JSONObject tag1 = new JSONObject();
                        tag1.put("tagName", "fan");
                        tag1.put("tagValue", fanF_input);

                        jsonArray.put(tag1);
                    }
                    if (jsonArray.length() > 0)
                        payload.put("tags", jsonArray);
                } catch (JSONException e) {
                    Log.e(TAG, "JSONEXception");
                }
                Log.i(TAG,"payload="+payload);
                if (payload.length() >0 ) {
                    Toast.makeText(DeviceActivity.this,"fan방향: FRONT", Toast.LENGTH_SHORT).show();
                    new UpdateShadow(DeviceActivity.this, urlStr).execute(payload);
                }
                else
                    Toast.makeText(DeviceActivity.this,"변경할 상태 정보를 선택하세요", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void clearTextView() {
        //TextView reported_ledTV = findViewById(R.id.reported_led);
        //TextView reported_tempTV = findViewById(R.id.reported_temp);
        //reported_tempTV.setText("");
        //reported_ledTV.setText("");

        // 현재상태 조회 클리어
        TextView reported_fanledTV = findViewById(R.id.reported_fanled);
        TextView reported_dustinTV = findViewById(R.id.reported_dustin);
        TextView reported_dustinsTV = findViewById(R.id.reported_dustin_state);
        TextView reported_dustoutTV = findViewById(R.id.reported_dustout);
        TextView reported_dustoutsTV = findViewById(R.id.reported_dustout_state);
        reported_dustinTV.setText("");
        reported_fanledTV.setText("");
        reported_dustinsTV.setText("");
        reported_dustoutTV.setText("");
        reported_dustoutsTV.setText("");

       // TextView desired_ledTV = findViewById(R.id.desired_led);
        //TextView desired_tempTV = findViewById(R.id.desired_temp);
        //desired_tempTV.setText("");
        //desired_ledTV.setText("");
    }

}
