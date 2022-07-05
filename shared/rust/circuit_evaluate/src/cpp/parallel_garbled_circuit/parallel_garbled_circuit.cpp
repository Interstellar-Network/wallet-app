// lib_garble
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