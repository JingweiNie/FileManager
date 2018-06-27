package com.niejingwei.filemanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreateNewFileActivity extends AppCompatActivity {
    private Button btn_ensure;
    private Button btn_cancel;
    private EditText et_newfilename;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_file);
        btn_cancel=findViewById(R.id.btn_cancel_createnewfile);
        btn_ensure=findViewById(R.id.btn_ensure_createnewfile);
        et_newfilename=findViewById(R.id.et_newfilename_createnewfile);
        btn_ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=et_newfilename.getText().toString();
                Intent info=new Intent();
                info.putExtra("newfilename",s);
                setResult(0,info);
                finish();
                overridePendingTransition(R.anim.out_anim,R.anim.out_anim);
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(1);
                finish();
                overridePendingTransition(R.anim.out_anim,R.anim.out_anim);
            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}
