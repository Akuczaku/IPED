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
 Metadata parse(byte[] b)throws Exception {
  Metadata m=new Metadata();m.set(Metadata.CONTENT_TYPE,"image/gif");
  new KnownMalformedGifParser().parse(new ByteArrayInputStream(b),new BodyContentHandler(),m,new ParseContext());return m;
 }
 @Test public void bothExportedGifsRecoverAndRemainUnchanged()throws Exception {
  for(String n:new String[]{"1","2"}){
   Path p=Paths.get("C:/TEST-GIF",n,"pixel.gif");byte[] before=Files.readAllBytes(p);
   Metadata m=parse(before);assertNotNull(m.get(KnownMalformedGifParser.RECOVERY));assertEquals("true",m.get("iped:originalMalformed"));
   assertArrayEquals(before,Files.readAllBytes(p));
  }
 }
 @Test public void validGifUsesNormalParser()throws Exception {
  ByteArrayOutputStream out=new ByteArrayOutputStream();assertTrue(ImageIO.write(new BufferedImage(2,3,BufferedImage.TYPE_INT_RGB),"gif",out));
  assertNull(parse(out.toByteArray()).get(KnownMalformedGifParser.RECOVERY));
 }
 @Test public void truncatedGifStillFails()throws Exception {
  byte[] b=Files.readAllBytes(Paths.get("C:/TEST-GIF/1/pixel.gif"));
  try{parse(java.util.Arrays.copyOf(b,20));fail("Truncated input must fail");}catch(org.apache.tika.exception.TikaException expected){}
 }
 @Test public void otherMalformedGifIsNotSilenced()throws Exception {
  byte[] b=Files.readAllBytes(Paths.get("C:/TEST-GIF/1/pixel.gif"));b[25]='X';
  try{parse(b);fail("Unknown malformed input must fail");}catch(org.apache.tika.exception.TikaException expected){}
 }
}
