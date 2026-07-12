import os
import re

layout_dir = 'c:/Driveapps/app/src/main/res/layout'
dimens_file = 'c:/Driveapps/app/src/main/res/values/dimens.xml'

with open(dimens_file, 'r', encoding='utf-8') as f:
    dimens_content = f.read()

# Extract all valid spacing from dimens.xml
valid_spacings = set(re.findall(r'<dimen name="(spacing_\d+)">', dimens_content))
valid_ints = sorted([int(s.split('_')[1]) for s in valid_spacings])

def get_closest(val):
    return min(valid_ints, key=lambda x: abs(x - val))

for root, _, files in os.walk(layout_dir):
    for file in files:
        if file.endswith('.xml'):
            path = os.path.join(root, file)
            with open(path, 'r', encoding='utf-8') as f:
                content = f.read()
                
            new_content = content
            matches = set(re.findall(r'@dimen/(spacing_\d+)', content))
            
            for m in matches:
                if m not in valid_spacings:
                    val = int(m.split('_')[1])
                    closest = get_closest(val)
                    print(f"Replacing {m} with spacing_{closest} in {file}")
                    new_content = new_content.replace(f'@dimen/{m}', f'@dimen/spacing_{closest}')
                    
            if new_content != content:
                with open(path, 'w', encoding='utf-8') as f:
                    f.write(new_content)
