package zelosin.pack.Data;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import zelosin.pack.Configurations.Query.QueryTypeAction;

import java.util.HashMap;

public class AJAXConfiguration {

    public static HashMap<QueryTypeAction, Multimap<String, String>> mAJAXConfigurationsList = new HashMap<>();

    static{
        for (QueryTypeAction value : QueryTypeAction.values()) {
            mAJAXConfigurationsList.put(value, ArrayListMultimap.create());
        }
    }

    public AJAXConfiguration(String mAssignName, String pURLPart, String pVariableName, QueryTypeAction pQueryType) {
        var tQueryMap = mAJAXConfigurationsList.get(pQueryType);
        tQueryMap.put(mAssignName, pURLPart);
        tQueryMap.put(mAssignName, pVariableName);
    }


}
