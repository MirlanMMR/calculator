import os, shutil, subprocess

HERE = os.path.dirname(os.path.abspath(__file__))
SHOTS = os.path.join(HERE, "shots")
OUT = os.path.join(HERE, "..", "mockups")
ICON = os.path.join(HERE, "..", "app-icon-512.png")
CHROME = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"

# (file, gradient from, gradient to, glow, RU title, RU sub, EN title, EN sub)
ITEMS = [
    ("01", "#8a5200", "#1a0f00", "#ffb347", "Ваши клавиши, ваши правила", "Калькулятор под вашу руку",
     "Your keys, your rules", "A calculator built around your hand"),
    ("02", "#14707c", "#06232b", "#19c3c9", "Переставьте любую клавишу", "Тяните, убирайте, возвращайте",
     "Move any key", "Drag, take off, put back"),
    ("03", "#5a45a8", "#14102b", "#a38ae8", "Выберите стиль", "16 тем, 5 форм и 6 раскладок",
     "Pick your style", "16 themes, 5 shapes, 6 layouts"),
    ("04", "#1fa37a", "#073b2b", "#8fe3c6", "Наука по требованию", "fx: корень, sin, cos, ln и другие",
     "Scientific on demand", "Tap fx for root, sin, cos, ln and more"),
    ("05", "#3b8f7a", "#0b2f27", "#9fe0cf", "Все результаты сохранены", "Тап вставляет, удержание копирует",
     "Every result saved", "Tap to reuse, hold to copy"),
    ("06", "#0a8a52", "#021a0f", "#00ff88", "Любая форма", "Шестиугольники, круги, квадраты",
     "Any shape you like", "Hexagons, circles, squares"),
    ("07", "#c04a3a", "#2a0f1c", "#ff9a7a", "Операторы как удобно", "Слева или справа, ноль сверху или снизу",
     "Operators your way", "Left or right, zero on top or bottom"),
    ("08", "#14808a", "#03161a", "#5be0ea", "Несколько экранов сразу", "Смахните дисплей, цифры правит курсор",
     "Several screens at once", "Swipe the display, fix digits with the cursor"),
]
NAMES = {"01": "layout", "02": "constructor", "03": "style", "04": "functions", "05": "history", "06": "shapes", "07": "operators", "08": "screens"}

CSS = """
*{box-sizing:border-box;margin:0;padding:0}
body{width:1080px;height:1920px;overflow:hidden;font-family:-apple-system,"SF Pro Display","Helvetica Neue",Arial,sans-serif;position:relative;color:#fff}
.glow{position:absolute;border-radius:50%}
.cap{position:absolute;left:60px;right:60px;top:78px;text-align:center}
.cap h1{font-size:62px;line-height:1.08;font-weight:800;letter-spacing:-0.5px}
.cap p{margin-top:18px;font-size:34px;line-height:1.25;font-weight:500;opacity:.9}
.phone{position:absolute;left:190px;top:330px;width:700px;height:1560px;border-radius:84px;background:#0a0b0c;padding:15px;
 box-shadow:0 40px 90px rgba(0,0,0,.55), inset 0 0 0 2px #2b2e31}
.phone:before{content:"";position:absolute;right:-7px;top:260px;width:7px;height:120px;border-radius:0 5px 5px 0;background:#23262a}
.phone:after{content:"";position:absolute;right:-7px;top:420px;width:7px;height:190px;border-radius:0 5px 5px 0;background:#23262a}
.screen{position:relative;width:100%;height:100%;border-radius:70px;overflow:hidden;background:#000}
.screen img{display:block;width:100%;height:100%;object-fit:cover;object-position:top}
.hole{position:absolute;left:50%;top:20px;width:20px;height:20px;margin-left:-10px;border-radius:50%;background:#050506;box-shadow:0 0 0 3px rgba(0,0,0,.35)}
"""

