package com.example.hahaj.yeogida8;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class BuyItemActivity extends AppCompatActivity {

    NetworkUrl url = new NetworkUrl();

    //ImageView imageView;
    Bitmap bitmap;
    ImageView imageView1;
    TextView itemName, itemAddr, itemURL, itemStartDate, itemEndDate, itemPrice, new_itemPrice, itemDeadline, aboutItem2;
    ImageButton btn_onof;
    Button btn_buy;
    int i=0;
    boolean isLike;
    private int ppid;
    private int productpid = 9;

    //지도API
    //LinearLayout mapview;
    TMapView tmap;
    private final String MAPAPPKEY = "";
    double lat;
    double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_item);

        imageView1 = (ImageView)findViewById(R.id.image1);
        //imageView1.setImageResource(R.drawable.item1);
        itemName = (TextView)findViewById(R.id.itemName);
        itemAddr = (TextView)findViewById(R.id.itemAddr);
        itemURL = (TextView)findViewById(R.id.itemURL);
        itemStartDate = (TextView)findViewById(R.id.itemStartDate);
        itemEndDate = (TextView)findViewById(R.id.itemEndDate);

        itemPrice = (TextView)findViewById(R.id.itemPrice);
        new_itemPrice = (TextView)findViewById(R.id.new_itemPrice);
        itemDeadline = (TextView)findViewById(R.id.itemDeadline);
        aboutItem2 = (TextView)findViewById(R.id.aboutItem2);


        //personpid 불러오기
        SharedPreferences pref = getSharedPreferences("pref_PERSONPID", Context.MODE_PRIVATE);
        ppid = pref.getInt("personpid", 0); //pref_PERSONPID파일의 personpid 키에 있는 데이터를 가져옴. 없으면 0을 리턴
        Log.d("제품(상세)조회/구매화면 ppid>> ",""+ppid);

        //productpid 불러오기
        Intent productpidIntent = getIntent();
        //productpid = productpidIntent.getIntExtra("personpid", 0);

        //통신-제품상세정보 받아오기
        new JSONTask().execute(url.getMainUrl() + "/product/info");


        //double lat = 37.6026422;
        //double lng = 126.953058;
        likeItem();
        //initTmap(lng, lat);
        Log.d("문제확인6", "여기 아니다!!!!!!!!");
        //addMarker(lng, lat);
        butItem();
    }
    //찜 기능
    public void likeItem(){
        btn_onof = (ImageButton)findViewById(R.id.btn_likeonoff);
        btn_onof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i=1-i;
                if(i==1){
                    btn_onof.setImageResource(R.drawable.likeon);
                    Toast.makeText(getApplicationContext(), "찜 상품에 등록되었습니다.", Toast.LENGTH_LONG).show();
                    isLike = true;
                    //통신
                    //서버로 정보 전달
                    new JSONTask2().execute(url.getMainUrl() + "/choice/register");
                }
                else if(i==0){
                    btn_onof.setImageResource(R.drawable.likeoff);
                    Toast.makeText(getApplicationContext(), "찜 상품이 취소되었습니다.", Toast.LENGTH_LONG).show();
                    isLike = false;
                    //통신
                    //서버로 정보 전달
                    new JSONTask2().execute(url.getMainUrl() + "/choice/delete");
                }
            }
        });
    }

    //TMap 생성
    private void initTmap(double lng, double lat){
        tmap = new TMapView(this);
        tmap.setSKTMapApiKey(MAPAPPKEY);
        Log.d("문제확인1", "여기 아니다!!!!!!!!");
        ConstraintLayout mapview = (ConstraintLayout) findViewById(R.id.mapview);
        Log.d("문제확인2", "여기 아니다!!!!!!!!");
        this.lat = lat;
        this.lng = lng;
        //double lat = 37.5725749;
        //double lng = 126.9789405;
        tmap.setCenterPoint(lng, lat);
        tmap.setZoomLevel(15);

        mapview.addView(tmap);
        Log.d("문제확인3", "여기 아니다!!!!!!!!");
    }

    private void addMarker(double lng, double lat){
        //this.lat = lat;
        //this.lng = lng;
        TMapPoint tPoint = new TMapPoint(lat, lng);
        TMapMarkerItem marker = new TMapMarkerItem();
        marker.setTMapPoint(tPoint);
        Log.d("문제확인4", "여기 아니다!!!!!!!!");
        tmap.addMarkerItem("marker", marker);
        Log.d("문제확인5", "여기 아니다!!!!!!!!");
    }

    public void setProductimage(final String productimage){

        Thread mThread = new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL(productimage);

                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();//InputStream값 가져오기
                    bitmap = BitmapFactory.decodeStream(is);//Bitmap으로 변ㅇ환
                }catch (MalformedURLException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }
                //super.run();
            }
        };
        mThread.start();

        try{
            mThread.join();
            imageView1.setImageBitmap(bitmap);
        } catch (InterruptedException e){
            e.printStackTrace();
        }

    }

    public void butItem() {
        btn_buy = (Button) findViewById(R.id.btn_buy);
        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent productpidIntent = new Intent(getApplicationContext(), ChattingActivity.class);

                productpidIntent.putExtra("producupid", productpid);

                startActivity(productpidIntent);
                Log.d("버튼눌림","buybuttonpressed");
            }
        });

    }

    //제품 정보 보내는 통신코드
    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                Log.d("doInBackground 확인", "doIn");
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("productpid", productpid);
                jsonObject.put("personpid", ppid);
                Log.d("json put 다음", "");

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(urls[0]);
                    //연결을 함
                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();

                    //서버로 보내기위해서 스트림 만듬
                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();//버퍼를 받아줌

                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();//버퍼를 닫아줌
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Log.d("들어오는 pid", result);//서버로 부터 받은 값을 출력해주는 부분
            if(result==null) return;
            try {
                JSONObject jsonObject = new JSONObject(result);

                int propid = jsonObject.getInt("productpid");
                String productimg = jsonObject.getString("productimage");
                String productname = jsonObject.getString("productname");
                double addr_x = jsonObject.getDouble("productaddress_x");
                double addr_y = jsonObject.getDouble("productaddress_y");
                String productURL = jsonObject.getString("productUrl");
                String date_s = jsonObject.get("productdate_s").toString().substring(0, 10);
                String date_e = jsonObject.get("productdate_e").toString().substring(0, 10);
                String productaddr = jsonObject.getString("productaddress");
                int forprice = jsonObject.getInt("formerprice");
                int productprice = jsonObject.getInt("productprice");
                String productText = jsonObject.getString("text");
                int choice = jsonObject.getInt("choicechecker");

                Log.d("파싱확인1", "상품pid>"+propid+", 이미지"+productimg+", 이름"+productname);
                Log.d("파싱확인2", "x좌표"+addr_x+", y좌표"+addr_y+", 홈주소"+productURL);
                Log.d("파싱확인3", "시작"+date_s+", 마지막"+date_e+", 주소"+productaddr);
                Log.d("파싱확인4", "정가"+forprice+", 판매가"+productprice+", 설명"+productText);
                Log.d("파싱확인5", "찜여부"+choice);

                //찜 상태
                if(choice==1){
                    btn_onof.setImageResource(R.drawable.likeon);
                } else{
                    btn_onof.setImageResource(R.drawable.likeoff);
                }

                //지도
                initTmap(addr_x, addr_y);
                addMarker(addr_x, addr_y);

                //화면에 뿌리기
                setProductimage(productimg);
                itemName.setText(productname);
                itemAddr.setText(productaddr);
                itemURL.setText(productURL);
                itemStartDate.setText(date_s);
                itemEndDate.setText(date_e);
                itemPrice.setText(forprice+"");
                new_itemPrice.setText(productprice+"");
                aboutItem2.setText(productText);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
    }

    //찜 등록 및 삭제 통신코드
    public class JSONTask2 extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                Log.d("doInBackground 확인", "doIn");
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("choice_productpid", productpid);
                jsonObject.put("choice_personpid", ppid);
                Log.d("json put 다음", "");

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(urls[0]);
                    //연결을 함
                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();

                    //서버로 보내기위해서 스트림 만듬
                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();//버퍼를 받아줌

                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();//버퍼를 닫아줌
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Log.d("들어오는 pid", result);//서버로 부터 받은 값을 출력해주는 부분
            if(result==null) return;
            if(result=="success"){Toast.makeText(getApplication(), "기능 성공", Toast.LENGTH_SHORT).show();}

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){

            case BasicInfo.REQUEST_CODE_LIKEDTOBUY:
                if(resultCode == RESULT_OK){
                    //productpid = data.getIntExtra("productpid", 0);
                    Log.d("찜->구매 이동 ", "");
                }
        }
    }
}
