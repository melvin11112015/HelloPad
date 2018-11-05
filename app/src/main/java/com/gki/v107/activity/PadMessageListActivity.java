package com.gki.v107.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gki.managerment.MainMenu;
import com.gki.managerment.R;

import com.gki.managerment.util.ToastUtil;
import com.gki.v107.activity.dummy.DummyContent;
import com.gki.v107.entity.PadMessageInfo;
import com.gki.v107.net.ApiTool;
import com.gki.v107.net.GenericOdataCallback;
import com.gki.v107.tool.DatetimeTool;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of PadMessages. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link PadMessageDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class PadMessageListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_padmessage_list);

        if (findViewById(R.id.padmessage_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.show();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        View recyclerView = findViewById(R.id.padmessage_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
       adapter = new SimpleItemRecyclerViewAdapter(this, mdatas, mTwoPane);
       recyclerView.setAdapter(adapter);

        ApiTool.callPadMessageList(new GenericOdataCallback<PadMessageInfo>() {
            @Override
            public void onDataAvailable(List<PadMessageInfo> datas) {
                mdatas.clear();
                mdatas.addAll(datas);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onDataUnAvailable(String msg, int errorCode) {
                ToastUtil.show(PadMessageListActivity.this,"获取消息失败");
            }
        });
    }

    private List<PadMessageInfo> mdatas = new ArrayList<>();
    private SimpleItemRecyclerViewAdapter adapter;

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final PadMessageListActivity mParentActivity;
        private final List<PadMessageInfo> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PadMessageInfo item = (PadMessageInfo) view.getTag();
                if (true) {
                    Bundle arguments = new Bundle();
                    arguments.putString(PadMessageDetailFragment.ARG_ITEM_ID, item.getMsg());
                    PadMessageDetailFragment fragment = new PadMessageDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.padmessage_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, PadMessageDetailActivity.class);
                    intent.putExtra(PadMessageDetailFragment.ARG_ITEM_ID, item.getMsg());

                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(PadMessageListActivity parent,
                                      List<PadMessageInfo> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.padmessage_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            String title = mValues.get(position).getCreate_User() + " "+mValues.get(position).getProdLineName();
            holder.mIdView.setText(title);
            String datetime=mValues.get(position).getCreate_DateTime();

            datetime = DatetimeTool.convertOdataTimezone(datetime,DatetimeTool.TYPE_DATETIME,DatetimeTool.DEFAULT_ADJUST_TIMEZONE);

            holder.mContentView.setText(datetime);

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;


            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);

            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
