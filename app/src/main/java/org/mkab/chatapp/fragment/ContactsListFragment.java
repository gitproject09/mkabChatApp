package org.mkab.chatapp.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;
import org.mkab.chatapp.R;
import org.mkab.chatapp.activity.ActivityFriendProfile;
import org.mkab.chatapp.activity.DoctorDetailsActivity;
import org.mkab.chatapp.adapter.FriendListAdapter;
import org.mkab.chatapp.model.StaticInfo;
import org.mkab.chatapp.model.User;
import org.mkab.chatapp.service.DataContext;
import org.mkab.chatapp.service.IFireBaseAPI;
import org.mkab.chatapp.service.LocalUserService;
import org.mkab.chatapp.service.Tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;

public class ContactsListFragment extends Fragment {

    private static final String TAG = "DoctorsListFragment";
    private View rootView;
    private DataContext db;
    User user;
    private ProgressDialog pd;
    private List<User> userFriendList;
    ListAdapter adapter;
    ListView lv_FriendList;
    SwipeRefreshLayout swipeRefreshLayout;
    boolean apiCall = false;
    public ContactsListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if (user == null) {
            user = LocalUserService.getLocalUserFromPreferences(getActivity());
        }
        db = new DataContext(getActivity(), null, null, 1);
        pd = new ProgressDialog(getActivity());
        pd.setCancelable(false);
        pd.setMessage("Refreshing...");

        rootView = inflater.inflate(R.layout.fragment_contact, container, false);

        lv_FriendList = rootView.findViewById(R.id.lv_FriendList);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshlContact);

        userFriendList = db.getUserFriendList();

        if (!apiCall && Tools.isNetworkAvailable(getActivity())) {
            apiCall = true;
            FriendListTask friendListTask = new FriendListTask();
            friendListTask.execute();
        } else {
            userFriendList = db.getUserFriendList();
        }

        adapter = new FriendListAdapter(getActivity(), userFriendList);
        lv_FriendList.setAdapter(adapter);

        lv_FriendList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (userFriendList != null && userFriendList.size() > 0) {
                            TextView email = (TextView) view.findViewById(R.id.tv_HiddenEmail);
                            TextView tv_Name = (TextView) view.findViewById(R.id.tv_FriendFullName);

                            Intent intent = new Intent(getActivity(), DoctorDetailsActivity.class);
                            intent.putExtra(DoctorDetailsActivity.EXTRA_FROM, "Contacts");
                            intent.putExtra(DoctorDetailsActivity.EXTRA_TITLE, userFriendList.get(position).FirstName);
                            intent.putExtra(DoctorDetailsActivity.EXTRA_BODY, userFriendList.get(position).Majlish);
                            intent.putExtra(DoctorDetailsActivity.EXTRA_MOBILE, userFriendList.get(position).LastName);
                            intent.putExtra(DoctorDetailsActivity.EXTRA_EMAIL, email.getText().toString());

                            startActivity(intent);
                        } else {

                        }

                    }
                }

        );

        lv_FriendList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (userFriendList.size() <= position) return false;
                final User selectedUser = userFriendList.get(position);
                final CharSequence options[] = new CharSequence[]{"Profile", "Delete Contact"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(selectedUser.FirstName + " " + selectedUser.LastName);
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        // the user clicked on list[index]
                        if (index == 0) {
                            // Profile
                            Intent intent = new Intent(getActivity(), ActivityFriendProfile.class);
                            intent.putExtra("Email", selectedUser.Email);
                            startActivityForResult(intent, StaticInfo.ChatAciviityRequestCode);
                        } else {
                            // Delete Contact
                            new AlertDialog.Builder(getActivity())
                                    .setTitle(selectedUser.FirstName + " " + selectedUser.LastName)
                                    .setMessage("Are you sure to delete this contact?")
                                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Firebase ref = new Firebase(StaticInfo.EndPoint + "/friends/" + user.Email + "/" + selectedUser.Email);
                                            ref.removeValue();
                                            // delete from local database
                                            db.deleteFriendByEmailFromLocalDB(selectedUser.Email);
                                            Toast.makeText(getActivity(), "Contact deleted successfully", Toast.LENGTH_SHORT).show();
                                            userFriendList = db.getUserFriendList();
                                            ListAdapter adp = new FriendListAdapter(getActivity(), userFriendList);
                                            ListView lv_FriendList = (ListView) rootView.findViewById(R.id.lv_FriendList);
                                            lv_FriendList.setAdapter(adp);
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
                FriendListTask friendListTask = new FriendListTask();
                friendListTask.execute();
            }
        });

        return rootView;
    }

    public class FriendListTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            user = LocalUserService.getLocalUserFromPreferences(getActivity());
            IFireBaseAPI api = Tools.makeRetroFitApi();
            Call<String> call = api.getUserFriendsListAsJsonString(StaticInfo.FriendsURL + "/" + user.Email + ".json");
            try {
                return call.execute().body();
            } catch (IOException e) {
                e.printStackTrace();
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String jsonListString) {

            try {

                if(swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }

                user = LocalUserService.getLocalUserFromPreferences(getActivity());
                List<User> friendList = new ArrayList<>();

                if (jsonListString != null) {
                    JSONObject userFriendTree = new JSONObject(jsonListString);
                    for (Iterator iterator = userFriendTree.keys(); iterator.hasNext(); ) {
                        String key = (String) iterator.next();
                        User friend = new User();
                        JSONObject friendJson = userFriendTree.getJSONObject(key);
                        friend.Email = friendJson.getString("Email");
                        friend.FirstName = friendJson.getString("FirstName");
                        friend.LastName = friendJson.getString("LastName");
                        friend.Majlish = "";
                        friendList.add(friend);
                    }

                    // refresh local database
                    db = new DataContext(getActivity(), null, null, 1);

                    db.deleteAllFriendsFromLocalDB();

                    db.refreshUserFriendList(friendList);

                    userFriendList.clear();
                    userFriendList = db.getUserFriendList();
                   // userFriendList = friendList;

                    // set to adapter
                    adapter = new FriendListAdapter(getActivity(), userFriendList);
                    lv_FriendList.setAdapter(adapter);

                    if (getView() != null && pd != null && pd.isShowing()) {
                        pd.dismiss();
                    }

                    //Toast.makeText(getActivity(), "Contacts list is updated", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}

