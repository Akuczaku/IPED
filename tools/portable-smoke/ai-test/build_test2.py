"""Assemble test2 from the pinned, previously verified AI ZIP (Python 3.12+)."""
import argparse,hashlib,json,pathlib,zipfile
p=argparse.ArgumentParser();p.add_argument('--base',type=pathlib.Path,required=True);p.add_argument('--output',type=pathlib.Path,required=True);p.add_argument('--fixtures',type=pathlib.Path,required=True);a=p.parse_args()
assert hashlib.file_digest(a.base.open('rb'),'sha256').hexdigest()=='79e261c8c3a96f5e14c5548c46c74d6e9f33d161b7fdde797ad2d7058c98cb80','Wrong base ZIP'
assert not a.output.exists(),'Refusing overwrite'
stage=pathlib.Path(__file__).resolve().parent/'package-files'
overlay={f.relative_to(stage).as_posix():f.read_bytes() for f in stage.rglob('*') if f.is_file()}
expected={'01-fikcyjne-twarze.mp4','02-kontrola-bez-twarzy.mp4','03-obraz-wzorcowy.png','04-te-same-osoby-inne-zdjecie.png','11-dom-baza.png','12-dom-podobny.jpg','13-dom-mniejszy.png','14-inny-wzor.png','OCR-PL-obraz.png','OCR-PL-skan.pdf'}
assert {f.name for f in a.fixtures.iterdir() if f.is_file()}==expected,'Unexpected fixture set'
fixture_hashes={}
for name in sorted(expected):
 data=(a.fixtures/name).read_bytes();overlay['DANE-TESTOWE/INPUT/'+name]=data;fixture_hashes[name]=hashlib.sha256(data).hexdigest()
overlay['DANE-TESTOWE/SHA256-INPUT.json']=(json.dumps(fixture_hashes,indent=2)+'\n').encode()
with zipfile.ZipFile(a.base) as z:
 info=json.loads(z.read('IPED-PL/BUILD-INFO.json'));info.update(version='IPED Polish Edition 4.3.1 PORTABLE AI OCR PL TEST2',base_archive=a.base.name,base_sha256='79e261c8c3a96f5e14c5548c46c74d6e9f33d161b7fdde797ad2d7058c98cb80')
 info['ai']['video_face_test']='User GUI: static-frame synthetic MP4 matched 76%; no-face MP4 excluded; selected-one-face overlay confirmed'
 info['new_archive_gui_test']='Pending: test2 one-click launcher and second computer; base AI archive manually processed test fixtures successfully per user'
 info['fixtures']='10 synthetic test files; no user cases or private logs'
 overlay['BUILD-INFO.json']=(json.dumps(info,indent=2)+'\n').encode()
 state=z.read('IPED-PL/STAN-WERSJI.txt').decode('utf-8-sig')
 state+='\nTEST2 2026-11-03: GUI uzytkownika potwierdzilo nowa paczke AI na zdjeciach\ni filmie ze statycznych obrazow (76%); jedna wybrana twarz zaznaczana\nw klatkach, kontrola bez twarzy pominieta. Dodano osobne fikcyjne dane\ni Uruchom-test.cmd. Nowy start jednym kliknieciem i drugi komputer\npozostaja do potwierdzenia. Pozostale moduly AI nadal wylaczone.\n'
 overlay['STAN-WERSJI.txt']=state.encode()
 for key in ['enableOCR','enableImageSimilarity','enableFaceRecognition','enableVideoThumbs','enableHash']:
  cfg=z.read('IPED-PL/iped-4.3.1/IPEDConfig.txt').decode('utf-8-sig');assert key+' = true' in cfg,key
 assert 'OCRLanguage = pol' in z.read('IPED-PL/iped-4.3.1/conf/OCRConfig.txt').decode()
 assert 'sha-256' in z.read('IPED-PL/iped-4.3.1/conf/HashTaskConfig.txt').decode()
manifest={}
with zipfile.ZipFile(a.base) as src,zipfile.ZipFile(a.output,'w',zipfile.ZIP_DEFLATED,compresslevel=1) as dst:
 for item in src.infolist():
  name=item.filename.removeprefix('IPED-PL/')
  if item.is_dir() or name=='SHA256SUMS.txt' or name in overlay:continue
  data=src.read(item);dst.writestr('IPED-PL/'+name,data);manifest[name]=hashlib.sha256(data).hexdigest()
 for name,data in overlay.items():dst.writestr('IPED-PL/'+name,data);manifest[name]=hashlib.sha256(data).hexdigest()
 dst.writestr('IPED-PL/SHA256SUMS.txt',''.join(h+'  '+n+'\n' for n,h in sorted(manifest.items())))
with zipfile.ZipFile(a.output) as z:
 assert len(z.infolist())==len(manifest)+1
 for name,h in manifest.items():assert hashlib.sha256(z.read('IPED-PL/'+name)).hexdigest()==h,name
 for name,h in info['jar_sha256'].items():assert manifest['iped-4.3.1/'+name]==h
sha=hashlib.file_digest(a.output.open('rb'),'sha256').hexdigest();pathlib.Path(str(a.output)+'.sha256').write_text(sha+'  '+a.output.name+'\n',encoding='ascii')
print('PASS:',len(manifest),'files; full SHA256 verification; enabled OCR/image similarity/faces/video thumbs/hash.');print('ZIP bytes:',a.output.stat().st_size);print('SHA256:',sha)
