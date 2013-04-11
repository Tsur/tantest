package com.scripturesos.tantest;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
//import android.util.SparseArray;


public class MessageListAdapter extends BaseAdapter
{
	
	protected Activity activity;
	protected ArrayList<ChatMessage> chatsMessages;
	/*private RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
	         RelativeLayout.LayoutParams.WRAP_CONTENT,
	         RelativeLayout.LayoutParams.WRAP_CONTENT);*/
	
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
			holder.message.setBackgroundResource(R.drawable.lbubble);
			holder.message.setPadding(15, 20, 15, 10);
		}
		else
		{
			/*rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			holder.container.setLayoutParams(rlp);*/
			holder.container.setGravity(Gravity.RIGHT);
			holder.message.setBackgroundResource(R.drawable.rbubble);
		}

		//TextView name = (TextView) vi.findViewById(R.id.contacts_lv_name);
		holder.message.setText(cm.message); 
		
		return vi;
	  }
	
	 class ViewHolder
	 {
	        TextView message;
	        LinearLayout container;
	 }
	 

}