package com.example.android.ali.myquotes.ui;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.ali.myquotes.R;
import com.example.android.ali.myquotes.adapters.BooksAdapter;
import com.example.android.ali.myquotes.model.Book;
import com.example.android.ali.myquotes.utils.AppConstants;
import com.example.android.ali.myquotes.utils.DatabaseBooks;
import com.example.android.ali.myquotes.utils.NetworkUtils;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BooksAdapter.OnClick, DatabaseBooks.DatabaseBooksListener {
    private DatabaseBooks db;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private static int RC_SIGN_IN = 123;

    private RecyclerView mRecyclerView;
    private BooksAdapter mAdapter;
    private GridLayoutManager mLayoutManager;
    private FloatingActionButton mAddBookFAB;
    private ProgressBar progressBar;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        adView = findViewById(R.id.adView);
        MobileAds.initialize(this,"ca-app-pub-3940256099942544~3347511713");
        AdRequest adRequest = new AdRequest.Builder().build();

        adView.loadAd(adRequest);


        //If no user logged in, the app will open the login page automatically
        if(mAuth.getCurrentUser() == null){
            updateUI(null);
            createSigninIntent();
        }else {
            mUser = mAuth.getCurrentUser();
            db = new DatabaseBooks(this, mUser.getUid(), this);
            updateUI(mUser);
        }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                mUser = mAuth.getCurrentUser();
                updateUI(mUser);
            }
            else if(resultCode == ErrorCodes.NO_NETWORK){
                Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Unknown error!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out:
                signout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        db.getUserBooks();
    }

    private void signout(){
        mAuth.signOut();
        updateUI(null);
    }

    @Override
    public void updateAdapter(List<Book> books) {
        this.mAdapter.setData(books);
    }

    @Override
    public void showProgressBar(boolean show) {
        if(show){
            progressBar.setVisibility(View.VISIBLE);
            }
        else{
            progressBar.setVisibility(View.GONE);
        }
    }

    private void updateUI(FirebaseUser user){
        if(user == null){
            mAdapter.setData(null);
            createSigninIntent();
            return;
        }
        else{
            progressBar = findViewById(R.id.progress_bar);
            mAdapter = new BooksAdapter(this, this);
            db.getUserBooks();
            mRecyclerView = findViewById(R.id.rv_books_list);
            mLayoutManager = new GridLayoutManager(this, 2);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAddBookFAB = findViewById(R.id.fab_add_book);

            mAddBookFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, AddBookActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onClickListener(int i) {
        Intent intent = new Intent(this, QuotesListActivity.class);
        Book book = mAdapter.getData().get(i);
        intent.putExtra(AppConstants.EXTRA_BOOK, book);
        startActivity(intent);
    }

}
