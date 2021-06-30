package com.example.whatsapp.Activitiy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.whatsapp.Configuration.ConfigurationFirebase;
import com.example.whatsapp.Fragment.ContactsFragment;
import com.example.whatsapp.Fragment.ConversationsFragment;
import com.example.whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class WelcomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseAuth auth;
    private MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        auth = ConfigurationFirebase.getAuth();
        searchView = findViewById(R.id.searchWelcome);
        toolbar = findViewById(R.id.toolbarWelcome);

        toolbar.setTitle("Welcome");
        setSupportActionBar(toolbar);

        //Settings tabs:
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                .add(R.string.titleA, ConversationsFragment.class)
                .add(R.string.titleB, ContactsFragment.class)
                .create());
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager(viewPager);

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                ConversationsFragment fragment = (ConversationsFragment) adapter.getPage(0);
                fragment.reloadChats();
            }
        });

        //Listener text box:
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                //Check if the search button is search in Chats or in Contacts:
                switch (viewPager.getCurrentItem()){
                    case 0:
                        ConversationsFragment fragment = (ConversationsFragment) adapter.getPage(0);
                        if (newText!=null && !newText.isEmpty()){
                            fragment.searchChats(newText.toLowerCase());
                        }else {
                            fragment.reloadChats();
                        }
                        break;
                    case 1:
                        ContactsFragment contactsFragment = (ContactsFragment) adapter.getPage(1);
                        if (newText!=null && !newText.isEmpty()){
                            contactsFragment.searchContacts(newText.toLowerCase());
                        }else {
                            contactsFragment.reloadContacts();
                        }
                        break;
                }

                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_welcome,menu);
        //Configure search button:
        MenuItem item = menu.findItem(R.id.menuSearch);
        searchView.setMenuItem(item);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuOut:
                logOutUser();
                finish();
                break;
            case R.id.menuSearch:
                break;
            case R.id.menuSettings:
                openSettings();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    public void logOutUser(){
        try {
            auth.signOut();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(WelcomeActivity.this,
                    "Error: "+e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
    public void openSettings(){
        Intent intent = new Intent(WelcomeActivity.this,
                SettingsProfileActivity.class);
        startActivity(intent);
    }
}
