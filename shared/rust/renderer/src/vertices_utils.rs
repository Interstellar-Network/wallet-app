use crate::texture::TextureBase;
use crate::vertex::Vertex;

// NOTE: for consistency we define our Rect(=bounding box) the same way than Android
// https://developer.android.com/reference/android/graphics/Rect#summary
// https://developer.android.com/ndk/reference/struct/a-rect
#[non_exhaustive] // make this struct NON-constructible(ie MUST use Rect::new)
pub struct Rect {
    bottom: f32,
    left: f32,
    right: f32,
    top: f32,
}

impl Rect {
    pub fn new(bottom: f32, left: f32, right: f32, top: f32) -> Self {
        if bottom > top {
            panic!("bottom > top")
        }
        if left > right {
            panic!("left > right")
        }

        Rect {
            bottom: bottom,
            left: left,
            right: right,
            top: top,
        }
    }
}

/**
 * [PINPAD] Return the vertices and indices for a given pinpad digit
 *
 * Append in-place into "vertices"
 */
pub fn get_vertices_pinpad_quad(
    rect: Rect,
    texture_base: &TextureBase,
    vertices: &mut Vec<Vertex>,
) {
    let texture_height_ratio =
        texture_base.data_size.height as f32 / texture_base.texture_size.height as f32;
    let texture_width_ratio =
        texture_base.data_size.width as f32 / texture_base.texture_size.width as f32;

    // screen:
    // A B
    // C D
    let mut vertices_pinpad = vec![
        Vertex {
            position: [rect.left, rect.top, 0.0],
            tex_coords: [0.0, 0.0],
        }, // A
        Vertex {
            position: [rect.right, rect.top, 0.0],
            tex_coords: [0.1 * texture_width_ratio, 0.0],
        }, // B
        Vertex {
            position: [rect.left, rect.bottom, 0.0],
            tex_coords: [0.0, texture_height_ratio],
        }, // C
        Vertex {
            position: [rect.right, rect.bottom, 0.0],
            tex_coords: [0.1 * texture_width_ratio, texture_height_ratio],
        }, // D
    ];

    vertices.append(&mut vertices_pinpad);
}

/**
 * [MESSAGE]
 * NOTE: MUST MATCH getIndicesFullscreen
 *
 * screen coords:
 * A B
 * C D
 */
pub fn get_vertices_fullscreen_from_texture_pot(texture_base: &TextureBase) -> Vec<Vertex> {
    let texture_height_ratio =
        texture_base.data_size.height as f32 / texture_base.texture_size.height as f32;
    let texture_width_ratio =
        texture_base.data_size.width as f32 / texture_base.texture_size.width as f32;

    vec![
        Vertex {
            position: [-1.0, 1.0, 0.0],
            tex_coords: [0.0, 0.0],
        }, // A
        Vertex {
            position: [1.0, 1.0, 0.0],
            tex_coords: [texture_width_ratio, 0.0],
        }, // B
        Vertex {
            position: [-1.0, -1.0, 0.0],
            tex_coords: [0.0, texture_height_ratio],
        }, // C
        Vertex {
            position: [1.0, -1.0, 0.0],
            tex_coords: [texture_width_ratio, texture_height_ratio],
        }, // D
    ]
}

// "full screen" eg for the Message
// Texture mapped as-is to the screen
const INDICES_FULLSCREEN: &[u16] = &[
    1, 0, 2, // top-left triangle: B->A->C
    1, 2, 3, // bottom-right triangle: B->C->D
    /* padding */ 0,
];

/**
 *
 * NOTE: MUST MATCH get_vertices_fullscreen_from_texture_pot
 */
pub fn get_indices_fullscreen() -> Vec<u16> {
    INDICES_FULLSCREEN.to_vec()
}
