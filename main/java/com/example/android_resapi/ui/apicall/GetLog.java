package com.example.android_resapi.ui.apicall;

import android.app.Activity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.example.android_resapi.R;
import com.example.android_resapi.httpconnection.GetRequest;

public class GetLog extends GetRequest {
    final static String TAG = "AndroidAPITest";
    String urlStr;
    public GetLog(Activity activity, String urlStr) {
        super(activity);
        this.urlStr = urlStr;
    }

    @Override
    protected void onPreExecute() {
        try {

            TextView textView_Date1 = activity.findViewById(R.id.textView_date1);
            TextView textView_Time1 = activity.findViewById(R.id.textView_time1);
            TextView textView_Date2 = activity.findViewById(R.id.textView_date2);
            TextView textView_Time2 = activity.findViewById(R.id.textView_time2);

            String params = String.format("?from=%s:00&to=%s:00",textView_Date1.getText().toString()+textView_Time1.getText().toString(),
                                                            textView_Date2.getText().toString()+textView_Time2.getText().toString());

            Log.i(TAG,"urlStr="+urlStr+params);
            url = new URL(urlStr+params);

        } catch (MalformedURLException e) {
            Toast.makeText(activity,"URL is invalid:"+urlStr, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        TextView message = activity.findViewById(R.id.message2);
        message.setText("조회중...");
    }

    @Override
    protected void onPostExecute(String jsonString) {
        TextView message = activity.findViewById(R.id.message2);
        if (jsonString == null) {
            message.setText("로그 없음");
            return;
        }
        message.setText("");
        ArrayList<Tag> arrayList = getArrayListFromJSONString(jsonString);

        final ArrayAdapter adapter = new ArrayAdapter(activity,
                android.R.layout.simple_list_item_1,
                arrayList.toArray());
        ListView txtList = activity.findViewById(R.id.logList);
        txtList.setAdapter(adapter);
        txtList.setDividerHeight(10);
    }

    protected ArrayList<Tag> getArrayListFromJSONString(String jsonString) {
        ArrayList<Tag> output = new ArrayList();
        try {
            // 처음 double-quote와 마지막 double-quote 제거
            jsonString = jsonString.substring(1,jsonString.length()-1);
            // \\\" 를 \"로 치환
            jsonString = jsonString.replace("\\\"","\"");

            Log.i(TAG, "jsonString="+jsonString);

            JSONObject root = new JSONObject(jsonString);
            JSONArray jsonArray = root.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = (JSONObject)jsonArray.get(i);

                Tag thing = new Tag(jsonObject.getString("dustsin"),
                                    jsonObject.getString("dustsout"),
                                    jsonObject.getString("fanled"),
                                    jsonObject.getString("fan"),
                                    jsonObject.getString("timestamp"));

               // Tag thing = new Tag(jsonObject.getString("dust_s_in"),
                //                   jsonObject.getString("dust_s_out"),
                //                   jsonObject.getString("dust_in_f"),
                //                   jsonObject.getString("dust_out-f"),
                //                   jsonObject.getString("fan"),
                //                   jsonObject.getString("fan_led"),
                //                   jsonObject.getString("timestamp"));
                output.add(thing);
            }

        } catch (JSONException e) {
            //Log.e(TAG, "Exception in processing JSONString.", e);
            e.printStackTrace();
        }
        return output;
    }

    class Tag {
        //String temperature1;
        //String temperature2;
        //String LED;
        String dustsin;
        String dustsout;
        String fanled;
        String fan;
        String timestamp;
        //String dust_s_in;
        //String dust_s_out;
       // String dust_in_f;
       // String dust_out_f;
        //String fan;
        //String fan_led;

        public Tag(String ndust_s_in,String ndust_s_out,String nfan_led,String nfan,String ntimestamp) { //String ndust_s_in,String ndust_s_out, String ndust_in_f, String ndust_out_f,String nfan,String nfan_led,String ntimestamp
           //String temp1,String temp2, String led, String timeString nfan
            //temperature1 = temp1;
            //temperature2 = temp2;
           // LED = led;
            // timestamp = time;

            dustsin=ndust_s_in;
            dustsout=ndust_s_out;
            fanled=nfan_led;
            fan=nfan;
            timestamp=ntimestamp;
            //dustfin=ndust_in_f;
            //dustfoutf=ndust_out_f;


        }

        public String toString() {
            //return String.format("[%s] Temperature1: %s,emperature1: %s, LED: %s", timestamp, temperature1,temperature2, LED);// 타임, 내부농도, 외부농도, 팬 세기
            return String.format("[%s] Dustsin: %s,Dustsout: %s, Fan: %s, Fanled: %s"
                    , timestamp, dustsin,dustsout,fan,fanled);
        }
    }
}

