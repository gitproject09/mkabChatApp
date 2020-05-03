package org.mkab.chatapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.mkab.chatapp.R;
import org.mkab.chatapp.model.User;

import java.util.List;

public class DoctorListAdapter extends ArrayAdapter<User> {

    public DoctorListAdapter(@NonNull Context context, List<User> contactList) {
        super(context, R.layout.custom_friend_list_row, contactList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_doctor_list_row, parent, false);
        User user = getItem(position);
        TextView hiddenEmail = customView.findViewById(R.id.tv_HiddenEmail);
        TextView tv_Name = customView.findViewById(R.id.tv_DoctorFullName);
        TextView tv_DoctorStatus = customView.findViewById(R.id.tv_DoctorStatus);
        hiddenEmail.setText(String.valueOf(user.Email));
        //tv_Name.setText(Tools.toProperName(user.FirstName) + " " + Tools.toProperName(user.LastName));
        //tv_Name.setText(Tools.toProperName(user.FirstName) + "\n" + Tools.toProperName(user.LastName));
        tv_Name.setText(user.FirstName + "\n" + user.Majlish + "\nContact : "+user.LastName);
        tv_DoctorStatus.setText("Time : " + user.OnDuty);
        return customView;
    }

}
