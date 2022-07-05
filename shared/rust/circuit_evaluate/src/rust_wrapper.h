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

// wrapper for our "lib_server" circuit generator

#pragma once

#include <memory>

#include "rust/cxx.h"

namespace interstellar::garble {
class ParallelGarbledCircuit;
}

namespace interstellar::packmsg {
class Packmsg;
}

/**
 * Wrapper around interstellar::garblehelper::GarbleHelper
 */
// TODO refacto? add field "outputs_", EvaluateWithPackmsg->return void, and add
// "GetOutputs"
class EvaluateWrapper {
 public:
  EvaluateWrapper(
      std::unique_ptr<interstellar::garble::ParallelGarbledCircuit> &&pgc,
      std::unique_ptr<interstellar::packmsg::Packmsg> &&packmsg);

  // needed b/c "ParallelGarbledCircuit" is forward declared
  // https://stackoverflow.com/questions/13414652/forward-declaration-with-unique-ptr
  ~EvaluateWrapper();

  /**
   * return a buffer containing a Protobuf-serialized Prepackmsg
   * It can later be used to create a Packmsg with a given tx message,
   * then finally be sent to a device allow the PGC to be evaluated.
   */
  // TEST/DEV ONLY
  rust::Vec<u_int8_t> EvaluateWithInputs(rust::Vec<u_int8_t> inputs) const;
  // TEST/DEV ONLY
  rust::Vec<u_int8_t> EvaluateWithPackmsgWithInputs(
      rust::Vec<u_int8_t> inputs) const;

  /**
   * PROD version
   */
  void EvaluateWithPackmsg(rust::Vec<u_int8_t> &outputs) const;

  size_t GetNbInputs() const;
  size_t GetNbOutputs() const;
  size_t GetWidth() const;
  size_t GetHeight() const;

 private:
  std::unique_ptr<interstellar::garble::ParallelGarbledCircuit> pgc_;
  std::unique_ptr<interstellar::packmsg::Packmsg> packmsg_;
};

std::unique_ptr<EvaluateWrapper> new_evaluate_wrapper(
    rust::Vec<u_int8_t> pgarbled_buffer, rust::Vec<u_int8_t> packmsg_buffer);