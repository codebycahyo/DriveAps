import os
import re

allowed_colors = {
    'surface_primary', 'surface_variant', 'text_primary', 'text_secondary',
    'icon_primary', 'icon_secondary', 'color_cta_primary', 'brand_ink',
    'white', 'neutral_50', 'neutral_500', 
    'transparent', 'black'
}

# we also need to allow any color defined in colors.xml
colors_xml_path = 'c:/Driveapps/app/src/main/res/values/colors.xml'
with open(colors_xml_path, 'r', encoding='utf-8') as f:
    content = f.read()
    defined = re.findall(r'<color name="([^"]+)">', content)
    allowed_colors.update(defined)

res_dir = 'c:/Driveapps/app/src/main/res'
found_colors = set()

for root, _, files in os.walk(res_dir):
    for file in files:
        if file.endswith('.xml'):
            path = os.path.join(root, file)
            with open(path, 'r', encoding='utf-8') as f:
                content = f.read()
                matches = re.findall(r'@color/([a-zA-Z0-9_]+)', content)
                for m in matches:
                    if m not in allowed_colors and not m.startswith('material_') and not m.startswith('design_') and not m.startswith('abc_'):
                        found_colors.add(m)

print("Missing colors:", found_colors)
