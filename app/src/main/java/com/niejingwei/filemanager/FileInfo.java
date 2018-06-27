package com.niejingwei.filemanager;

/**
 * Created by niejingwei on 2018/6/20.
 */

public class FileInfo {
    /**
     * 1 文件夹
     * 2 图片
     * 3 视频
     * 4 音频
     * 5 txt
     * 6 未知类型
     */
    public static final int FOLDER_FILETYPE=1;
    public static final int PICTURE_FILETYPE=2;
    public static final int VIDEO_FILETYPE=3;
    public static final int VIOCE_FILETYPE=4;
    public static final int TXT_FILETYPE=5;
    public static final int UNKNOWN_FILETYPE=6;

    private String Name;//文件（夹）名
    private int ContentItemNumber;//文件夹包含文件数
    private String FileType;//文件类型，文件类型决定文件图标
    private long ModifyDate;//最近修改时间
    private double size;//文件大小

    public FileInfo(String name, int contentItemNumber, String fileType, long modifyDate, double size) {
        Name = name;
        ContentItemNumber = contentItemNumber;
        FileType = fileType;
        ModifyDate = modifyDate;
        this.size = size;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getContentItemNumber() {
        return ContentItemNumber;
    }

    public void setContentItemNumber(int contentItemNumber) {
        ContentItemNumber = contentItemNumber;
    }

    public String getFileType() {
        return FileType;
    }

    public void setFileType(String fileType) {
        FileType = fileType;
    }

    public long getModifyDate() {
        return ModifyDate;
    }

    public void setModifyDate(long modifyDate) {
        ModifyDate = modifyDate;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }
}
