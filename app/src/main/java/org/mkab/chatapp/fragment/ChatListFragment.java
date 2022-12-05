package org.mkab.chatapp.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.mkab.chatapp.R;
import org.mkab.chatapp.activity.ActivityChat;
import org.mkab.chatapp.adapter.AdapterLastChat;
import org.mkab.chatapp.model.Message;
import org.mkab.chatapp.model.User;
import org.mkab.chatapp.service.DataContext;
import org.mkab.chatapp.service.LocalUserService;

import java.util.List;

public class ChatListFragment extends Fragment {

    private static final String TAG = "DoctorsListFragment";
    private View rootView;
    private ListView lv_LastChatList;
    private DataContext db;
    User user;
    ListAdapter adapter;

    private List<Message> userLastChatList;
    SwipeRefreshLayout swipeRefreshLayout;
    boolean chatListRefresh = false;

    public ChatListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if (user == null) {
            user = LocalUserService.getLocalUserFromPreferences(getActivity());
        }
        db = new DataContext(getActivity(), null, null, 1);

        rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        lv_LastChatList = (ListView) rootView.findViewById(R.id.lv_LastChatList);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshlChat);

        userLastChatList = db.getUserLastChatList(user.Email);

        adapter = new AdapterLastChat(getActivity(), userLastChatList);
        lv_LastChatList.setAdapter(adapter);

        lv_LastChatList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TextView email = (TextView) view.findViewById(R.id.tv_lastChat_HiddenEmail);
                        TextView tv_Name = (TextView) view.findViewById(R.id.tv_lastChat_FriendFullName);
                        Intent intend = new Intent(getActivity(), ActivityChat.class);
                        intend.putExtra("FriendEmail", email.getText().toString());
                        intend.putExtra("FriendFullName", tv_Name.getText().toString());
                        startActivity(intend);
                    }
                }
        );

        lv_LastChatList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (userLastChatList.size() <= position) return false;
                final Message selectedMessageItem = userLastChatList.get(position);
                final CharSequence options[] = new CharSequence[]{"Delete Chat"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(selectedMessageItem.FriendFullName);
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        // the user clicked on list[index]
                        if (index == 0) {
                            // Delete Chat
                            new AlertDialog.Builder(getActivity())
                                    .setTitle(selectedMessageItem.FriendFullName)
                                    .setMessage("Are you sure to delete this chat?")
                                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            db.deleteChat(user.Email, selectedMessageItem.FromMail);
                                            Toast.makeText(getActivity(), "Chat deleted successfully", Toast.LENGTH_SHORT).show();
                                            userLastChatList = db.getUserLastChatList(user.Email);
                                            ListAdapter adp = new AdapterLastChat(getActivity(), userLastChatList);
                                            lv_LastChatList = (ListView) rootView.findViewById(R.id.lv_LastChatList);
                                            lv_LastChatList.setAdapter(adp);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, null)
                                    .show();
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

                return true;
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                userLastChatList.clear();
                userLastChatList = db.getUserLastChatList(user.Email);

                adapter = new AdapterLastChat(getActivity(), userLastChatList);
                lv_LastChatList.setAdapter(adapter);

                if(swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }

            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}

