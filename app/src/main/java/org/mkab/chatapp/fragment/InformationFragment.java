package org.mkab.chatapp.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mkab.chatapp.R;
import org.mkab.chatapp.activity.FileViewActivity;
import org.mkab.chatapp.adapter.GridViewAdapter;
import org.mkab.chatapp.model.ImageItem;
import org.mkab.chatapp.model.Information;
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

public class InformationFragment extends Fragment {

    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private ProgressDialog pd;
    private DataContext db;
    User user;
    boolean apiCall = false;
    static final String[] categories = new String[]{"Prescriptions", "Hujurs Instructions","General Information",
                                                    "FAQ", "PPE","Home Quarantine", "Isolation", "Corona Info"};
    SwipeRefreshLayout swipeRefreshLayout;
    List<Information> informationList;

    public InformationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_information, container, false);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshInfo);

        informationList = new ArrayList<>();

        user = LocalUserService.getLocalUserFromPreferences(getActivity());
        db = new DataContext(getActivity(), null, null, 1);
        pd = new ProgressDialog(getActivity());
        pd.setCancelable(false);
        pd.setMessage("Loading...");

        setGridView(rootView);

        if (!apiCall && Tools.isNetworkAvailable(getActivity())) {
            apiCall = true;
            InformationListTask informationListTask = new InformationListTask();
            informationListTask.execute();
        } else {
            Toast.makeText(getActivity(), "No internet Connection", Toast.LENGTH_SHORT).show();
        }


      /*  gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String speciality = categories[position];
                Toast.makeText(getActivity(), speciality, Toast.LENGTH_SHORT).show();
                getGridItemDetails(speciality);
            }
        });*/

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                InformationListTask informationListTask = new InformationListTask();
                informationListTask.execute();
            }
        });

        return rootView;
    }

    private void setGridView(View v) {
        gridView = v.findViewById(R.id.gridViewDoctorCategory);
        gridAdapter = new GridViewAdapter(getActivity(), R.layout.categories_grid_item, informationList);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
               /* Intent doctorsList = new Intent(getActivity().getApplicationContext(), DoctorsList.class);
                startActivity(doctorsList);*/

                Intent intent = new Intent(getActivity(), FileViewActivity.class);
               /* intent.putExtra("Title", parkingList.get(position).title);
                intent.putExtra("Type", parkingList.get(position).type);
                intent.putExtra("FileLink", parkingList.get(position).fileName);*/
                intent.putExtra("Title", informationList.get(position).getTitle());
                intent.putExtra("Type", informationList.get(position).getType());
                intent.putExtra("FileLink", informationList.get(position).getFileName());
                startActivity(intent);
            }
        });
    }
    private ArrayList<ImageItem> getData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        TypedArray imgs = getResources().obtainTypedArray(R.array.image_ids);
        for (int i = 0; i < imgs.length(); i++) {
           // Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgs.getResourceId(i, -1));
            imageItems.add(new ImageItem( categories[i]));
        }
        return imageItems;
    }

    private void getGridItemDetails(final String specialty) {

    }

    public class InformationListTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            user = LocalUserService.getLocalUserFromPreferences(getActivity());
            IFireBaseAPI api = Tools.makeRetroFitApi();
            Call<String> call = api.getAllInformationAsJsonString();
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
                informationList.clear();

                if (jsonListString != null) {
                    LogUtil.printInfoMessage(InformationFragment.class.getSimpleName(), "Response", jsonListString);

                    /*JSONArray jsonarray = new JSONArray(jsonListString);

                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        String title = jsonobject.getString("title");
                        String type = jsonobject.getString("type");
                        String fileName = jsonobject.getString("fileName");

                        Information information = new Information(title, type, fileName);
                        informationList.add(information);
                    }*/

                    JSONObject userDoctorTree = new JSONObject(jsonListString);

                    for (Iterator iterator = userDoctorTree.keys(); iterator.hasNext(); ) {
                        String key = (String) iterator.next();

                        JSONObject friendJson = userDoctorTree.getJSONObject(key);

                        Information information = new Information(friendJson.getString("title"),
                                friendJson.getString("type"),
                                friendJson.getString("fileName"));
                        informationList.add(information);
                    }
                    // set to adapter
                    gridAdapter = new GridViewAdapter(getActivity(), R.layout.categories_grid_item, informationList);
                    gridView.setAdapter(gridAdapter);

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

}