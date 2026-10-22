import os, subprocess, math
ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "..")
RES = os.path.join(ROOT, "app/src/main/res")
OUT = os.path.join(ROOT, "store-listing")
CHROME = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"
BG, GRAY, DISP, ORANGE, WHITE = "#0D0D0D", "#3A3A3C", "#2C2C2E", "#FF9F0A", "#FFFFFF"

def rrect(x, y, w, h, r):
    return (f"M{x+r:g},{y:g}H{x+w-r:g}A{r:g},{r:g} 0 0 1 {x+w:g},{y+r:g}V{y+h-r:g}"
            f"A{r:g},{r:g} 0 0 1 {x+w-r:g},{y+h:g}H{x+r:g}A{r:g},{r:g} 0 0 1 {x:g},{y+h-r:g}"
            f"V{y+r:g}A{r:g},{r:g} 0 0 1 {x+r:g},{y:g}Z")

shapes = []  # (path, color, rotate pivot or None)
shapes.append((rrect(22, 22, 62, 12, 3), DISP, None))
shapes.append((rrect(64, 26, 16, 4, 2), WHITE, None))
xs = [22, 38, 54, 70]; ys = [38, 50.5, 63, 75.5]
for ri, y in enumerate(ys):
    for ci, x in enumerate(xs):
        if ri == 3 and ci == 3:
            continue
        if ri == 3 and ci == 1:
            continue
        if ri == 3 and ci == 0:
            shapes.append((rrect(22, y, 30, 10.5, 3), GRAY, None))
            continue
        shapes.append((rrect(x, y, 14, 10.5, 3), ORANGE if ci == 3 else GRAY, None))
shapes.append((rrect(64.5, 66.5, 24, 20, 6), BG, (76.5, 76.5, -9)))
shapes.append((rrect(67, 69, 19, 15, 4), ORANGE, (76.5, 76.5, -9)))

# adaptive foreground (vector drawable)
v = ['<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="108dp" android:height="108dp" android:viewportWidth="108" android:viewportHeight="108">']
for path, color, rot in shapes:
    p = f'<path android:fillColor="{color}" android:pathData="{path}"/>'
    if rot:
        p = f'<group android:pivotX="{rot[0]}" android:pivotY="{rot[1]}" android:rotation="{rot[2]}">{p}</group>'
    v.append("  " + p)
v.append("</vector>")
os.makedirs(os.path.join(RES, "drawable"), exist_ok=True)
open(os.path.join(RES, "drawable/ic_launcher_foreground.xml"), "w").write("\n".join(v) + "\n")

# monochrome layer: same silhouette, single colour
m = [l.replace(WHITE, "#000000").replace(DISP, "#000000").replace(GRAY, "#000000").replace(ORANGE, "#000000") for l in v if BG not in l]
open(os.path.join(RES, "drawable/ic_launcher_monochrome.xml"), "w").write("\n".join(m) + "\n")

os.makedirs(os.path.join(RES, "mipmap-anydpi-v26"), exist_ok=True)
icon = ('<?xml version="1.0" encoding="utf-8"?>\n<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">\n'
        '    <background android:drawable="@color/ic_launcher_background"/>\n'
        '    <foreground android:drawable="@drawable/ic_launcher_foreground"/>\n'
        '    <monochrome android:drawable="@drawable/ic_launcher_monochrome"/>\n</adaptive-icon>\n')
for n in ("ic_launcher.xml", "ic_launcher_round.xml"):
    open(os.path.join(RES, "mipmap-anydpi-v26", n), "w").write(icon)
open(os.path.join(RES, "values/ic_launcher_colors.xml"), "w").write(
    '<?xml version="1.0" encoding="utf-8"?>\n<resources>\n    <color name="ic_launcher_background">%s</color>\n</resources>\n' % BG)

# 512 PNG for Play (full square, no rounded corners)
def svg_el(path, color, rot):
    t = f' transform="rotate({rot[2]} {rot[0]} {rot[1]})"' if rot else ""
    return f'<path d="{path}" fill="{color}"{t}/>'
svg = (f'<svg xmlns="http://www.w3.org/2000/svg" width="512" height="512" viewBox="14 14 80 80">'
       f'<rect x="0" y="0" width="108" height="108" fill="{BG}"/>' + "".join(svg_el(*s) for s in shapes) + "</svg>")
html = f'<!DOCTYPE html><html><body style="margin:0;background:{BG}">{svg}</body></html>'
hp = os.path.join(OUT, "tools", "icon.html")
open(hp, "w").write(html)
subprocess.run([CHROME, "--headless=new", "--disable-gpu", "--hide-scrollbars", "--force-device-scale-factor=1",
                "--window-size=512,512", f"--screenshot={os.path.join(OUT, 'app-icon-512.png')}", "file://" + os.path.abspath(hp)],
               check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
print("done")
