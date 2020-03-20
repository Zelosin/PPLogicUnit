package zelosin.pack.Configurations.Form;

import zelosin.pack.Configurations.Query.QueryTypeAction;
import zelosin.pack.Data.ScienceWork.ScienceWork;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class SheetConfigurations {

    public String mSheetName;
    public Boolean mIsTextWrappable;
    public ArrayList<TableFormConfigurations> mTableFormConfigurations = new ArrayList<>();
    public static ArrayList<SheetConfigurations> mSheetConfigurationsList = new ArrayList<>();
    public ArrayList<SheetFilter> mSheetFiltersList = new ArrayList<>();
    public ArrayList<SimpleLabel> mLabelsList = new ArrayList<>();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.UK);

    public class TableFormConfigurations{

        public Integer mDisplayColumn, mDisplayRow;
        public String mStyleLink, mDisplayText, mVariable, mSectionName;
        public QueryTypeAction mQueryType;

        public TableFormConfigurations(String mDisplayText, QueryTypeAction mQueryType, String mSectionName, String mVariable,
                                       Integer mDisplayColumn, Integer mDisplayRow, String mStyleLink) {
            this.mStyleLink = mStyleLink;
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
        public String mDisplayText, mStyleLink;
        public int mDisplayRow, mDisplayColumn;

        public SimpleLabel(String mText, int mRow, int mColumn, String mStyleLink) {
            this.mDisplayText = mText;
            this.mDisplayRow = mRow;
            this.mDisplayColumn = mColumn;
            this.mStyleLink = mStyleLink;
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
    public boolean verifyScienceWork(ScienceWork pScienceWork) throws NullPointerException {
        boolean tRetuningValue = true;
        for (SheetFilter tFilter : mSheetFiltersList) {
            switch (tFilter.mFilterType){
                case StringCompare: {
                    switch (tFilter.mFilterAction){
                        case NotEqual:{
                            for(String mParam : tFilter.mComparableValue.split("::")) {
                                if (pScienceWork.mScienceWorkInformation.get(tFilter.mVariable).equals(mParam))
                                    tRetuningValue = false;
                            }
                            return tRetuningValue;
                        }
                        default:
                        case Equal:{
                            for(String mParam : tFilter.mComparableValue.split("::")) {
                                if (pScienceWork.mScienceWorkInformation.get(tFilter.mVariable).equals(mParam))
                                    return true;
                            }
                        }
                    }
                }
                case DateCompare: {
                    switch (tFilter.mFilterAction){
                        case BiggestThan:{
                            try {
                                if(!(DATE_FORMAT.parse(pScienceWork.mScienceWorkInformation.get(tFilter.mVariable)).after(
                                        DATE_FORMAT.parse(tFilter.mComparableValue)
                                        )))
                                    return false;
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        case LessThan:{
                            try {
                                if(DATE_FORMAT.parse(pScienceWork.mScienceWorkInformation.get(tFilter.mVariable))
                                        .after(
                                                DATE_FORMAT.parse(tFilter.mComparableValue)
                                        ))
                                    return false;
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        default:
                            return false;
                    }

            }
            }
        }
        return true;
    }
}






















