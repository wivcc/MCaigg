package gg.model;

import com.oraycn.es.communicate.framework.model.TransferingProject;

/**
 * Created by ZN on 2015/9/1.
 */
public class FileInfo {
    private String comment;//文件命令
    private String fileName;//文件名
    private String projectID;//文件传送项目id
    private TransferingProject resumedFileItem;
    private String senderID; //发送者
    private long totalSize;// 文件大小
    public String getComment() {
        return comment;
    }
    public String getFileName() {
        String newName="";
        if (fileName.contains("/"))
            newName = fileName.substring(fileName.lastIndexOf("/") + 1);
        else
            newName = fileName.substring(fileName.lastIndexOf("\\") + 1);
        return newName;
    }
    public String getProjectID() {
        return projectID;
    }
    public TransferingProject getResumedFileItem() {
        return resumedFileItem;
    }
    public String getSenderID() {
        return senderID;
    }
    public long getTotalSize() {
        return totalSize;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }
    public void setResumedFileItem(TransferingProject resumedFileItem) {
        this.resumedFileItem = resumedFileItem;
    }public void setSenderID(String senderID) {
        this.senderID = senderID;
    }
    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }


}
