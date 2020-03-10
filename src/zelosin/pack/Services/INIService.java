package zelosin.pack.Services;
/*
import zelosin.pack.Configurations.Form.TableFormConfigurations;
import zelosin.pack.Configurations.Query.QueryConfigurations;
import zelosin.pack.Configurations.Query.QueryType;
import zelosin.pack.Data.AJAXConfiguration;
import org.ini4j.Ini;
import org.ini4j.Profile;
import org.ini4j.Wini;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class INIService {
    private static Ini mQueryConfigurationFile = null;
    private static Ini mTableFormConfigurationFile = null;
    private static Ini mAJAXConfigurationFile = null;

    private static String mTableFormConfigurationFilePath = "table_form.pp";
    private static String mQueryConfigurationFilePath = "query.pp";
    private static String mAJAXConfigurationFilePath = "ajax_query.pp";

    public static void setmTableFormConfigurationFilePath(String mTableFormConfigurationFilePath) {
        INIService.mTableFormConfigurationFilePath = mTableFormConfigurationFilePath;
    }

    public static void setmQueryConfigurationFilePath(String mQueryConfigurationFilePath) {
        INIService.mQueryConfigurationFilePath = mQueryConfigurationFilePath;
    }

    public static void setmAJAXConfigurationFilePath(String mAJAXConfigurationFilePath) {
        INIService.mAJAXConfigurationFilePath = mAJAXConfigurationFilePath;
    }

    public static void loadConfigsFile(){
        try {
            mQueryConfigurationFile = new Ini(new FileReader(mQueryConfigurationFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String tSectionName: mQueryConfigurationFile.keySet()) {
            parseQueryConfiguration(mQueryConfigurationFile.get(tSectionName), tSectionName);
        }

        try {
            mTableFormConfigurationFile = new Ini(new FileReader(mTableFormConfigurationFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String tSectionName: mTableFormConfigurationFile.keySet()) {
            parseTableFormConfiguration(mTableFormConfigurationFile.get(tSectionName), tSectionName);
        }


        try {
            mAJAXConfigurationFile = new Ini(new FileReader(mAJAXConfigurationFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String tSectionName: mAJAXConfigurationFile.keySet()) {
            parseAJAXConfiguration(mAJAXConfigurationFile.get(tSectionName), tSectionName);
        }
    }

    public static void parseQueryConfiguration(Profile.Section pParsingSection, String pSectionName){
        if(pParsingSection.size() != 0) {
            if(pSectionName.equals("general")){
                QueryConfigurations.GeneralQueryType = QueryType.valueOf(pParsingSection.get("QueryType"));
                return;
            }

            switch (pSectionName.substring(0, 1)) {
                case ("r"): {
                    new QueryConfigurations(
                            pParsingSection.get("AssignName"),
                            pSectionName,
                            QueryType.Resource,
                            Boolean.valueOf(pParsingSection.get("Able"))
                    );
                    break;
                }
                case ("d"): {
                    new QueryConfigurations(
                            pParsingSection.get("AssignName"),
                            pSectionName,
                            QueryType.Document,
                            Boolean.valueOf(pParsingSection.get("Able"))
                    );
                    break;
                }
                case ("n"): {
                    new QueryConfigurations(
                            pParsingSection.get("AssignName"),
                            pSectionName,
                            QueryType.NIR,
                            Boolean.valueOf(pParsingSection.get("Able"))
                    );
                    break;
                }
                case ("q"): {
                    new QueryConfigurations(
                            pParsingSection.get("AssignName"),
                            pSectionName,
                            QueryType.RID,
                            Boolean.valueOf(pParsingSection.get("Able"))
                    );
                    break;
                }
            }
        }
    }

    public static void parseTableFormConfiguration(Profile.Section pParsingSection, String pSectionName){
        if(pSectionName.equals("general")){
            TableFormConfigurations.mIsTextWrappable = Boolean.parseBoolean(pParsingSection.get("IsTextWrappable"));
            TableFormConfigurations.mDisplayRow = Integer.valueOf(pParsingSection.get("DisplayRow"));
            TableFormConfigurations.GeneralQueryType = QueryType.valueOf(pParsingSection.get("QueryType"));
            return;
        }

        if(pParsingSection.size() != 0) {
            new TableFormConfigurations(
                    Integer.valueOf(pParsingSection.get("DisplayColumn")),
                    Integer.valueOf(pParsingSection.get("ColumnWidth")),
                    pParsingSection.get("DisplayName"),
                    pSectionName
            );
        }
    }

    public static void parseAJAXConfiguration(Profile.Section pParsingSection, String pSectionName){
        if(pParsingSection.size() != 0) {
            switch (pSectionName.substring(0, 1)) {
                case ("r"): {
                    new AJAXConfiguration(
                            pParsingSection.get("AssignName"),
                            pParsingSection.get("URLPart"),
                            QueryType.Resource
                    );
                    break;
                }
                case ("d"): {
                    new AJAXConfiguration(
                            pParsingSection.get("AssignName"),
                            pParsingSection.get("URLPart"),
                            QueryType.Document
                    );
                    break;
                }
                case ("n"): {
                    new AJAXConfiguration(
                            pParsingSection.get("AssignName"),
                            pParsingSection.get("URLPart"),
                            QueryType.NIR
                    );
                    break;
                }
                case ("q"): {
                    new AJAXConfiguration(
                            pParsingSection.get("AssignName"),
                            pParsingSection.get("URLPart"),
                            QueryType.RID
                    );
                    break;
                }
            }
        }
    }

    public static void saveConfigurations(){
        Wini mSaveConfigurationFile = null;
        try {
            mSaveConfigurationFile = new Wini(new File(mTableFormConfigurationFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSaveConfigurationFile.put("general", "IsTextWrappable" ,TableFormConfigurations.mIsTextWrappable);
        mSaveConfigurationFile.put("general", "DisplayRow" , TableFormConfigurations.mDisplayRow);
        mSaveConfigurationFile.put("general", "QueryType" , TableFormConfigurations.GeneralQueryType);

        for(TableFormConfigurations tConfiguration : TableFormConfigurations.mTableFormConfigurations){
            mSaveConfigurationFile.put(tConfiguration.SectionName.get(), "DisplayName" , tConfiguration.DisplayName.get());
            mSaveConfigurationFile.put(tConfiguration.SectionName.get(), "DisplayColumn" , tConfiguration.DisplayColumn.get());
            mSaveConfigurationFile.put(tConfiguration.SectionName.get(), "ColumnWidth" , tConfiguration.ColumnWidth.get());
        }
        try {
            mSaveConfigurationFile.store();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mSaveConfigurationFile = new Wini(new File(mQueryConfigurationFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSaveConfigurationFile.put("general", "QueryType" , QueryConfigurations.GeneralQueryType);

       /* for(BiMap.Entry<String, String> tConfiguration: QueryConfigurations.mAllQueryConfigurationsList.entrySet()){
            if(QueryConfigurations.mResourceQueryConfigurationsList.containsValue(tConfiguration.getKey()) ||
                    QueryConfigurations.mDocumentQueryConfigurationsList.containsValue(tConfiguration.getKey()) ||
                    QueryConfigurations.mNIRQueryConfigurationsList.containsValue(tConfiguration.getKey()) ||
                    QueryConfigurations.mRIDQueryConfigurationsList.containsValue(tConfiguration.getKey())
            )
                mSaveConfigurationFile.put(tConfiguration.getKey(), "Able" , true);
            else
                mSaveConfigurationFile.put(tConfiguration.getKey(), "Able" , false);
        }*/
/*

        try {
            mSaveConfigurationFile.store();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}








*/












