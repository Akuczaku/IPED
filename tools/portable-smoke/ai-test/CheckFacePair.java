import java.nio.file.*;import java.util.*;import org.apache.lucene.store.*;import org.apache.lucene.index.*;import org.apache.lucene.document.*;
public class CheckFacePair {
static double dist(float[] a,float[]b){double d=0;for(int i=0;i<a.length;i++)d+=(a[i]-b[i])*(a[i]-b[i]);return Math.sqrt(d);}
public static void main(String[] args)throws Exception{
Map<String,List<float[]>> v=new TreeMap<>();Map<String,String> counts=new TreeMap<>();
try(Directory dir=FSDirectory.open(Paths.get(args[0]));DirectoryReader r=DirectoryReader.open(dir)){
for(LeafReaderContext l:r.leaves()){
for(int i=0;i<l.reader().maxDoc();i++){Document d=l.reader().document(i);if(d.get("face_count")!=null)counts.put(d.get("name"),d.get("face_count"));}
for(FieldInfo f:l.reader().getFieldInfos())if(f.getVectorDimension()>0&&f.name.startsWith("face_encodings")){
VectorValues vv=l.reader().getVectorValues(f.name);for(int id=vv.nextDoc();id!=Integer.MAX_VALUE;id=vv.nextDoc()){String name=l.reader().document(id).get("name");v.computeIfAbsent(name,k->new ArrayList<>()).add(vv.vectorValue().clone());}
}}}
System.out.println("Counts: "+counts);
List<float[]>a=v.get("01-dwie-fikcyjne-osoby.png"),b=v.get("03-te-same-osoby-inne-zdjecie.png");
if(a==null||b==null||a.size()!=2||b.size()!=2||!"0".equals(counts.get("02-bez-twarzy.png")))throw new Exception("Missing indexed faces");
for(int i=0;i<2;i++){double nearest=9,furthest=0;for(int j=0;j<2;j++){double d=dist(a.get(i),b.get(j));System.out.println("Indexed distance "+i+" -> "+j+": "+d);nearest=Math.min(nearest,d);furthest=Math.max(furthest,d);}if(nearest>=.6||furthest<=.6)throw new Exception("Pair discrimination failed");}
System.out.println("PASS: IPED indexed both synthetic people in both images; corresponding matches distinct from other person");
}}
