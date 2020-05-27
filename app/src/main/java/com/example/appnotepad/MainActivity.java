package com.example.appnotepad;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private ListView listView;

    private ArrayList<String> listDate = new ArrayList<>();
    private static final int MY_REQUEST_CODE = 1000;

    private final List<Note> noteList = new ArrayList<Note>();
    private ArrayAdapter<Note> listViewAdapter;
    private CustomNoteAdapter adapter;

    private MyReceiver myReceiver = new MyReceiver();
    private IntentFilter filter = new IntentFilter();
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Ghi chú");


        //BroadcastReceiver
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);

        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);

        filter.addAction(Intent.ACTION_BATTERY_OKAY);
        filter.addAction(Intent.ACTION_BATTERY_LOW);

        filter.addAction(Intent.ACTION_HEADSET_PLUG);

        MainActivity.this.registerReceiver(myReceiver, filter);

        // Navigation
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = (NavigationView) findViewById(R.id.nested);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        drawer = findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();



        // Service
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.FOREGROUND_SERVICE}, PackageManager.PERMISSION_GRANTED);

        // Get ListView object from xml
        this.listView = (ListView) findViewById(R.id.listView);

        DBNoteHelper db = new DBNoteHelper(this);

        List<Note> list = db.getAllNotes();
        this.noteList.addAll(list);

        // Define a new Adapter
        this.adapter = new CustomNoteAdapter(this, (ArrayList<Note>) noteList);
        this.listView.setAdapter(adapter);

        // Register the ListView for Context menu
        registerForContextMenu(this.listView);

        intent = new Intent(this, AlarmService.class);
        ServiceCaller(intent);



    }

    @Override
    protected void onStart() {
        super.onStart();
        // check Wifi
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(myReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.this.unregisterReceiver(myReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_item_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        searchView.setQueryHint("Search here");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_add_note) {
            Intent intent = new Intent(this, AddEditNoteActivity.class);

            // Start AddEditNoteActivity, (with feedback).
            this.startActivityForResult(intent, MY_REQUEST_CODE);
        } else if (item.getItemId() == R.id.menu_add_folder) {
            Toast.makeText(MainActivity.this, "Click add folder", Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.menu_sort_word) {
            Collections.sort(noteList, new Comparator<Note>() {
                @Override
                public int compare(Note n1, Note n2) {
                    return n1.getNoteTitle().compareTo(n2.getNoteTitle());
                }
            });
            adapter.notifyDataSetChanged();
            ServiceCaller(intent);
            Toast.makeText(MainActivity.this, "Sorted by word", Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.menu_sort_date) {

            adapter.notifyDataSetChanged();
            ServiceCaller(intent);
            Toast.makeText(MainActivity.this, "Sorted by date", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.context_menu, menu);
        super.onCreateContextMenu(menu, view, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        final Note selectedNote = (Note) this.listView.getItemAtPosition(info.position);

        if (item.getItemId() == R.id.menu_item_view) {
            Toast.makeText(getApplicationContext(), selectedNote.getNoteContent(), Toast.LENGTH_LONG).show();
        } else if (item.getItemId() == R.id.menu_item_edit) {
            Intent intent = new Intent(this, AddEditNoteActivity.class);
            intent.putExtra("note", selectedNote);

            // Start AddEditNoteActivity, (with feedback).
            this.startActivityForResult(intent, MY_REQUEST_CODE);
        } else if (item.getItemId() == R.id.menu_item_delete) {

            // Ask before deleting.
            new AlertDialog.Builder(this)
                    .setMessage(selectedNote.getNoteTitle() + ". Are you sure you want to delete?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            deleteNote(selectedNote);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        } else {
            return false;
        }
        return true;
    }

    // Delete a record
    private void deleteNote(Note note) {
        DBNoteHelper db = new DBNoteHelper(this);
        db.deleteNote(note);
        this.noteList.remove(note);
        // Refresh ListView.
        this.adapter.notifyDataSetChanged();
        ServiceCaller(intent);
    }

    // When AddEditNoteActivity completed, it sends feedback.
    // (If you start it using startActivityForResult ())
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == MY_REQUEST_CODE) {
            boolean needRefresh = data.getBooleanExtra("needRefresh", true);
            // Refresh ListView
            if (needRefresh) {
                this.noteList.clear();
                DBNoteHelper db = new DBNoteHelper(this);
                List<Note> list = db.getAllNotes();
                this.noteList.addAll(list);

                // Notify the data change (To refresh the ListView).
                this.adapter.notifyDataSetChanged();
                ServiceCaller(intent);
            }
        }
    }

    private void ServiceCaller(Intent intent) {

        stopService(intent);
        for (int i = 0; i < noteList.size(); i++) {
            listDate.add(noteList.get(i).getNoteDate());
        }
        intent.putExtra("alarm", listDate);

        startService(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_about_us) {
            Intent intent = new Intent(MainActivity.this, AboutUsActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.nav_exit) {
            System.exit(1);
        }
        return false;
    }
}
