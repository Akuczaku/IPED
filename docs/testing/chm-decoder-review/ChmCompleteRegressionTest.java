import java.nio.*;
import java.nio.file.*;
import java.io.*;
import org.apache.tika.parser.microsoft.chm.*;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.exception.TikaException;
import org.junit.Test;
import static org.junit.Assert.*;
public class ChmCompleteRegressionTest {
 @Test public void acceptsSingleCompressedBlock()throws Exception {
  ChmLzxcResetTable t=new ChmLzxcResetTable();t.setBlockAddress(new long[]{0});
  ChmAssert.assertChmBlockSegment(new byte[16],t,0,0,16);
 }
 @Test(expected=TikaException.class) public void rejectsEmptyBlockTable()throws Exception {
  ChmLzxcResetTable t=new ChmLzxcResetTable();t.setBlockAddress(new long[0]);
  ChmAssert.assertChmBlockSegment(new byte[16],t,0,0,16);
 }
 @Test(expected=TikaException.class) public void rejectsOutOfBoundsBlock()throws Exception {
  ChmLzxcResetTable t=new ChmLzxcResetTable();t.setBlockAddress(new long[]{0});
  ChmAssert.assertChmBlockSegment(new byte[16],t,1,0,16);
 }
 byte[] header(long offset) {
  ByteBuffer b=ByteBuffer.allocate(96).order(ByteOrder.LITTLE_ENDIAN);
  b.put(new byte[]{'I','T','S','F'});b.putInt(3);b.putInt(96);
  b.putLong(72,128);b.putLong(80,4096);b.putLong(88,offset);return b.array();
 }
 @Test public void usesExplicitV3DataOffset()throws Exception {
  for(long v:new long[]{0,136,4300}){
   ChmItsfHeader h=new ChmItsfHeader();h.parse(header(v),h);assertEquals(v,h.getDataOffset());
  }
 }
 @Test(expected=TikaException.class) public void rejectsTruncatedV3Header()throws Exception {
  ChmItsfHeader h=new ChmItsfHeader();h.parse(java.util.Arrays.copyOf(header(136),95),h);
 }
 @Test public void extractsPublicSingleBlockHelp()throws Exception {
  for(String name:new String[]{"CProcess.chm","LastActivityView.chm"}){
   BodyContentHandler handler=new BodyContentHandler(-1);
   try(InputStream in=Files.newInputStream(Paths.get(System.getProperty("sample.dir"),name))){
    new ChmParser().parse(in,handler,new Metadata(),new ParseContext());
   }
   assertTrue(name,handler.toString().length()>1000);
  }
 }
 @Test public void comparesAllPublicHtmlBytes()throws Exception {
  CompareAllChmHtml.main(new String[]{Paths.get(System.getProperty("sample.dir")).getParent().toString()});
 }
}
