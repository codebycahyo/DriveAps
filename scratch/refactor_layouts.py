import os
import re

layout_dir = 'c:/Driveapps/app/src/main/res/layout'
files = [f for f in os.listdir(layout_dir) if f.endswith('.xml')]

def replace_tokens(content):
    # 1. Spacing exact matches
    spacings = {
        '0dp': '@dimen/spacing_0',
        '2dp': '@dimen/spacing_2',
        '4dp': '@dimen/spacing_4',
        '8dp': '@dimen/spacing_8',
        '12dp': '@dimen/spacing_12',
        '16dp': '@dimen/spacing_16',
        '20dp': '@dimen/spacing_20',
        '24dp': '@dimen/spacing_24',
        '32dp': '@dimen/spacing_32',
        '40dp': '@dimen/spacing_40',
        '48dp': '@dimen/spacing_48',
        '64dp': '@dimen/spacing_64',
        '72dp': '@dimen/spacing_72',
        '96dp': '@dimen/spacing_96',
        '128dp': '@dimen/spacing_128',
        
        # Round strange spacings to nearest standard
        '13dp': '@dimen/spacing_12',
        '15dp': '@dimen/spacing_16',
        '41dp': '@dimen/spacing_40',
        '45dp': '@dimen/spacing_48',
        '52dp': '@dimen/spacing_48',
        '59dp': '@dimen/spacing_64',
        '67dp': '@dimen/spacing_64',
        '73dp': '@dimen/spacing_72',
        '74dp': '@dimen/spacing_72',
        '78dp': '@dimen/spacing_72',
        '98dp': '@dimen/spacing_96'
    }
    
    # Replace layout_margin*, padding* values
    for attr in ['layout_margin', 'layout_marginStart', 'layout_marginEnd', 'layout_marginTop', 'layout_marginBottom',
                 'padding', 'paddingStart', 'paddingEnd', 'paddingTop', 'paddingBottom']:
        for val, token in spacings.items():
            content = re.sub(rf'{attr}="{val}"', f'{attr}="{token}"', content)
            
    # 2. Elevations
    elevations = {
        '2dp': '@dimen/elevation_card',
        '4dp': '@dimen/elevation_card',
        '8dp': '@dimen/elevation_card',
        '10dp': '@dimen/elevation_dock',
        '12dp': '@dimen/elevation_dock'
    }
    for attr in ['cardElevation', 'elevation']:
        for val, token in elevations.items():
            content = re.sub(rf'{attr}="{val}"', f'{attr}="{token}"', content)
            
    # 3. Radius
    radius = {
        '8dp': '@dimen/radius_card_small',
        '12dp': '@dimen/radius_card_small',
        '16dp': '@dimen/radius_card_default',
        '24dp': '@dimen/radius_large'
    }
    for attr in ['cardCornerRadius', 'cornerRadius', 'boxCornerRadiusTopStart', 'boxCornerRadiusTopEnd', 'boxCornerRadiusBottomStart', 'boxCornerRadiusBottomEnd']:
        for val, token in radius.items():
            content = re.sub(rf'{attr}="{val}"', f'{attr}="{token}"', content)
            
    # 4. Icon Sizes
    # Find all ImageView and ImageButton sizes
    # We will just replace width/height if they are small numbers and it's an ImageView/ImageButton
    # But since regexing across tags is hard, we'll just replace specific sizes on width/height globally
    # Except we don't want to break other components. So we match <ImageView ... /> block
    
    def replace_icon_sizes(match):
        block = match.group(0)
        sizes = {
            # Small
            '5dp': '@dimen/icon_size_small', '7dp': '@dimen/icon_size_small', '9dp': '@dimen/icon_size_small', '10dp': '@dimen/icon_size_small', '11dp': '@dimen/icon_size_small', '12dp': '@dimen/icon_size_small', '13dp': '@dimen/icon_size_small', '14dp': '@dimen/icon_size_small', '15dp': '@dimen/icon_size_small', '16dp': '@dimen/icon_size_small',
            # Medium
            '17dp': '@dimen/icon_size_medium', '18dp': '@dimen/icon_size_medium', '19dp': '@dimen/icon_size_medium', '20dp': '@dimen/icon_size_medium', '21dp': '@dimen/icon_size_medium', '22dp': '@dimen/icon_size_medium',
            # Large
            '23dp': '@dimen/icon_size_large', '24dp': '@dimen/icon_size_large', '27dp': '@dimen/icon_size_large', '28dp': '@dimen/icon_size_large', '29dp': '@dimen/icon_size_large', '30dp': '@dimen/icon_size_large',
            # XLarge
            '32dp': '@dimen/icon_size_xlarge', '36dp': '@dimen/icon_size_xlarge'
        }
        for attr in ['layout_width', 'layout_height']:
            for val, token in sizes.items():
                block = re.sub(rf'{attr}="{val}"', f'{attr}="{token}"', block)
        return block

    content = re.sub(r'<ImageView[^>]*>', replace_icon_sizes, content)
    content = re.sub(r'<ImageButton[^>]*>', replace_icon_sizes, content)
    content = re.sub(r'<com.google.android.material.imageview.ShapeableImageView[^>]*>', replace_icon_sizes, content)
    
    return content

for f in files:
    path = os.path.join(layout_dir, f)
    with open(path, 'r', encoding='utf-8') as file:
        content = file.read()
        
    new_content = replace_tokens(content)
    
    if new_content != content:
        with open(path, 'w', encoding='utf-8') as file:
            file.write(new_content)
