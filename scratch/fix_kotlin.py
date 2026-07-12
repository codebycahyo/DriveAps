import os
import re

java_dir = 'c:/Driveapps/app/src/main/java'

for root, _, files in os.walk(java_dir):
    for file in files:
        if file.endswith('.kt'):
            path = os.path.join(root, file)
            with open(path, 'r', encoding='utf-8') as f:
                content = f.read()
                
            new_content = content.replace('R.dimen.space_', 'R.dimen.spacing_')
            
            if new_content != content:
                print(f"Fixed {file}")
                with open(path, 'w', encoding='utf-8') as f:
                    f.write(new_content)

# Add missing colors
def append_colors(filepath):
    missing_colors = {
        'status_neutral_bg': '@color/surface_variant',
        'status_teal_bg': '@color/surface_variant',
        'status_teal_text': '@color/text_primary',
        'reminder_standard_accent': '@color/icon_secondary',
        'reminder_urgent_title': '@color/text_primary',
        'reminder_standard_title': '@color/text_primary',
        'reminder_standard_subtitle': '@color/text_secondary'
    }
    
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
        
    mapping = ""
    for k, v in missing_colors.items():
        if f'name="{k}"' not in content:
            mapping += f'    <color name="{k}">{v}</color>\n'
            
    if mapping:
        new_content = content.replace('</resources>', mapping + '</resources>')
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(new_content)

append_colors('c:/Driveapps/app/src/main/res/values/colors.xml')
append_colors('c:/Driveapps/app/src/main/res/values-night/colors.xml')
