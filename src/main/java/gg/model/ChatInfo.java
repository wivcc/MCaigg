package gg.model;

/**
 * Created by ZN on 2015/9/1.
 */
/**
 * 封装了聊天记录信息，包括了来源ID、目标ID、聊天内容、记录时间
 *
 */
public class ChatInfo {

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    /** 来源USER ID*/
    private String from;
    /** 目标USER ID*/
    private String to;
    /** 聊天内容*/
    private String content;
    /** 记录时间*/
    private long time;
    /**消息类型*/
    private int type;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

}