def page(item, lang):
    n, c1, c2, glow, rt, rs, et, es = item
    title, sub = (rt, rs) if lang == "ru" else (et, es)
    return f"""<!DOCTYPE html><html><head><meta charset="utf-8"><style>{CSS}
body{{background:radial-gradient(1200px 900px at 20% 0%, {c1} 0%, {c2} 70%)}}
</style></head><body>
<div class="glow" style="left:-220px;top:900px;width:620px;height:620px;background:radial-gradient(circle,{glow}55 0%,transparent 70%)"></div>
<div class="glow" style="right:-260px;top:-120px;width:760px;height:760px;background:radial-gradient(circle,{glow}40 0%,transparent 70%)"></div>
<div class="cap"><h1>{title}</h1><p>{sub}</p></div>
<div class="phone"><div class="screen"><img src="{n}.png"><div class="hole"></div></div></div>
</body></html>"""

def shot(html_path, png_path, w, h):
    subprocess.run([CHROME, "--headless=new", "--disable-gpu", "--hide-scrollbars", "--force-device-scale-factor=1",
                    f"--window-size={w},{h}", f"--screenshot={png_path}", "file://" + html_path],
                   check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)

work = os.path.join(HERE, "work"); os.makedirs(work, exist_ok=True)
for it in ITEMS:
    shutil.copy(os.path.join(SHOTS, it[0] + ".png"), os.path.join(work, it[0] + ".png"))
shutil.copy(ICON, os.path.join(work, "icon.png"))
for lang in ("en",):
    os.makedirs(os.path.join(OUT, lang), exist_ok=True)
    for it in ITEMS:
        h = os.path.join(work, f"{lang}-{it[0]}.html")
        open(h, "w").write(page(it, lang))
        shot(h, os.path.join(OUT, lang, f"{it[0]}-{NAMES[it[0]]}.png"), 1080, 1920)

card = """<!DOCTYPE html><html><head><meta charset="utf-8"><style>
*{box-sizing:border-box;margin:0;padding:0}
body{width:1024px;height:500px;overflow:hidden;font-family:-apple-system,"SF Pro Display","Helvetica Neue",Arial,sans-serif;color:#fff;position:relative;
 background:radial-gradient(900px 600px at 15% 20%, #3a2a10 0%, #0d0d0d 75%)}
.glow{position:absolute;border-radius:50%}
.icon{position:absolute;left:64px;top:70px;width:170px;height:170px;border-radius:40px;overflow:hidden;box-shadow:0 18px 40px rgba(0,0,0,.5)}
.icon img{width:100%;height:100%;display:block}
.t{position:absolute;left:64px;top:262px;width:560px}
.t h1{font-size:54px;font-weight:800;letter-spacing:-1px;line-height:1.05}
.t p{margin-top:14px;font-size:27px;line-height:1.3;font-weight:500;opacity:.92}
.t p+p{margin-top:6px;opacity:.7;font-size:24px}
.ph{position:absolute;width:250px;height:520px;border-radius:36px;background:#0a0b0c;padding:7px;box-shadow:0 20px 50px rgba(0,0,0,.55), inset 0 0 0 1.5px #2b2e31}
.ph .s{width:100%;height:100%;border-radius:29px;overflow:hidden}
.ph img{display:block;width:100%;height:100%;object-fit:cover;object-position:top}
</style></head><body>
<div class="glow" style="right:-120px;top:-160px;width:560px;height:560px;background:radial-gradient(circle,#ff9f0a55 0%,transparent 70%)"></div>
<div class="icon"><img src="icon.png"></div>
<div class="t"><h1>Calculator: Themes &amp; History</h1><p>Move keys, pick themes and shapes.</p><p>No ads. No accounts. No internet.</p></div>
<div class="ph" style="left:640px;top:70px;transform:rotate(-6deg)"><div class="s"><img src="06.png"></div></div>
<div class="ph" style="left:790px;top:120px;transform:rotate(5deg)"><div class="s"><img src="07.png"></div></div>
</body></html>"""
h = os.path.join(work, "card.html"); open(h, "w").write(card)
shot(h, os.path.join(OUT, "feature-graphic-1024x500.png"), 1024, 500)
print("built")
