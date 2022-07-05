// api_garble
// Copyright (C) 2O22  Nathan Prat

// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.

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

rust::Vec<u_int8_t> EvaluateWrapper::EvaluateWithPackmsg(
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

size_t EvaluateWrapper::GetNbInputs() const { return pgc_->nb_inputs_; }

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