package com.gki.v107.myinterface;

import android.widget.TextView;

public interface FragmentInteractionInterface {
    void acquireDatas(final String orderno, int stepCode, String sourceCode, final TextView tvDate, final TextView tvstarttime, final TextView tvendtime);
    void submitDatas( final TextView tvDate,final TextView tvstarttime, final TextView tvendtime);
}
