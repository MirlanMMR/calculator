import os, subprocess, math
ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "..")
RES = os.path.join(ROOT, "app/src/main/res")
OUT = os.path.join(ROOT, "store-listing")
CHROME = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"
BG, INK = "#0D0D0D", "#0D0D0D"
ORANGE, CORAL, TEAL, PURPLE = "#FF9F0A", "#FF6B5A", "#2EC4B6", "#8E44D6"

def rrect(x, y, w, h, r):
    return (f"M{x+r:g},{y:g}H{x+w-r:g}A{r:g},{r:g} 0 0 1 {x+w:g},{y+r:g}V{y+h-r:g}"
            f"A{r:g},{r:g} 0 0 1 {x+w-r:g},{y+h:g}H{x+r:g}A{r:g},{r:g} 0 0 1 {x:g},{y+h-r:g}"
            f"V{y+r:g}A{r:g},{r:g} 0 0 1 {x+r:g},{y:g}Z")
def circle(cx, cy, r):
    return f"M{cx-r:g},{cy:g}A{r:g},{r:g} 0 1 0 {cx+r:g},{cy:g}A{r:g},{r:g} 0 1 0 {cx-r:g},{cy:g}Z"
def hexagon(cx, cy, r):
    pts = [(cx + r * math.cos(math.radians(a)), cy + r * math.sin(math.radians(a))) for a in (-90, -30, 30, 90, 150, 210)]
    return "M" + "L".join(f"{x:.2f},{y:.2f}" for x, y in pts) + "Z"

S, GAP = 30, 5
x0, y0 = 54 - S - GAP / 2, 54 - S - GAP / 2
cx = [x0 + S / 2, x0 + S + GAP + S / 2]
cy = [y0 + S / 2, y0 + S + GAP + S / 2]

keys, symbols = [], []   # (path, color, rot)
keys.append((hexagon(cx[0], cy[0], 17.2), ORANGE, None))
keys.append((circle(cx[1], cy[0], 15.5), CORAL, None))
keys.append((rrect(cx[0] - S / 2, cy[1] - S / 2, S, S, 8), TEAL, None))
keys.append((rrect(cx[1] - S / 2, cy[1] - S / 2, S, S, 2), PURPLE, None))

T, L = 5, 17   # stroke thickness, symbol length
def bar(x, y, w, h, rot=None):
    return (rrect(x - w / 2, y - h / 2, w, h, 1.2), INK, rot)
symbols += [bar(cx[0], cy[0], L, T), bar(cx[0], cy[0], T, L)]                      # plus
symbols += [bar(cx[1], cy[0], L, T)]                                                # minus
symbols += [bar(cx[0], cy[1], L + 2, T, (cx[0], cy[1], 45)), bar(cx[0], cy[1], L + 2, T, (cx[0], cy[1], -45))]  # multiply
symbols += [bar(cx[1], cy[1], L, T), (circle(cx[1], cy[1] - 7.5, 2.9), INK, None), (circle(cx[1], cy[1] + 7.5, 2.9), INK, None)]  # divide

def vec(shapes, mono=False):
    v = ['<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="108dp" android:height="108dp" android:viewportWidth="108" android:viewportHeight="108">']
    for path, color, rot in shapes:
        c = "#000000" if mono else color
        p = f'<path android:fillColor="{c}" android:pathData="{path}"/>'
        if rot:
            p = f'<group android:pivotX="{rot[0]}" android:pivotY="{rot[1]}" android:rotation="{rot[2]}">{p}</group>'
        v.append("  " + p)
    v.append("</vector>")
    v.insert(1, '  <group android:pivotX="54" android:pivotY="54" android:scaleX="0.9" android:scaleY="0.9">')
    v.insert(len(v) - 1, "  </group>")
    return "\n".join(v) + "\n"

os.makedirs(os.path.join(RES, "drawable"), exist_ok=True)
open(os.path.join(RES, "drawable/ic_launcher_foreground.xml"), "w").write(vec(keys + symbols))
open(os.path.join(RES, "drawable/ic_launcher_monochrome.xml"), "w").write(vec(symbols, mono=True))
os.makedirs(os.path.join(RES, "mipmap-anydpi-v26"), exist_ok=True)
icon = ('<?xml version="1.0" encoding="utf-8"?>\n<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">\n'
        '    <background android:drawable="@color/ic_launcher_background"/>\n'
        '    <foreground android:drawable="@drawable/ic_launcher_foreground"/>\n'
        '    <monochrome android:drawable="@drawable/ic_launcher_monochrome"/>\n</adaptive-icon>\n')
for n in ("ic_launcher.xml", "ic_launcher_round.xml"):
    open(os.path.join(RES, "mipmap-anydpi-v26", n), "w").write(icon)
open(os.path.join(RES, "values/ic_launcher_colors.xml"), "w").write(
    '<?xml version="1.0" encoding="utf-8"?>\n<resources>\n    <color name="ic_launcher_background">%s</color>\n</resources>\n' % BG)

def svg_el(path, color, rot):
    t = f' transform="rotate({rot[2]} {rot[0]} {rot[1]})"' if rot else ""
    return f'<path d="{path}" fill="{color}"{t}/>'
svg = (f'<svg xmlns="http://www.w3.org/2000/svg" width="512" height="512" viewBox="14 14 80 80">'
       f'<rect x="0" y="0" width="108" height="108" fill="{BG}"/>' + "".join(svg_el(*s) for s in keys + symbols) + "</svg>")
hp = os.path.join(OUT, "tools", "icon.html")
open(hp, "w").write(f'<!DOCTYPE html><html><body style="margin:0;background:{BG}">{svg}</body></html>')
subprocess.run([CHROME, "--headless=new", "--disable-gpu", "--hide-scrollbars", "--force-device-scale-factor=1",
                "--window-size=512,512", f"--screenshot={os.path.join(OUT, 'app-icon-512.png')}", "file://" + os.path.abspath(hp)],
               check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
print("done")
