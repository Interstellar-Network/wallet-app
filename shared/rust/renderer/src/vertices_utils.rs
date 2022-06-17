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

    pub fn center(&self) -> [f32; 2] {
        return [
            (self.right - self.left) / 2.0,
            (self.top - self.bottom) / 2.0,
        ];
    }
}

/**
 * [PINPAD] Return the vertices and indices for a given pinpad digit
 *
 * Append in-place into "vertices"
 *
 * NOTE: it is assumed the "raw data" is in the from of
 * ----------------------------------------------------
 * | digit0 | digit1 | etc
 * ----------------------------------------------------
 * - there are 10 digits in a ROW
 * - so each digit is 1/10 of the width
 *  - eg the first digit tex_coords WIDTH is (0.0, 0.1)
 *  - the second one is (0.1, 0.2)
 *  - etc
 * - only one row so the tex_coords HEIGHT is b/w 0.0 and 1.0
 */
pub fn get_vertices_pinpad_quad(
    digit_id: u8,
    rect: Rect,
    texture_base: &TextureBase,
    vertices: &mut Vec<Vertex>,
) {
    let texture_height_ratio =
        texture_base.data_size.height as f32 / texture_base.texture_size.height as f32;
    let texture_width_ratio =
        texture_base.data_size.width as f32 / texture_base.texture_size.width as f32;

    let tex_coords_min_width = digit_id as f32 * 0.1;
    let tex_coords_max_width = (digit_id + 1) as f32 * 0.1;

    // screen:
    // A B
    // C D
    let mut vertices_pinpad = vec![
        // Vertex {
        //     position: [rect.left, rect.top, 0.0],
        //     tex_coords: [-1.0 * texture_height_ratio / texture_width_ratio, 1.0],
        // }, // A
        // Vertex {
        //     position: [rect.right, rect.top, 0.0],
        //     tex_coords: [1.0 * texture_height_ratio / texture_width_ratio, 1.0],
        // }, // B
        // Vertex {
        //     position: [rect.left, rect.bottom, 0.0],
        //     tex_coords: [-1.0 * texture_height_ratio / texture_width_ratio, -1.0],
        // }, // C
        // Vertex {
        //     position: [rect.right, rect.bottom, 0.0],
        //     tex_coords: [1.0 * texture_height_ratio / texture_width_ratio, -1.0],
        // }, // D
        // Vertex {
        //     position: [rect.left, rect.top, 0.0],
        //     tex_coords: [
        //         -1.0 * texture_height_ratio / texture_width_ratio,
        //         1.0 * texture_height_ratio / texture_width_ratio,
        //     ],
        // }, // A
        // Vertex {
        //     position: [rect.right, rect.top, 0.0],
        //     tex_coords: [
        //         1.0 * texture_height_ratio / texture_width_ratio,
        //         1.0 * texture_height_ratio / texture_width_ratio,
        //     ],
        // }, // B
        // Vertex {
        //     position: [rect.left, rect.bottom, 0.0],
        //     tex_coords: [
        //         -1.0 * texture_height_ratio / texture_width_ratio,
        //         -1.0 * texture_height_ratio / texture_width_ratio,
        //     ],
        // }, // C
        // Vertex {
        //     position: [rect.right, rect.bottom, 0.0],
        //     tex_coords: [
        //         1.0 * texture_height_ratio / texture_width_ratio,
        //         -1.0 * texture_height_ratio / texture_width_ratio,
        //     ],
        // }, // D

        //
        // Vertex {
        //     position: [rect.left, rect.top, 0.0],
        //     tex_coords: [-1.0, 1.0],
        // }, // A
        // Vertex {
        //     position: [rect.right, rect.top, 0.0],
        //     tex_coords: [1.0, 1.0],
        // }, // B
        // Vertex {
        //     position: [rect.left, rect.bottom, 0.0],
        //     tex_coords: [-1.0, -1.0],
        // }, // C
        // Vertex {
        //     position: [rect.right, rect.bottom, 0.0],
        //     tex_coords: [1.0, -1.0],
        // }, // D

        //
        //
        Vertex {
            position: [rect.left, rect.top, 0.0],
            tex_coords: [0.0, 0.0],
        }, // A
        Vertex {
            position: [rect.right, rect.top, 0.0],
            tex_coords: [1.0, 0.0],
        }, // B
        Vertex {
            position: [rect.left, rect.bottom, 0.0],
            tex_coords: [1.0, 0.0],
        }, // C
        Vertex {
            position: [rect.right, rect.bottom, 0.0],
            tex_coords: [1.0, 1.0],
        }, // D

           // Vertex {
           //     position: [rect.left, rect.top, 0.0],
           //     tex_coords: [0.5, 0.5],
           // }, // A
           // Vertex {
           //     position: [rect.right, rect.top, 0.0],
           //     tex_coords: [0.5, 0.5],
           // }, // B
           // Vertex {
           //     position: [rect.left, rect.bottom, 0.0],
           //     tex_coords: [0.5, 0.5],
           // }, // C
           // Vertex {
           //     position: [rect.right, rect.bottom, 0.0],
           //     tex_coords: [0.5, 0.5],
           // }, // D
           //
           // Vertex {
           //     position: [rect.left, rect.top, 0.0],
           //     tex_coords: rect.center(),
           // }, // A
           // Vertex {
           //     position: [rect.right, rect.top, 0.0],
           //     tex_coords: rect.center(),
           // }, // B
           // Vertex {
           //     position: [rect.left, rect.bottom, 0.0],
           //     tex_coords: rect.center(),
           // }, // C
           // Vertex {
           //     position: [rect.right, rect.bottom, 0.0],
           //     tex_coords: rect.center(),
           // }, // D
    ];

    vertices.append(&mut vertices_pinpad);
}

