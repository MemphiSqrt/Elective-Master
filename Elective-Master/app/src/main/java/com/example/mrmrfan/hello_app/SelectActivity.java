package com.example.mrmrfan.hello_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mrmrfan.hello_app.WebConnect.WebConnect;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;


public class SelectActivity extends AppCompatActivity {
    ListView lv = null;
    Button btn_selectAll = null;
    Button btn_calcel = null;
    Button btn_confirm = null;
    EditText showlists = null;
    // 控制按钮文字
    public boolean select_course = true;
    // 是否选上课程
    public static boolean select_flag = false;
    // 是否继续刷课
    public static boolean flag = true;
    public static Handler handler;
    public static String name = null;
    public int num;

    // 接收课程名列表
    String course[] = new String[50];
    String course_end[] = new String[50];

    // 已选中的课程名，可以传给supplying
    ArrayList<String> listStr = null;
    private List<HashMap<String, Object>> list = null;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_course);

        lv = (ListView)findViewById(R.id.lv);
        btn_selectAll = (Button)findViewById(R.id.selectall);
        btn_calcel = (Button)findViewById(R.id.cancel);
        btn_confirm = (Button)findViewById(R.id.confirm);
        showlists = (EditText)findViewById(R.id.showlist);

        BufferedReader br = null;
        num = 0;
        try {
            br = new BufferedReader(new FileReader("data/data/com.example.mrmrfan.hello_app/PageWebInfo.txt"));
            String s = br.readLine();
            num = Integer.valueOf(s);
            for (int i = 0; i < num; i++)
                course[i] = br.readLine();
            for (int i = 0; i < num; i++) {
                course_end[i] = br.readLine();
            }

        }catch (IOException e) {}

        showCheckBoxListView();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.arg1 == 1) {
                    Toast.makeText(SelectActivity.this, "选课成功", Toast.LENGTH_SHORT).show();
                    btn_confirm.setText("刷课");
                    select_course = !select_course;
                }
                else if (msg.arg1 == 0) {
                    Toast.makeText(SelectActivity.this, name+" 发现空位", Toast.LENGTH_SHORT).show();
                }else if (msg.arg1 == 2)
                    Toast.makeText(SelectActivity.this, name+" 选课人数没有变化", Toast.LENGTH_SHORT).show();

            }
        };

        //刷课
        btn_confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(select_course) {
                    Toast.makeText(SelectActivity.this, "刷课开始", Toast.LENGTH_SHORT).show();
                    select_course = !select_course;
                    btn_confirm.setText("停止");
                    new Thread(runnable).start();
                }
                else {
                    Toast.makeText(SelectActivity.this, "停止刷课", Toast.LENGTH_SHORT).show();
                    select_course = !select_course;
                    flag = true;
                    btn_confirm.setText("刷课");
                }
            }
        });

        //全选
        btn_selectAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                listStr = new ArrayList<String>();
                for (int i = 0; i < list.size(); i++) {
                    MyAdapter.isSelected.put(i, true);
                    listStr.add(course[i]);
                }
                adapter.notifyDataSetChanged();//注意这一句必须加上，否则checkbox无法正常更新状态
                refresh();
            }
        });

        //取消已选
        btn_calcel.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                for(int i=0;i<list.size();i++){
                    if(MyAdapter.isSelected.get(i)){
                        MyAdapter.isSelected.put(i, false);
                        listStr.remove(course[i]);
                    }
                }
                adapter.notifyDataSetChanged();
                refresh();
            }

        });
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            flag = !flag;
            WebConnect X = new WebConnect();
            try {
                if (X.Login(LoginActivity.USERNAME,LoginActivity.PASSWORD)) {
                    Vector<String> lst = new Vector<>();
                    for (int i = 0; i < listStr.size(); i++) {
                        System.out.println(listStr.get(i));
                        String s = null;
                        s = listStr.get(i);
                        lst.add(s);
                    }
                    X.supply(lst);
                    if (SelectActivity.select_flag == true) {
                        Message message = Message.obtain();
                        message.arg1 = 1;
                        handler.sendMessage(message);
                    }
                }
                else {
                    System.out.println("user name or password is wrong!");
                }
            } catch (Exception e) {
                System.out.println("Connect time limit exceeded!");
            }
        }
    };

    public void refresh() {
        String s = "已选课程列表：";
        for (int i = 0; i < listStr.size(); i++)
            s += ("    " + listStr.get(i));
        showlists.setText(s);
    }

    public void courseinfo(View v) {
        Intent intent = new Intent();
        intent.setClass(SelectActivity.this, CourseInfoActivity.class);
        startActivity(intent);
    }

    // 显示带有checkbox的listview
    public void showCheckBoxListView() {
        list = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < num; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("item_course", "课程名称： "+course[i] + "\n授课老师： "+course_end[i]);      // 课程名
            map.put("item_cb", false);        // checkbox
            list.add(map);

            adapter = new MyAdapter(this, list, R.layout.listviewitem,
                    new String[] { "item_course","item_cb" }, new int[] {
                    R.id.item_course, R.id.item_cb });
            lv.setAdapter(adapter);
            listStr = new ArrayList<String>();
            lv.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View view,
                                        int position, long arg3) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    holder.cb.toggle();// 在每次获取点击的item时改变checkbox的状态
                    MyAdapter.isSelected.put(position, holder.cb.isChecked()); // 同时修改map的值保存状态
                    if (holder.cb.isChecked()) {
                        listStr.add(course[position]);
                    } else {
                        listStr.remove(course[position]);
                    }
                    refresh();
                }

            });
        }
    }

    //为listview自定义适配器内部类
    public static class MyAdapter extends BaseAdapter {
        public static HashMap<Integer, Boolean> isSelected;
        private Context context = null;
        private LayoutInflater inflater = null;
        private List<HashMap<String, Object>> list = null;
        private String keyString[] = null;
        private String itemString = null; // 记录每个item中textview的值
        private int idValue[] = null;// id值

        public MyAdapter(Context context, List<HashMap<String, Object>> list,
                         int resource, String[] from, int[] to) {
            this.context = context;
            this.list = list;
            keyString = new String[from.length];
            idValue = new int[to.length];
            System.arraycopy(from, 0, keyString, 0, from.length);
            System.arraycopy(to, 0, idValue, 0, to.length);
            inflater = LayoutInflater.from(context);
            init();
        }

        // 初始化 设置所有checkbox都为未选择
        public void init() {
            isSelected = new HashMap<Integer, Boolean>();
            for (int i = 0; i < list.size(); i++) {
                isSelected.put(i, false);
            }
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int arg0) {
            return list.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup arg2) {
            ViewHolder holder = null;
            if (holder == null) {
                holder = new ViewHolder();
                if (view == null) {
                    view = inflater.inflate(R.layout.listviewitem, null);
                }
                holder.tv = (TextView) view.findViewById(R.id.item_course);
                holder.cb = (CheckBox) view.findViewById(R.id.item_cb);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            HashMap<String, Object> map = list.get(position);
            if (map != null) {
                itemString = (String) map.get(keyString[0]);
                holder.tv.setText(itemString);
            }
            holder.cb.setChecked(isSelected.get(position));
            return view;
        }

    }
}
