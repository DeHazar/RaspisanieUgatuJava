package hazarsoftware.raspisanieugatu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

public class ContentSetingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_container);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
        fm.beginTransaction()
                    .add(R.id.fragmentContainer, new ContentFragmetOptions())
                    .commit();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}
