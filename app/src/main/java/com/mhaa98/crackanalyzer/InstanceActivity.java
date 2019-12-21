package com.mhaa98.crackanalyzer;

import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.IllegalFormatCodePointException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InstanceActivity extends AppCompatActivity {

    TextView instaceGempa1, instaceGempa2, instaceGempa3, instaceGempa4, instaceGempa5;
    String[] arr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instance);

        getSupportActionBar().setTitle("Instance Gempa");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        arr = new String[5];

        instaceGempa1 = findViewById(R.id.gempa_1);
        instaceGempa2 = findViewById(R.id.gempa_2);
        instaceGempa3 = findViewById(R.id.gempa_3);
        instaceGempa4 = findViewById(R.id.gempa_4);
        instaceGempa5 = findViewById(R.id.gempa_5);

        instaceGempa1.setVisibility(View.GONE);
        instaceGempa2.setVisibility(View.GONE);
        instaceGempa3.setVisibility(View.GONE);
        instaceGempa4.setVisibility(View.GONE);
        instaceGempa5.setVisibility(View.GONE);

        if (haveNetwork()){
            Toast.makeText(this, "Sedang mencari", Toast.LENGTH_SHORT).show();
            cariInstance();
        }
        else {
            Toast.makeText(this, "Tidak ada koneksi", Toast.LENGTH_SHORT).show();
        }


    }

    private void cariInstance() {
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<InstanceModel>> call = apiInterface.getInstance();
        call.enqueue(new Callback<List<InstanceModel>>() {
            @Override
            public void onResponse(Call<List<InstanceModel>> call, Response<List<InstanceModel>> response) {
                List<InstanceModel> instanceModel = response.body();
                int i=0;
                boolean ada = false;
                for(InstanceModel im:instanceModel)
                {
                    ada = true;
                    if (i<=4){
                        if (i==0){
                            instaceGempa1.setVisibility(View.VISIBLE);
                            instaceGempa1.setText(""+im.getInstance());
                            instaceGempa1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    menentukanInstance(instaceGempa1.getText()+"");
                                }
                            });
                        }
                        else if (i==1){
                            instaceGempa2.setVisibility(View.VISIBLE);
                            instaceGempa2.setText(""+im.getInstance());
                            instaceGempa2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    menentukanInstance(instaceGempa2.getText()+"");
                                }
                            });
                        }
                        else if (i==2){
                            instaceGempa3.setVisibility(View.VISIBLE);
                            instaceGempa3.setText(""+im.getInstance());
                            instaceGempa3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    menentukanInstance(instaceGempa3.getText()+"");
                                }
                            });
                        }
                        else if (i==3){
                            instaceGempa4.setVisibility(View.VISIBLE);
                            instaceGempa4.setText(""+im.getInstance());
                            instaceGempa4.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    menentukanInstance(instaceGempa4.getText()+"");
                                }
                            });
                        }
                        else if (i==4){
                            instaceGempa5.setVisibility(View.VISIBLE);
                            instaceGempa5.setText(""+im.getInstance());
                            instaceGempa5.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    menentukanInstance(instaceGempa5.getText()+"");
                                }
                            });
                        }
                    }
                    i++;
                }

                if (ada==false){
                    instaceGempa1.setVisibility(View.VISIBLE);
                    instaceGempa1.setText("kosong");
                    LoginActivity.mSQLiteHelper.updateInstance("kosong");
                }
            }
            @Override
            public void onFailure(Call<List<InstanceModel>> call, Throwable t) {
            }
        });
    }

    void menentukanInstance(String ins){
        LoginActivity.mSQLiteHelper.updateInstance(ins);
        LoginActivity.mSQLiteHelper.updateInstanceGlobal(ins);
        Toast.makeText(this, ""+ins, Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    private boolean haveNetwork(){
        boolean have_WIFI=false;
        boolean have_MobileData=false;

        ConnectivityManager connectivityManager=(ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos=connectivityManager.getAllNetworkInfo();

        for(NetworkInfo info:networkInfos){
            if(info.getTypeName().equalsIgnoreCase("WIFI")){
                if(info.isConnected()){
                    have_WIFI=true;
                }
            }
            if(info.getTypeName().equalsIgnoreCase("MOBILE")){
                if (info.isConnected()){
                    have_MobileData=true;
                }
            }
        }
        return have_MobileData||have_WIFI;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
