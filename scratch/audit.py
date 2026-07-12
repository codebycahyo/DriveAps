import os
import xml.etree.ElementTree as ET
from collections import defaultdict

layout_dir = 'c:/Driveapps/app/src/main/res/layout'
files = [f for f in os.listdir(layout_dir) if f.endswith('.xml')]

radius_used = defaultdict(list)
elevation_used = defaultdict(list)
spacing_used = defaultdict(list)
icon_size_used = defaultdict(list)
colors_used = defaultdict(list)
components_with_no_style = defaultdict(list)

def extract_values(file_path, file_name):
    tree = ET.parse(file_path)
    root = tree.getroot()
    for elem in root.iter():
        tag = elem.tag.split('.')[-1]
        
        # radius
        if 'app:cardCornerRadius' in elem.attrib:
            radius_used[elem.attrib['app:cardCornerRadius']].append((file_name, tag))
        if 'app:boxCornerRadiusTopStart' in elem.attrib:
            radius_used[elem.attrib['app:boxCornerRadiusTopStart']].append((file_name, tag))
            
        # elevation
        if 'app:cardElevation' in elem.attrib:
            elevation_used[elem.attrib['app:cardElevation']].append((file_name, tag))
        if '{http://schemas.android.com/apk/res/android}elevation' in elem.attrib:
            elevation_used[elem.attrib['{http://schemas.android.com/apk/res/android}elevation']].append((file_name, tag))

        # spacing
        for attr in ['layout_margin', 'layout_marginStart', 'layout_marginEnd', 'layout_marginTop', 'layout_marginBottom',
                     'padding', 'paddingStart', 'paddingEnd', 'paddingTop', 'paddingBottom']:
            ns_attr = '{http://schemas.android.com/apk/res/android}' + attr
            if ns_attr in elem.attrib:
                spacing_used[elem.attrib[ns_attr]].append((file_name, tag, attr))
                
        # icon size & normal size
        for attr in ['layout_width', 'layout_height']:
            ns_attr = '{http://schemas.android.com/apk/res/android}' + attr
            if ns_attr in elem.attrib:
                val = elem.attrib[ns_attr]
                if tag in ['ImageView', 'ShapeableImageView', 'ImageButton'] and val not in ['wrap_content', 'match_parent', '0dp']:
                    icon_size_used[val].append((file_name, tag, attr))
                    
        # colors
        for attr in ['background', 'backgroundTint', 'textColor', 'tint', 'strokeColor']:
            ns_attr = '{http://schemas.android.com/apk/res/android}' + attr
            if ns_attr in elem.attrib:
                val = elem.attrib[ns_attr]
                if val.startswith('#') or val.startswith('@color/'):
                    colors_used[val].append((file_name, tag, attr))
            app_attr = '{http://schemas.android.com/apk/res-auto}' + attr
            if app_attr in elem.attrib:
                val = elem.attrib[app_attr]
                if val.startswith('#') or val.startswith('@color/'):
                    colors_used[val].append((file_name, tag, attr))

for f in files:
    extract_values(os.path.join(layout_dir, f), f)

with open('c:/Driveapps/scratch/audit_report.txt', 'w') as out:
    out.write("RADIUS USED:\n")
    for k, v in sorted(radius_used.items()):
        out.write(f"{k}: {len(v)} occurrences. e.g. {v[:5]}\n")
        
    out.write("\nELEVATION USED:\n")
    for k, v in sorted(elevation_used.items()):
        out.write(f"{k}: {len(v)} occurrences. e.g. {v[:5]}\n")
        
    out.write("\nSPACING USED:\n")
    for k, v in sorted(spacing_used.items()):
        out.write(f"{k}: {len(v)} occurrences. e.g. {v[:5]}\n")
        
    out.write("\nICON SIZES USED:\n")
    for k, v in sorted(icon_size_used.items()):
        out.write(f"{k}: {len(v)} occurrences. e.g. {v[:5]}\n")
        
    out.write("\nCOLORS USED:\n")
    for k, v in sorted(colors_used.items()):
        out.write(f"{k}: {len(v)} occurrences. e.g. {v[:5]}\n")
