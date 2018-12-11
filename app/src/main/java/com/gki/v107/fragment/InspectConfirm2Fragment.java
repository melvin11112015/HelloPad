package com.gki.v107.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gki.managerment.R;
import com.gki.managerment.util.ToastUtil;
import com.gki.v107.adapter.MyInspection2Adapter;
import com.gki.v107.entity.Polymorph;
import com.gki.v107.entity.ProdConfirmDetailsAddon;
import com.gki.v107.entity.ProdConfirmItemsInfo;
import com.gki.v107.myinterface.FragmentInteractionInterface;
import com.gki.v107.net.ApiTool;
import com.gki.v107.net.BaseOdataCallback;
import com.gki.v107.net.GenericOdataCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InspectConfirm2Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InspectConfirm2Fragment extends Fragment implements FragmentInteractionInterface {


    public InspectConfirm2Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment InspectConfirm1Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InspectConfirm2Fragment newInstance(String param1, String param2) {
        InspectConfirm2Fragment fragment = new InspectConfirm2Fragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



    private ProgressBar progressBar;

    @Override
    public void acquireDatas(final String orderno, final int stepCode, String sourceCode, final TextView tvstarttime, final TextView tvendtime) {

        if(orderno.isEmpty() || adapter == null)return;

        progressBar.setVisibility(View.VISIBLE);

        ApiTool.callProdConfirmItemsList(new GenericOdataCallback<ProdConfirmItemsInfo>() {
            @Override
            public void onDataAvailable(List<ProdConfirmItemsInfo> datas) {
                polyList.clear();
                polyList.addAll(adapter.createPolyList(datas, stepCode, orderno, tvstarttime, tvendtime));
                adapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onDataUnAvailable(String msg, int errorCode) {
                polyList.clear();
                adapter.notifyDataSetChanged();
                ToastUtil.show(getContext(),msg);

                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private int successCount = 0,totalCount = 0;

    @Override
    public void submitDatas(final TextView tvDate, final TextView tvstarttime, final TextView tvendtime) {
        if (adapter == null || tvendtime == null ||tvstarttime == null || tvDate == null) return;
        if(polyList.isEmpty()){
            ToastUtil.show(getContext(),"没有提交数据");
            return;
        }
        /*
        if(tvstarttime.getText().toString().compareTo(tvendtime.getText().toString())>0){
            ToastUtil.show(getContext(),"始业时不能大于终业时");
            return;
        }
        */


        progressBar.setVisibility(View.VISIBLE);

        successCount = 0;
        totalCount = 0;
        final StringBuilder stringBuilder = new StringBuilder();

        String startDatetime = tvDate.getText().toString() + 'T' + tvstarttime.getText().toString() + "+08:00";
        String endDatetime = tvDate.getText().toString() + 'T' + tvendtime.getText().toString() + "+08:00";


        for (final Polymorph<ProdConfirmDetailsAddon, ProdConfirmItemsInfo> polymorph : polyList) {
            ProdConfirmDetailsAddon addon = polymorph.getAddonEntity();

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

                    ApiTool.updateProdConfirmDetailsList(itemDesc,
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
                                    adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    t.printStackTrace();
                                    stringBuilder.append(t.getMessage()).append('\n');
                                    totalCount++;
                                    toastResult(stringBuilder, polyList.size());
                                    polymorph.setState(Polymorph.State.FAILURE_EDIT);
                                    adapter.notifyDataSetChanged();
                                }
                            });

                    break;
                case UNCOMMITTED_NEW:
                case FAILURE_NEW:

                    ApiTool.addProdConfirmDetails(addon, new BaseOdataCallback<Map<String, Object>>() {
                        @Override
                        public void onDataAvailable(Map<String, Object> datas) {
                            successCount++;
                            totalCount++;
                            toastResult(stringBuilder, polyList.size());
                            polymorph.setState(Polymorph.State.COMMITTED);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onDataUnAvailable(String msg, int errorCode) {
                            stringBuilder.append(msg).append('\n');
                            totalCount++;
                            toastResult(stringBuilder, polyList.size());
                            polymorph.setState(Polymorph.State.FAILURE_NEW);
                            adapter.notifyDataSetChanged();
                        }
                    });
                    break;
                default:
                    totalCount++;
                    toastResult(stringBuilder, polyList.size());
                    break;
            }
        }

    }

    private void toastResult(StringBuilder stringBuilder,int size){
        if(totalCount>=size) {
            stringBuilder.append("(确认项目)共").append(totalCount).append("条记录,").append("成功提交").append(successCount).append("条");
            ToastUtil.show(getContext(), stringBuilder.toString());
            progressBar.setVisibility(View.GONE);
        }
    }


    private MyInspection2Adapter adapter;
    private List<Polymorph<ProdConfirmDetailsAddon, ProdConfirmItemsInfo>> polyList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inspect_confirm1, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar2_inspection1);

        progressBar.setVisibility(View.GONE);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler2_inspection1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyInspection2Adapter(polyList);
        View headerView = inflater.inflate(R.layout.item2_inspection2_header,container,false);
        adapter.addHeaderView(headerView);
        adapter.bindToRecyclerView(recyclerView);

        return view;
    }

}
