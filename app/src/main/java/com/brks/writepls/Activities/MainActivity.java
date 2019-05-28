package com.brks.writepls.Activities;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import com.brks.writepls.Note.NotesFragment;
import com.brks.writepls.R;
import com.brks.writepls.Reminder.RemindersFragment;
import com.brks.writepls.ShoppingList.ShoppingListFragment;
import com.brks.writepls.ToDoList.ToDoListFragment;

public class MainActivity extends AppCompatActivity  {

    final NotesFragment notesFragment = new NotesFragment();
    final RemindersFragment remindersFragment = new RemindersFragment();
    final ToDoListFragment toDoListFragment = new ToDoListFragment();
    final ShoppingListFragment shoppingListFragment = new ShoppingListFragment();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NotesFragment()).commit();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = notesFragment;


                switch (menuItem.getItemId()) {
                    case R.id.action_notes:
                        selectedFragment = notesFragment;

                        break;
                    case R.id.action_reminders:
                        selectedFragment = remindersFragment;
                        break;

                    case R.id.action_to_do_list :
                        selectedFragment = toDoListFragment;
                        break;

                    case R.id.action_to_buy_list:
                        selectedFragment = shoppingListFragment;
                        break;

                }

                assert selectedFragment != null;
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit();
                return true;
            }
        });

    }
}
