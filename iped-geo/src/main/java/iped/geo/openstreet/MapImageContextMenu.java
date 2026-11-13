package iped.geo.openstreet;
import iped.geo.localization.Messages;
import javafx.scene.web.WebView;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.concurrent.Worker;
public final class MapImageContextMenu {
 private MapImageContextMenu() {}
 static boolean allowed(String s){return s!=null && (s.startsWith("data:image/") || s.startsWith("https://") || s.startsWith("http://"));}
 public static void install(WebView view){
  view.setContextMenuEnabled(false);
  ContextMenu menu=new ContextMenu();
  view.setOnContextMenuRequested(event->{
   menu.hide();
   try {
    Object value=view.getEngine().executeScript("(function(){var el=document.elementFromPoint("+event.getX()+","+event.getY()+");if(!el)return '';if(typeof track!=='undefined' && track.markers){for(var k in track.markers){var m=track.markers[k];if(m._icon===el){var d=document.createElement('div');d.innerHTML=m.descr||'';var img=d.querySelector('img');return img?img.src:'';}}}return el.tagName==='IMG' && el.closest('.kml-popup')?el.src:'';})()");
    String src=value instanceof String?(String)value:"";
    if(!allowed(src))return;
    MenuItem open=new MenuItem(Messages.getString("mapMenu.OpenImage"));
    MenuItem copy=new MenuItem(Messages.getString("mapMenu.CopyImage"));
    open.setOnAction(e->loadImage(view,src,false));copy.setOnAction(e->loadImage(view,src,true));
    menu.getItems().setAll(open,copy);menu.show(view,event.getScreenX(),event.getScreenY());event.consume();
   }catch(RuntimeException ex){error(view);}
  });
  view.setOnMousePressed(e->menu.hide());
  view.getEngine().getLoadWorker().stateProperty().addListener((o,a,b)->{if(b==Worker.State.SCHEDULED)menu.hide();});
 }
 private static void loadImage(WebView owner,String src,boolean copy){
  final Image image;
  try{image=decodeImage(src);}catch(RuntimeException ex){error(owner);return;}
  Runnable done=()->{
   if(image.isError() || image.getWidth()<=0){error(owner);return;}
   if(copy){ClipboardContent content=new ClipboardContent();content.putImage(image);if(!Clipboard.getSystemClipboard().setContent(content))error(owner);}
   else{
    ImageView iv=new ImageView(image);iv.setPreserveRatio(true);
    ScrollPane pane=new ScrollPane(iv);pane.setPannable(true);
    Stage stage=new Stage();if(owner.getScene()!=null)stage.initOwner(owner.getScene().getWindow());
    try(java.io.InputStream icon=MapImageContextMenu.class.getResourceAsStream("/iped/geo/openstreet/marcador_normal.png")){if(icon!=null)stage.getIcons().add(new Image(icon));}catch(java.io.IOException ignored){}stage.setTitle(Messages.getString("mapMenu.ImageTitle"));
    stage.setScene(new Scene(pane,Math.min(900,Math.max(240,image.getWidth()+20)),Math.min(650,Math.max(160,image.getHeight()+20))));stage.show();
   }
  };
  if(image.getProgress()>=1)done.run();
  else image.progressProperty().addListener((o,a,b)->{if(b.doubleValue()>=1)done.run();});
 }
 static Image decodeImage(String src){
  if(src.startsWith("data:image/")){
   int comma=src.indexOf(',');
   if(comma<0 || !src.substring(0,comma).endsWith(";base64"))throw new IllegalArgumentException("Unsupported image encoding");
   byte[] bytes=java.util.Base64.getDecoder().decode(src.substring(comma+1));
   return new Image(new java.io.ByteArrayInputStream(bytes));
  }
  return new Image(src,true);
 }
 private static void error(WebView owner){Alert a=new Alert(Alert.AlertType.ERROR,Messages.getString("mapMenu.ImageError"),ButtonType.OK);a.setTitle(Messages.getString("mapMenu.ImageTitle"));a.setHeaderText(null);if(owner.getScene()!=null)a.initOwner(owner.getScene().getWindow());a.show();}
}