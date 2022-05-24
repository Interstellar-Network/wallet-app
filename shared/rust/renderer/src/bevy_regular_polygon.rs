// https://github.com/rparrett/bevy/blob/e0b04a6a724c01e2a0f1f859aef61458a44497d0/crates/bevy_render/src/mesh/shape/regular_polygon.rs
// Which is not yet present as of Bevy 0.7
// NOTE: there is also a crate "bevy_prototype_lyon", but it seems to work only for 2D
// Could not make it work with PbrBundle...
// cf examples: https://github.com/bevyengine/bevy/blob/f487407e07c15af878e0d6886f9cd4c146f1f94f/examples/2d/shapes.rs

// use bevy::mesh::{Indices, Mesh};
use bevy::prelude::*;
use bevy::render::mesh::Indices;
use wgpu::PrimitiveTopology;

/// A regular polygon in the xy plane
#[derive(Debug, Copy, Clone)]
pub struct RegularPolygon {
    /// Inscribed radius in the xy plane.
    pub radius: f32,
    /// Number of sides.
    pub sides: usize,
}

impl Default for RegularPolygon {
    fn default() -> Self {
        Self {
            radius: 0.5,
            sides: 6,
        }
    }
}

impl RegularPolygon {
    /// Creates a regular polygon in the xy plane
    pub fn new(radius: f32, sides: usize) -> Self {
        Self { radius, sides }
    }
}

impl From<RegularPolygon> for Mesh {
    fn from(polygon: RegularPolygon) -> Self {
        let RegularPolygon { radius, sides } = polygon;

        debug_assert!(sides > 2, "RegularPolygon requires at least 3 sides.");

        let mut positions = Vec::with_capacity(sides);
        let mut normals = Vec::with_capacity(sides);
        let mut uvs = Vec::with_capacity(sides);

        let step = std::f32::consts::TAU / sides as f32;
        for i in 0..sides {
            let theta = std::f32::consts::FRAC_PI_2 - i as f32 * step;
            let (sin, cos) = theta.sin_cos();

            positions.push([cos * radius, sin * radius, 0.0]);
            normals.push([0.0, 0.0, 1.0]);
            uvs.push([0.5 * (cos + 1.0), 1.0 - 0.5 * (sin + 1.0)]);
        }

        let mut indices = Vec::with_capacity((sides - 2) * 3);
        for i in 1..(sides as u32 - 1) {
            indices.extend_from_slice(&[0, i + 1, i]);
        }

        let mut mesh = Mesh::new(PrimitiveTopology::TriangleList);
        mesh.insert_attribute(Mesh::ATTRIBUTE_POSITION, positions);
        mesh.insert_attribute(Mesh::ATTRIBUTE_NORMAL, normals);
        mesh.insert_attribute(Mesh::ATTRIBUTE_UV_0, uvs);
        mesh.set_indices(Some(Indices::U32(indices)));
        mesh
    }
}

/// A circle in the xy plane
pub struct Circle {
    /// Inscribed radius in the xy plane.
    pub radius: f32,
    /// The number of vertices used.
    pub vertices: usize,
}

impl Default for Circle {
    fn default() -> Self {
        Self {
            radius: 0.5,
            vertices: 64,
        }
    }
}

impl Circle {
    /// Creates a circle in the xy plane
    pub fn new(radius: f32) -> Self {
        Self {
            radius,
            ..Default::default()
        }
    }
}

impl From<Circle> for RegularPolygon {
    fn from(circle: Circle) -> Self {
        Self {
            radius: circle.radius,
            sides: circle.vertices,
        }
    }
}

impl From<Circle> for Mesh {
    fn from(circle: Circle) -> Self {
        Mesh::from(RegularPolygon::from(circle))
    }
}
