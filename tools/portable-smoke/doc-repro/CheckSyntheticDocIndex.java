import java.nio.file.*;
import org.apache.lucene.store.*;
import org.apache.lucene.index.*;
import org.apache.lucene.document.*;
import org.apache.lucene.search.*;
public class CheckSyntheticDocIndex {
 public static void main(String[] a)throws Exception {
  try(Directory dir=FSDirectory.open(Paths.get(a[0]));DirectoryReader r=DirectoryReader.open(dir)){
   IndexSearcher s=new IndexSearcher(r);Terms terms=MultiTerms.getTerms(r,"content");
   if(terms!=null){TermsEnum it=terms.iterator();org.apache.lucene.util.BytesRef t;while((t=it.next())!=null){String v=t.utf8ToString();if(v.contains("20261105")){System.out.println("MARKER_TERM "+v+" count="+s.count(new TermQuery(new Term("content",v))));}}}
   int docs=0,authors=0;
   for(int i=0;i<r.maxDoc();i++){Document d=r.document(i);String name=d.get("name");if(name!=null&&name.matches("synthetic-(1|100|5000)\\.doc")){
    docs++;StringBuilder line=new StringBuilder("DOC "+d.get("path"));boolean author=false;
    for(IndexableField f:d.getFields()){String value=f.stringValue();if(value!=null&&(value.equals("IPED synthetic test")||value.startsWith("Synthetic DOC "))){line.append(" | "+f.name()+"="+value);if(value.equals("IPED synthetic test"))author=true;}}
    if(author)authors++;System.out.println(line);
   }}
   System.out.println("DOC_ITEMS="+docs+" AUTHOR_ITEMS="+authors);
   if(a.length>1&&(docs!=9||authors!=9))throw new Exception("Missing Office metadata on one or more DOC items");
  }
 }
}
