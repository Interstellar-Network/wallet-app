/// IMPORTANT it will compile(and even work) WITHOUT "bevy_winit", but it will render nothing
/// ie main will "run once" and exit
use bevy::prelude::*;
use ndarray::Array2;

extern crate renderer;
use renderer::vertices_utils::Rect;

fn main() {
    let mut app = renderer::App::new();

    app.insert_resource(WindowDescriptor {
        width: 1080. / 2.,
        height: 1920. / 2.,
        ..default()
    });

    // roughly match what we get from Android; this is just for consistency
    // cf test_convert_rect_floatArr_to_vec_rect
    let rect_message =
        renderer::vertices_utils::Rect::new_to_ndc_android(0., 0., 1080.0, 381.0, 1080., 1920.);
    let rects_pinpad = generate_pinpad_rects();

    renderer::init_app(
        &mut app,
        rect_message,
        rects_pinpad,
        3,
        4,
        bevy::render::color::Color::WHITE,
        bevy::render::color::Color::WHITE,
        bevy::render::color::Color::hex("0080FFFF").unwrap(),
        bevy::render::color::Color::BLACK,
    );

    // add "dev/debug only systems"
    // eg we DO NOT need movement in the apps, but is useful to dev/debug
    app.add_system(bevy::input::system::exit_on_esc_system);
    // app.add_system(camera_movement);
    // app.add_system(light_movement);

    app.run();
    println!("exiting...");
}

// fn light_movement(
//     input: Res<Input<KeyCode>>,
//     time: Res<Time>,
//     mut point_light: Query<&mut Transform, With<PointLight>>,
// ) {
//     for mut transform in point_light.iter_mut() {
//         let mut direction = Vec3::ZERO;
//         if input.pressed(KeyCode::Z) {
//             direction.y += 1.0;
//         }
//         if input.pressed(KeyCode::S) {
//             direction.y -= 1.0;
//         }
//         if input.pressed(KeyCode::Q) {
//             direction.x -= 1.0;
//         }
//         if input.pressed(KeyCode::D) {
//             direction.x += 1.0;
//         }

//         transform.translation += time.delta_seconds() * 2.0 * direction;
//     }
// }

// fn camera_movement(
//     input: Res<Input<KeyCode>>,
//     time: Res<Time>,
//     mut camera: Query<&mut Transform, With<Camera>>,
// ) {
//     for mut transform in camera.iter_mut() {
//         let mut direction = Vec3::ZERO;
//         if input.pressed(KeyCode::Up) {
//             direction.y += 1.0;
//         }
//         if input.pressed(KeyCode::Down) {
//             direction.y -= 1.0;
//         }
//         if input.pressed(KeyCode::Left) {
//             direction.x -= 1.0;
//         }
//         if input.pressed(KeyCode::Right) {
//             direction.x += 1.0;
//         }

//         transform.translation += time.delta_seconds() * 2.0 * direction;
//     }
// }

pub fn generate_pinpad_rects() -> Array2<Rect> {
    const NB_COLS: usize = 3;
    const NB_ROWS: usize = 4;
    let mut pinpad_rects = Array2::default((NB_ROWS, NB_COLS));

    // roughly match what we get from Android; this is just for consistency
    // result = {Rect[12]@20528}
    // 0 = {Rect@20764} Rect.fromLTRB(212.0, 670.0, 399.0, 857.0)
    // 1 = {Rect@20765} Rect.fromLTRB(446.0, 670.0, 633.0, 857.0)
    // 2 = {Rect@20766} Rect.fromLTRB(680.0, 670.0, 867.0, 857.0)
    // 3 = {Rect@20767} Rect.fromLTRB(212.0, 872.0, 399.0, 1059.0)
    // 4 = {Rect@20768} Rect.fromLTRB(446.0, 872.0, 633.0, 1059.0)
    // 5 = {Rect@20769} Rect.fromLTRB(680.0, 872.0, 867.0, 1059.0)
    // 6 = {Rect@20770} Rect.fromLTRB(212.0, 1074.0, 399.0, 1261.0)
    // 7 = {Rect@20771} Rect.fromLTRB(446.0, 1074.0, 633.0, 1261.0)
    // 8 = {Rect@20772} Rect.fromLTRB(680.0, 1074.0, 867.0, 1261.0)
    // 9 = {Rect@20773} Rect.fromLTRB(212.0, 1276.0, 399.0, 1463.0)
    // 10 = {Rect@20774} Rect.fromLTRB(446.0, 1276.0, 633.0, 1463.0)
    // 11 = {Rect@20775} Rect.fromLTRB(680.0, 1276.0, 867.0, 1463.0)
    pinpad_rects[[0, 0]] = Rect::new_to_ndc_android(212.0, 670.0, 399.0, 857.0, 1080.0, 1920.0);
    pinpad_rects[[0, 1]] = Rect::new_to_ndc_android(446.0, 670.0, 633.0, 857.0, 1080.0, 1920.0);
    pinpad_rects[[0, 2]] = Rect::new_to_ndc_android(680.0, 670.0, 867.0, 857.0, 1080.0, 1920.0);
    pinpad_rects[[1, 0]] = Rect::new_to_ndc_android(212.0, 872.0, 399.0, 1059.0, 1080.0, 1920.0);
    pinpad_rects[[1, 1]] = Rect::new_to_ndc_android(446.0, 872.0, 633.0, 1059.0, 1080.0, 1920.0);
    pinpad_rects[[1, 2]] = Rect::new_to_ndc_android(680.0, 872.0, 867.0, 1059.0, 1080.0, 1920.0);
    pinpad_rects[[2, 0]] = Rect::new_to_ndc_android(212.0, 1074.0, 399.0, 1261.0, 1080.0, 1920.0);
    pinpad_rects[[2, 1]] = Rect::new_to_ndc_android(446.0, 1074.0, 633.0, 1261.0, 1080.0, 1920.0);
    pinpad_rects[[2, 2]] = Rect::new_to_ndc_android(680.0, 1074.0, 867.0, 1261.0, 1080.0, 1920.0);
    pinpad_rects[[3, 0]] = Rect::new_to_ndc_android(212.0, 1276.0, 399.0, 1463.0, 1080.0, 1920.0);
    pinpad_rects[[3, 1]] = Rect::new_to_ndc_android(446.0, 1276.0, 633.0, 1463.0, 1080.0, 1920.0);
    pinpad_rects[[3, 2]] = Rect::new_to_ndc_android(680.0, 1276.0, 867.0, 1463.0, 1080.0, 1920.0);

    pinpad_rects
}
