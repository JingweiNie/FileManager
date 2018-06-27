package com.niejingwei.filemanager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {



    private Button btn_Cancel;//取消按钮
    private Button btn_Allselect;//全选按钮
    private Button btn_createNewFile;//创建新文件
    private Button btn_order;//排序
    private Button btn_copy;//复制
    private Button btn_move;//移动
    private Button btn_delete;//删除
    private Button btn_paste;//粘贴
    private TextView tv_current_directory;//当前目录显示
    private FrameLayout topPanel;
    private LinearLayout bottomPanel;
    private LinearLayout pastePanel;//复制或剪切模式是显示粘贴选项的底部面板
    
    private ArrayList<FileInfo> data=new ArrayList<>();
    
    private static File[] files;
    public static Stack<File[]> history;//历史记录，用于后退时返回上一级
    private FileInfoAadpter fileInfoAadpter;
    private File currentLocation;//当前位置
    /**
     *
     */
    public static final int NORMAL_STATE=0;
    public static final int COPY_STATE=1;
    public static final int MOVE_STATE=2;
    private int currentState=0;//当前是否处于复制或移动状态
    private List<File> filesToCopyOrMove;//记录被选中的待复制或移动的文件
    //handler用来处理UI更新
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.arg1==FileInfoAadpter.SELECT_STATE){
                topPanel.setVisibility(View.VISIBLE);
                bottomPanel.setVisibility(View.VISIBLE);
            }
            else if(msg.arg1==FileInfoAadpter.UNSELECT_STATE){
                topPanel.setVisibility(View.GONE);
                bottomPanel.setVisibility(View.GONE);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        topPanel=findViewById(R.id.topPanel_framelayout);
        bottomPanel=findViewById(R.id.bottomPanel_linearlayout);
        pastePanel=findViewById(R.id.pastePanel_linearlayout);
        btn_Cancel=findViewById(R.id.btn_cancel);//取消按钮
        btn_Allselect=findViewById(R.id.btn_allselect);//全选按钮
        btn_createNewFile=findViewById(R.id.btn_createnewfile);
        btn_copy=findViewById(R.id.btn_copy);
        btn_move=findViewById(R.id.btn_move);
        btn_delete=findViewById(R.id.btn_delete);
        btn_order=findViewById(R.id.btn_order);
        btn_paste=findViewById(R.id.btn_paste);
        tv_current_directory=findViewById(R.id.tv_current_directory);
        btn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topPanel.setVisibility(View.GONE);
                bottomPanel.setVisibility(View.GONE);
                fileInfoAadpter.notifyDataSetChanged();
            }
        });
        btn_Allselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileInfoAadpter.selectAll();
            }
        });
        btn_createNewFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startCreateNewFileActivity=new Intent(MainActivity.this,CreateNewFileActivity.class);
                startActivityForResult(startCreateNewFileActivity,0);
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileInfoAadpter.deleteFile();//删除所有选中文件
                updateCurrentData();//更新data
                fileInfoAadpter.notifyDataSetChanged();//通知adapter数据更新
            }
        });
        btn_order.setOnClickListener(new View.OnClickListener() {//排序按钮监听器
            @Override
            public void onClick(View v) {
                data.sort(new Comparator<FileInfo>() {
                    @Override
                    public int compare(FileInfo o1, FileInfo o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
                fileInfoAadpter.notifyDataSetChanged();
            }
        });
        btn_copy.setOnClickListener(new View.OnClickListener() {//复制模式
            @Override
            public void onClick(View v) {
                pastePanel.setVisibility(View.VISIBLE);
                currentState=COPY_STATE;
                filesToCopyOrMove=fileInfoAadpter.getCopyOrMoveFileInfo();//获取被选中的file集合

            }
        });
        btn_move.setOnClickListener(new View.OnClickListener() {//剪切模式
            @Override
            public void onClick(View v) {
                pastePanel.setVisibility(View.VISIBLE);
                currentState=MOVE_STATE;
                filesToCopyOrMove=fileInfoAadpter.getCopyOrMoveFileInfo();//获取被选中的file集合
            }
        });
        btn_paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("点击粘贴", String.valueOf(filesToCopyOrMove.size()));
                if(currentState==COPY_STATE){

                    for(File x:filesToCopyOrMove){
                        try {
                            Files.copy(Paths.get(x.getAbsolutePath()),Paths.get(String.valueOf(currentLocation),x.getName()));
                            Log.d("开始复制：从 ", x.getAbsolutePath()+" 到 "+currentLocation+x.getName());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else if(currentState==MOVE_STATE){
                    for(File x:filesToCopyOrMove){
                        try {
                            Files.move(Paths.get(x.getAbsolutePath()),Paths.get(currentLocation+x.getName()));
                            Log.d("开始移动：从 ", x.getAbsolutePath()+" 到 "+currentLocation+x.getName());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                currentState=NORMAL_STATE;
            }
        });

        history=new Stack<>();//历史记录初始化
        ListView listView=findViewById(R.id.lv_showfileinfo);
        File file=new File("/storage/emulated/0");
        currentLocation=file;//初始化当前父目录
        //String[] filenames=file.list();
        files=file.listFiles();
        history.push(files);//根目录入栈
        for(int i=0;i<files.length;i++){
            data.add(new FileInfo(files[i].getName(),files[i].list()==null?0:files[i].list().length,files[i].isDirectory()==true?"folder":"file",files[i].lastModified(),files[i].length()/1024));
        }
        Log.d("data初始化完成时：data.size=", String.valueOf(data.size()));
        /*for(int i=0;i<filenames.length;i++){
            data.add(new FileInfo(filenames[i],2,null,123,1.0));
        }*/
        fileInfoAadpter=new FileInfoAadpter(getApplicationContext(),R.layout.layout_listview_item,data,handler);
        listView.setAdapter(fileInfoAadpter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//listview的每个item的点击事件
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //点击进入新的目录后多选模式自动退出
                topPanel.setVisibility(View.GONE);
                bottomPanel.setVisibility(View.GONE);


                //Toast.makeText(getApplicationContext(),files[position].getAbsolutePath(),Toast.LENGTH_SHORT).show();
                if(history.peek()[position].isDirectory()){//如果点击的时文件夹时进入，否则点击的是文件
                    File fileroot=new File(history.peek()[position].getAbsolutePath());
                    currentLocation=fileroot;//更新当前父目录
                    tv_current_directory.setText(currentLocation.getAbsolutePath());
                    Log.d("当前位置更新：", currentLocation.getAbsolutePath());
                    files=fileroot.listFiles();
                    history.push(files);//每点击一次下一级目录入栈
                    data.clear();
                    for(int i=0;i<files.length;i++){
                        data.add(new FileInfo(files[i].getName(),files[i].list()==null?0:files[i].list().length,files[i].isDirectory()==true?"folder":"file",files[i].lastModified(),files[i].length()/1024));
                    }
                    fileInfoAadpter.notifyDataSetChanged();
                }
                else {
                    File file=new File(history.peek()[position].getAbsolutePath());
                    //String end = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length()).toLowerCase(Locale.getDefault());
                    //System.out.println("文件类型为："+end);
                    Intent intent = null;
                    intent=new OpenFileUtil(getApplicationContext()).openFile(file.getAbsolutePath());
                    startActivity(intent);
                    System.out.println(Environment.getExternalStorageDirectory());

                }

            }
        });
    }
    private void updateCurrentData(){
        data.clear();
        File[] files=currentLocation.listFiles();
        for(int i=0;i<files.length;i++){
            data.add(new FileInfo(files[i].getName(),files[i].list()==null?0:files[i].list().length,files[i].isDirectory()==true?"folder":"file",files[i].lastModified(),files[i].length()/1024));
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==0){
            File newFileToCreate=new File(currentLocation.getAbsolutePath()+"/"+data.getStringExtra("newfilename"));
            newFileToCreate.mkdir();
            files=currentLocation.listFiles();
            history.pop();
            history.push(files);//更新历史记录
            this.data.clear();
            for(int i=0;i<files.length;i++){
                this.data.add(new FileInfo(files[i].getName(),files[i].list()==null?0:files[i].list().length,files[i].isDirectory()==true?"folder":"file",files[i].lastModified(),files[i].length()/1024));
            }
            fileInfoAadpter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        Log.d("之前历史记录数：", String.valueOf(history.size()));
        Log.d("之前data.size：", String.valueOf(data.size()));
        if(history.size()>1){
            history.pop();
            File[] files=history.peek();
            data.clear();
            for(int i=0;i<files.length;i++){
                this.data.add(new FileInfo(files[i].getName(),files[i].list()==null?0:files[i].list().length,files[i].isDirectory()==true?"folder":"file",files[i].lastModified(),files[i].length()/1024));
            }
            fileInfoAadpter.notifyDataSetChanged();

        }
        else {
            super.onBackPressed();
        }
        Log.d("之后历史记录数：", String.valueOf(history.size()));
        Log.d("之后data.size：", String.valueOf(data.size()));
        //计算返回后的父目录
        String[] strArr=currentLocation.getAbsolutePath().split("\\/");
        String temp="";
        for(int i=1;i<strArr.length-1;i++){
            temp=temp+"/"+strArr[i];
        }
        currentLocation=new File(temp);//更新当前父目录
        tv_current_directory.setText(currentLocation.getAbsolutePath());//更新当前父目录的显示
        Log.d("当前位置更新：", currentLocation.getAbsolutePath());
    }

}
