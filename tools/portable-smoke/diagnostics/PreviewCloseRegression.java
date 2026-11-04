import java.lang.reflect.*;import java.util.*;import javax.swing.*;import bibliothek.gui.dock.common.DefaultSingleCDockable;import iped.app.ui.App;
public class PreviewCloseRegression {
 static sun.misc.Unsafe unsafe() throws Exception{Field f=sun.misc.Unsafe.class.getDeclaredField("theUnsafe");f.setAccessible(true);return (sun.misc.Unsafe)f.get(null);}
 static void set(App a,String field,Object v)throws Exception{Field f=App.class.getDeclaredField(field);f.setAccessible(true);f.set(a,v);}
 static void close(App a)throws Exception{Method m=App.class.getDeclaredMethod("removeAllDockables");m.setAccessible(true);m.invoke(a);}
 public static void main(String[] args)throws Exception{
 App partial=(App)unsafe().allocateInstance(App.class);
 close(partial);
 List<DefaultSingleCDockable> docks=new ArrayList<>();docks.add(new DefaultSingleCDockable("empty"));docks.add(new DefaultSingleCDockable("filled",new JPanel()));
 set(partial,"viewerDocks",docks);set(partial,"rsTabDock",new ArrayList<DefaultSingleCDockable>());
 close(partial);close(partial);
 System.out.println("PASS: close before viewer initialization, empty/populated docks and repeated cleanup"); System.exit(0);
 }
}

