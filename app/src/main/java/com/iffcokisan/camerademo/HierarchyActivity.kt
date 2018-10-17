package com.iffcokisan.camerademo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.iffcokisan.camerademo.Adapter.MovieModel
import com.iffcokisan.camerademo.LoadMoreData.ContactAdapter
import com.iffcokisan.camerademo.LoadMoreData.OnLoadMoreListener
import java.util.*

class HierarchyActivity : AppCompatActivity() {

    var mRecyclerView: RecyclerView? = null
    var mLayoutManager: RecyclerView.LayoutManager? = null
    internal var results: MutableList<MovieModel> = ArrayList()

    var mAdapter: ContactAdapter? = null
    var random: Random? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hierarchy)

        random = Random()
        for (i in 0..9) {
            val contact = MovieModel()
            contact.title = "Title IFFCO Kisan $i"
            contact.rating = "iffcokisan$i@gmail.com"
            contact.type = "movie"
            results.add(contact)
        }


        mRecyclerView = findViewById(R.id.rvAll)

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView!!.setHasFixedSize(true)

        // use a linear layout manager
        mLayoutManager = LinearLayoutManager(this)
        mRecyclerView!!.setLayoutManager(mLayoutManager)

        // specify an adapter (see also next example)
        mAdapter = ContactAdapter(mRecyclerView, results, this)
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

        mRecyclerView!!.setAdapter(mAdapter)
    }
}
