package uk.co.impactnottingham.benh.impact;

import android.support.v4.app.FragmentManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import uk.co.impactnottingham.benh.viewholders.FeaturedHeadlineHolder;
import uk.co.impactnottingham.benh.viewholders.HeadlineHolder;
import uk.co.impactnottingham.benh.viewholders.LandscapeHeadlineHolder;
import uk.co.impactnottingham.benh.wordpress.Article;

import java.util.List;
import java.util.Random;

/**
 * Created by benh14 on 12/22/17.
 */
public class HeadlineAdapter extends RecyclerView.Adapter<HeadlineHolder> {

    private static final String TAG = "HeadlineAdapter";
    private static final int    CARD_LAYOUT           = R.layout.headline_list_row;
    public static final int     HOLDER_TYPE_FEATURED  = 0x01;
    public static final int     HOLDER_TYPE_LANDSCAPE = 0x02;
    public static final int FEATURED_RATIO = 4;

    private final List<Article>   mArticles;
    private final FragmentManager mFragmentManager;

    private final Random mRandom;

    public HeadlineAdapter(@NonNull List<Article> initialDataSet, FragmentManager fragmentManager) {
        mArticles = initialDataSet;
        mFragmentManager = fragmentManager;
        mRandom = new Random();
    }

    public void add(@NonNull Article article) {
        Log.d(TAG, "add: Adding article to recycler");
        mArticles.add(article);
        notifyDataSetChanged();
    }

    public void clear() {
        mArticles.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HeadlineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View v = inflater.inflate(CARD_LAYOUT, parent, false);

        HeadlineHolder holder = null;

        switch (viewType) {
            case HOLDER_TYPE_LANDSCAPE:
                holder = new LandscapeHeadlineHolder(v, parent.getContext(), mFragmentManager);
                break;
            case HOLDER_TYPE_FEATURED:
                holder = new FeaturedHeadlineHolder(v, parent.getContext(), mFragmentManager);
                break;
        }


        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HeadlineHolder holder, int position) {
        holder.setArticle(mArticles.get(position));
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    @Override
    public int getItemViewType(int position) {
        Article article = mArticles.get(position);
        if (article.isBreaking() || article.hashCode() % FEATURED_RATIO == 0) {  //Make breaking articles featured as well as about 1 in 10 other articles
            return HOLDER_TYPE_FEATURED;
        } else {
            return HOLDER_TYPE_LANDSCAPE;
        }
    }
}
