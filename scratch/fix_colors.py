import re

def fix_file(filepath):
    missing_colors = {'glass_blob_blue', 'surface_total_cost', 'surface_indigo_light', 'doc_card_border', 'surface_lavender_light', 'surface_close_button', 'tab_indicator_track', 'text_heading_alt', 'surface_profile_header', 'reminder_urgent_border', 'glass_blob_teal', 'status_amber_bg', 'glass_stat_block_bg', 'border_divider_subtle', 'border_card_subtle', 'handle_bar', 'divider_faint', 'icon_blue', 'glass_stat_panel_border', 'tab_text_inactive', 'reminder_urgent_accent', 'glass_stat_panel_bg_light', 'glass_stat_panel_bg', 'timeline_dot_ring', 'status_done_bg', 'shadow_link_20', 'surface_teal_light', 'reminder_standard_border', 'settings_card_border', 'glass_blob_violet', 'status_error_strong', 'text_white_70', 'glass_stat_block_border', 'timeline_card_border', 'glass_overlay_blue_5', 'glass_overlay_cyan_10', 'reminder_urgent_bg', 'reminder_standard_bg', 'surface_input_faint', 'overlay_black_20', 'text_muted_alt', 'icon_warning_bright', 'status_amber_text', 'profile_header_border', 'reminder_urgent_subtitle', 'border_alert_red_20', 'surface_header', 'glass_blob_peach', 'text_heading_dark', 'glass_dock_bg', 'glass_overlay_press', 'border_card_translucent', 'border_card', 'border_default', 'border_divider_translucent', 'neutral_900', 'neutral_300', 'avatar_placeholder_bg', 'brand_link', 'text_placeholder', 'status_success', 'status_active_dot', 'timeline_divider', 'surface_white', 'surface_highlight'}

    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    # Filter out those that already exist
    existing = set(re.findall(r'<color name="([^"]+)">', content))
    to_add = missing_colors - existing
    
    mapping = ""
    for c in sorted(to_add):
        if 'text' in c or 'subtitle' in c or 'heading' in c or 'title' in c or 'link' in c or 'neutral_900' in c:
            val = '@color/text_primary'
        elif 'bg' in c or 'surface' in c or 'glass' in c or 'overlay' in c or 'shadow' in c or 'highlight' in c:
            val = '@color/surface_variant'
        elif 'border' in c or 'divider' in c or 'ring' in c or 'neutral_300' in c:
            val = '#336B7280'
        elif 'icon' in c or 'indicator' in c or 'handle' in c:
            val = '@color/icon_secondary'
        elif 'status' in c or 'urgent' in c or 'standard' in c:
            val = '@color/text_primary'
        elif 'white' in c:
            val = '#B3FFFFFF'
        else:
            val = '@color/surface_variant'
        
        # overrides
        if c == 'surface_white': val = '@color/surface_primary'
        if c == 'glass_dock_bg': val = '@color/surface_primary'
        if c == 'glass_overlay_press': val = '#33191B23'
        
        mapping += f'    <color name="{c}">{val}</color>\n'
    
    new_content = content.replace('</resources>', mapping + '</resources>')
    
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(new_content)

fix_file('c:/Driveapps/app/src/main/res/values/colors.xml')
fix_file('c:/Driveapps/app/src/main/res/values-night/colors.xml')
