package iped.app.graph;
import java.lang.reflect.*;import iped.app.ui.App;import iped.engine.core.Manager;
public class GraphPreviewRegression {
 static sun.misc.Unsafe u()throws Exception{Field f=sun.misc.Unsafe.class.getDeclaredField("theUnsafe");f.setAccessible(true);return(sun.misc.Unsafe)f.get(null);}
 static class Probe extends AppGraphAnalytics {boolean disabled; public void setEnabled(boolean x){disabled=!x;} }
 public static void main(String[] args)throws Exception{
 App app=(App)u().allocateInstance(App.class);Manager manager=(Manager)u().allocateInstance(Manager.class);manager.setProcessingFinished(false);
 Field m=App.class.getDeclaredField("processingManager");m.setAccessible(true);m.set(app,manager);
 Field global=App.class.getDeclaredField("app");global.setAccessible(true);Object old=global.get(null);global.set(null,app);
 try{Probe probe=(Probe)u().allocateInstance(Probe.class);LoadGraphDatabaseWorker worker=new LoadGraphDatabaseWorker(probe);worker.doInBackground();Field pending=LoadGraphDatabaseWorker.class.getDeclaredField("awaitingProcessing");pending.setAccessible(true);if(!pending.getBoolean(worker)||!probe.disabled)throw new AssertionError("Processing preview must defer graph initialization");System.out.println("PASS: active processing defers graph before configuration/service/database access");}finally{global.set(null,old);}System.exit(0);
 }
}
