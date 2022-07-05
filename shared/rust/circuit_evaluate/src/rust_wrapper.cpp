// Copyright 2022 Nathan Prat

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at

//     http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

#include "rust_wrapper.h"

#include <functional>

// generated
// needed only if shared structs
#include "circuit-evaluate/src/lib.rs.h"

// #include "garble_helper.h"
// #include "packmsg_helper.h"
// #include "serialize_packmsg/serialize.h"
#include "evaluate/evaluate.h"
#include "parallel_garbled_circuit/parallel_garbled_circuit.h"
#include "serialize_packmsg/serialize.h"
#include "serialize_pgc/serialize.h"

using namespace interstellar;

EvaluateWrapper::EvaluateWrapper(
    std::unique_ptr<interstellar::garble::ParallelGarbledCircuit> &&pgc,
    std::unique_ptr<interstellar::packmsg::Packmsg> &&packmsg)
    : pgc_(std::move(pgc)), packmsg_(std::move(packmsg)) {}

// https://stackoverflow.com/questions/13414652/forward-declaration-with-unique-ptr
EvaluateWrapper::~EvaluateWrapper() = default;

rust::Vec<u_int8_t> EvaluateWrapper::EvaluateWithInputs(
    rust::Vec<u_int8_t> inputs) const {
  // copy rust::Vec -> std::vector
  std::vector<uint8_t> inputs_buf_cpp;
  std::copy(inputs.begin(), inputs.end(), std::back_inserter(inputs_buf_cpp));

  auto outputs_cpp = garble::EvaluateWithInputs(*pgc_, inputs_buf_cpp);

  rust::Vec<u_int8_t> vec;
  std::copy(outputs_cpp.begin(), outputs_cpp.end(), std::back_inserter(vec));
  return vec;
}

rust::Vec<u_int8_t> EvaluateWrapper::EvaluateWithPackmsgWithInputs(
    rust::Vec<u_int8_t> inputs) const {
  // copy rust::Vec->std::vector
  std::vector<uint8_t> inputs_buf_cpp;
  std::copy(inputs.begin(), inputs.end(), std::back_inserter(inputs_buf_cpp));

  auto outputs_cpp =
      garble::EvaluateWithPackmsg(*pgc_, inputs_buf_cpp, *packmsg_);

  rust::Vec<u_int8_t> vec;
  std::copy(outputs_cpp.begin(), outputs_cpp.end(), std::back_inserter(vec));
  return vec;
}

void EvaluateWrapper::EvaluateWithPackmsg(rust::Vec<u_int8_t> &outputs) const {
  // TODO randomize inputs, or get from Rust?
  std::vector<uint8_t> inputs_buf_cpp(pgc_->nb_inputs_);

  auto outputs_cpp =
      garble::EvaluateWithPackmsg(*pgc_, inputs_buf_cpp, *packmsg_);

  // MUST clear else we append at the end; which means each call we go length =
  // 1x -> 2x -> 3x, etc
  outputs.clear();

  std::copy(outputs_cpp.begin(), outputs_cpp.end(),
            std::back_inserter(outputs));
}

size_t EvaluateWrapper::GetNbInputs() const { return pgc_->nb_inputs_; }
size_t EvaluateWrapper::GetNbOutputs() const { return pgc_->nb_outputs_; }
size_t EvaluateWrapper::GetWidth() const { return pgc_->config_.at("WIDTH"); }
size_t EvaluateWrapper::GetHeight() const { return pgc_->config_.at("HEIGHT"); }

std::unique_ptr<EvaluateWrapper> new_evaluate_wrapper(
    rust::Vec<u_int8_t> pgarbled_buffer, rust::Vec<u_int8_t> packmsg_buffer) {
  // copy rust::Vec -> std::vector
  std::string pgarbled_buffer_cpp;
  std::copy(pgarbled_buffer.begin(), pgarbled_buffer.end(),
            std::back_inserter(pgarbled_buffer_cpp));

  auto pgc = std::make_unique<garble::ParallelGarbledCircuit>();
  garble::DeserializeFromBuffer(pgc.get(), pgarbled_buffer_cpp);

  // copy rust::Vec -> std::vector
  std::string packmsg_buffer_cpp;
  std::copy(packmsg_buffer.begin(), packmsg_buffer.end(),
            std::back_inserter(packmsg_buffer_cpp));

  auto packmsg_ptr = packmsg::DeserializePackmsgFromBuffer(packmsg_buffer_cpp);

  return std::make_unique<EvaluateWrapper>(std::move(pgc),
                                           std::move(packmsg_ptr));
}