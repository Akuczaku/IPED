package iped.engine.task;
import java.io.*;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.mime.MediaType;
import org.apache.commons.compress.archivers.*;
public class VisualStudioIpiTest {
 static final MediaType ARJ=MediaType.application("x-arj");
 // Synthetic option streams: no external files or machine-specific properties.
 @Test public void optionStreamsAreCorrectedWithoutConsumingBytes()throws Exception {
  int n=0;for(int variant=0;variant<5;variant++){
   byte[] b=new byte[5120];new Random(variant).nextBytes(b);
   byte[] header={(byte)0x60,(byte)0xea,0,0,1,0,0,0};
   System.arraycopy(header,0,b,0,header.length);assertEquals("arj",ArchiveStreamFactory.detect(new BufferedInputStream(new ByteArrayInputStream(b))));
   try(TikaInputStream s=TikaInputStream.get(b)){
    assertEquals(MediaType.OCTET_STREAM,SignatureTask.refineVisualStudioIpiType(s,ARJ));
    assertEquals(0x60,s.read());
   }n++;
  }assertEquals(5,n);
 }
 @Test public void contextIsRequired(){
  assertTrue(SignatureTask.isVisualStudioIpiCandidate(ARJ,"IPI_test","a.opt>>IPI_test",true,5120L));
  assertFalse(SignatureTask.isVisualStudioIpiCandidate(ARJ,"IPI_test","a.zip>>IPI_test",true,5120L));
  assertFalse(SignatureTask.isVisualStudioIpiCandidate(ARJ,"IPI_test","a.opt>>IPI_test",false,5120L));
  assertFalse(SignatureTask.isVisualStudioIpiCandidate(ARJ,"real.arj","a.opt>>real.arj",true,5120L));
  assertFalse(SignatureTask.isVisualStudioIpiCandidate(ARJ,"IPI_test","a.opt>>IPI_test",true,100L));
 }
 @Test public void genuineArjRemainsArj()throws Exception {
  byte[] b;
  try(InputStream in=VisualStudioIpiTest.class.getResourceAsStream("/test-files/ipi/control.arj")) {
   assertNotNull("Bundled ARJ control fixture",in);b=in.readAllBytes();
  }
  try(TikaInputStream s=TikaInputStream.get(b)){assertEquals(ARJ,SignatureTask.refineVisualStudioIpiType(s,ARJ));}
  try(ArchiveInputStream a=new ArchiveStreamFactory().createArchiveInputStream(new BufferedInputStream(new ByteArrayInputStream(b)))){assertNotNull(a.getNextEntry());}
 }
 @Test public void truncatedArjIsNotSilenced()throws Exception {
  byte[] b={0x60,(byte)0xea,30,0,0,0,0,0};
  try(TikaInputStream s=TikaInputStream.get(b)){assertEquals(ARJ,SignatureTask.refineVisualStudioIpiType(s,ARJ));}
  try{new ArchiveStreamFactory().createArchiveInputStream(new BufferedInputStream(new ByteArrayInputStream(b)));fail("Should reject malformed ARJ");}catch(ArchiveException expected){}
 }
}
