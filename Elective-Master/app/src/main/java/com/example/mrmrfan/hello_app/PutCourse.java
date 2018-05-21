package com.example.mrmrfan.hello_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;


// 显示课程相关信息
public class PutCourse extends AppCompatActivity {
    EditText coursename, teacher, field, Eintro, Cintro, comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager);

        Intent intent = getIntent();
        int i = getIntent().getIntExtra("extra data", 1);

        coursename = (EditText)findViewById(R.id.coursename);
        teacher = (EditText)findViewById(R.id.teacher);
        field = (EditText)findViewById(R.id.field);
        Eintro = (EditText)findViewById(R.id.Eintro);
        Cintro = (EditText)findViewById(R.id.Cintro);
        comment = (EditText)findViewById(R.id.comment);

        if (i != 10) {
            coursename.setText("课程名称： "+CourseInfoActivity.courses[i]);
            teacher.setText("授课老师： "+CourseInfoActivity.teachers[i]);
            field.setText("通选课领域："+CourseInfoActivity.fields[i]);
            Eintro.setText("英文简介：\n"+CourseInfoActivity.Eintros[i]);
            Cintro.setText("中文简介： \n"+CourseInfoActivity.Cintros[i]);
            comment.setText("课程评价：\n"+CourseInfoActivity.comments[i]);
        }
    }
}
