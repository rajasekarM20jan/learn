package com.example.learn;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    TextView myText;
    String resultData;
    EditText etForSearch;
    Button button;
    ImageView img;
    DbClass dbClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myText=findViewById(R.id.myText);
        etForSearch=findViewById(R.id.etForSearching);
        button=findViewById(R.id.button1);
        img=findViewById(R.id.imageView);

        OkHttpClient myClient=new OkHttpClient();
        String url="https://dummyjson.com/products";

        Request fetchData= new Request.Builder().url(url).build();
        myClient.newCall(fetchData).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("My Response: Error");
                e.printStackTrace();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                   resultData=response.body().string();
                }
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject obj=new JSONObject(resultData);
                            JSONArray arr=obj.getJSONArray("products");
                            dbClass=new DbClass(MainActivity.this);
                            dbClass.getCount();
                            int a=dbClass.count;
                            System.out.println("MY COUNT: "+a);
                            for(int i=a;i<arr.length();i++){
                                JSONObject jerry=(JSONObject) arr.get(i);
                                String id= Integer.toString((Integer) jerry.get("id")) ;
                                String title=(String) jerry.get("title");
                                String description=(String) jerry.get("description");
                                String price=Integer.toString((Integer) jerry.get("price"));
                                String discountPercentage=Double.toString((Double)jerry.get("discountPercentage"));
                                String rating=Double.toString(Double.parseDouble(jerry.get("rating").toString()));
                                String stock=Integer.toString((Integer) jerry.get("stock"));
                                String brand=(String) jerry.get("brand");
                                String category=(String) jerry.get("category");
                                String thumbnail=(String) jerry.get("thumbnail");
                                dbClass=new DbClass(MainActivity.this);
                                dbClass.insert(id,title,description,price,discountPercentage,rating,stock,brand,category,thumbnail);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etForSearch.length()!=0){
                    String data=String.valueOf(etForSearch.getText());
                    dbClass =new DbClass(MainActivity.this);
                    dbClass.getData(data);
                    String url=dbClass.thumbnail;
                    System.out.println("MY DATA: "+url);
                    Picasso.with(MainActivity.this).load(url).into(img);
                }
            }
        });


    }
}