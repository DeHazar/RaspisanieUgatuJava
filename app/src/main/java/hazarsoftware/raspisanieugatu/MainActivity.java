package hazarsoftware.raspisanieugatu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import hazarsoftware.raspisanieugatu.NotesPart.data.NotesDataBaseHelper;
import hazarsoftware.raspisanieugatu.NotesPart.notes.NotesActivity;


public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    public static SQLiteDatabase noteDatabaseHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        noteDatabaseHelper = new NotesDataBaseHelper(getApplicationContext()).getWritableDatabase();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        Schedule schedule = Schedule.getSchedule(getApplicationContext());
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        FragmentManager fm = getSupportFragmentManager();

        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
        boolean firstOnApp = myPreferences.getBoolean("firstOnApp", true);
        if (firstOnApp) {

            myPreferences.edit().putBoolean("firstOnApp", false).apply();
            Intent intent = new Intent(getApplicationContext(), ContentSetingActivity.class);
            startActivity(intent);
        } else {
            Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
            if (fragment == null) {
                fragment = new DayOfScheduleFragment();
                fm.beginTransaction()
                        .replace(R.id.fragmentContainer, fragment)
                        .commit();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Open the navigation drawer when the home icon is selected from the toolbar.
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.settings:
                            startActivity(new Intent(getApplicationContext(), ContentSetingActivity.class));
                            break;
                        case R.id.Notes:
                            startActivity(new Intent(getApplicationContext(), NotesActivity.class));
                            break;
                        case R.id.loadAll:

                            break;
                        default:
                            break;
                    }
                    // Close the navigation drawer when an item is selected.
                    mDrawerLayout.closeDrawers();
                    return true;
                });
    }
}
