package zelosin.pack.base;

import zelosin.pack.Configurations.Query.QueryConfigurations;
import zelosin.pack.Configurations.Query.QueryTypeAction;
import zelosin.pack.Services.ExcelService;
import zelosin.pack.Services.PSUService;
import zelosin.pack.Services.XMLService;





public class Main {

    public static void main(String[] args) {
        XMLService.secureFilesOpenCall();
        PSUService.assignDepartmentMemberList(QueryConfigurations.mQueryType);
        PSUService.parseScienceWorkPageByQueryType(QueryConfigurations.mParseType);
        ExcelService.writeSheetToExcelFileByColumnConfiguration(QueryConfigurations.mExportPath);
    }
}

























