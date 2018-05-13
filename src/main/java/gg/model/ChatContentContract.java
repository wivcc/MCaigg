package gg.model;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gg.example.utils.SerializeHelper;
import io.netty.buffer.ByteBuf;

/**
 * Created by ZN on 2015/9/3.
 */
public class ChatContentContract {
    private int ChatBoxContentLen;
    private Map<Integer, Integer> EmotionMap;
    private Map<Integer, byte[]> ForeignImageMap;
    private List<Integer> PicPosList;
    private int R;
    private int G;
    private int B;
    private int FontLen = -1;
    private String Text=" ";//此属性在只发图片时显得尤为重要，因图片需要描述在聊天文字中的索引位置;
    private byte[] TextBytes;

    public ChatContentContract()
    {}

    public ChatContentContract(Map<Integer, Integer> emotionMap, Map<Integer, byte[]> foreignImageMap, List<Integer> picPosList, String text) {
        this.EmotionMap = emotionMap;
        this.ForeignImageMap = foreignImageMap;
        this.PicPosList = picPosList;
        this.Text = text;
        try {
            this.TextBytes = text.getBytes("utf-8");
        } catch (Exception ee) {

        }
    }

    public Map<Integer, Integer> getEmotionMap() {
        return EmotionMap;
    }

    public void setEmotionMap(Map<Integer, Integer> emotionMap) {
        EmotionMap = emotionMap;
    }

    public Map<Integer, byte[]> getForeignImageMap() {
        return ForeignImageMap;
    }

    public void setForeignImageMap(Map<Integer, byte[]> foreignImageMap) {
        ForeignImageMap = foreignImageMap;
    }

    public List<Integer> getPicPosList() {
        return PicPosList;
    }

    public void setPicPosList(List<Integer> picPosList) {
        PicPosList = picPosList;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
        try {
            this.TextBytes = text.getBytes("utf-8");
        } catch (Exception ee) {

        }
    }

    public void deserialize(byte[] info) throws IOException {
         ByteBuf buffer=  SerializeHelper.wrappedBuffer(info);

        this.ChatBoxContentLen = buffer.readInt();
        this.R = buffer.readByte();
        this.G = buffer.readByte();
        this.B = buffer.readByte();
        int emotionCount = buffer.readInt();
        EmotionMap = new HashMap<Integer, Integer>();
        if (emotionCount > 0) {
            for (int i = 0; i < emotionCount; i++) {
                this.EmotionMap.put(buffer.readInt(), buffer.readInt());
            }
        }

        this.FontLen = buffer.readInt();
        if(this.FontLen>0)
        {
            byte[] bytes = new byte[this.FontLen];
            buffer.readBytes(bytes);
        }

        this.ForeignImageMap = new HashMap<Integer, byte[]>();
        int foreignImageCount = buffer.readInt();
        if (foreignImageCount > 0) {
            for (int i = 0; i < foreignImageCount; i++) {
                int key = buffer.readInt();
                int valueLen = buffer.readInt();
                byte[] value = new byte[valueLen];
                buffer.readBytes(value);
                this.ForeignImageMap.put(key, value);
            }
        }

        this.PicPosList = new ArrayList<Integer>();
        int picCount = buffer.readInt();
        if (picCount > 0) {
            for (int i = 0; i < picCount; i++) {
                this.PicPosList.add(buffer.readInt());
            }
        }

        this.Text = SerializeHelper.readStrIntLen(buffer);
    }

    public byte[] serialize() throws Exception {
        ByteBuf body = SerializeHelper.newBuffer();

        body.writeInt(ChatBoxContentLen);
        body.writeByte(this.R);
        body.writeByte(this.G);
        body.writeByte(this.B);
        ChatBoxContentLen = 3;
        ChatBoxContentLen += 4;
        if (this.EmotionMap == null) {
            body.writeInt(0);
        } else {
            body.writeInt(EmotionMap.size());

            for (Map.Entry<Integer, Integer> entry : EmotionMap.entrySet()) {
                body.writeInt(entry.getKey());
                body.writeInt(entry.getValue());
            }
            ChatBoxContentLen += EmotionMap.size() * (4 + 4);
        }

        body.writeInt(this.FontLen);
        ChatBoxContentLen += 4;
        ChatBoxContentLen += 4;
        if (this.ForeignImageMap == null) {
            body.writeInt(0);
        } else {
            body.writeInt(ForeignImageMap.size());
            for (Map.Entry<Integer, byte[]> entry : ForeignImageMap.entrySet()) {
                body.writeInt(entry.getKey());
                ChatBoxContentLen += 4;
                body.writeInt(entry.getValue().length);
                ChatBoxContentLen += 4;
                body.writeBytes(entry.getValue());
                ChatBoxContentLen += entry.getValue().length;
            }
        }
        ChatBoxContentLen += 4;
        if (this.PicPosList == null) {
            body.writeInt(0);
        } else {
            body.writeInt(this.PicPosList.size());
            for (int i = 0; i < this.PicPosList.size(); i++) {
                body.writeInt(this.PicPosList.get(i));
            }
            ChatBoxContentLen += this.PicPosList.size() * 4;
        }
        ChatBoxContentLen += 4;
        if (this.Text == null) {
            body.writeInt(-1);
        } else {
            body.writeInt(TextBytes.length);
            body.writeBytes(TextBytes);
            ChatBoxContentLen += TextBytes.length;
        }
        body.resetWriterIndex();
        body.writeInt(ChatBoxContentLen);


//        try
//        {
//            this.deserialize(body.array());
//        }
//        catch (Exception ee)
//        {
//
//        }
        return body.array();
    }
}
