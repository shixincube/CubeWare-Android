package cube.ware.ui.contact.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import cube.ware.R;

/**
 * @author YangFan
 * @date 2017/2/9
 */

public class ContactPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragments;
    private Context        mContext;

    public ContactPagerAdapter(List<Fragment> fragments, FragmentManager fm, Context context) {
        super(fm);
        this.mFragments = fragments;
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        return this.mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
    @Override
    public CharSequence getPageTitle(int position) {
        String title;
        switch (position) {
            case 0:
                title = mContext.getString(R.string.contact_friend);
                break;
            case 1:
                title = mContext.getString(R.string.contact_group);
                break;
            default:
                title = mContext.getString(R.string.contact_friend);
                break;
        }
        return title;
    }
}
