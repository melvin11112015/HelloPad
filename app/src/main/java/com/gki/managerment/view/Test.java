package com.gki.managerment.view;

import java.util.List;

/**
 * Created by summit on 2/17/16.
 */
public class Test {

    public List<REPINFOVO> REPINFOVO;
    public String nextPageToken;


    public static class REPINFOVO {
        public List<REPADDINFOVO> REPADDINFOVO;
        public List<REPLABINFOVO> REPLABINFOVO;
        public List<REPPARINFOVO> REPPARINFOVO;

        public String REMARK;
        public String OWNER_NAME;
        // ....
        public String id;
    }


    public static class REPADDINFOVO {

    }

    public static class REPLABINFOVO {

    }

    public static class REPPARINFOVO {

    }

}
