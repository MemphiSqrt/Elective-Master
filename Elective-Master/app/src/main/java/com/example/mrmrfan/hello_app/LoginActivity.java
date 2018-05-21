package com.example.mrmrfan.hello_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mrmrfan.hello_app.WebConnect.WebConnect;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Button btnLogin;
    private EditText Username;
    private EditText Password;
    private CheckBox remPassword;
    private TextView text;
    public static boolean flag = false;
    public static String USERNAME, PASSWORD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        pref= PreferenceManager.getDefaultSharedPreferences(this);
        Username = (EditText)findViewById(R.id.student_id);
        Password = (EditText)findViewById(R.id.password);
        remPassword = (CheckBox)findViewById(R.id.rem_password);
        text = (TextView)findViewById(R.id.textView);
        btnLogin = (Button) findViewById(R.id.login);
        boolean isRemenber=pref.getBoolean("remPassword",false);


        if(isRemenber){
            //将账号和密码都设置到文本中
            String USERNAME=pref.getString("USERNAME","");
            String PASSWORD=pref.getString("PASSWORD","");
            Username.setText(USERNAME);
            Password.setText(PASSWORD);
            remPassword.setChecked(true);
        }

        btnLogin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                flag = false;
                if (TextUtils.isEmpty(Username.getText().toString())){
                    Username.setError("请输入用户名");
                    Username.requestFocus();
                }
                else if(TextUtils.isEmpty(Password.getText().toString())){
                    Password.setError("请输入密码");
                    Password.requestFocus();
                }
                else {
                    String s = null;
                    Thread thread = new Thread(runnable);
                    thread.start();
                    try {
                        thread.join();
                    }catch(InterruptedException e){
                        System.out.println(e);
                    }
                    if (flag)
                        s = "登陆成功";
                    else
                        s = "用户名或密码错误";
                    Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    Runnable runnable;
    {
        runnable = new Runnable() {
            @Override
            public void run() {
                WebConnect X = new WebConnect();
                try {
                    USERNAME = Username.getText().toString();
                    PASSWORD = Password.getText().toString();
                    if (X.Login(USERNAME,PASSWORD)) {
                        flag = true;
                        editor=pref.edit();
                        if(remPassword.isChecked()){
                            editor.putBoolean("remPassword",true);
                            editor.putString("USERNAME",USERNAME);
                            editor.putString("PASSWORD",PASSWORD);
                        }else {
                            editor.clear();
                        }
                        editor.apply();

                        X.getPageInfo();

                        //进入选课阶段
                        Intent intent = new Intent();
                        intent.setClass(LoginActivity.this,SelectActivity.class);
                        startActivity(intent);
                    }
                    else {
                        System.out.println("user name or password is wrong!");
                    }
                } catch (Exception e) {
                    System.out.println("Connect time limit exceeded!");
                }
            }
        };
    }
}