/**
 * Return the indices corresponding to each QUAD generated by get_vertices_pinpad_quad
 * IMPORANT: there is NO LOGIC here, it is a DUMB function
 * It assumes get_vertices_pinpad_quad() was called in order(id=0,id=1,etc) and from TOP to BOTTOM
 */
pub fn get_indices_pinpad(vertices: &Vec<Vertex>) -> Vec<u16> {
    assert!(
        vertices.len() % 4 == 0,
        "get_indices_pinpad: MUST be called with % 4 len param"
    );

    let nb_quads = vertices.len() / 4;
    let mut indices = Vec::<u16>::with_capacity(nb_quads * 6);
    for i in 0..nb_quads {
        let mut indices_for_quad: Vec<u16> = vec![
            ((i * 4) + 1).try_into().unwrap(),
            ((i * 4) + 0).try_into().unwrap(),
            ((i * 4) + 2).try_into().unwrap(), // top-left triangle: B->A->C
            ((i * 4) + 1).try_into().unwrap(),
            ((i * 4) + 2).try_into().unwrap(),
            ((i * 4) + 3).try_into().unwrap(), // bottom-right triangle: B->C->D
                                               // TODO? /* padding */ i + 0,
        ];
        indices.append(&mut indices_for_quad);
    }

    // TODO? /* padding */ i + 0,
    let mut padding = vec![0];
    indices.append(&mut padding);

    indices
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
    // vec![
    //     Vertex {
    //         position: [-0.1, 0.1, 0.0],
    //         tex_coords: [0.0, 0.0],
    //     }, // A
    //     Vertex {
    //         position: [0.1, 0.1, 0.0],
    //         tex_coords: [texture_width_ratio, 0.0],
    //     }, // B
    //     Vertex {
    //         position: [-0.1, -0.1, 0.0],
    //         tex_coords: [0.0, texture_height_ratio],
    //     }, // C
    //     Vertex {
    //         position: [0.1, -0.1, 0.0],
    //         tex_coords: [texture_width_ratio, texture_height_ratio],
    //     }, // D
    // ]
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
