package gg.model;

import gg.example.utils.SerializeHelper;
import io.netty.buffer.ByteBuf;

/**
 * Created by ZN on 2015/9/2.
 */
public enum UserStatus
{
    Online(2),Away(3),  Busy (4), DontDisturb (5), OffLine (6),Hide(7);

    private int type;

    public static UserStatus getUserStatusByCode(int code) {
        for (UserStatus type : UserStatus.values()) {
            if (type.getType() == code) {
                return type;
            }
        }
        return null;
    }

    public static   byte[] GetStatusBytes(UserStatus status)
    {
        ByteBuf body = SerializeHelper.newBuffer();

        body.writeInt(status.getType());

        return  body.array();
    }

    public static  String GetStatusName(UserStatus status)
    {
        ByteBuf body = SerializeHelper.newBuffer();

        String name="";
        switch (status)
        {
            case  Online:
            name="在线";
            break;
            case  Away:
                name="离开";
                break;
            case  Busy:
                name="正在忙";
                break;
            case  DontDisturb:
                name="请勿打扰";
                break;
            default:
                name="离线";
                break;
        }

        return  name;
    }

    private UserStatus(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
