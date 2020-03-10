package zelosin.pack.Configurations.Form;

import zelosin.pack.Configurations.Query.QueryTypeAction;
import zelosin.pack.Data.ScienceWork.ScienceWork;

import java.util.ArrayList;

public class SheetConfigurations {

    public String mSheetName;
    public Boolean mIsTextWrappable;
    public ArrayList<TableFormConfigurations> mTableFormConfigurations = new ArrayList<>();
    public static ArrayList<SheetConfigurations> mSheetConfigurationsList = new ArrayList<>();
    public ArrayList<SheetFilter> mSheetFiltersList = new ArrayList<>();
    public ArrayList<SimpleLabel> mLabelsList = new ArrayList<>();

    public class TableFormConfigurations{

        public Integer mColumnWidth, mDisplayColumn, mDisplayRow;
        public String mDisplayText, mVariable, mSectionName;
        public QueryTypeAction mQueryType;

        public TableFormConfigurations(String mDisplayText, QueryTypeAction mQueryType, String mSectionName, String mVariable,
                                       Integer mDisplayColumn, Integer mDisplayRow, Integer mColumnWidth) {
            this.mColumnWidth = mColumnWidth;
            this.mDisplayColumn = mDisplayColumn;
            this.mDisplayRow = mDisplayRow;
            this.mDisplayText = mDisplayText;
            this.mVariable = mVariable;
            this.mSectionName = mSectionName;
            this.mQueryType = mQueryType;
            mTableFormConfigurations.add(this);
        }
    }
    public class SimpleLabel {
        public String mDisplayText;
        public int mDisplayRow, mDisplayColumn;

        public SimpleLabel(String mText, int mRow, int mColumn) {
            this.mDisplayText = mText;
            this.mDisplayRow = mRow;
            this.mDisplayColumn = mColumn;
            mLabelsList.add(this);
        }
    }
    public class SheetFilter{
        private FilterAction mFilterAction;
        private FilterType mFilterType;
        private String mVariable, mComparableValue;

        public SheetFilter(FilterType mFilterType, String mVariable, FilterAction mFilterAction, String mComparableValue) {
            this.mFilterAction = mFilterAction;
            this.mFilterType = mFilterType;
            this.mVariable = mVariable;
            this.mComparableValue = mComparableValue;
            mSheetFiltersList.add(this);
        }
    }

    public SheetConfigurations(String mSheetName, Boolean mIsTextWrappable) {
        this.mSheetName = mSheetName;
        this.mIsTextWrappable = mIsTextWrappable;
        mSheetConfigurationsList.add(this);
    }
    public boolean verifyScienceWork(ScienceWork pScienceWork) throws NullPointerException{
        for (SheetFilter tFilter : mSheetFiltersList) {
            switch (tFilter.mFilterType){
                case StringCompare: {
                    switch (tFilter.mFilterAction){
                        case NotEqual:{
                            if(pScienceWork.mScienceWorkInformation.get(tFilter.mVariable).equals(tFilter.mComparableValue))
                                return false;
                            break;
                        }
                        default:
                        case Equal:{
                            if(!pScienceWork.mScienceWorkInformation.get(tFilter.mVariable).equals(tFilter.mComparableValue))
                                return false;
                            break;
                        }
                    }
                    break;
                }
                case DateCompare: {break;}
            }
        }
        return true;
    }
}






















