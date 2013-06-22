package com.scripturesos.tantest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MessageListAdapter extends BaseAdapter
{
	
	protected Activity activity;
	protected ArrayList<ChatMessage> chatsMessages;
	private DateFormat date = new SimpleDateFormat("HH:mm", Locale.getDefault());
	
	public MessageListAdapter(Activity activity, ArrayList<ChatMessage> chats) 
	{
		    this.activity = activity;
		    this.chatsMessages = chats;
	}
	
	public void add(ChatMessage client)
	{
		chatsMessages.add(client);
		//notifyDataSetChanged();
	}
	
	@Override
	public int getCount() 
	{
		  return chatsMessages.size();
	}
	 
	@Override
	public Object getItem(int position) 
	{
		  return chatsMessages.get(position);
	}
	 
	@Override
	public long getItemId(int position) 
	{
		  return position;
	}
	 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View vi = convertView;
		ViewHolder holder = null;

		if(vi == null || !( vi.getTag() instanceof ViewHolder))
		{
			
			LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vi = inflater.inflate(R.layout.chat_message, null);

			//holder
			holder = new ViewHolder();
            holder.message =(TextView) vi.findViewById(R.id.chat_message);
            holder.container =(LinearLayout) vi.findViewById(R.id.chat_wrapper);
            holder.chat =(LinearLayout) vi.findViewById(R.id.chat_view);
            holder.info = (TextView) vi.findViewById(R.id.chat_info);
            //Save holder
            vi.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) vi.getTag();
		}
		
		
		ChatMessage cm = chatsMessages.get(position);
		
		if(cm.left)
		{
			/*rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			holder.container.setLayoutParams(rlp);*/
			holder.container.setGravity(Gravity.LEFT);
			holder.chat.setBackgroundResource(R.drawable.leftbub);
		}
		else
		{
			/*rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			holder.container.setLayoutParams(rlp);*/
			holder.container.setGravity(Gravity.RIGHT);
			holder.chat.setBackgroundResource(R.drawable.rightbub);
			
		}
		
		holder.info.setText(date.format(new Date()));
		
		if(cm.root)
		{
			holder.chat.setBackgroundResource(R.drawable.rootbub);
			//holder.message.setPadding(35, 30, 30, 30);
		}
		
		holder.message.setText(cm.message); 

		if(cm.confirmed)
		{
			//&#x2713;
			holder.info.setText(holder.info.getText() + " " + "✓");
			//holder.confirmed.setBackgroundResource(R.drawable.msg_confirmed);
			//holder.message.setPadding(40, 15, 15, 20);
		}
		/*else
		{
			holder.info.setText("Enviado: " +date.format(new Date()));
		}*/
		//TextView name = (TextView) vi.findViewById(R.id.contacts_lv_name);
		
		return vi;
	  }
	
	 class ViewHolder
	 {
	        TextView message;
	        LinearLayout container;
	        LinearLayout chat;
	        TextView info;
	 }
	 

}