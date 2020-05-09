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

import org.json.JSONException;
import org.json.JSONObject;
import org.mkab.chatapp.R;
import org.mkab.chatapp.activity.ActivityFriendProfile;
import org.mkab.chatapp.activity.DoctorDetailsActivity;
import org.mkab.chatapp.adapter.DoctorListAdapter;
import org.mkab.chatapp.model.StaticInfo;
import org.mkab.chatapp.model.User;
import org.mkab.chatapp.service.DataContext;
import org.mkab.chatapp.service.IFireBaseAPI;
import org.mkab.chatapp.service.LocalUserService;
import org.mkab.chatapp.service.Tools;
import org.mkab.chatapp.utils.LogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;

public class AurvedicListFragment extends Fragment {

    private static final String TAG = "DoctorsListFragment";
    private View rootView;
    private List<User> userDoctorList;
    private DataContext db;
    User user;
    private ProgressDialog pd;
    ListAdapter adapter;
    ListView lv_DoctorList;
    SwipeRefreshLayout swipeRefreshLayout;
    boolean apiCall = false;
    String doctorType = "Aurvedic";
    public AurvedicListFragment() {
        LogUtil.printInfoMessage(AurvedicListFragment.class.getSimpleName(), "First Fragment","doctorType : " + doctorType);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        user = LocalUserService.getLocalUserFromPreferences(getActivity());
        db = new DataContext(getActivity(), null, null, 1);
        pd = new ProgressDialog(getActivity());
        pd.setCancelable(false);
        pd.setMessage("Refreshing...");

        rootView = inflater.inflate(R.layout.fragment_aurvedic, container, false);
        lv_DoctorList = rootView.findViewById(R.id.lv_DoctorList);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshlAurvedic);

        userDoctorList = db.getUserDoctorList(doctorType);

        if (!apiCall && Tools.isNetworkAvailable(getActivity())) {
            apiCall = true;
            DoctorListTask doctorListTask = new DoctorListTask();
            doctorListTask.execute();
        } else {
            userDoctorList = db.getUserDoctorList(doctorType);
        }

        adapter = new DoctorListAdapter(getActivity(), userDoctorList);
        lv_DoctorList.setAdapter(adapter);

        lv_DoctorList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (userDoctorList != null && userDoctorList.size() > 0) {
                            TextView email = view.findViewById(R.id.tv_HiddenEmail);
                            TextView tv_Name = view.findViewById(R.id.tv_FriendFullName);

                            Intent intent = new Intent(getActivity(), DoctorDetailsActivity.class);
                            intent.putExtra(DoctorDetailsActivity.EXTRA_FROM, "Aurvedic");
                            intent.putExtra(DoctorDetailsActivity.EXTRA_TITLE, userDoctorList.get(position).FirstName);
                            intent.putExtra(DoctorDetailsActivity.EXTRA_BODY, userDoctorList.get(position).Majlish);
                            intent.putExtra(DoctorDetailsActivity.EXTRA_MOBILE, userDoctorList.get(position).LastName);
                            intent.putExtra(DoctorDetailsActivity.EXTRA_EMAIL, email.getText().toString());

                            startActivity(intent);
                        }
                    }
                }
        );

        lv_DoctorList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (userDoctorList.size() <= position) return false;
                final User selectedUser = userDoctorList.get(position);
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
                                                   /* Firebase ref = new Firebase(StaticInfo.EndPoint + "/friends/" + user.Email + "/" + selectedUser.Email);
                                                    ref.removeValue();
                                                    // delete from local database
                                                    db.deleteFriendByEmailFromLocalDB(selectedUser.Email);
                                                    Toast.makeText(getActivity(), "Contact deleted successfully", Toast.LENGTH_SHORT).show();
                                                    userFriendList = db.getUserDoctorList();
                                                    ListAdapter adp = new FriendListAdapter(getActivity(), userDoctorList);
                                                    ListView lv_FriendList = (ListView) rootView.findViewById(R.id.lv_FriendList);
                                                    lv_FriendList.setAdapter(adp);*/
                                            Toast.makeText(getActivity(), "Feature Coming soon", Toast.LENGTH_SHORT).show();
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
                DoctorListTask doctorListTask = new DoctorListTask();
                doctorListTask.execute();
            }
        });

        return rootView;
    }

    public class DoctorListTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            user = LocalUserService.getLocalUserFromPreferences(getActivity());
            IFireBaseAPI api = Tools.makeRetroFitApi();
            Call<String> call = api.getAllDoctorsAsJsonString();
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
                List<User> friendList = new ArrayList<>();

                if (jsonListString != null) {
                    JSONObject userDoctorTree = new JSONObject(jsonListString);
                    for (Iterator iterator = userDoctorTree.keys(); iterator.hasNext(); ) {
                        String key = (String) iterator.next();
                        User friend = new User();
                        JSONObject friendJson = userDoctorTree.getJSONObject(key);
                        friend.Email = friendJson.getString("Email");
                        friend.FirstName = friendJson.getString("FirstName");
                        friend.LastName = friendJson.getString("LastName");
                        friend.Majlish = friendJson.getString("Majlish");
                        friend.OnDuty = friendJson.getString("OnDuty");
                        friendList.add(friend);
                    }

                    // refresh local database
                    db = new DataContext(getActivity(), null, null, 1);
                    db.deleteAllDoctorsFromLocalDB();
                    db.refreshUserDoctorList(friendList);

                    userDoctorList.clear();

                    userDoctorList = db.getUserDoctorList(doctorType);
                   // userDoctorList = friendList;

                    // set to adapter
                    adapter = new DoctorListAdapter(getActivity(), userDoctorList);
                    lv_DoctorList.setAdapter(adapter);

                    if (pd != null && pd.isShowing()) {
                        pd.dismiss();
                    }
                    //Toast.makeText(getActivity(), "Doctors list is updated", Toast.LENGTH_SHORT).show();
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

}

