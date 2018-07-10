package cc.sdkutil.model.inject;

import android.support.v4.view.ViewPager;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.RadioGroup;
import android.widget.SlidingDrawer;

import cc.sdkutil.view.CCPagerSlidingTabStrip;
import cc.sdkutil.view.CCPagerSlidingTabStripCompat;
import cc.sdkutil.view.CCViewPagerCompat;

/**
 * Created by wangcong on 15-5-22.
 * 继承常用android控件监听，用于动态处理这些监听 @see {@link java.lang.reflect.Proxy}
 */
public interface CCInjectEventListener extends View.OnClickListener, View.OnLongClickListener
    , AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemSelectedListener
    , CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener, View.OnTouchListener
    , CCViewPagerCompat.OnPageChangeListener, ViewPager.OnPageChangeListener, CCPagerSlidingTabStrip.OnTabSelectedListener
    , CCPagerSlidingTabStripCompat.OnTabSelectedListener, AbsListView.OnScrollListener, SlidingDrawer.OnDrawerOpenListener
    , SlidingDrawer.OnDrawerCloseListener, View.OnCreateContextMenuListener, ExpandableListView.OnChildClickListener
    , ExpandableListView.OnGroupClickListener, ExpandableListView.OnGroupCollapseListener, ExpandableListView.OnGroupExpandListener
    , Chronometer.OnChronometerTickListener, View.OnDragListener, View.OnFocusChangeListener, TextWatcher {
}
