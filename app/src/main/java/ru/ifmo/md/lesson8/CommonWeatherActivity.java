package ru.ifmo.md.lesson8;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import ru.ifmo.md.lesson8.provider.WeatherDatabaseHelper;
import ru.ifmo.md.lesson8.provider.WeatherProvider;


public class CommonWeatherActivity extends Activity
        implements LoaderManager.LoaderCallbacks<Cursor>, ActionBar.OnNavigationListener {
    private static String CITY_ID_EXTRA = "city_id";
    private int cityId = -1;

    private ArrayList <City> cities = new ArrayList<City>();
    private ArrayAdapter<String> adapter;
    private ArrayList <String> citiesName = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_weather);
        if (savedInstanceState != null)
            cityId = savedInstanceState.getInt(CITY_ID_EXTRA);
        getLoaderManager().restartLoader(42, null, this);
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        //getActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CITY_ID_EXTRA, cityId);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, WeatherProvider.CITY_CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        int id = -1;
        cities.clear();
        citiesName.clear();
        while (cursor.moveToNext()) {
            City c = WeatherDatabaseHelper.CityCursor.getCity(cursor);
            cities.add(c);
            citiesName.add(c.getName());
        }
        for (int i = 0; i < cities.size(); ++i)
            if (cities.get(i).getId() == cityId)
                id = i;

        if (id == -1)
            for (int i = 0; i < cities.size(); ++i)
                if (cities.get(i).isSelected())
                    id = i;
        adapter = new ArrayAdapter<String>(getActionBar().getThemedContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1, citiesName);
        getActionBar().setListNavigationCallbacks(adapter, this);
        getActionBar().setSelectedNavigationItem(id);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter = null;
    }

    @Override
    public boolean onNavigationItemSelected(int i, long l) {
        Fragment f = getFragmentManager().findFragmentById(R.id.container);
        if (f == null ||
                f.getArguments() == null ||
                f.getArguments().getInt(CommonWeatherFragment.CITY_ID_EXTRA) != cities.get(i).getId()) {
            cityId = cities.get(i).getId();
            Fragment fragment = new CommonWeatherFragment();
            Bundle args = new Bundle();
            args.putInt(CommonWeatherFragment.CITY_ID_EXTRA, cities.get(i).getId());
            args.putString(CommonWeatherFragment.CITY_NAME_EXTRA, cities.get(i).getName());
            fragment.setArguments(args);
            getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        }
        return true;
    }
}