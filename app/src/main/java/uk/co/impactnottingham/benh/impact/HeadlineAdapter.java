package uk.co.impactnottingham.benh.impact;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by benh14 on 12/22/17.
 */
public class HeadlineAdapter extends ArrayAdapter<Headline> {

    public HeadlineAdapter(Context c, List<Headline> items) {
        super(c, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
