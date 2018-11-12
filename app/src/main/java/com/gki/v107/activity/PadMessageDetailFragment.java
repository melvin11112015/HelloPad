package com.gki.v107.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gki.managerment.LoginUser;
import com.gki.managerment.R;
import com.gki.v107.entity.PadMessageInfo;
import com.gki.v107.tool.DatetimeTool;

/**
 * A fragment representing a single PadMessage detail screen.
 * This fragment is either contained in a {@link PadMessageListActivity}
 * in two-pane mode (on tablets) or a {@link PadMessageDetailActivity}
 * on handsets.
 */
public class PadMessageDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_SHIFT = "shift";

    /**
     * The dummy content this fragment is presenting.
     */
    private PadMessageInfo mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PadMessageDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = (PadMessageInfo) getArguments().getSerializable(ARG_ITEM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.padmessage_detail, container, false);

        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.padmessage_detail)).setText(mItem.getMsg());
            ((TextView) rootView.findViewById(R.id.padmessage_detail2)).setText(mItem.getMsg2());
            ((TextView) rootView.findViewById(R.id.padmessage_detail3)).setText(mItem.getMsg3());
            ((TextView) rootView.findViewById(R.id.padmessage_detail4)).setText(mItem.getMsg4());
            ((TextView) rootView.findViewById(R.id.padmessage_detail5)).setText(mItem.getMsg5());
            ((TextView) rootView.findViewById(R.id.padmessage_detail6)).setText(mItem.getMsg6());
            ((TextView) rootView.findViewById(R.id.tv2_message_frag_name)).setText(mItem.getCreate_User());
            ((TextView) rootView.findViewById(R.id.tv2_message_frag_time)).setText(DatetimeTool.convertOdataTimezone(mItem.getCreate_DateTime(), DatetimeTool.TYPE_DATETIME, false));

            Button buttonEdit = (Button) rootView.findViewById(R.id.button2_message_frag_edit);

            buttonEdit.setVisibility(mItem.getCreate_User().equals(LoginUser.getUser().getUserId()) ? View.VISIBLE : View.INVISIBLE);

            buttonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), BuildMessageActivity.class);
                    intent.putExtra("padmessage", mItem);
                    startActivity(intent);
                }
            });
        }

        return rootView;
    }
}
