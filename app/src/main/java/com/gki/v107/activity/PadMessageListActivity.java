package com.gki.v107.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.gki.managerment.LoginUser;
import com.gki.managerment.R;
import com.gki.managerment.global.BaseListItem;
import com.gki.managerment.global.BaseListItemParser;
import com.gki.managerment.global.ListItemAdapter;
import com.gki.managerment.http.Service.getService;
import com.gki.managerment.util.ToastUtil;
import com.gki.v107.entity.PadMessageInfo;
import com.gki.v107.net.ApiTool;
import com.gki.v107.net.GenericOdataCallback;
import com.gki.v107.tool.DatetimeTool;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    private RadioButton radioButtonDay;
    private TextView tvProdDate;
    private FrameLayout container;
    private Button buttonNew;
    private Spinner spProdLine;
    private String currentProdline, currentProdlineName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //去除title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去掉Activity上面的状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_padmessage_list);

        //隐藏actionbar
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        if (findViewById(R.id.padmessage_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        tvProdDate = (TextView) findViewById(R.id.tv2_message_date);
        tvProdDate.setText(DatetimeTool.getCurrentOdataDate());
        tvProdDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(PadMessageListActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                SimpleDateFormat sdfDat = new SimpleDateFormat("yyyy-MM-dd");
                                Calendar c = Calendar.getInstance();
                                c.set(year, monthOfYear, dayOfMonth);
                                tvProdDate.setText(sdfDat.format(c.getTime()));
                                checkMessageList();
                            }
                        },
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        buttonNew = (Button) findViewById(R.id.button2_message_new);
        buttonNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PadMessageListActivity.this, BuildMessageActivity.class);
                intent.putExtra("shift", radioButtonDay.isChecked());
                intent.putExtra("datetime", tvProdDate.getText().toString().trim());
                intent.putExtra("prodline", currentProdline);
                intent.putExtra("prodlinename", currentProdlineName);
                startActivity(intent);
            }
        });
        findViewById(R.id.button2_message_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.show();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        View recyclerView = findViewById(R.id.padmessage_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        radioButtonDay = (RadioButton) findViewById(R.id.radiobutton2_message_day);
        radioButtonDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkMessageList();
            }
        });

        container = (FrameLayout) findViewById(R.id.padmessage_detail_container);

        spProdLine = (Spinner) findViewById(R.id.spinner2_message);
        currentProdline = LoginUser.getUser().Prod_Line;
        currentProdlineName = LoginUser.getUser().ProdLineName;
        new GetUserProdLine().execute(LoginUser.getUser().User_ID);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter = new SimpleItemRecyclerViewAdapter(this, mdatas, mTwoPane);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkMessageList();
    }

    private void checkMessageList() {

        String filterSb = "shift eq '" +
                (radioButtonDay.isChecked() ? "Day" : "Night") +
                "' and ProdDate eq DateTime'" +
                tvProdDate.getText().toString().trim() +
                "' and ProdLine eq '" +
                currentProdline +
                "'";
        ApiTool.callPadMessageList(filterSb, new GenericOdataCallback<PadMessageInfo>() {
            @Override
            public void onDataAvailable(List<PadMessageInfo> datas) {
                mdatas.clear();
                mdatas.addAll(datas);
                adapter.notifyDataSetChanged();
                if (datas.isEmpty()) {
                    container.setVisibility(View.INVISIBLE);
                    buttonNew.setVisibility(View.VISIBLE);
                } else {
                    container.setVisibility(View.VISIBLE);
                    adapter.selectItem(0);
                    buttonNew.setVisibility(View.GONE);
                }
            }

            @Override
            public void onDataUnAvailable(String msg, int errorCode) {
                ToastUtil.show(PadMessageListActivity.this,"获取消息失败");
                mdatas.clear();
                adapter.notifyDataSetChanged();
                container.setVisibility(View.INVISIBLE);
                buttonNew.setVisibility(View.INVISIBLE);
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
        private int selectedPosition = -1;

        SimpleItemRecyclerViewAdapter(PadMessageListActivity parent,
                                      List<PadMessageInfo> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PadMessageInfo item = (PadMessageInfo) view.getTag();

                selectedPosition = mValues.indexOf(item);
                notifyDataSetChanged();

                if (true) {
                    Bundle arguments = new Bundle();
                    arguments.putSerializable(PadMessageDetailFragment.ARG_ITEM_ID, item);
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

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.padmessage_list_content, parent, false);
            return new ViewHolder(view);
        }

        public void selectItem(int selectedPosition) {
            this.selectedPosition = selectedPosition;
            this.notifyDataSetChanged();
            if (selectedPosition >= 0) {
                Bundle arguments = new Bundle();
                arguments.putSerializable(PadMessageDetailFragment.ARG_ITEM_ID, mValues.get(selectedPosition));
                PadMessageDetailFragment fragment = new PadMessageDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.padmessage_detail_container, fragment)
                        .commit();
            }
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
            holder.linearLayout.setBackgroundColor(selectedPosition == position ? 0xFFAAFFAA : 0XFFFFFFFF);

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;
            final LinearLayout linearLayout;


            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
                linearLayout = (LinearLayout) view.findViewById(R.id.la2_item_message_list);

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

    private class GetUserProdLine extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = getService.GetUserProdLineService(params[0]);
            return result;
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(String result) {
            System.out.println("result:" + result);
            if (!result.trim().equals("null") && !result.trim().equals("[]") && !result.trim().equals("{}") && !result.trim().equals("") && !result.trim().equals("anyType{}")) {
                List<BaseListItem> lstProdLine = new BaseListItemParser().getListFromJson(result, "Prod_Line", "Prod_Line_Name", false);
                ListItemAdapter adapter = new ListItemAdapter(getBaseContext(), lstProdLine);
                spProdLine.setAdapter(adapter);
                for (int i = 0; i < lstProdLine.size(); i++) {
                    if (lstProdLine.get(i).getItemId().equals(LoginUser.getUser().Prod_Line)) {
                        spProdLine.setSelection(i);
                        break;
                    }
                }
                spProdLine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        currentProdline = ((BaseListItem) parent.getSelectedItem()).getItemId();
                        currentProdlineName = ((BaseListItem) parent.getSelectedItem()).getItemName();
                        checkMessageList();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        }
    }

}
