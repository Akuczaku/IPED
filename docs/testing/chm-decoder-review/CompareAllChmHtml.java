import java.nio.file.*;
import java.io.*;
import java.util.*;
import org.apache.tika.parser.microsoft.chm.*;
public class CompareAllChmHtml {
 public static void main(String[] args)throws Exception {
  Path root=Paths.get(args[0]);int count=0;
  for(String name:new String[]{"CProcess","LastActivityView","7-zip-current"}){
   try(InputStream in=Files.newInputStream(root.resolve("INPUT/"+name+".chm"))){
    ChmExtractor extractor=new ChmExtractor(in);
    for(DirectoryListingEntry e:extractor.getChmDirList().getDirectoryListingEntryList()){
     if(!(e.getName().endsWith(".htm")||e.getName().endsWith(".html")))continue;
     String relative=e.getName().replaceFirst("^/+","");
     Path extracted=root.resolve("EXTRACTED-"+name).resolve(relative);
     if(!Arrays.equals(Files.readAllBytes(extracted),extractor.extractChmEntry(e))){ Files.write(root.resolve("mismatch.html"),extractor.extractChmEntry(e)); throw new Exception("HTML byte mismatch: "+e.getName()); };
     count++;System.out.println(name+": HTML_BYTES_EQUAL");
    }
   }
  }
  if(count!=72)throw new Exception("Expected 72 HTML pages");
 }
}
