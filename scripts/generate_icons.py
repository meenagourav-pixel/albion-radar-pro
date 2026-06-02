#!/usr/bin/env python3
"""
Generate launcher icons for Albion Radar Android app.
This script creates radar-style icons for all screen densities.
Run: python3 scripts/generate_icons.py
"""

from PIL import Image, ImageDraw
import os

# Define sizes for each density (Android icon size guidelines)
DENSITIES = {
    'mdpi': 48,
    'hdpi': 72,
    'xhdpi': 96,
    'xxhdpi': 144,
    'xxxhdpi': 192
}

# Colors
BG_COLOR = (30, 30, 46, 255)  # Dark blue background
RING_COLOR = (255, 255, 255, 255)  # White rings
CENTER_DOT_COLOR = (255, 107, 53, 255)  # Orange center dot


def create_icon(size: int, round_shape: bool = False) -> Image.Image:
    """Create a radar-style icon of the given size."""
    
    if round_shape:
        # Round icon with transparent background
        img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
        # Create circular mask
        mask = Image.new('L', (size, size), 0)
        mask_draw = ImageDraw.Draw(mask)
        mask_draw.ellipse([0, 0, size, size], fill=255)
        # Draw on content layer
        content = Image.new('RGBA', (size, size), BG_COLOR)
        # Apply mask to make it circular
        img = Image.composite(content, img, mask)
        draw = ImageDraw.Draw(img)
    else:
        # Regular square icon with background
        img = Image.new('RGBA', (size, size), BG_COLOR)
        draw = ImageDraw.Draw(img)
    
    # Calculate dimensions
    margin = size // 8
    center = size // 2
    ring_width = max(2, size // 24)
    
    # Draw outer ring
    draw.ellipse(
        [margin, margin, size - margin, size - margin],
        outline=RING_COLOR,
        width=ring_width
    )
    
    # Draw inner ring
    inner_margin = size // 3
    draw.ellipse(
        [inner_margin, inner_margin, size - inner_margin, size - inner_margin],
        outline=(255, 255, 255, 200),
        width=max(1, ring_width // 2)
    )
    
    # Draw center dot (orange/red)
    dot_size = size // 10
    draw.ellipse(
        [center - dot_size, center - dot_size, center + dot_size, center + dot_size],
        fill=CENTER_DOT_COLOR
    )
    
    return img


def main():
    # Base path for resources
    script_dir = os.path.dirname(os.path.abspath(__file__))
    res_dir = os.path.join(script_dir, '..', 'app', 'src', 'main', 'res')
    
    print("Generating launcher icons...")
    
    for density, size in DENSITIES.items():
        density_dir = os.path.join(res_dir, f'mipmap-{density}')
        
        # Create directory if it doesn't exist
        os.makedirs(density_dir, exist_ok=True)
        
        # Generate regular icon
        icon = create_icon(size, round_shape=False)
        icon_path = os.path.join(density_dir, 'ic_launcher.png')
        icon.save(icon_path, 'PNG')
        print(f"  Created {icon_path}")
        
        # Generate round icon
        round_icon = create_icon(size, round_shape=True)
        round_path = os.path.join(density_dir, 'ic_launcher_round.png')
        round_icon.save(round_path, 'PNG')
        print(f"  Created {round_path}")
    
    print("\n✅ All launcher icons generated successfully!")
    print(f"   Total: {len(DENSITIES) * 2} icons created")


if __name__ == '__main__':
    main()
