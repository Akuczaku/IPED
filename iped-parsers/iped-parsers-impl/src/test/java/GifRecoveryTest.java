import java.io.*;
import java.nio.file.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.junit.Test;
import static org.junit.Assert.*;
import org.apache.tika.metadata.*;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import iped.parsers.image.KnownMalformedGifParser;
public class GifRecoveryTest {
 byte[] fixture(String n)throws IOException {
  try(InputStream in=GifRecoveryTest.class.getResourceAsStream("/test-files/gif-recovery/pixel-"+n+".gif")) {
   if(in==null) throw new FileNotFoundException("Missing GIF test resource: "+n);
   return in.readAllBytes();
  }
 }
 Metadata parse(byte[] b)throws Exception {
  Metadata m=new Metadata();m.set(Metadata.CONTENT_TYPE,"image/gif");
  new KnownMalformedGifParser().parse(new ByteArrayInputStream(b),new BodyContentHandler(),m,new ParseContext());return m;
 }
 @Test public void bothExportedGifsRecoverAndRemainUnchanged()throws Exception {
  for(String n:new String[]{"1","2"}){
   Path p=Files.createTempFile("iped-gif-recovery-", ".gif");
   try {
   Files.write(p,fixture(n));byte[] before=Files.readAllBytes(p);
   Metadata m=parse(before);assertNotNull(m.get(KnownMalformedGifParser.RECOVERY));assertEquals("true",m.get("iped:originalMalformed"));
   assertArrayEquals(before,Files.readAllBytes(p));
   } finally {Files.deleteIfExists(p);}
  }
 }
 @Test public void validGifUsesNormalParser()throws Exception {
  ByteArrayOutputStream out=new ByteArrayOutputStream();assertTrue(ImageIO.write(new BufferedImage(2,3,BufferedImage.TYPE_INT_RGB),"gif",out));
  assertNull(parse(out.toByteArray()).get(KnownMalformedGifParser.RECOVERY));
 }
 @Test public void truncatedGifStillFails()throws Exception {
  byte[] b=fixture("1");
  try{parse(java.util.Arrays.copyOf(b,20));fail("Truncated input must fail");}catch(org.apache.tika.exception.TikaException expected){}
 }
 @Test public void otherMalformedGifIsNotSilenced()throws Exception {
  byte[] b=fixture("1");b[25]='X';
  try{parse(b);fail("Unknown malformed input must fail");}catch(org.apache.tika.exception.TikaException expected){}
 }
}
