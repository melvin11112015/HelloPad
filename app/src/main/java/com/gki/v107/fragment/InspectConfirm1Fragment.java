package com.gki.v107.fragment;


import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.gki.managerment.R;
import com.gki.managerment.util.ToastUtil;
import com.gki.v107.adapter.MyInspection1aAdapter;
import com.gki.v107.entity.Polymorph;
import com.gki.v107.entity.ProdConfirmBomItemsAddon;
import com.gki.v107.entity.WebPordOrderCompInfo;
import com.gki.v107.myinterface.FragmentInteractionInterface;
import com.gki.v107.net.ApiTool;
import com.gki.v107.net.BaseOdataCallback;
import com.gki.v107.net.GenericOdataCallback;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InspectConfirm1Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InspectConfirm1Fragment extends Fragment implements FragmentInteractionInterface {


    MyInspection1aAdapter adapter1;
    private TextView tvstarttime, tvendtime, tvDate;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv2_inspection1_time1:
                    showTimePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog, tvstarttime, Calendar.getInstance());
                    break;
                case R.id.tv2_inspection1_time2:
                    showTimePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog, tvendtime, Calendar.getInstance());
                    break;
            }
        }
    };
    private int successCount = 0, totalCount = 0;
    private List<Polymorph<ProdConfirmBomItemsAddon, WebPordOrderCompInfo>> polyList = new ArrayList<>();


    public InspectConfirm1Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment InspectConfirm1Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InspectConfirm1Fragment newInstance(String param1, String param2) {
        InspectConfirm1Fragment fragment = new InspectConfirm1Fragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 时间选择
     *
     * @param context
     * @param themeResId
     * @param tv
     * @param calendar
     */
    public static void showTimePickerDialog(Context context, int themeResId, final TextView tv, Calendar calendar) {
        // Calendar c = Calendar.getInstance();
        // 创建一个TimePickerDialog实例，并把它显示出来
        new TimePickerDialog(context, themeResId,
                // 绑定监听器
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        tv.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }
                // 设置初始时间
                , calendar.get(Calendar.HOUR_OF_DAY)
                , calendar.get(Calendar.MINUTE)
                // true表示采用24小时制
                , true).show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public void acquireDatas(final String orderno, final int stepCode) {

        if (orderno.isEmpty() || adapter1 == null) return;

        String filter = "Prod_Order_No eq '" + orderno + "' and Status eq 'Released'";
        ApiTool.callWebPordOrderComp(filter, new GenericOdataCallback<WebPordOrderCompInfo>() {
            @Override
            public void onDataAvailable(List<WebPordOrderCompInfo> datas) {
                polyList.clear();
                polyList.addAll(adapter1.createPolyList(datas, stepCode, orderno, tvDate, tvstarttime, tvendtime));
                adapter1.notifyDataSetChanged();
            }

            @Override
            public void onDataUnAvailable(String msg, int errorCode) {
                polyList.clear();
                adapter1.notifyDataSetChanged();
                ToastUtil.show(getContext(), msg);
            }
        });
    }

    public void submitDatas() {
        if (adapter1 == null || tvstarttime == null || tvendtime == null || tvDate == null) return;

        if (tvstarttime.getText().toString().compareTo(tvendtime.getText().toString()) > 0) {
            ToastUtil.show(getContext(), "始业时不能大于终业时");
            return;
        }
        successCount = 0;
        totalCount = 0;
        final StringBuilder stringBuilder = new StringBuilder();

        String startDatetime = tvDate.getText().toString() + 'T' + tvstarttime.getText().toString();
        String endDatetime = tvDate.getText().toString() + 'T' + tvendtime.getText().toString();

        for (final Polymorph<ProdConfirmBomItemsAddon, WebPordOrderCompInfo> polymorph : polyList) {
            ProdConfirmBomItemsAddon addon = polymorph.getAddonEntity();

            addon.setStrat_Time(startDatetime);
            addon.setEnd_Time(endDatetime);

            switch (polymorph.getState()) {
                case FAILURE_EDIT:
                case UNCOMMITTED_EDIT:
                    @SuppressLint("DefaultLocale")
                    String itemDesc = String.format("Prod_Order_No='%s',Step=%d,Line_No=%d",
                            addon.getProd_Order_No(),
                            addon.getStep(),
                            addon.getLine_No());

                    ApiTool.updateProdConfirmBomItemsList(itemDesc,
                            addon,
                            new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    totalCount++;
                                    if (response.isSuccessful()) {
                                        successCount++;
                                        toastResult(stringBuilder, polyList.size());
                                        polymorph.setState(Polymorph.State.COMMITTED);
                                    } else {
                                        String err = "";
                                        try {
                                            err = response.errorBody().string();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        stringBuilder.append(err).append('\n');
                                        toastResult(stringBuilder, polyList.size());
                                        polymorph.setState(Polymorph.State.FAILURE_EDIT);
                                    }
                                    adapter1.notifyDataSetChanged();
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    t.printStackTrace();
                                    stringBuilder.append(t.getMessage()).append('\n');
                                    totalCount++;
                                    toastResult(stringBuilder, polyList.size());
                                    polymorph.setState(Polymorph.State.FAILURE_EDIT);
                                    adapter1.notifyDataSetChanged();
                                }
                            });

                    break;
                case UNCOMMITTED_NEW:
                case FAILURE_NEW:

                    ApiTool.addProdConfirmBomItems(addon, new BaseOdataCallback<Map<String, Object>>() {
                        @Override
                        public void onDataAvailable(Map<String, Object> datas) {
                            successCount++;
                            totalCount++;
                            toastResult(stringBuilder, polyList.size());
                            polymorph.setState(Polymorph.State.COMMITTED);
                            adapter1.notifyDataSetChanged();
                        }

                        @Override
                        public void onDataUnAvailable(String msg, int errorCode) {
                            stringBuilder.append(msg).append('\n');
                            totalCount++;
                            toastResult(stringBuilder, polyList.size());
                            polymorph.setState(Polymorph.State.FAILURE_NEW);
                            adapter1.notifyDataSetChanged();
                        }
                    });
                    break;
                default:
                    totalCount++;
                    successCount++;
                    break;
            }
        }

    }

    private void toastResult(StringBuilder stringBuilder, int size) {
        if (totalCount >= size) {
            stringBuilder.append("(构成部件)共").append(totalCount).append("条记录,").append("成功提交").append(successCount).append("条");
            ToastUtil.show(getContext(), stringBuilder.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inspect_confirm1, container, false);

        tvstarttime = (TextView) view.findViewById(R.id.tv2_inspection1_time1);
        tvendtime = (TextView) view.findViewById(R.id.tv2_inspection1_time2);

        tvstarttime.setOnClickListener(onClickListener);
        tvendtime.setOnClickListener(onClickListener);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler2_inspection1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter1 = new MyInspection1aAdapter(polyList);
        View headerView = inflater.inflate(R.layout.item2_inspection1_header, container, false);
        adapter1.addHeaderView(headerView);
        adapter1.bindToRecyclerView(recyclerView);
        Calendar calendar = Calendar.getInstance();
        tvDate = (TextView) view.findViewById(R.id.tv2_inspection1_date);

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        tvDate.setText(sdf1.format(calendar.getTime()));
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
        tvstarttime.setText(sdf2.format(calendar.getTime()));
        tvendtime.setText(sdf2.format(calendar.getTime()));

        return view;
    }

}
