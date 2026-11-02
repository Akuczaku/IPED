import java.nio.file.*;
import jep.*;
public class JepPortableTest {
 public static void main(String[] args) throws Exception {
  Path app=Path.of(args[0]).toRealPath();
  Path py=app.resolve("python").toRealPath();
  Path nativeLib=py.resolve("Lib/site-packages/jep/jep.dll");
  if(!Files.isRegularFile(nativeLib))throw new IllegalStateException("Missing bundled jep.dll");
  MainInterpreter.setJepLibraryPath(nativeLib.toString());
  MainInterpreter.setInitParams(new PyConfig().setPythonHome(py.toString()).setNoUserSiteDirectory(1).setIgnoreEnvironmentFlag(1));
  SharedInterpreter.setConfig(new JepConfig().addIncludePaths(py.resolve("Lib/site-packages").toString()));
  try(SharedInterpreter j=new SharedInterpreter()) {
   j.set("expected_home",py.toString());
   j.set("polish_text","Zażółć gęślą jaźń — Łódź");
   j.exec("import sys, os, sqlite3, json\n"
    +"assert os.path.samefile(sys.prefix, expected_home), (sys.prefix, expected_home)\n"
    +"assert sys.version_info[:2] == (3, 9), sys.version\n"
    +"assert 6 * 7 == 42\n"
    +"assert polish_text.encode('utf-8').decode('utf-8') == polish_text\n"
    +"db = sqlite3.connect(':memory:')\n"
    +"db.execute('create table test(value text)')\n"
    +"db.execute('insert into test values (?)', (polish_text,))\n"
    +"assert db.execute('select value from test').fetchone()[0] == polish_text\n"
    +"db.close()\n"
    +"from java.lang import StringBuilder\n"
    +"assert str(StringBuilder().append(polish_text).toString()) == polish_text\n"
    +"report = json.dumps({'status':'PASS', 'python':sys.version, 'prefix':sys.prefix, 'unicode':polish_text, 'sqlite':sqlite3.sqlite_version, 'checks':['bundled Python home','Python 3.9','arithmetic','UTF-8 round trip','SQLite memory database','Python to Java call']}, ensure_ascii=False, indent=2)\n");
   Files.writeString(Path.of(args[1]),j.getValue("report",String.class));
  }
  System.out.println("PASS: bundled Python/JEP, UTF-8, SQLite, Python-to-Java call.");
 }
}
