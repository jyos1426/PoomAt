package caps.poomat.NetWork;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import caps.poomat.FragmentMap.FoodModel;
import caps.poomat.UserInfo;

/**
 * Created by Hyeon on 2016-04-25.
 */
public class SearchFromUserId {
    private FinishCallBack callBack;

    private List<FoodModel> searchModels = new ArrayList<>();

    public void execute(){
        searchModels.clear();
        new connectToServer().execute();

    }
    private class connectToServer extends AsyncTask<String,Void,String> {
        private String urlString = "http://poom.dothome.co.kr/userPoom.php";
        private String data;
        private HttpURLConnection urlConnection;
        private OutputStreamWriter outputStreamWriter;
        private URL url;
        private String result;
        private JSONObject jsonObject;
        private JSONArray jsonArray;
        @Override
        protected String doInBackground(String ... args)
        {
            try {
                url = new URL(urlString);
                urlConnection = (HttpURLConnection)url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

                urlConnection.connect();

                data = "userKakaoId=" + UserInfo.getKakaoId();
                Log.d("hello!!!!!!",data);
                outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream());
                outputStreamWriter.write(data);
                outputStreamWriter.flush();
                outputStreamWriter.close();


                InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream(), "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line + '\n');
                }

                result = stringBuilder.toString();

                jsonObject = new JSONObject(result);
                jsonArray = jsonObject.getJSONArray("poomFood");
                Log.d("hello",jsonArray+"");
                for(int i =0; i< jsonArray.length(); i++){
                    jsonObject = jsonArray.getJSONObject(i);
                    FoodModel model = new FoodModel();
                    model.setServiceUserId(jsonObject.getString("serviceUserId"));
                    model.setFoodName(jsonObject.getString("foodName"));
                    model.setFoodImagePath(jsonObject.getString("imagePath"));
                    model.setCategoryNum(jsonObject.getInt("categoryNum"));
                    model.setDate(jsonObject.getString("date"));
                    model.setWriteNum(jsonObject.getInt("writeNum"));
//                    model.setDistance(jsonObject.getDouble("distance"));
                    model.setLatitude(jsonObject.getDouble("latitude"));
                    model.setLongitude(jsonObject.getDouble("longitude"));
                    searchModels.add(model);
                }
                Log.d("hello",searchModels.size()+"");
                inputStreamReader.close();
                urlConnection.disconnect();

            }catch (Exception e){
                e.printStackTrace();
            }

            return "";
        }
        @Override
        protected void onPostExecute(String result){
            if(callBack != null){
                callBack.onFinish(searchModels);
            }
        }
    }
    public interface FinishCallBack{
        void onFinish(List<FoodModel> dataSet);
    }
    public void setFinishCallBack(FinishCallBack callBack){
        this.callBack = callBack;
    }

}
