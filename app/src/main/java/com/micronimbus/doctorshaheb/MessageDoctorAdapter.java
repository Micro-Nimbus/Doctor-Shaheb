package com.micronimbus.doctorshaheb;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageDoctorAdapter extends  RecyclerView.Adapter<MessageDoctorAdapter.MyViewHolder>{
   List<MessageDoctor> messageDoctorList;
    public MessageDoctorAdapter(List<MessageDoctor> messageDoctorList) {
this.messageDoctorList=messageDoctorList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View chatView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bot_item,null);
       MyViewHolder myViewHolder=new MyViewHolder(chatView);
       return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
MessageDoctor messageDoctor=messageDoctorList.get(position);
if (messageDoctor.getSentby().equals(MessageDoctor.Sent_BY_ME)){
    holder.leftChatview.setVisibility(View.GONE);
    holder.rightChatView.setVisibility(View.VISIBLE);
    holder.rightTextView.setText(messageDoctor.getMessage());

}
else {
    holder.rightChatView.setVisibility(View.GONE);
    holder.leftChatview.setVisibility(View.VISIBLE);
    holder.leftTextview.setText(messageDoctor.getMessage());
}
    }

    @Override
    public int getItemCount() {
        return messageDoctorList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
LinearLayout leftChatview,rightChatView;
TextView leftTextview,rightTextView;
      public MyViewHolder(@NonNull View itemView) {
          super(itemView);
leftChatview=itemView.findViewById(R.id.left_chat_view);
rightChatView=itemView.findViewById(R.id.right_chat_view);
leftTextview=itemView.findViewById(R.id.left_chat_text_view);
rightTextView=itemView.findViewById(R.id.right_chat_text_view);



      }
  }




}
