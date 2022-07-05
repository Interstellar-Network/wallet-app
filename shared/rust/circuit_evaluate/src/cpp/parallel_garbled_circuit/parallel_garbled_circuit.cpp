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

#include "parallel_garbled_circuit.h"

namespace {}  // anonymous namespace

namespace interstellar {

namespace garble {

ParallelGarbledCircuit::ParallelGarbledCircuit() {}

/**
 * This is used by:
 * - to prepare the Packmsg
 * - [dev/test] when evaluating a circuit directly(ie WITHOUT a .packmsg)
 *
 * input_bits: vector of 0/1
 */
std::vector<Block> ParallelGarbledCircuit::ExtractLabels(
    const std::vector<uint8_t> &input_bits) const {
  assert(input_bits.size() == nb_inputs_ && "input size MUST nb nb_inputs!");

  std::vector<Block> extracted_labels;

  size_t input_bits_size = input_bits.size();

  // TODO? n+1 is needed for the Packmsg, cf patch_inputs
  extracted_labels.reserve(input_bits_size);

  for (unsigned int i = 0; i < input_bits_size; i++) {
    assert(input_bits[i] == 0 || input_bits[i] == 1);
    extracted_labels.push_back(input_labels_[2 * i + input_bits[i]]);
  }

  assert(extracted_labels.size() == input_bits_size &&
         "ExtractLabels: wrong final size!");

  return extracted_labels;
}

}  // namespace garble

}  // namespace interstellar