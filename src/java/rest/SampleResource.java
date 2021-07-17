package rest;

import com.spire.xls.FileFormat;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.RequestScoped;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

/**
 * PDFとエクセルの出力サンプル
 * http://localhost:8080/lawyersample/webresources/sample/downloadPdf
 * http://localhost:8080/lawyersample/webresources/sample/downloadExcel
 * http://localhost:8080/lawyersample/webresources/sample/downloadPdfFromExcel
 * @author satouxr
 */
@RequestScoped
@Path("/sample")
public class SampleResource {
    
    @Context
    ServletContext context;
    
    @GET
    @Path("/downloadPdf")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadPdf() {
        try {
            // テンプレートファイルパスを指定
            String formPath = context.getRealPath("resources/form/template.jasper");
            File jasperFile = new File(formPath);
            JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperFile);
            
            Map<String, Object> params = new HashMap<>();
            params.put("userName", "佐藤");
            // 画像表示
            String imgPath = context.getRealPath("resources/img/inkan.png");
            InputStream imgSrc = new FileInputStream(imgPath);
            params.put("reportImage", imgSrc);

            // バーコード情報
            params.put("tNumber", "T210514");

            // 一覧
            List<SampleDto> sampleList = new ArrayList<>();
            sampleList.add(new SampleDto(1, "あいうえお", LocalDate.now()));
            sampleList.add(new SampleDto(2, "かきくけこ", LocalDate.of(2021, Month.JUNE, 30)));
            JasperPrint pdf = JasperFillManager.fillReport(jasperReport, params, new JRBeanCollectionDataSource(sampleList));

            // 一覧のないフォームの場合は、new JREmptyDataSource()を渡す。
            //JasperPrint pdf = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());

            // 帳票の出力
            byte[] bytes = JasperExportManager.exportReportToPdf(pdf);
            // レスポンスを生成する。
            String encodedFilename = URLEncoder.encode("サンプル.pdf", "UTF-8");
            Response.ResponseBuilder response = Response.ok(bytes);
            String headerVal = "attachment; filename=" + encodedFilename;
            response.header("Content-Disposition", headerVal);
            return response.build();
        } catch (JRException | IOException  ex) {
            throw new InternalServerErrorException();
        }
    }
    
    @GET
    @Path("/downloadExcel")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadExcel() {
        try {
            List<SampleDto> sampleList = new ArrayList<>();
            sampleList.add(new SampleDto(1, "あいうえお", LocalDate.now()));
            sampleList.add(new SampleDto(2, "かきくけこ", LocalDate.of(2021, Month.JUNE, 30)));

            // Excel生成
            // テンプレートファイルパスを指定
            String templatePath = context.getRealPath("resources/excel/template2.xlsx");
            Workbook wb = new XSSFWorkbook(templatePath);
            Sheet sheet = wb.getSheetAt(0);

            for (int rowIndex = 2; rowIndex <= sampleList.size() + 1; rowIndex++) {
                SampleDto sample = sampleList.get(rowIndex - 2);
                Row row = sheet.getRow(rowIndex);
                if(row == null){
                    row = sheet.createRow(rowIndex);
                }
                // セルの枠線を設定する。
                CellStyle cellStyle = wb.createCellStyle();
                cellStyle.setBorderLeft(BorderStyle.THIN);
                cellStyle.setBorderRight(BorderStyle.THIN);
                cellStyle.setBorderTop(BorderStyle.THIN);
                cellStyle.setBorderBottom(BorderStyle.THIN);
                // 上詰め
                cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
                // 改行して表示
                cellStyle.setWrapText(true);
                // 日付用のセルスタイル
                CellStyle dateCellStyle = wb.createCellStyle();
                dateCellStyle.cloneStyleFrom(cellStyle);
                dateCellStyle.setDataFormat((short)0xe);

                // 番号
                setCellValue(row, 0, sample.getId(), cellStyle);
                // 内容
                setCellValue(row, 1, sample.getDetail(), cellStyle);
                // 登録日
                setCellValue(row, 2, sample.getRegistDate(), dateCellStyle);
            }
            
            // 画像挿入
            String imgPath = context.getRealPath("resources/img/inkan.png");
            byte[] imgBytes = Files.readAllBytes(Paths.get(imgPath));
            //画像をworkbookに追加
            int pictureIdx = wb.addPicture(imgBytes, Workbook.PICTURE_TYPE_PNG);
            //Drawingインスタンスを取得
            Drawing patriarch = sheet.createDrawingPatriarch();
            //ClientAnchorインスタンスを取得
            ClientAnchor anchor = wb.getCreationHelper().createClientAnchor();
            //画像挿入位置・サイズを指定する
            anchor.setCol1(4);
            anchor.setRow1(0);
            anchor.setCol2(6);
            anchor.setRow2(4);
            //指定した表示位置に画像を設定
            patriarch.createPicture(anchor, pictureIdx);
            
            // バーコードを生成し、一旦画像ファイルを作成する。
            // %PAYARA_HOME%/glassfish/domains/domain1/config に生成
            Code128Bean barcodeBean = new Code128Bean();
            String message = "T210514";
            int dpi = 400;
            try (FileOutputStream os = new FileOutputStream(new File("barcode.png"))) {
                BitmapCanvasProvider canvas = new BitmapCanvasProvider(os, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
                barcodeBean.generateBarcode(canvas, message);
                canvas.finish();
            }
            // 生成したバーコード画像をExcelに挿入
            byte[] barcodeBytes = Files.readAllBytes(Paths.get("barcode.png"));
            //画像をworkbookに追加
            int barcodeIdx = wb.addPicture(barcodeBytes, Workbook.PICTURE_TYPE_PNG);
            //Drawingインスタンスを取得
            Drawing patriarch2 = sheet.createDrawingPatriarch();
            //ClientAnchorインスタンスを取得
            ClientAnchor anchor2 = wb.getCreationHelper().createClientAnchor();
            //画像挿入位置・サイズを指定する
            anchor2.setCol1(4);
            anchor2.setRow1(5);
            anchor2.setCol2(6);
            anchor2.setRow2(8);
            //指定した表示位置に画像を設定
            patriarch2.createPicture(anchor2, barcodeIdx);
            
            // シートを保護する。
            sheet.protectSheet("12345");

            // レスポンスを生成する。
            String encodedFilename = URLEncoder.encode("サンプル.xlsx", "UTF-8");
            Response.ResponseBuilder response = Response.ok(byteArray(wb));
            String headerVal = "attachment; filename=" + encodedFilename;
            response.header("Content-Disposition", headerVal);
            return response.build();
        }  catch (IOException ex) {
            throw new InternalServerErrorException();
        }
    }
    
    @GET
    @Path("/downloadPdfFromExcel")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadPdfFromExcel() {
        try {
            List<SampleDto> sampleList = new ArrayList<>();
            sampleList.add(new SampleDto(1, "あいうえお", LocalDate.now()));
            sampleList.add(new SampleDto(2, "かきくけこ", LocalDate.of(2021, Month.JUNE, 30)));

            // Excel生成
            // テンプレートファイルパスを指定
            String templatePath = context.getRealPath("resources/excel/template2.xlsx");
            Workbook wb = new XSSFWorkbook(templatePath);
            Sheet sheet = wb.getSheetAt(0);

            for (int rowIndex = 2; rowIndex <= sampleList.size() + 1; rowIndex++) {
                SampleDto sample = sampleList.get(rowIndex - 2);
                Row row = sheet.getRow(rowIndex);
                if(row == null){
                    row = sheet.createRow(rowIndex);
                }
                // セルの枠線を設定する。
                CellStyle cellStyle = wb.createCellStyle();
                cellStyle.setBorderLeft(BorderStyle.THIN);
                cellStyle.setBorderRight(BorderStyle.THIN);
                cellStyle.setBorderTop(BorderStyle.THIN);
                cellStyle.setBorderBottom(BorderStyle.THIN);
                // 上詰め
                cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
                // 改行して表示
                cellStyle.setWrapText(true);
                // 日付用のセルスタイル
                CellStyle dateCellStyle = wb.createCellStyle();
                dateCellStyle.cloneStyleFrom(cellStyle);
                dateCellStyle.setDataFormat((short)0xe);

                // 番号
                setCellValue(row, 0, sample.getId(), cellStyle);
                // 内容
                setCellValue(row, 1, sample.getDetail(), cellStyle);
                // 登録日
                setCellValue(row, 2, sample.getRegistDate(), dateCellStyle);
            }
            
            // 画像挿入
            String imgPath = context.getRealPath("resources/img/inkan.png");
            byte[] imgBytes = Files.readAllBytes(Paths.get(imgPath));
            //画像をworkbookに追加
            int pictureIdx = wb.addPicture(imgBytes, Workbook.PICTURE_TYPE_PNG);
            //Drawingインスタンスを取得
            Drawing patriarch = sheet.createDrawingPatriarch();
            //ClientAnchorインスタンスを取得
            ClientAnchor anchor = wb.getCreationHelper().createClientAnchor();
            //画像挿入位置・サイズを指定する
            anchor.setCol1(4);
            anchor.setRow1(0);
            anchor.setCol2(6);
            anchor.setRow2(4);
            //指定した表示位置に画像を設定
            patriarch.createPicture(anchor, pictureIdx);
            
            // バーコードを生成し、一旦画像ファイルを作成する。
            // %PAYARA_HOME%/glassfish/domains/domain1/config に生成
            Code128Bean barcodeBean = new Code128Bean();
            String message = "T210514";
            int dpi = 400;
            try (FileOutputStream os = new FileOutputStream(new File("barcode.png"))) {
                BitmapCanvasProvider canvas = new BitmapCanvasProvider(os, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
                barcodeBean.generateBarcode(canvas, message);
                canvas.finish();
            }
            // 生成したバーコード画像をExcelに挿入
            byte[] barcodeBytes = Files.readAllBytes(Paths.get("barcode.png"));
            //画像をworkbookに追加
            int barcodeIdx = wb.addPicture(barcodeBytes, Workbook.PICTURE_TYPE_PNG);
            //Drawingインスタンスを取得
            Drawing patriarch2 = sheet.createDrawingPatriarch();
            //ClientAnchorインスタンスを取得
            ClientAnchor anchor2 = wb.getCreationHelper().createClientAnchor();
            //画像挿入位置・サイズを指定する
            anchor2.setCol1(4);
            anchor2.setRow1(5);
            anchor2.setCol2(6);
            anchor2.setRow2(8);
            //指定した表示位置に画像を設定
            patriarch2.createPicture(anchor2, barcodeIdx);
            
            // ExcelをPDFに変換し、一旦ファイルを生成する。
            // %PAYARA_HOME%/glassfish/domains/domain1/config に生成
            com.spire.xls.Workbook swb = new com.spire.xls.Workbook();
            swb.loadFromStream(new ByteArrayInputStream(byteArray(wb)));
            swb.saveToFile("sample.pdf", FileFormat.PDF);
            
            // 生成したファイルをバイト列に変換する。
            byte[] bytes = Files.readAllBytes(Paths.get("sample.pdf"));

            // レスポンスを生成する。
            String encodedFilename = URLEncoder.encode("サンプル.pdf", "UTF-8");
            Response.ResponseBuilder response = Response.ok(bytes);
            String headerVal = "attachment; filename=" + encodedFilename;
            response.header("Content-Disposition", headerVal);
            return response.build();
        }  catch (IOException ex) {
            throw new InternalServerErrorException();
        }
    }
    
    // セルに値を設定
    private void setCellValue(Row row, int cellIndex, Object value, CellStyle cellStyle) {
        Cell cell = row.getCell(cellIndex);
        if(cell == null){
            cell = row.createCell(cellIndex);
        }
        if (cellStyle != null) {
            cell.setCellStyle(cellStyle);
        }
        if (value != null) {
            if (value instanceof String) {
                cell.setCellValue((String) value);
            } else if (value instanceof Number) {
                Number numValue = (Number) value;
                if (numValue instanceof Float) {
                    Float floatValue = (Float) numValue;
                    numValue = Double.valueOf(String.valueOf(floatValue));
                }
                cell.setCellValue(numValue.doubleValue());
            } else if (value instanceof LocalDate) {
                LocalDate dateValue = (LocalDate) value;
                cell.setCellValue(dateValue);
            } else if (value instanceof Boolean) {
                Boolean boolValue = (Boolean) value;
                cell.setCellValue(boolValue);
            }
        }
    }
    
    /**
    * ワークブックからbyte配列取得
    */
    private byte[] byteArray(Workbook workbook) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new IOException();
        }
   }
}
