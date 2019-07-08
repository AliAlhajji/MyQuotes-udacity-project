package com.example.android.ali.myquotes.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.ali.myquotes.R;
import com.example.android.ali.myquotes.adapters.QuotesAdapter;
import com.example.android.ali.myquotes.model.Book;
import com.example.android.ali.myquotes.model.Quote;
import com.example.android.ali.myquotes.utils.AppConstants;
import com.example.android.ali.myquotes.utils.DatabaseBooks;
import com.example.android.ali.myquotes.utils.DatabaseQuotes;
import com.example.android.ali.myquotes.utils.NetworkUtils;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class QuotesListActivity extends AppCompatActivity implements QuotesAdapter.QuoteClickListener, DatabaseQuotes.DatabaseQuotesListener {

    private FirebaseAuth mAuth;
    private static int RC_SIGN_IN = 123;
    private DatabaseQuotes db;
    private Book book;
    private RecyclerView mRecyclerView;
    private QuotesAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private FloatingActionButton mAddQuoteFAB;
    private ProgressBar progressBar;

    @Override
    public void updateQuote(Quote quote) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotes_list);

        if(!getIntent().hasExtra(AppConstants.EXTRA_BOOK)){
            finish();
        }

        mAuth = FirebaseAuth.getInstance();
        book = getIntent().getParcelableExtra(AppConstants.EXTRA_BOOK);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        updateUI(mAuth.getCurrentUser());
    }

    @Override
    public void updateAdapter(List<Quote> quotes) {
        mAdapter.setData(quotes);
    }

    @Override
    public void showProgressBar(boolean show) {
        if(show){
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show_book, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit:
                Intent intent = new Intent(this, AddBookActivity.class);
                intent.putExtra(AppConstants.EXTRA_BOOK, this.book);
                startActivity(intent);
                finish();
                break;

            case R.id.delete:
                deleteBook();
        }
        return super.onOptionsItemSelected(item);
    }

    private void createSigninIntent(){
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
        );
        if(NetworkUtils.isConnected(this)) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setIsSmartLockEnabled(false)
                            .build(), RC_SIGN_IN
            );
        }else{
            Toast.makeText(this, getString(R.string.no_network), Toast.LENGTH_SHORT).show();
        }
    }

    private void signout(){
        mAuth.signOut();
        updateUI(null);
    }

    private void deleteBook(){
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage(R.string.dialog_confirm_delete)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseBooks databaseBooks = new DatabaseBooks(getApplicationContext(), mAuth.getCurrentUser().getUid());
                        databaseBooks.deleteBook(book.getId());
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

    @Override
    public void onQuoteClickListener(int i) {
        Intent intent = new Intent(QuotesListActivity.this, ShowQuoteActivity.class);
        Quote quote = mAdapter.getData().get(i);
        intent.putExtra(AppConstants.EXTRA_BOOK, book);
        Log.i("BOOKID", book.getId());
        intent.putExtra(AppConstants.EXTRA_QUOTE, quote);
        startActivity(intent);
    }

    private void updateUI(FirebaseUser user){
        if(user == null){
            createSigninIntent();
        }
        else{
            progressBar = findViewById(R.id.progress_bar);
            mAdapter = new QuotesAdapter(this, this);
            db = new DatabaseQuotes(this, book.getId(), this);
            db.getBookQuotes();
            mRecyclerView = findViewById(R.id.rv_quotes_list);
            mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAddQuoteFAB = findViewById(R.id.fab_add_quote);
            getSupportActionBar().setTitle(book.getTitle());
            getSupportActionBar().setSubtitle(book.getAuthor());

            mAddQuoteFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(QuotesListActivity.this, AddQuoteActivity.class);
                    intent.putExtra(AppConstants.EXTRA_BOOK, book);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        db.getBookQuotes();
    }
}
