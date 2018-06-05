package uk.co.impactnottingham.benh.impact;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import uk.co.impactnottingham.benh.wordpress.Article;

import java.util.List;

/**
 * Created by benh14 on 12/22/17.
 */
public class HeadlineAdapter extends RecyclerView.Adapter<HeadlineHolder> {

    private static final String TAG                   = "HeadlineAdapter";
    private static final int    CARD_LAYOUT           = R.layout.headline_list_row;
    public static final int     HOLDER_TYPE_FEATURED  = 0x01;
    public static final int     HOLDER_TYPE_LANDSCAPE = 0x02;

    private final List<Article> mArticles;

    public HeadlineAdapter(@NonNull List<Article> initialDataSet) {
        mArticles = initialDataSet;
    }

    public void add(@NonNull Article article) {
        Log.d(TAG, "add: Adding article to recycler");
        mArticles.add(article);
        notifyDataSetChanged();
    }

    public void clear() {
        mArticles.clear();
    }

    @NonNull
    @Override
    public HeadlineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View v = inflater.inflate(CARD_LAYOUT, parent, false);

        HeadlineHolder holder = null;

        switch (viewType) {
            case HOLDER_TYPE_LANDSCAPE:
                holder = new HeadlineHolder.LandscapeHeadlineHolder(v, parent.getContext());
                break;
            case HOLDER_TYPE_FEATURED:
                holder = new HeadlineHolder.FeaturedHeadlineHolder(v, parent.getContext());
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
        if (position % 3 == 0) {
            return HOLDER_TYPE_FEATURED;
        } else {
            return HOLDER_TYPE_LANDSCAPE;
        }
    }
}
