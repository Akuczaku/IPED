package iped.parsers.ocr;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;
import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.*;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.xml.sax.*;
import iped.parsers.standard.StandardParser;
import iped.utils.ImageUtil;

/** Opt-in diagnostic wrapper; the original JPEG and base OCR implementation remain unchanged. */
public class LargeJpegOCRParser extends OCRParser {
 private static final long serialVersionUID=1L;
 public static final String ENABLE="iped.ocr.resizeLargeJpeg";
 public static final String TRANSFORM="ocr:inputTransform";
 static boolean shouldResize(String mime,int w,int h,boolean enabled){return enabled&&"image/jpeg".equals(mime)&&Math.max(w,h)>6000;}
 static File prepare(File source,TemporaryResources tmp,Metadata metadata)throws IOException {
  ImageReader reader=null;
  try(ImageInputStream in=ImageIO.createImageInputStream(source)){
   if(in==null)throw new IOException("Cannot open JPEG for bounded OCR conversion");
   Iterator<ImageReader> readers=ImageIO.getImageReaders(in);
   if(!readers.hasNext())throw new IOException("No JPEG image reader available");
   reader=readers.next();reader.setInput(in,true,true);
   int w=reader.getWidth(0),h=reader.getHeight(0);
   if(!shouldResize("image/jpeg",w,h,true))return source;
   ImageReadParam param=reader.getDefaultReadParam();
   int sample=Math.max(1,(int)Math.ceil(Math.max(w,h)/6000.0));
   param.setSourceSubsampling(sample,sample,0,0);
   BufferedImage decoded=reader.read(0,param);
   if(decoded==null)throw new IOException("JPEG reader returned no image");
   BufferedImage scaled=null;
   try{
    scaled=ImageUtil.resizeImage(decoded,3000,3000,BufferedImage.TYPE_3BYTE_BGR);
    File result=tmp.createTemporaryFile();
    if(!ImageIO.write(scaled,"png",result))throw new IOException("PNG writer unavailable");
    metadata.set(TRANSFORM,"JPEG OCR copy: "+w+"x"+h+" -> "+scaled.getWidth()+"x"+scaled.getHeight()+"; source subsampling="+sample+"; original bytes unchanged");
    metadata.set("ocr:inputDownsampled","true");
    return result;
   }finally{if(scaled!=null&&scaled!=decoded)scaled.flush();decoded.flush();}
  }finally{if(reader!=null)reader.dispose();}
 }
 @Override public void parse(InputStream stream,ContentHandler handler,Metadata metadata,ParseContext context)throws IOException,SAXException,TikaException {
  if(!Boolean.getBoolean(ENABLE)||!Boolean.parseBoolean(System.getProperty(ENABLE_PROP,"false"))||!"image/jpeg".equals(metadata.get(StandardParser.INDEXER_CONTENT_TYPE))){super.parse(stream,handler,metadata,context);return;}
  try(TemporaryResources tmp=new TemporaryResources()){
   TikaInputStream input=TikaInputStream.get(stream,tmp);
   File source=input.getFile();File candidate=prepare(source,tmp,metadata);
   if(candidate==source){super.parse(input,handler,metadata,context);}
   else{try(InputStream reduced=new FileInputStream(candidate)){super.parse(reduced,handler,metadata,context);}}
  }
 }
}
