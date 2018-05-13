package gg.dragon.persondata;

import java.util.HashMap;
import java.util.Map;

import gg.example.android_qqfix.R;
import gg.model.GGUser;
import gg.model.UserStatus;

public class ContactsInfo {

	public static Map<Integer,Integer> headImgMap=new HashMap<Integer,Integer>()
	{
		{
			put(0, R.drawable.i0);
			put(1, R.drawable.i1);
			put(2, R.drawable.i2);
			put(3, R.drawable.i3);
			put(4, R.drawable.i4);
			put(5, R.drawable.i5);
			put(6, R.drawable.i6);
			put(7, R.drawable.i7);
			put(8, R.drawable.i8);
			put(9, R.drawable.i9);
		}
	};

	public String userName=null;
	public String userSign=null;
	public int userImage=0;
	public String groupInfo=null;
	public UserStatus userStatus;
	public String userID;
	public int UnReadMessage=0;
	public ContactsInfo(String userName, String userSign, int userImage,
			String groupInfo) {
		super();
		this.userName = userName;
		this.userSign = userSign;
		this.userImage = userImage;
		this.groupInfo = groupInfo;
	}

	public ContactsInfo(GGUser user) {
		super();
		this.userName = user.getName();
		this.userSign = user.getSignature();
		this.userImage = headImgMap.get(user.getHeadImageIndex());
		this.groupInfo = "我的好友";
		this.userStatus=user.getUserStatus();
		this.userID=user.getUserID();
	}

}
