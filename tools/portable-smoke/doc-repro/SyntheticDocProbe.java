import java.nio.file.*;
import java.io.*;
import java.util.*;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.OfficeParser;
import org.apache.tika.sax.BodyContentHandler;

public class SyntheticDocProbe {
 public static void main(String[] args) throws Exception {
  if(args[0].equals("generate")) {
   Path template=Paths.get(args[1]), out=Paths.get(args[2]);Files.createDirectories(out);
   for(int count:new int[]{1,100,5000}) {
    try(InputStream in=Files.newInputStream(template);HWPFDocument doc=new HWPFDocument(in)) {
     String text="IPED_SYNTHETIC_DOC_BEGIN_20261105\r";
     String line="Synthetic test document. Za\u017c\u00f3\u0142\u0107 g\u0119\u015bl\u0105 ja\u017a\u0144. No private data.\r";
     StringBuilder sb=new StringBuilder(text);for(int i=0;i<count;i++)sb.append(line);sb.append("IPED_SYNTHETIC_DOC_END_20261105\r");
     doc.getRange().replaceText(doc.getRange().text(),sb.toString());
     doc.createInformationProperties();doc.getSummaryInformation().setAuthor("IPED synthetic test");doc.getSummaryInformation().setTitle("Synthetic DOC "+count);
     try(OutputStream dest=Files.newOutputStream(out.resolve("synthetic-"+count+".doc"))){doc.write(dest);}
    }
   }
   System.out.println("Generated 3 synthetic Word binary DOC files.");return;
  }
  for(String name:args) {
   Path p=Paths.get(name);
   for(String mode:new String[]{"stream","file","memory"}) {
    Metadata m=new Metadata();m.set(TikaCoreProperties.RESOURCE_NAME_KEY,p.getFileName().toString());
    BodyContentHandler h=new BodyContentHandler(-1);
    try(InputStream in=mode.equals("file")?TikaInputStream.get(p):mode.equals("memory")?new ByteArrayInputStream(Files.readAllBytes(p)):Files.newInputStream(p)) {
     new OfficeParser().parse(in,h,m,new ParseContext());
     if(!h.toString().contains("IPED_SYNTHETIC_DOC_END_20261105"))throw new Exception("Missing end marker");
     System.out.println("PASS "+p.getFileName()+" "+mode+" chars="+h.toString().length());
    }catch(Exception e){System.out.println("FAIL "+p.getFileName()+" "+mode);e.printStackTrace(System.out);}
   }
  }
 }
}
