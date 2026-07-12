import os
import re

layout_dir = 'c:/Driveapps/app/src/main/res/layout'
files = [f for f in os.listdir(layout_dir) if f.endswith('.xml')]

def replace_tokens(content):
    # Fix paddingVertical/Horizontal
    spacings = {
        '0dp': '@dimen/spacing_0', '2dp': '@dimen/spacing_2', '4dp': '@dimen/spacing_4', '8dp': '@dimen/spacing_8', '12dp': '@dimen/spacing_12',
        '16dp': '@dimen/spacing_16', '20dp': '@dimen/spacing_20', '24dp': '@dimen/spacing_24', '32dp': '@dimen/spacing_32', '40dp': '@dimen/spacing_40',
        '48dp': '@dimen/spacing_48', '64dp': '@dimen/spacing_64', '72dp': '@dimen/spacing_72', '96dp': '@dimen/spacing_96', '128dp': '@dimen/spacing_128',
    }
    for attr in ['paddingHorizontal', 'paddingVertical', 'layout_marginHorizontal', 'layout_marginVertical']:
        for val, token in spacings.items():
            content = re.sub(rf'{attr}="{val}"', f'{attr}="{token}"', content)
            
    # Replace @dimen/space_ with @dimen/spacing_
    content = content.replace('@dimen/space_', '@dimen/spacing_')
    
    # Check for empty layout state loading handling
    return content

for f in files:
    path = os.path.join(layout_dir, f)
    with open(path, 'r', encoding='utf-8') as file:
        content = file.read()
        
    new_content = replace_tokens(content)
    
    if new_content != content:
        with open(path, 'w', encoding='utf-8') as file:
            file.write(new_content)
