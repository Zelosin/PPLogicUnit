package zelosin.pack.base;

import org.jsoup.nodes.Document;
import zelosin.pack.Configurations.Query.QueryTypeAction;
import zelosin.pack.Services.ExcelService;
import zelosin.pack.Services.PSUService;
import zelosin.pack.Services.XMLService;


public class Main {

    public static void main(String[] args) {
        long m = System.currentTimeMillis();

        XMLService.secureFilesOpenCall();
        PSUService.assignDepartmentMemberList(QueryTypeAction.Resource);
        PSUService.parseScienceWorkPageByQueryTypeAndSectionName(QueryTypeAction.Resource, "r_article");
        //ExcelService.writeSheetToExcelFileByColumnConfiguration("C:\\Users\\theze\\Desktop\\PresindetParserLogicUnit\\test.xls");
        System.out.println((double) (System.currentTimeMillis() - m));
    }
}

























