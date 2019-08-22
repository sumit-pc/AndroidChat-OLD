package com.example.aymen.androidchat;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class ChatBoxAdapter  extends RecyclerView.Adapter<ChatBoxAdapter.MyViewHolder> {

    private static int TYPE_SELF = 1;
    private static int TYPE_OTHER = 2;
    private List<Message> MessageList;

    public  class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nickname;
        public TextView message;


        public MyViewHolder(View view) {
            super(view);
            nickname = (TextView) view.findViewById(R.id.nickname);
            message = (TextView) view.findViewById(R.id.message);
        }
    }
    public ChatBoxAdapter(List<Message>MessagesList) {

        this.MessageList = MessagesList;


    }

    @Override
    public int getItemCount() {
        return MessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (MessageList.get(position).getNickname().equals(ChatBoxActivity.Nickname)) {
            return TYPE_SELF;

        } else {
            return TYPE_OTHER;
        }
    }

    @Override
    public ChatBoxAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        if(viewType == TYPE_SELF)
        {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_right, parent, false);
        }
        else
        {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_left, parent, false);
        }


        return new ChatBoxAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ChatBoxAdapter.MyViewHolder holder, final int position) {
        final Message m = MessageList.get(position);
        holder.nickname.setText(m.getNickname() +" : ");

        holder.message.setText(m.getMessage() );




    }



}