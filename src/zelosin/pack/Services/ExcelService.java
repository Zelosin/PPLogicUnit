package zelosin.pack.Services;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import zelosin.pack.Configurations.Form.CellStyle;
import zelosin.pack.Configurations.Form.SheetConfigurations;
import zelosin.pack.Data.DepartmentMember;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelService {
    private static FileOutputStream mOutputStream = null;
    private static HSSFWorkbook mWorkBook = null;
    private static CreationHelper mCreateHelper = null;

    private static HSSFSheet mCurrentSheet = null;
    private static HSSFRow tCurrentRow = null;

    public static void fillExcelCell(int pRow, int pColumn, String pValue, String mStyleLink){
        tCurrentRow = mCurrentSheet.getRow(pRow);
        if(tCurrentRow == null)
            tCurrentRow = mCurrentSheet.createRow(pRow);
        Cell tCurrentCell = tCurrentRow.createCell(pColumn);
        tCurrentCell.setCellValue(pValue);

        org.apache.poi.ss.usermodel.CellStyle tOriginalStyle = mWorkBook.createCellStyle();
        CellStyle tCustomStyle = CellStyle.getStyle(mStyleLink);

        tOriginalStyle.setWrapText(tCustomStyle.mIsWrappable);
        Font tCellFont = mWorkBook.createFont();
        tCellFont.setColor(HSSFColor.HSSFColorPredefined.valueOf(tCustomStyle.mFontColor).getIndex());
        tCellFont.setFontHeight((short)(tCustomStyle.mFontSize*20));
        tCellFont.setFontName(tCustomStyle.mFontFamily);
        if(tCustomStyle.mIsUnderLine)
            tCellFont.setUnderline(HSSFFont.U_SINGLE);
        tCellFont.setItalic(tCustomStyle.mIsItalic);
        tCellFont.setBold(tCustomStyle.mIsBold);
        tOriginalStyle.setFont(tCellFont);

        tCurrentCell.setCellStyle(tOriginalStyle);
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
                fillExcelCell(tLabel.mDisplayRow, tLabel.mDisplayColumn, tLabel.mDisplayText, tLabel.mStyleLink);
            });

            tConfig.mTableFormConfigurations.forEach(tCell ->{
                lambdaContext.tCellRow = tCell.mDisplayRow;
                fillExcelCell(tCell.mDisplayRow, tCell.mDisplayColumn, tCell.mDisplayText, tCell.mStyleLink);


                DepartmentMember.mDepartmentMembersList.forEach((key, tMember) ->{

                    for(String mParam : tCell.mSectionName.split("::")) {
                        var tScienceWorkArray = tMember.mMemberInformationList.get(tCell.mQueryType, mParam);
                        if(tScienceWorkArray != null) {
                            tScienceWorkArray.forEach(tScienceWork -> {
                                try {
                                    if (!tConfig.verifyScienceWork(tScienceWork))
                                        return;
                                } catch (NullPointerException e) {
                                    return;
                                }

                                lambdaContext.tCellRow++;
                                fillExcelCell(lambdaContext.tCellRow, tCell.mDisplayColumn, tScienceWork.mScienceWorkInformation.get(tCell.mVariable), tCell.mStyleLink);
                            });
                        }
                    }
                });

            });


        });
        flushSheet();
    }
}
