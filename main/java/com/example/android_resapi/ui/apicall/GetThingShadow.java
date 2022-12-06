package com.example.android_resapi.ui.apicall;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.example.android_resapi.R;
import com.example.android_resapi.httpconnection.GetRequest;

public class GetThingShadow extends GetRequest {
    final static String TAG = "AndroidAPITest";
    String urlStr;
    public GetThingShadow(Activity activity, String urlStr) {
        super(activity);
        this.urlStr = urlStr;
    }

    @Override
    protected void onPreExecute() {
        try {
            Log.e(TAG, urlStr);
            url = new URL(urlStr);

        } catch (MalformedURLException e) {
            Toast.makeText(activity,"URL is invalid:"+urlStr, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            activity.finish();
        }
    }

    @Override
    protected void onPostExecute(String jsonString) {
        if (jsonString == null)
            return;
        Map<String, String> state = getStateFromJSONString(jsonString);
        TextView reported_fanledTV = activity.findViewById(R.id.reported_fanled);
        TextView reported_dtfinTV = activity.findViewById(R.id.reported_dustin);
        TextView reported_dtfoutTV = activity.findViewById(R.id.reported_dustout);
        TextView reported_dtsinTV = activity.findViewById(R.id.reported_dustin_state);
        TextView reported_dtsoutTV = activity.findViewById(R.id.reported_dustout_state);
        reported_dtfinTV.setText(state.get("reported_dustfin")); //안농도
        reported_dtfoutTV.setText(state.get("reported_dustfout")); //밖농도
        reported_dtsinTV.setText(state.get("reported_dustsin")); // 안상태
        reported_dtsoutTV.setText(state.get("reported_dustsout")); //밖상태
        reported_fanledTV.setText(state.get("reported_fanled")); //팬세기




        /*Map<String, String> state = getStateFromJSONString(jsonString);
        TextView reported_fanledTV = activity.findViewById(R.id.reported_fanled);
        TextView reported_dtfinTV = activity.findViewById(R.id.reported_dustin);
        TextView reported_dtfoutTV = activity.findViewById(R.id.reported_dustout);
        reported_dtfinTV.setText(state.get("reported_dust_in_f"));
        reported_dtfoutTV.setText(state.get("reported_dust_out_f"));
        reported_fanledTV.setText(state.get("reported_fan_led")); */

       // TextView desired_ledTV = activity.findViewById(R.id.desired_led);
       // TextView desired_tempTV = activity.findViewById(R.id.desired_temp);
       // desired_tempTV.setText(state.get("desired_temperature"));
        //desired_ledTV.setText(state.get("desired_LED"));



        /*Map<String, String> state = getStateFromJSONString(jsonString);
        TextView reported_ledTV = activity.findViewById(R.id.reported_fanled);
        TextView reported_tempTV = activity.findViewById(R.id.reported_dustin);
        reported_tempTV.setText(state.get("reported_temperature"));
        reported_ledTV.setText(state.get("reported_LED")); */

    }

    protected Map<String, String> getStateFromJSONString(String jsonString) {
        Map<String, String> output = new HashMap<>();
        try {

            jsonString = jsonString.substring(1,jsonString.length()-1);
            // \\\" 를 \"로 치환
            jsonString = jsonString.replace("\\\"","\"");
            Log.i(TAG, "jsonString="+jsonString);
            JSONObject root = new JSONObject(jsonString);
            JSONObject state = root.getJSONObject("state");
            JSONObject reported = state.getJSONObject("reported");
            String dtfinValue = reported.getString("dustfin");
            String dtfoutValue = reported.getString("dustfout");
            String dtsinValue = reported.getString("dustsin");
            String dtsoutValue = reported.getString("dustsout");
            String fanledValue = reported.getString("fanled");
            output.put("reported_dustfin", dtfinValue);
            output.put("reported_dustfout", dtfoutValue);
            output.put("reported_dustsin", dtsinValue);
            output.put("reported_dustsout", dtsoutValue);
            output.put("reported_fanled",fanledValue);





            /*jsonString = jsonString.substring(1,jsonString.length()-1);
            // \\\" 를 \"로 치환
            jsonString = jsonString.replace("\\\"","\"");
            Log.i(TAG, "jsonString="+jsonString);
            JSONObject root = new JSONObject(jsonString);
            JSONObject state = root.getJSONObject("state");
            JSONObject reported = state.getJSONObject("reported");
            String dtfinValue = reported.getString("dust_in_f");
            String dtfoutValue = reported.getString("dust_out_f");
            String fanledValue = reported.getString("fan_led");
            output.put("reported_dust_in_f", dtfinValue);
            output.put("reported_dust_out_f", dtfoutValue);
            output.put("reported_fan_led",fanledValue); */

            //JSONObject desired = state.getJSONObject("desired");
           // String desired_tempValue = desired.getString("temperature");
            //String desired_ledValue = desired.getString("LED");
           // output.put("desired_temperature", desired_tempValue);
           // output.put("desired_LED",desired_ledValue);


            /*
            // 처음 double-quote와 마지막 double-quote 제거
            jsonString = jsonString.substring(1,jsonString.length()-1);
            // \\\" 를 \"로 치환
            jsonString = jsonString.replace("\\\"","\"");
            Log.i(TAG, "jsonString="+jsonString);
            JSONObject root = new JSONObject(jsonString);
            JSONObject state = root.getJSONObject("state");
            JSONObject reported = state.getJSONObject("reported");
            String tempValue = reported.getString("temperature");
            String ledValue = reported.getString("LED");
            output.put("reported_temperature", tempValue);
            output.put("reported_LED",ledValue);

            JSONObject desired = state.getJSONObject("desired");
            String desired_tempValue = desired.getString("temperature");
            String desired_ledValue = desired.getString("LED");
            output.put("desired_temperature", desired_tempValue);
            output.put("desired_LED",desired_ledValue);  */

        } catch (JSONException e) {
            Log.e(TAG, "Exception in processing JSONString.", e);
            e.printStackTrace();
        }
        return output;
    }
}