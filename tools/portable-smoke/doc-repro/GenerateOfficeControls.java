import java.nio.file.*;import java.io.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFTextBox;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
public class GenerateOfficeControls {
 public static void main(String[] a)throws Exception{
  Path root=Paths.get(a[0]);Files.createDirectories(root);
  try(HSSFWorkbook w=new HSSFWorkbook();OutputStream o=Files.newOutputStream(root.resolve("control.xls"))){w.createSheet("Synthetic").createRow(0).createCell(0).setCellValue("IPED_CONTROL_XLS_20261105");w.write(o);}
  try(HSLFSlideShow w=new HSLFSlideShow();OutputStream o=Files.newOutputStream(root.resolve("control.ppt"))){HSLFSlide s=w.createSlide();HSLFTextBox t=new HSLFTextBox();t.setText("IPED_CONTROL_PPT_20261105");s.addShape(t);w.write(o);}
  try(XWPFDocument w=new XWPFDocument();OutputStream o=Files.newOutputStream(root.resolve("control.docx"))){w.createParagraph().createRun().setText("IPED_CONTROL_DOCX_20261105");w.write(o);}
 }
}
