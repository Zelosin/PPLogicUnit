package zelosin.pack.Configurations.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QueryConfigurations {

    public static String mSessionDepartmentLink;
    public static String mGroupListLink;
    public static String mProfileService;

    public static QueryTypeAction mQueryType;
    public static QueryTypeAction mParseType;
    public static String mExportPath;


    public static class QueryConfiguration {
        public String mVariable, mAssignName;
        public boolean mIsAble;
        public static Map<QueryTypeAction, ArrayList<QueryConfiguration>> mQueryConfigurationsList = new HashMap<>();

        public QueryConfiguration(QueryTypeAction pQueryType, String pAssignName, String pVariable, boolean pIsAble) {
            this.mVariable = pVariable;
            this.mAssignName = pAssignName;
            this.mIsAble = pIsAble;
            var tArray = mQueryConfigurationsList.computeIfAbsent(pQueryType, k -> new ArrayList<>());
            tArray.add(this);
        }

        public static String getAssignName(QueryTypeAction pQueryType, String pVariable) {
            var tArray = mQueryConfigurationsList.get(pQueryType);
            for (QueryConfiguration tConfig : tArray) {
                if (pVariable.equals(tConfig.mVariable)) return tConfig.mAssignName;
            }
            return null;
        }

        public static String getVariable(QueryTypeAction pQueryType, String pAssignName) {
            var tArray = mQueryConfigurationsList.get(pQueryType);
            for (QueryConfiguration tConfig : tArray) {
                if (pAssignName.equals(tConfig.mAssignName)) return tConfig.mVariable;
            }
            return null;
        }
    }
}











