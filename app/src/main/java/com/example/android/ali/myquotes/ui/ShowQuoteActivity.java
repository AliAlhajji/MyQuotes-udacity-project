package com.example.android.ali.myquotes.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.ali.myquotes.R;
import com.example.android.ali.myquotes.model.Book;
import com.example.android.ali.myquotes.model.Quote;
import com.example.android.ali.myquotes.ui.widget.AppWidget;
import com.example.android.ali.myquotes.utils.AppConstants;
import com.example.android.ali.myquotes.utils.DatabaseQuotes;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ShowQuoteActivity extends AppCompatActivity implements DatabaseQuotes.DatabaseQuotesListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private FirebaseAuth mAuth;
    private DatabaseQuotes db;
    private Quote quote;
    private Book book;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_quote);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null || !getIntent().hasExtra(AppConstants.EXTRA_QUOTE) || !getIntent().hasExtra(AppConstants.EXTRA_BOOK)) {
            finish();
        }

        book = getIntent().getParcelableExtra(AppConstants.EXTRA_BOOK);
        quote = getIntent().getParcelableExtra(AppConstants.EXTRA_QUOTE);
        db = new DatabaseQuotes(this, book.getId(), this);

        db.getSingleQuote(quote.getId());
    }

    private void updateUI(){
        toolbar = findViewById(R.id.toolbar);

        if(quote.getPage() != 0){
            toolbar.setSubtitle(getString(R.string.page_number)+ " " + quote.getPage());
        }
        else{
            toolbar.setSubtitle(getString(R.string.page_number)+ " unknown" );
        }

        toolbar.setTitle(book.getTitle());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show_quote, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit:
                Intent intent = new Intent(this, AddQuoteActivity.class);
                intent.putExtra(AppConstants.EXTRA_QUOTE, quote);
                intent.putExtra(AppConstants.EXTRA_BOOK, book);
                startActivity(intent);
                finish();
                break;

            case R.id.delete:
                deleteQuote();
                break;

            case R.id.add_to_widget:
                addToPreferences();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateAdapter(List<Quote> quotes) {

    }

    @Override
    public void showProgressBar(boolean show) {

    }

    @Override
    public void updateQuote(Quote quote) {
        this.quote = quote;
        updateUI();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            switch (position) {
                case 0:
                    fragment = QuoteFragment.newInstance(quote.getText());
                    break;

                case 1:
                    fragment = RemarkFragment.newInstance(quote.getRemark());
                    break;

                default:
                    fragment = PlaceholderFragment.newInstance(0);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {

        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_show_quote, container, false);
            return rootView;
        }
    }

    private void deleteQuote(){
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage(R.string.dialog_confirm_delete)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteQuote(quote.getId());
                        dialog.cancel();
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create();
        alertDialog.show();
    }

    private void addToPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.PREF_WIDGET, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AppConstants.PREF_QUOTE, quote.getText());
        editor.putString(AppConstants.PREF_BOOK, book.getTitle());
        editor.apply();
        AppWidget.updateWidgets(this);
    }
}
