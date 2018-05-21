package com.example.mrmrfan.hello_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.mrmrfan.hello_app.WebConnect.WebConnect;
import com.example.mrmrfan.hello_app.WebConnect.crawl.Crawl1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.mrmrfan.hello_app.WebConnect.WebConnect.isQueEmpty;

public class CourseInfoActivity extends AppCompatActivity {
    public static String courses[] = new String[]{"信息","信息","信息"};
    public static String teachers[] = new String[]{"刘","刘","刘"};
    public static String fields[] = new String[]{"ss","ss","ss"};
    public static String Eintros[] = new String[]{"ss","ss","ss"};
    public static String Cintros[] = new String[]{"ss","ss","ss"};
    public static String comments[] = new String[]{"ss","ss","ss"};
    public static String classname = "乒乒球";
    public static boolean course_exist = true;
    private Handler handler;
    public int tot;
    private ListView listview;
    private SimpleAdapter simpleAdapter;
    private List<Map<String, Object>> datalist;
    SearchView searchview = null;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_info);

        searchview = (SearchView)findViewById(R.id.course_info_searchview);
        listview = (ListView)findViewById(R.id.course_info_listview);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                showlistview();

            }
        };

        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                classname = query;
                new Thread(runnable).start();
                showlistview();
                searchview.setIconified(true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        // p1-listview    p2-clicked_view      i-position     p4-id
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> p1, View p2, int i, long p4){
                if (tot == 0)
                    course_exist = false;
                Intent intent = new Intent();
                intent.setClass(CourseInfoActivity.this, PutCourse.class);
                intent.putExtra("extra data", i);
                startActivity(intent);
            }
        });
    }

    public void showlistview(){
        datalist = new ArrayList<Map<String, Object>>();

        //tot 搜索到的课程数
        for (int i = 0; i < tot;i++) {
            HashMap<String,Object> items = new HashMap<String,Object>();
            items.put("course", courses[i]);
            items.put("teacher", teachers[i]);
            datalist.add(items);
        }

        simpleAdapter = new SimpleAdapter(this, datalist, R.layout.courselistitem,
                new String[]{"course", "teacher"},new int[]{R.id.course,R.id.teacher});
        listview.setAdapter(simpleAdapter);
    }

    public void selectcourse(View v) {
        Intent intent = new Intent();
        intent.setClass(CourseInfoActivity.this, SelectActivity.class);
        startActivity(intent);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            WebConnect X = new WebConnect();
            try {
                if (X.Login(LoginActivity.USERNAME,LoginActivity.PASSWORD)) {
                    X.Crawl2getWeb();
                    tot = 0;
                    while(!isQueEmpty()) {
                        String website = X.getFirstWebsite();
                        X.popFirstWebsite();
                        String name = Crawl1.GetName(website);
                        if(classname.equals(name) == false)
                            continue;
                        ++tot;

                        // 记录课程名
                        courses[tot - 1] = Crawl1.GetName(website);
                        // 记录授课老师
                        teachers[tot - 1] = Crawl1.GetTeacher(website);
                        fields[tot - 1] = Crawl1.GetField(website);
                        Cintros[tot - 1] = Crawl1.GetDescription(website);
                        Eintros[tot - 1] = Crawl1.GetDescriptionEng(website);
                        comments[tot - 1] = Crawl1.GetScore(website);
                    }
                    Message message = Message.obtain();
                    handler.sendMessage(message);
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
