package com.wildchild.locationpickermodule.locationpickermodule.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wildchild.locationpickermodule.R;
import com.wildchild.locationpickermodule.locationpickermodule.Adapters.WatchAdapter;
import com.wildchild.locationpickermodule.locationpickermodule.DBSynchronisation.Database.Factory.RetrofitServiceProvider;
import com.wildchild.locationpickermodule.locationpickermodule.DBSynchronisation.Database.Interfaces.BraceletApiService;
import com.wildchild.locationpickermodule.locationpickermodule.DBSynchronisation.Database.Interfaces.CompletionHandler;
import com.wildchild.locationpickermodule.locationpickermodule.DBSynchronisation.Models.Bracelet;

import com.wildchild.locationpickermodule.locationpickermodule.DBSynchronisation.Models.User;
import com.wildchild.locationpickermodule.locationpickermodule.DBSynchronisation.Models.ViewType;
import com.wildchild.locationpickermodule.locationpickermodule.ViewHolders.Interfaces.RecyclerOnItemClickListener;
import com.wildchild.locationpickermodule.locationpickermodule.ViewHolders.Interfaces.RowType;
import com.wildchild.locationpickermodule.locationpickermodule.Utility.MapUtility;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.prefs.Prefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends Activity {

    private static final int ADDRESS_PICKER_REQUEST = 1020;

    List<RowType> bracelets = new ArrayList<>();

    RecyclerOnItemClickListener mItemClickListener = (childView, position) -> {
        System.out.println("Clicked On position " + position);
        Intent intent = new Intent(MainActivity.this, LocationPickerActivity.class);
        intent.putExtra(MapUtility.ADDRESS, "Maranello");
        intent.putExtra(MapUtility.LATITUDE, "44.525551");
        intent.putExtra(MapUtility.LONGITUDE, "10.866320");
        startActivityForResult(intent, ADDRESS_PICKER_REQUEST);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MapUtility.apiKey = getResources().getString(R.string.api_key);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        WatchAdapter adapter = new WatchAdapter(bracelets, mItemClickListener);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fetchWatchs(new CompletionHandler<List<Bracelet>>() {
            @Override
            public void onSuccess(List<Bracelet> response) {
                bracelets = populateData(response);
                adapter.updateData(bracelets);
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });

    }

    private List<RowType> populateData(List<Bracelet> braceletList) {

        List<RowType> rowTypeList = new ArrayList<>();
        boolean tickTock = false;

        for (Bracelet item : braceletList) {
            if (tickTock) {
                item.setItemViewType(ViewType.v1);
                rowTypeList.add(item);
            } else {
                item.setItemViewType(ViewType.v2);
                rowTypeList.add(item);
            }
            tickTock = !tickTock;
        }

        return rowTypeList;
    }

    private void fetchWatchs(CompletionHandler<List<Bracelet>> completionHandler) {


        @NonNull
        String id = "";
        if (User.currentUser != null) {
            id = User.currentUser.getId();
        }else{
            id = Prefs.with(this).read("user_id");
        }

        BraceletApiService apiService = RetrofitServiceProvider.getBraceletApiService();
        apiService.getBracelets(id).enqueue(new Callback<List<Bracelet>>() {
            @Override
            public void onResponse(Call<List<Bracelet>> call, Response<List<Bracelet>> response) {
                System.out.println("Response : " + response);
                if (response.body() != null) {
                    Toast.makeText(getApplicationContext(), "Loaded watches " + response.body().size(),
                            Toast.LENGTH_LONG).show();
                    completionHandler.onSuccess(response.body());
                } else {
                    // handle error or empty
                }
            }

            @Override
            public void onFailure(Call<List<Bracelet>> call, Throwable t) {
                System.out.println("Failure for error  : " + t.getMessage());
                Toast.makeText(getApplicationContext(), "Failure " + t.getMessage(), Toast.LENGTH_LONG)
                        .show();
                completionHandler.onFailure(t);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADDRESS_PICKER_REQUEST) {
            try {
                if (data != null && data.getStringExtra(MapUtility.ADDRESS) != null) {
                    String address = data.getStringExtra(MapUtility.ADDRESS);
                    double currentLatitude = data.getDoubleExtra(MapUtility.LATITUDE, 0.0);
                    double currentLongitude = data.getDoubleExtra(MapUtility.LONGITUDE, 0.0);

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
