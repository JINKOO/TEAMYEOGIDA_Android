package com.example.hahaj.yeogida8;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class SearchAddrActivity extends AppCompatActivity {


    // 우체국 오픈api 인증키
    private String _key = "";
    private TextView _addressEdit;
    private Button _searchBtn;
    private ListView _addressListView;

    private ArrayAdapter<String> _addressListAdapter;

    // 사용자가 입력한 주소
    private String _putAddress;
    // 우체국으로부터 반환 받은 우편주소 리스트
    private ArrayList<String> _addressSearchResultArr = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_addr);


        _addressEdit = (EditText)findViewById(R.id.addressedit);
        _searchBtn = (Button)findViewById(R.id.btnsearch);
        _addressListView = (ListView)findViewById(R.id.addresslist);

        _searchBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                getAddress(_addressEdit.getText().toString());
            }
        });
    }

    public void getAddress(String kAddress) {
        _putAddress = kAddress;
        new GetAddressDataTask().execute();
    }

    private class GetAddressDataTask extends AsyncTask<String, Void, HttpResponse> {
        @Override
        protected HttpResponse doInBackground(String... urls)
        {
            HttpResponse response = null;
            final String apiurl = "http://biz.epost.go.kr/KpostPortal/openapi";

            ArrayList<String> addressInfo = new ArrayList<String>();

            HttpURLConnection conn = null;
            try
            {
                StringBuffer sb = new StringBuffer(3);
                sb.append(apiurl);
                sb.append("?regkey=" + _key + "&target=postNew&query=");
                sb.append(URLEncoder.encode(_putAddress, "EUC-KR"));
                String query = sb.toString();

                URL url = new URL(query);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("accept-language", "ko");

                DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                byte[] bytes = new byte[4096];
                InputStream in = conn.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while (true)
                {
                    int red = in.read(bytes);
                    if (red < 0)
                        break;
                    baos.write(bytes, 0, red);
                }
                String xmlData = baos.toString("utf-8");
                baos.close();
                in.close();
                conn.disconnect();

                Document doc = docBuilder.parse(new InputSource(new StringReader(xmlData)));
                Element el = (Element) doc.getElementsByTagName("itemlist").item(0);
                for (int i = 0; i < ((Node) el).getChildNodes().getLength(); i++)
                {
                    Node node = ((Node) el).getChildNodes().item(i);
                    if (!node.getNodeName().equals("item"))
                    {
                        continue;
                    }
                    String address = node.getChildNodes().item(1).getFirstChild().getNodeValue();
                    String post = node.getChildNodes().item(3).getFirstChild().getNodeValue();
                    Log.w("jaeha", "address = " + address);
                    Log.w("post", "post ="+post);

                    //addressInfo.add(address + "\n우편번호:" + post.substring(0, 3) + "-" + post.substring(3));
                    addressInfo.add(post);
                }

                _addressSearchResultArr = addressInfo;
                publishProgress();
            } catch (Exception e)
            {
                e.printStackTrace();
            } finally
            {
                try
                {
                    if (conn != null)
                        conn.disconnect();
                } catch (Exception e)
                {
                }
            }

            return response;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);

            String[] addressStrArray = new String[_addressSearchResultArr.size()];
            addressStrArray = _addressSearchResultArr.toArray(addressStrArray);

            _addressListAdapter = new ArrayAdapter<String>(SearchAddrActivity.this, android.R.layout.simple_list_item_1, addressStrArray);
            _addressListView.setAdapter(_addressListAdapter);


            //사용자가 선택한 항목
            _addressListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String resAddr = (String)parent.getItemAtPosition(position);
                    Log.d("사용자가 선택한 주소", resAddr);


                    /*등록 Activity에서 온것인지, 수정 Activity에서 온것인지 구분 필요*/
                    Intent intent = getIntent();

                    int which  = intent.getIntExtra("whereFrom", 0);
                    Log.d("어디서 들어온것일까? ", Integer.toString(which));

                    /*
                      which값이 1이면 RegisterActivity에서 부른 것이다.
                                2이면 UpdateActivity에서 부른 것이다.

                    */
                    if(which == 1) {
                        Log.d("어디서 들어온것일까?1 ", Integer.toString(which));
                        Intent intent2 = new Intent();
                        intent2.putExtra("address_register", resAddr);
                        setResult(BasicInfo.REDIRECT_REGISTER, intent2);
                        finish();
                    }

                    else if(which == 2) {
                        Log.d("어디서 들어온것일까?2 ", Integer.toString(which));
                        Intent intent3 = new Intent();
                        intent3.putExtra("address_update", resAddr);
                        setResult(BasicInfo.REDIRECT_UPDATE, intent3);
                        finish();
                    }

                    //나머지 which에따른 예외처리가 필요하다.

//                    Log.d("어디서 들어온것일까?3", Integer.toString(which));
                }
            });
        }
    }
}
