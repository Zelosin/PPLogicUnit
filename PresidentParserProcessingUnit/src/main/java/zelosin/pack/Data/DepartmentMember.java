package zelosin.pack.Data;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import zelosin.pack.Configurations.Query.QueryTypeAction;
import zelosin.pack.Data.Abstract.DepartmentMemberInformation;
import zelosin.pack.Data.ScienceWork.ScienceWork;

import java.util.ArrayList;
import java.util.HashMap;


public class DepartmentMember extends DepartmentMemberInformation {

    protected String mGroupNumber;

    public Table<QueryTypeAction, String, ArrayList<ScienceWork>> mMemberInformationList = HashBasedTable.create();
    public static HashMap<String, DepartmentMember> mDepartmentMembersList = new HashMap<>();

    public DepartmentMember(String pName) {
        this.mName = pName;
        mDepartmentMembersList.put(pName, this);
    }

    public void setPSUProfileLink(String pLink){
        this.mPSUProfileLink = pLink;
        this.mPSUProfileID = pLink.replaceAll("\\D+","");
        mAssignedProfileCount++;
    }

    public DepartmentMember(String pName, String pGroupNumber) {
        this.mName = pName;
        mGroupNumber = pGroupNumber;
        mDepartmentMembersList.put(pName, this);
    }

    public String getPSUProfileLink(){
        return mPSUProfileLink;
    }

    public String getmName() {
        return mName;
    }

    public String getmGroupNumber() {
        return mGroupNumber;
    }


    public ArrayList<ScienceWork> getSectionArray(QueryTypeAction pQueryType, String pSectionName){
        ArrayList<ScienceWork> tScienceWorkArray = mMemberInformationList.get(pQueryType, pSectionName);
        if(tScienceWorkArray == null) {
            tScienceWorkArray = new ArrayList<>();
            mMemberInformationList.put(pQueryType, pSectionName, tScienceWorkArray);
        }
        return tScienceWorkArray;
    }

    public String getmPSUProfileID() {
        return mPSUProfileID;
    }

}
