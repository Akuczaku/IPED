import sys, sqlite3, json
from pathlib import Path
case=Path(sys.argv[1]); rows=[]
for db in case.rglob('ocr-results.db'):
    con=sqlite3.connect(db.as_uri()+'?mode=ro',uri=True)
    try:
        for key,text in con.execute('select id,text from ocr'):
            rows.append({'id':key,'text':text,'marker':'IPEDOCRALPHA2026' in text,'polish_phrase':'Zażółć gęślą jaźń.' in text})
    finally: con.close()
passed=sum(r['marker'] and r['polish_phrase'] for r in rows)>=2
report={'status':'OCR_TEXT_CHECK_PASS' if passed else 'REVIEW','rows':rows,'scope':'Database text check for two synthetic inputs; inspect full processing log and GUI separately.'}
Path(sys.argv[2]).write_text(json.dumps(report,ensure_ascii=False,indent=2),encoding='utf-8')
print(report['status']);print('Report: '+sys.argv[2])
sys.exit(0 if passed else 1)
