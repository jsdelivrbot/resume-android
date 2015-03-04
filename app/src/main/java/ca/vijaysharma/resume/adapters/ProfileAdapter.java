package ca.vijaysharma.resume.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.vijaysharma.resume.R;
import ca.vijaysharma.resume.utils.Action1;

public class ProfileAdapter extends PagerAdapter implements Action1<Object> {
    private final Context context;
    private final LayoutInflater inflater;

    public ProfileAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
//        View view = new TextButtonBuilder<>(this.context, null)
//            .setText("Profile")
//            .setConnectorColor(this.context.getResources().getColor(R.color.white))
//            .setBackgroundDrawable(this.context.getDrawable(R.drawable.white_circle))
//            .setAddConnection(position != 0)
//            .setListener(this)
//            .build();

        View view = new ImageButtonBuilder<>(this.context, null)
            .setConnectorColor(this.context.getResources().getColor(R.color.white))
            .setBackgroundDrawable(this.context.getDrawable(R.drawable.white_circle))
            .setAddConnection(position != 0)
            .setListener(this)
            .build();

        collection.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public void call(Object item) {
        Log.i("ProfileAdapter", "Object: " + item);
    }
}