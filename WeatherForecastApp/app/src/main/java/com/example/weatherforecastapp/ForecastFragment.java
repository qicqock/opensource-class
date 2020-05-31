package com.example.weatherforecastapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForecastFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForecastFragment extends Fragment {
    static String data;
    private ListView listview;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ForecastFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ForecastFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ForecastFragment newInstance(String param1, String param2) {
        ForecastFragment fragment = new ForecastFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private ArrayAdapter<String> mForecastAdapter;
    private BroadcastReceiver mBroadcastReceiver;

    {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(FetchWeatherService.ACTION_RETRIEVE_WEATHER_DATA)) {
                    String[] data = intent.getStringArrayExtra(FetchWeatherService.EXTRA_WEATHER_DATA);
                    mForecastAdapter.clear();
                    for(String dayForecastStr : data) {
                        mForecastAdapter.add(dayForecastStr);
                    }
                }
            }
        };
    }


    private void refreshWeatherData() {
        Intent intent = new Intent(getActivity(), FetchWeatherService.class);
        intent.setAction(FetchWeatherService.ACTION_RETRIEVE_WEATHER_DATA);
        getActivity().startService(intent);
    }





    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refresh,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            Toast.makeText(getActivity(), "refresh complete", Toast.LENGTH_SHORT).show();
            refreshWeatherData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }








    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = getLayoutInflater().inflate(R.layout.fragment_forecast, container, false);
        ArrayList<String> info = new ArrayList<>();
        info.add("Seoul");
        info.add("Daejeon");
        info.add("Taegu");
        info.add("Busan");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, info);
        ListView listview = (ListView) view.findViewById(R.id.listview);
        listview.setAdapter(adapter);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent(getActivity(),DetailActivity.class);
                    intent.putExtra("data",data);
                    startActivity(intent);
                } else if (position == 1) {
                    Toast.makeText(getActivity(), "clicked position 1", Toast.LENGTH_SHORT).show();
                } else if (position == 2) {
                    Toast.makeText(getActivity(), "clicked position 2", Toast.LENGTH_SHORT).show();
                } else if (position == 3) {
                    Toast.makeText(getActivity(), "clicked position 3", Toast.LENGTH_SHORT).show();
                }
            }
        });
        IntentFilter intentFilter = new IntentFilter(FetchWeatherService.ACTION_RETRIEVE_WEATHER_DATA);
        getActivity().registerReceiver(mBroadcastReceiver, intentFilter);
        return view;
}
    @Override
    public void onDestroyView() {
        getActivity().unregisterReceiver(mBroadcastReceiver);
        super.onDestroyView();
    }


    }
