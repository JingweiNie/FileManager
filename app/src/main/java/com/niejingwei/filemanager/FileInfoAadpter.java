package com.niejingwei.filemanager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by niejingwei on 2018/6/25.
 */

public class FileInfoAadpter extends ArrayAdapter<FileInfo> {
    private List<FileInfo> data=new ArrayList<>();
    private boolean[] selectState;//选中情况
    public int selectNumber;//选中数量
    private Handler mHandler;//用于在UI线程更新UI
    private boolean isselectAll=false;//是否全选标志
    public FileInfoAadpter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public FileInfoAadpter(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public FileInfoAadpter(@NonNull Context context, int resource, @NonNull FileInfo[] objects) {
        super(context, resource, objects);
    }

    public FileInfoAadpter(@NonNull Context context, int resource, int textViewResourceId, @NonNull FileInfo[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public FileInfoAadpter(@NonNull Context context, int resource, @NonNull List<FileInfo> objects,Handler handler) {
        super(context, resource, objects);
        data=objects;
        selectState=new boolean[data.size()];
        selectNumber=0;
        mHandler=handler;
    }

    public FileInfoAadpter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<FileInfo> objects) {
        super(context, resource, textViewResourceId, objects);
        data=objects;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        view= LayoutInflater.from(getContext().getApplicationContext()).inflate(R.layout.layout_listview_item,parent,false);
        ImageView icon=view.findViewById(R.id.iv_file_icon);//文件夹图标
        TextView filename=view.findViewById(R.id.tv_filename);//文件名
        TextView contentItemNumber=view.findViewById(R.id.tv_item_amount);//包含文件数
        TextView modifytime=view.findViewById(R.id.tv_modify_data);//最近修改时间
        final ImageView select_icon=view.findViewById(R.id.iv_select_icon);//选中图标
        icon.setImageResource(getContext().getResources().getIdentifier(getItem(position).getFileType(),"drawable",getContext().getPackageName()));
        filename.setText(getItem(position).getName());
        if(data.get(position).getFileType().equals("folder")){
            contentItemNumber.setText(String.valueOf(getItem(position).getContentItemNumber())+"项 |");
        }
        else {
            contentItemNumber.setText(String.valueOf(getItem(position).getSize())+"KB |");
        }
        modifytime.setText(DataTransfer.TimestampToDate(getItem(position).getModifyDate(),"yyyy年MM月dd日 HH:mm:ss"));
        if(selectState[position]==true){//防止listview中的布局复用造成的
            select_icon.setImageResource(R.drawable.select);
        }
        else {
            select_icon.setImageResource(R.drawable.unselect);
        }
        select_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext().getApplicationContext(),"点击的是"+position,Toast.LENGTH_SHORT).show();
                if(selectState[position]==false){//如果点击时是未选中状态
                    select_icon.setImageResource(R.drawable.select);
                    selectState[position]=true;
                    selectNumber++;
                }
                else {
                    select_icon.setImageResource(R.drawable.unselect);
                    selectState[position]=false;
                    selectNumber--;
                }
                Message message=new Message();
                if(selectNumber>0){//如果至少有一个文件被选中

                    message.arg1=SELECT_STATE;
                }
                else {
                    message.arg1=UNSELECT_STATE;
                }
                mHandler.sendMessage(message);
            }
        });
        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if(isselectAll==false){//判断是否是全选，若是全选则不必重新初始化选中状态记录数组
            selectState=new boolean[data.size()];
            selectNumber=0;
        }
    }

    /**
     * 用于全选
     */
    public void selectAll(){

        for(int i=0;i<selectState.length;i++){
            selectState[i]=true;
        }
        selectNumber=selectState.length;
        isselectAll=true;
        notifyDataSetChanged();
        isselectAll=false;
    }

    /**
     * 删除所有选中的文件（夹）
     */
    public void deleteFile(){
        File[] temp=MainActivity.history.peek();
        for(int i=0;i<temp.length;i++){
            //Log.d("当前目录文件数：", String.valueOf(temp.length));
            //Log.d("selectstate[i]=", String.valueOf(selectState[i]));
            if(selectState[i]==true){
                //Log.d("删除一个文件，文件名：", temp[i].getName());
                temp[i].delete();
                //Log.d("删除完成","");
            }
        }
    }
    public List<File> getCopyOrMoveFileInfo(){
        List<File> retureData=new ArrayList<>();
        File[] currentFiles=MainActivity.history.peek();
        for(int i=0;i<selectState.length;i++){
            if(selectState[i]==true){
                retureData.add(currentFiles[i]);
            }
        }
        return retureData;
    }
    public static final int SELECT_STATE=0;
    public static final int UNSELECT_STATE=1;
}
