package zelosin.pack.base;

import zelosin.pack.Configurations.Query.QueryTypeAction;
import zelosin.pack.Services.ExcelService;
import zelosin.pack.Services.PSUService;
import zelosin.pack.Services.XMLService;

public class Main {

    public static void main(String[] args) {

        XMLService.secureFilesOpenCall();
        PSUService.assignDepartmentMemberList(QueryTypeAction.Resource);
        PSUService.parseScienceWorkPageByQueryTypeAndSectionName(QueryTypeAction.Resource, "r_article");
        ExcelService.writeSheetToExcelFileByColumnConfiguration("C:\\Users\\theze\\Desktop\\PresindetParserLogicUnit\\test.xls");

    }
}
