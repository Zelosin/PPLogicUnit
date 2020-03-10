package zelosin.pack.Services;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import zelosin.pack.Configurations.Form.SheetConfigurations;
import zelosin.pack.Data.DepartmentMember;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelService {
    private static  FileOutputStream mOutputStream = null;
    private static   HSSFWorkbook mWorkBook = null;
    private static CreationHelper mCreateHelper = null;

    private static HSSFSheet mCurrentSheet = null;
    private static HSSFRow tCurrentRow = null;

    public static void fillExcelCell(int pRow, int pColumn, String pValue){
        tCurrentRow = mCurrentSheet.getRow(pRow);
        if(tCurrentRow == null)
            tCurrentRow = mCurrentSheet.createRow(pRow);
        tCurrentRow.createCell(pColumn).setCellValue(pValue);
    }

    public static void openStreams(String pFilePath){
        try {
            mOutputStream = new FileOutputStream(pFilePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        mWorkBook = new HSSFWorkbook();
        mCreateHelper = mWorkBook.getCreationHelper();
    }

    private static void flushSheet(){
        try {
            mWorkBook.write(mOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeSheetToExcelFileByColumnConfiguration(String pFilePath) {
        var lambdaContext = new Object() {
            int tCellRow = 0;
        };
        openStreams(pFilePath);
        SheetConfigurations.mSheetConfigurationsList.forEach( tConfig ->{
            mCurrentSheet = mWorkBook.createSheet(tConfig.mSheetName);
            tConfig.mLabelsList.forEach(tLabel ->{
                fillExcelCell(tLabel.mDisplayRow, tLabel.mDisplayColumn, tLabel.mDisplayText);
            });

            tConfig.mTableFormConfigurations.forEach(tCell ->{
                lambdaContext.tCellRow = tCell.mDisplayRow;
                fillExcelCell(tCell.mDisplayRow, tCell.mDisplayColumn, tCell.mDisplayText);
                DepartmentMember.mDepartmentMembersList.forEach((key, tMember) ->{
                    tMember.mMemberInformationList.get(tCell.mQueryType, tCell.mSectionName).forEach(tScienceWork -> {
                        try{
                            if(!tConfig.verifyScienceWork(tScienceWork))
                                return;
                        }
                        catch(NullPointerException e){
                            return;
                        }

                        lambdaContext.tCellRow++;
                        fillExcelCell(lambdaContext.tCellRow, tCell.mDisplayColumn, tScienceWork.mScienceWorkInformation.get(tCell.mVariable));
                    });
                });
            });
        });
        flushSheet();
    }
}
