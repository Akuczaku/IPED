from pathlib import Path
from PIL import Image, ImageDraw, ImageFont
from reportlab.pdfgen import canvas
from reportlab.lib.utils import ImageReader
from pypdf import PdfReader
import json
root=Path(__file__).parent
lines=['IPED OCR - DANE FIKCYJNE','IPEDOCRALPHA2026','Zażółć gęślą jaźń.','Łódź, źródło, ćma, śnieg.','Ą Ć Ę Ł Ń Ó Ś Ź Ż','ą ć ę ł ń ó ś ź ż','Numer testu: 1234567890','Koniec testu OCR.']
im=Image.new('RGB',(2480,3508),'white')
d=ImageDraw.Draw(im)
font=ImageFont.truetype('C:/Windows/Fonts/arial.ttf',64)
heading=ImageFont.truetype('C:/Windows/Fonts/arialbd.ttf',72)
for i,line in enumerate(lines):
    d.text((200,260+i*190),line,font=heading if i==0 else font,fill='black')
png=root/'INPUT'/'OCR-PL-obraz.png'
im.save(png,dpi=(300,300))
pdf=root/'INPUT'/'OCR-PL-skan.pdf'
c=canvas.Canvas(str(pdf),pagesize=(595.2,841.92),pageCompression=1)
c.drawImage(ImageReader(im),0,0,width=595.2,height=841.92)
c.showPage();c.save()
r=PdfReader(pdf)
assert len(r.pages)==1
assert not (r.pages[0].extract_text() or '').strip()
assert len(r.pages[0].images)==1
extracted=r.pages[0].images[0].image.convert('RGB')
assert extracted.size==im.size
assert extracted.tobytes()==im.tobytes()
extracted.thumbnail((710,1004))
extracted.save(root/'qa'/'preview.png')
(root/'EXPECTED.txt').write_text('\n'.join(lines)+'\n',encoding='utf-8')
(root/'verification.json').write_text(json.dumps({'pdf_pages':1,'embedded_images':1,'extractable_text':'','image_pixels_match_source':True,'image_dpi':300,'synthetic':True},indent=2),encoding='utf-8')
print('PASS: scanned PDF has one image, no text layer; embedded pixels match PNG.')
