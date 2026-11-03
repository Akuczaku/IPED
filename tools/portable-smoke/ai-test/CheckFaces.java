import java.nio.file.*;import java.util.*;import org.apache.lucene.store.*;import org.apache.lucene.index.*;import org.apache.lucene.document.*;
public class CheckFaces{public static void main(String[] a)throws Exception{
 Map<String,String> counts=new TreeMap<>();int vectorFields=0;
 try(Directory dir=FSDirectory.open(Paths.get(a[0]));DirectoryReader r=DirectoryReader.open(dir)){
 for(int i=0;i<r.maxDoc();i++){Document d=r.document(i);String n=d.get("name");String c=d.get("face_count");if(c!=null){counts.put(n,c);System.out.println(n+": face_count="+c);} }
 for(LeafReaderContext leaf:r.leaves())for(FieldInfo f:leaf.reader().getFieldInfos())if(f.getVectorDimension()>0){System.out.println("Vector field: "+f.name+" dimension="+f.getVectorDimension());vectorFields++;}
 }
 if(!"2".equals(counts.get("01-dwie-fikcyjne-osoby.png"))||!"0".equals(counts.get("02-bez-twarzy.png"))||vectorFields==0)throw new Exception("Face index validation failed");
 System.out.println("PASS: IPED detected 2 synthetic faces, 0 in control, indexed vectors");
}}
