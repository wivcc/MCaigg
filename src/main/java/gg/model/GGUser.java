package gg.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gg.example.utils.SerializeHelper;
import io.netty.buffer.ByteBuf;

/**
 * Created by ZN on 2015/9/2.
 */
public class GGUser {

    public Date getCreateTime() {
        return CreateTime;
    }

    public String getDefaultFriendCatalog() {
        return DefaultFriendCatalog;
    }

    public String getFriends() {
        return Friends;
    }

    public String getGroups() {
        return Groups;
    }

    public int getHeadImageIndex() {
        return HeadImageIndex;
    }

    public byte[] getHeadImageData() {
        return HeadImageData;
    }

    public String getName() {
        return Name;
    }

    public String getPasswordMD5() {
        return PasswordMD5;
    }

    public String getSignature() {
        return Signature;
    }

    public String getUserID() {
        return UserID;
    }

    public int getFriendsLen() {
        return FriendsLen;
    }

    public gg.model.UserStatus getUserStatus() {
        return UserStatus;
    }

    public void setUserStatus(gg.model.UserStatus userStatus) {
        UserStatus = userStatus;
    }

    public int getVersion() {
        return Version;
    }

    private Date CreateTime;
    private String DefaultFriendCatalog;
    private  String Friends;
    private  String Groups;
    private  int HeadImageIndex;
    private byte[] HeadImageData;
    private String Name;
    private String PasswordMD5;
    private  String Signature;
    private  int FriendsLen;
    private String UserID;
    private  int Version;
    private UserStatus UserStatus;



    public void deserialize(ByteBuf buffer) throws IOException {

        buffer.readInt();
        this.CreateTime=new Date(buffer.readLong());
        this.DefaultFriendCatalog=SerializeHelper.readStrIntLen(buffer);
        this.Friends=SerializeHelper.readStrIntLen(buffer);
        this.Groups=SerializeHelper.readStrIntLen(buffer);

         int headImageDataLen=buffer.readInt();
        if(headImageDataLen>0)
        {
            this.HeadImageData=new byte[headImageDataLen];
            buffer.readBytes(HeadImageData);
        }
        this.HeadImageIndex=buffer.readInt();
        this.Name=SerializeHelper.readStrIntLen(buffer);
        this.PasswordMD5=SerializeHelper.readStrIntLen(buffer);
        this.Signature=SerializeHelper.readStrIntLen(buffer);

        this.UserID= SerializeHelper.readStrIntLen(buffer);
        this.UserStatus=UserStatus.getUserStatusByCode(buffer.readInt());
        this.Version=buffer.readInt();
    }

    public static List<GGUser> deserializeUserList(ByteBuf buffer) throws IOException
    {
        List<GGUser> list=new ArrayList<GGUser>();
        int userCount=buffer.readInt();

        for (int i=0;i<userCount;i++)
        {
            GGUser user=new GGUser();
            user.deserialize(buffer);
            list.add(user);
        }
        return  list;
    }
}


