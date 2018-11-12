package com.gki.v107.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gki.managerment.R;
import com.gki.managerment.util.ToastUtil;
import com.gki.v107.adapter.MyInspection3Adapter;
import com.gki.v107.entity.ItemVsSpecItemInfo;
import com.gki.v107.entity.Polymorph;
import com.gki.v107.entity.ProdSpecDetailsAddon;
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
 * Use the {@link InspectConfirm3Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InspectConfirm3Fragment extends Fragment implements FragmentInteractionInterface {


    public InspectConfirm3Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment InspectConfirm1Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InspectConfirm3Fragment newInstance(String param1, String param2) {
        InspectConfirm3Fragment fragment = new InspectConfirm3Fragment();
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
    private EditText etNo1,etNo2,etNo3,etNo4,etNo5;

    @Override
    public void acquireDatas(final String orderno, final int stepCode,String sourceCode,final TextView tvDate,final TextView tvstarttime,final TextView tvendtime) {

        if (sourceCode.isEmpty() || adapter == null) return;

        etNo1.setText("0");
        etNo2.setText("0");
        etNo3.setText("0");
        etNo4.setText("0");
        etNo5.setText("0");

        progressBar.setVisibility(View.VISIBLE);

        currentOrderNo = orderno;

        String filter = "Item_No eq '" + sourceCode + "'";

        ApiTool.callItemVsSpecItemList(filter, new GenericOdataCallback<ItemVsSpecItemInfo>() {
            @Override
            public void onDataAvailable(List<ItemVsSpecItemInfo> datas) {
                polyList.clear();
                polyList.addAll(adapter.createPolyList(datas, currentOrderNo,etNo1,etNo2,etNo3,etNo4,etNo5));
                adapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onDataUnAvailable(String msg, int errorCode) {
                polyList.clear();
                adapter.notifyDataSetChanged();
                ToastUtil.show(getContext(), msg);

                progressBar.setVisibility(View.GONE);
            }
        });

    }


    private int successCount = 0, totalCount = 0;
    private String currentOrderNo = "";

    public void submitDatas(final TextView tvDate,final TextView tvstarttime,final TextView tvendtime) {
        if (adapter == null) return;
        if(polyList.isEmpty()){
            ToastUtil.show(getContext(),"没有提交数据");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        successCount = 0;
        totalCount = 0;
        final StringBuilder stringBuilder = new StringBuilder();

        for (final Polymorph<ProdSpecDetailsAddon, ItemVsSpecItemInfo> polymorph : polyList) {
            ProdSpecDetailsAddon addon = polymorph.getAddonEntity();
            addon.setQty1(etNo1.getText().toString().trim());
            addon.setQty2(etNo2.getText().toString().trim());
            addon.setQty3(etNo3.getText().toString().trim());
            addon.setQty4(etNo4.getText().toString().trim());
            addon.setQty5(etNo5.getText().toString().trim());

            switch (polymorph.getState()) {
                case FAILURE_EDIT:
                case UNCOMMITTED_EDIT:
                    @SuppressLint("DefaultLocale")
                    String itemDesc = String.format("Prod_Order_No='%s',Line_No=%d",
                            addon.getProd_Order_No(),
                            addon.getLine_No());

                    ApiTool.updateProdSpecDetailsList(itemDesc,
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

                    ApiTool.addProdSpecDetails(addon, new BaseOdataCallback<Map<String, Object>>() {
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

    private void toastResult(StringBuilder stringBuilder, int size) {
        if (totalCount >= size) {
            stringBuilder.append("(检查报告书)共").append(totalCount).append("条记录,").append("成功提交").append(successCount).append("条");
            ToastUtil.show(getContext(), stringBuilder.toString());
            progressBar.setVisibility(View.GONE);
        }
    }

    private MyInspection3Adapter adapter;
    private List<Polymorph<ProdSpecDetailsAddon, ItemVsSpecItemInfo>> polyList = new ArrayList<>();
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inspect_confirm3, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar2_inspection3);

        etNo1 = (EditText) view.findViewById(R.id.et2_inspection3_no1);
        etNo2 = (EditText) view.findViewById(R.id.et2_inspection3_no2);
        etNo3 = (EditText) view.findViewById(R.id.et2_inspection3_no3);
        etNo4 = (EditText) view.findViewById(R.id.et2_inspection3_no4);
        etNo5 = (EditText) view.findViewById(R.id.et2_inspection3_no5);

        progressBar.setVisibility(View.GONE);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler2_inspection3);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyInspection3Adapter(polyList);
        View headerView = inflater.inflate(R.layout.item2_inspection3_header, container, false);
        adapter.addHeaderView(headerView);
        adapter.bindToRecyclerView(recyclerView);


        return view;
    }

}
