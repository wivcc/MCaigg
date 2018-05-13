package gg.example.android_qqfix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gg.model.FileInfo;

/**
 * Created by ZN on 2015/9/11.
 */
public class FileMessageAdapter extends BaseAdapter {
    private Context context;
    private View.OnClickListener clickListener;

    public FileMessageAdapter(Context context,View.OnClickListener clickListener) {
        this.context = context;
        this.clickListener=clickListener;
    }

    public List<FileInfo> fileList = new ArrayList<FileInfo>();

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public FileInfo getItem(int position) {
        return fileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FileInfo info=fileList.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.file_message_item, null);
            MessageHold hold = new MessageHold();
            hold.user = (TextView) convertView.findViewById(R.id.userName);
            hold.msg = (TextView) convertView.findViewById(R.id.messageContext);
            hold.accept = (Button) convertView.findViewById(R.id.file_accept);
            hold.cancle = (Button) convertView.findViewById(R.id.file_cancle);
            hold.bar=(ProgressBar)convertView.findViewById(R.id.proccess);
            hold.accept.setTag(info);
            hold.cancle.setTag(info);
            hold.accept.setOnClickListener(clickListener);
            hold.cancle.setOnClickListener(clickListener);
            convertView.setTag(hold);
        }
        MessageHold hold = (MessageHold) convertView.getTag();
        //hold.user.setText(info.getSenderID());
        hold.msg.setText(info.getFileName());
//        if(info.getResumedFileItem()==null){
//            hold.accept.setVisibility(View.GONE);
//        }

        return convertView;
    }

    public class MessageHold {
        public TextView user;
        public TextView msg;
        public Button accept;
        public Button cancle;
        public ProgressBar bar;
    }

}