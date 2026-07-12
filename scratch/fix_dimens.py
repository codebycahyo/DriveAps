import os
import re

layout_dir = 'c:/Driveapps/app/src/main/res/layout'

replacements = {
    '@dimen/spacing_13': '@dimen/spacing_12',
    '@dimen/spacing_210': '210dp',
    '@dimen/spacing_6': '@dimen/spacing_8',
    '@dimen/spacing_10': '@dimen/spacing_8'
}

for root, _, files in os.walk(layout_dir):
    for file in files:
        if file.endswith('.xml'):
            path = os.path.join(root, file)
            with open(path, 'r', encoding='utf-8') as f:
                content = f.read()
                
            new_content = content
            for old, new in replacements.items():
                new_content = new_content.replace(old, new)
                
            if new_content != content:
                with open(path, 'w', encoding='utf-8') as f:
                    f.write(new_content)
