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