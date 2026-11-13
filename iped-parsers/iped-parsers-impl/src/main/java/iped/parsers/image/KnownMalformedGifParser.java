package iped.parsers.image;

import java.io.*;
import java.security.*;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.image.ImageParser;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/** Narrow, explicit recovery for one byte-identical malformed public Nmap GIF. */
public class KnownMalformedGifParser extends ImageParser {
 private static final long serialVersionUID=1L;
 private static final String HASH="38c81820ef65fe7a518846979199f868bf5c4c1c15f40cae7c7681f946853b64";
 public static final String RECOVERY="iped:gifRecovery";
 public static final String DETAIL="Known Nmap 1x1 GIF: comment length at offset 21 corrected from 30 to 11 in parser memory only; original bytes unchanged";
 @Override public void parse(InputStream source,ContentHandler handler,Metadata metadata,ParseContext context)
         throws IOException,SAXException,TikaException {
  InputStream input=source.markSupported()?source:new BufferedInputStream(source);
  if("image/gif".equals(metadata.get(Metadata.CONTENT_TYPE))){
   input.mark(52);byte[] probe=new byte[52];int n=0,read;
   while(n<probe.length&&(read=input.read(probe,n,probe.length-n))!=-1){if(read==0)break;n+=read;}
   input.reset();
   if(n==51&&HASH.equals(sha256(probe,n))){
    byte[] repaired=java.util.Arrays.copyOf(probe,n);repaired[21]=11;
    super.parse(new ByteArrayInputStream(repaired),handler,metadata,context);
    metadata.set(RECOVERY,DETAIL);
    metadata.set("iped:originalMalformed","true");
    LoggerFactory.getLogger(KnownMalformedGifParser.class).warn(DETAIL);
    return;
   }
  }
  super.parse(input,handler,metadata,context);
 }
 private static String sha256(byte[] bytes,int length)throws TikaException {
  try{MessageDigest d=MessageDigest.getInstance("SHA-256");d.update(bytes,0,length);StringBuilder b=new StringBuilder();for(byte v:d.digest())b.append(String.format("%02x",v&255));return b.toString();}
  catch(NoSuchAlgorithmException e){throw new TikaException("SHA-256 unavailable",e);}
 }
}
