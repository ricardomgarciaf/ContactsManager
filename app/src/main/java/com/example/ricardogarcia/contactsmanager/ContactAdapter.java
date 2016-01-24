package com.example.ricardogarcia.contactsmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by ricardogarcia on 1/22/16.
 */
public class ContactAdapter extends BaseAdapter{

    public static String CONTACT="com.example.ricardogarcia.contactsmanager.CONTACT";
    private Activity activity;
    private LayoutInflater inflater;
    public ContactAdapter(Activity activity) {
        inflater= (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity=activity;
    }

    @Override
    public int getCount() {
        return Application.listContacts.size();
    }

    @Override
    public Object getItem(int i) {
        return Application.listContacts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewholder;
        if(Application.listContacts.size()>0) {
            if (view == null) {
                view=inflater.inflate(R.layout.contact_row,viewGroup,false);
                viewholder= new ViewHolder();
                viewholder.textId = (TextView) view.findViewById(R.id.textId);
                viewholder.textName = (TextView) view.findViewById(R.id.textName);
                view.setTag(viewholder);
            }else{
                viewholder= (ViewHolder) view.getTag();
            }
            viewholder.textId.setText(Application.listContacts.get(i).getId());
            viewholder.textName.setText(Application.listContacts.get(i).getName());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent= new Intent(activity,ContactDescription.class);
                    intent.putExtra(CONTACT,i);
                    activity.startActivity(intent);
                }
            });

        }

        return view;
    }

    private class ViewHolder{
        TextView textId;
        TextView textName;
    }
}
