package uk.co.impactnottingham.benh.impact;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import uk.co.impactnottingham.benh.glide.GlideApp;
import uk.co.impactnottingham.benh.wordpress.Article;
import uk.co.impactnottingham.benh.wordpress.GetImageLinkTask;
import uk.co.impactnottingham.benh.wordpress.WordpressREST;

import java.net.URL;
import java.util.List;

/**
 * Created by benh14 on 12/22/17.
 */
public class HeadlineAdapter extends RecyclerView.Adapter<HeadlineHolder> {

    private static final String TAG         = "HeadlineAdapter";
    private static final int    CARD_LAYOUT = R.layout.headline_list_row;

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


    static int x = 0;

    @NonNull
    @Override
    public HeadlineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View v = inflater.inflate(CARD_LAYOUT, parent, false);

        // return the inflated recycler
        x++;
        HeadlineHolder h = (x % 3 == 0) ? new HeadlineHolder.FeaturedHeadlineHolder(v, parent.getContext()) : new HeadlineHolder.LandscapeHeadlineHolder(v, parent.getContext());
        h.setBreaking(true);
        return h;
    }

    @Override
    public void onBindViewHolder(@NonNull HeadlineHolder holder, int position) {
        holder.setArticle(mArticles.get(position));
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

}
