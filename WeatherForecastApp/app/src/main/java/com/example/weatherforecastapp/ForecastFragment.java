package com.example.weatherforecastapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.weatherforecastapp.provider.WeatherContract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;





/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForecastFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
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

    private static final int FORECAST_LOADER = 0;
    private ArrayAdapter<String> mForecastAdapter;
    private IFetchWeatherService mService;
    private BroadcastReceiver mBroadcastReceiver;

    {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(FetchWeatherService.ACTION_RETRIEVE_WEATHER_DATA)) {
                    /*String[] data = intent.getStringArrayExtra(FetchWeatherService.EXTRA_WEATHER_DATA);
                    mForecastAdapter.clear();
                    for(String dayForecastStr : data) {
                        mForecastAdapter.add(dayForecastStr);
                    }*/
                }
            }
        };
    }


    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherColumns._ID,
            WeatherContract.WeatherColumns.COLUMN_DATE,
            WeatherContract.WeatherColumns.COLUMN_SHORT_DESC,
            WeatherContract.WeatherColumns.COLUMN_MAX_TEMP,
            WeatherContract.WeatherColumns.COLUMN_MIN_TEMP,
    };


    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IFetchWeatherService.Stub.asInterface(service);

            try {
                mService.registerFetchDataListener(mFetchDataListener);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };


    private IFetchDataListener.Stub mFetchDataListener = new IFetchDataListener.Stub() {
        @Override
        public void onWeatherDataRetrieved(String[] data) throws RemoteException {
            mForecastAdapter.clear();
            for (String dayForecastStr : data) {
                mForecastAdapter.add(dayForecastStr);
            }
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    private void refreshWeatherData() {


        if (mService != null) {
            try {
                mService.retrieveWeatherData();
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refresh, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        setHasOptionsMenu(true);
        getActivity().bindService(new Intent(getActivity(), FetchWeatherService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    public void onDestroy() {
        if (mService != null) {
            try {
                mService.unregisterFetchDataListener(mFetchDataListener);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }
        getActivity().unbindService(mServiceConnection);
        super.onDestroy();
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
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long L) {
                if (position == 0) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra("data", data);
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
        /*IntentFilter intentFilter = new IntentFilter(FetchWeatherService.ACTION_RETRIEVE_WEATHER_DATA);
        getActivity().registerReceiver(mBroadcastReceiver, intentFilter);*/
        return view;
    }


    @Override
    public void onDestroyView() {
        //getActivity().unregisterReceiver(mBroadcastReceiver);
        super.onDestroyView();
    }


    private class WeatherForecastAdapter extends CursorAdapter {
        public class ViewHolder {
            public final TextView mTextView;

            public ViewHolder(View view) {
                mTextView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            }
        }

        public WeatherForecastAdapter(Context context, Cursor cursor, int flags) {
            super(context, cursor, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.fragment_forecast, parent, false);

            ViewHolder viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);

            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();

            StringBuilder sb = new StringBuilder();
            long dateInMillis = cursor.getLong(COL_WEATHER_DATE);
            sb.append(getReadableDateString(dateInMillis));

            String description = cursor.getString(COL_WEATHER_DESC);
            sb.append(" - " + description);

            double high = cursor.getDouble(COL_WEATHER_MAX_TEMP);

            double low = cursor.getDouble(COL_WEATHER_MIN_TEMP);

            String highAndLow = formatHighLows(high, low);
            sb.append(" - " + highAndLow);

            holder.mTextView.setText(sb.toString());
        }
    }

    private String getReadableDateString(long time) {
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }

    private String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, filter the query to return weather only for
        // dates after or including today.

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherColumns.COLUMN_DATE + " ASC";

        long now = System.currentTimeMillis();
        long normalizedDate = normalizeDate(now);

        return new CursorLoader(getActivity(),
                WeatherContract.WeatherColumns.CONTENT_URI,
                FORECAST_COLUMNS,
                WeatherContract.WeatherColumns.COLUMN_DATE + " >= ?",
                new String[]{Long.toString(now)},
                sortOrder);
    }

        @Override
        public void onLoadFinished (Loader < Cursor > loader, Cursor data){
            // Swap the new cursor in.  (The framework will take care of closing the

            // old cursor once we return.)

            //mForecastAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset (Loader < Cursor > loader) {
            // This is called when the last Cursor provided to onLoadFinished()

            // above is about to be closed.  We need to make sure we are no

            // longer using it.

            //mForecastAdapter.swapCursor(null);
        }



    }
