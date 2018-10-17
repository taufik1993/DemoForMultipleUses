package com.iffcokisan.camerademo;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.iffcokisan.camerademo.Adapter.MovieModel;
import com.iffcokisan.camerademo.LoadMoreData.ContactAdapter;
import com.iffcokisan.camerademo.LoadMoreData.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RecycelrviewLoadMoreActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
//    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    List<MovieModel> results=new ArrayList<>();


    private ContactAdapter mAdapter;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycelrview_load_more);
        random = new Random();
        for (int i = 0; i < 10; i++) {
            MovieModel contact = new MovieModel();
            contact.setTitle("Title IFFCO Kisan "+i);
            contact.setRating("iffcokisan" + i + "@gmail.com");
            contact.setType("movie");
            results.add(contact);
        }


        mRecyclerView = (RecyclerView) findViewById(R.id.rvAll);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new ContactAdapter(mRecyclerView,results,this);
//        mAdapter.setLoadMoreListener(new MyAdapter.OnLoadMoreListener() {
//            @Override
//            public void onLoadMore() {
//
//                mRecyclerView.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        int index = results.size() - 1;
//                        loadMore(index);// a method which requests remote data
//                    }
//                });
//                //Calling loadMore function in Runnable to fix the
//                // java.lang.IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling error
//            }
//        });

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                results.add(null);
                mAdapter.notifyItemInserted(results.size() - 1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        results.remove(results.size() - 1);
                        mAdapter.notifyItemRemoved(results.size());

                        //Generating more data
                        int index = results.size();
                        int end = index + 10;
                        for (int i = index; i < end; i++) {
                            MovieModel contact = new MovieModel();
                            contact.setTitle("Title IFFCO Kisan "+i);
                            contact.setRating("iffcokisan" + i + "@gmail.com");
                            contact.setType("movie");
                            results.add(contact);
                        }
                        mAdapter.notifyDataSetChanged();
                        mAdapter.setLoaded();
                    }
                }, 5000);
//                if (results.size() <= 20) {
//
//                } else {
//                    Toast.makeText(RecycelrviewLoadMoreActivity.this, "Loading data completed", Toast.LENGTH_SHORT).show();
//                }
            }
        });
    }

//    private void loadMore(int index){
//
//        //add loading progress view
//        results.add(new MovieModel("load"));
//        mAdapter.notifyItemInserted(results.size()-1);
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                results.remove(results.size() - 1);
//                mAdapter.notifyItemRemoved(results.size());
//
//                //Generating more data
//                int index = results.size();
//                int end = index + 10;
//                for (int i = index; i < end; i++) {
//                    MovieModel contact = new MovieModel();
//                    contact.setTitle("Title IFFCO Kisan");
//                    contact.setRating("iffcokisan" + i + "@gmail.com");
//                    contact.setType("movie");
//                    results.add(contact);
//                }
//                mAdapter.notifyDataSetChanged();
//                mAdapter.setMoreDataAvailable(true);
//            }
//        }, 5000);
//    }

}
