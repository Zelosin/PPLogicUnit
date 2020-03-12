package zelosin.pack.Services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import zelosin.pack.Configurations.Form.CellStyle;
import zelosin.pack.Configurations.Form.FilterAction;
import zelosin.pack.Configurations.Form.FilterType;
import zelosin.pack.Configurations.Form.SheetConfigurations;
import zelosin.pack.Configurations.Query.QueryConfigurations;
import zelosin.pack.Configurations.Query.QueryTypeAction;
import zelosin.pack.Data.AJAXConfiguration;
import zelosin.pack.Data.DepartmentMember;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class XMLService {

    private static FileInputStream mQueryFileStream;
    private static Document mQueryFileDocument;

    public static void secureFilesOpenCall(){
        try {
            openAllFileStream();
            getFileDocument(mQueryFileStream);
            mQueryFileStream.close();
            parseQueryFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void openAllFileStream() throws FileNotFoundException {
        mQueryFileStream = new FileInputStream(new File("QueryConfiguration.xml"));
    }

    private static void getFileDocument(FileInputStream pFileStream) throws IOException {
         mQueryFileDocument = Jsoup.parse(pFileStream, null, "", Parser.xmlParser());
    }

    public static Document getmQueryFileDocument() {
        return mQueryFileDocument;
    }

    public static void parseQueryFile(){
        parseGeneralPart(mQueryFileDocument);
        parseForPersons(mQueryFileDocument);
        parseAJAXPart(mQueryFileDocument);
        parseForExcelSheets(mQueryFileDocument);
        parseQueryConfigurationSection(mQueryFileDocument);
    }

    public static void parseAJAXPart(Document pParsingDocument){
        for(Element mAJAXLElement : pParsingDocument.selectFirst("resource_ajax_url_list").children())
            new AJAXConfiguration(mAJAXLElement.attr("assign_name"), mAJAXLElement.attr("url_part"), mAJAXLElement.attr("variable"), QueryTypeAction.Resource);
        for(Element mAJAXLElement : pParsingDocument.selectFirst("document_ajax_url_list").children())
            new AJAXConfiguration(mAJAXLElement.attr("assign_name"), mAJAXLElement.attr("url_part"), mAJAXLElement.attr("variable"), QueryTypeAction.Document);
        for(Element mAJAXLElement : pParsingDocument.selectFirst("nir_ajax_url_list").children())
            new AJAXConfiguration(mAJAXLElement.attr("assign_name"), mAJAXLElement.attr("url_part"), mAJAXLElement.attr("variable"), QueryTypeAction.NIR);
        for(Element mAJAXLElement : pParsingDocument.selectFirst("rid_ajax_url_list").children())
            new AJAXConfiguration(mAJAXLElement.attr("assign_name"), mAJAXLElement.attr("url_part"), mAJAXLElement.attr("variable"), QueryTypeAction.RID);
    }

    public static void parseGeneralPart(Document pParsingDocument) {
        Element mGeneralListElement = pParsingDocument.selectFirst("general");
        for(Element mGeneralElement : mGeneralListElement.children())
            switch (mGeneralElement.tagName()){
                case("psu_query_settings"): {
                    QueryConfigurations.mSessionDepartmentLink = mGeneralElement.attr("department_link");
                    QueryConfigurations.mGroupListLink = mGeneralElement.attr("list_of_study_groups_link");
                    QueryConfigurations.mProfileService = mGeneralElement.attr("profile_service_link");
                    QueryConfigurations.mQueryType = QueryTypeAction.valueOf(mGeneralElement.attr("query_type"));
                    QueryConfigurations.mParseType =  QueryTypeAction.valueOf(mGeneralElement.attr("parse_type"));
                    QueryConfigurations.mExportPath = mGeneralElement.attr("export_path");
                    break;
                }
            }
    }

    public static void parseForPersons(Document pParsingDocument) {
        Element mPersonListElement = pParsingDocument.selectFirst("person_query_list");
        for(Element tPersonElement : mPersonListElement.children()){
            String tGroup = tPersonElement.attr("group");
            if(tGroup != null){
                new DepartmentMember(tPersonElement.attr("full_name"), tGroup);
            }
            else
                new DepartmentMember(tPersonElement.attr("full_name"));
        }
    }

    public static void parseForExcelSheets(Document pParsingDocument) {
        for(Element mStylesList : pParsingDocument.selectFirst("excel_styles").children())
            new CellStyle(
                    mStylesList.attr("Name"),
                    mStylesList.attr("FontFamily"),
                    mStylesList.attr("Color"),
                    Integer.valueOf(mStylesList.attr("ColumnWidth")),
                    Integer.valueOf(mStylesList.attr("FontSize")),
                    Boolean.valueOf(mStylesList.attr("Wrappable")),
                    Boolean.valueOf(mStylesList.attr("Italic")),
                    Boolean.valueOf(mStylesList.attr("Bold")),
                    Boolean.valueOf(mStylesList.attr("UnderLine"))
                    );
        Element mSheetList = pParsingDocument.selectFirst("sheets_list");
        for(Element tSheetElement : mSheetList.children()){
            SheetConfigurations tCurrentSheet = new SheetConfigurations(tSheetElement.attr("name"), false);

            for(Element tSheetFilterElement : tSheetElement.selectFirst("filters_list").children()) {
                tCurrentSheet.new SheetFilter(
                        FilterType.valueOf(tSheetFilterElement.attr("type")),
                        tSheetFilterElement.attr("variable"),
                        FilterAction.valueOf(tSheetFilterElement.attr("action")),
                        tSheetFilterElement.attr("value"));
            }

            for(Element tTableConfig : tSheetElement.selectFirst("sheet_body").children()){
                switch (tTableConfig.tagName()){
                    case("cell"):{
                        tCurrentSheet.new TableFormConfigurations(
                            tTableConfig.attr("display_text"),
                            QueryTypeAction.valueOf(tTableConfig.attr("query_type")),
                            tTableConfig.attr("section_name"),
                            tTableConfig.attr("variable"),
                            Integer.valueOf(tTableConfig.attr("column")),
                            Integer.valueOf(tTableConfig.attr("row")),
                            tTableConfig.attr("style"));
                            break;
                    }
                    case("label"):{
                        tCurrentSheet.new SimpleLabel(
                                tTableConfig.attr("display_text"),
                                Integer.parseInt(tTableConfig.attr("row")),
                                Integer.parseInt(tTableConfig.attr("column")),
                                tTableConfig.attr("style")
                        );
                        break;
                    }
                }

            }
        }
    }

    private static void parseQuerySection(QueryTypeAction pQueryType, String pListName, Element pQueryTypeListElement){
        for(Element mQueryTypeElement : pQueryTypeListElement.selectFirst(pListName).children()){
            new QueryConfigurations.QueryConfiguration(
                    pQueryType,  mQueryTypeElement.attr("assign_name"), mQueryTypeElement.attr("variable"), Boolean.parseBoolean(mQueryTypeElement.attr("able")));
        }
    }

    public static void parseQueryConfigurationSection(Document pParsingDocument){
        Element mQueryTypeListElement = pParsingDocument.selectFirst("query_configurations_list");
        Map<QueryTypeAction, String> tQueryTypeMap = new HashMap<>(){{
            put(QueryTypeAction.Resource, "resource_query");
            put(QueryTypeAction.Document, "document_query");
            put(QueryTypeAction.NIR, "nir_query");
            put(QueryTypeAction.RID, "rid_query");
        }};
        tQueryTypeMap.forEach((key, value) -> {
            parseQuerySection(key, value, mQueryTypeListElement);
        });
    }
}



















