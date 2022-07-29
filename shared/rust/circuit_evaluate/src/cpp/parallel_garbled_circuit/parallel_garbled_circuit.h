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

#pragma once

#include <fstream>
#include <numeric>
#include <unordered_map>
#include <vector>

#include "justgarble/block.h"

namespace interstellar {

namespace garble {

/**
 * Part of the circuit gen pipeline, last step:
 * BlifParser -> Skcd -> GarbledCircuit -> ParallelGarbledCircuit
 * It can only be constructed from a (moved) GarbledCircuit.
 *
 */
// TODO refactor this class to separate the "garbled circuit" and the runtime
// part(ie the eval) : remove wires, outputs, etc?
// TODO private fields
class ParallelGarbledCircuit {
 public:
  uint32_t nb_inputs_, nb_outputs_, nb_gates_, nb_wires_;
  uint32_t nb_layers_;  // Nb of layers, including the input layer
  // TODO? get nonXorCount,layerCount,layerNonXORCount from
  // GarbleCircuit(XorGraph?)
  uint32_t non_xor_count_;
  std::vector<uint32_t> layer_counts_;  // Nb of gates for each layer
  std::vector<uint32_t>
      layer_nonxor_counts_;           // Nb of non-xor gates for each layer
  std::vector<Block> input_labels_;   // n*2 inputLabels
  std::vector<Block> output_labels_;  // m*2 outputlabels
  // TODO(garbledGates) replace this packing by basic struct(+bitfield if
  // useful)
  std::vector<uint64_t> garbled_gates_;  // 21 bits input0 Id, 21 bits input1
                                         // Id, 21 bits output Id, 1 bit XOR
  std::vector<Block> garbled_table_[4];
  // output of eval (should be in another struct)
  std::vector<uint32_t> outputs_;
  Block global_key_;
  std::unordered_map<std::string, uint32_t> config_;

  // INTERNAL/TEST ONLY
  ParallelGarbledCircuit();

  // INTERNAL/TEST ONLY
  bool operator==(const ParallelGarbledCircuit &other) const {
    return (nb_inputs_ == other.nb_inputs_) &&
           (nb_outputs_ == other.nb_outputs_) &&
           (nb_gates_ == other.nb_gates_) && (nb_wires_ == other.nb_wires_) &&
           (nb_layers_ == other.nb_layers_) &&
           (non_xor_count_ == other.non_xor_count_) &&
           (layer_counts_ == other.layer_counts_) &&
           (layer_nonxor_counts_ == other.layer_nonxor_counts_) &&
           (input_labels_ == other.input_labels_) &&
           (output_labels_ == other.output_labels_) &&
           (garbled_gates_ == other.garbled_gates_) &&
           // WARNING DO NOT compare "garbled_table_" directly b/c it is a
           // pointer
           (garbled_table_[0] == other.garbled_table_[0]) &&
           (garbled_table_[1] == other.garbled_table_[1]) &&
           (garbled_table_[2] == other.garbled_table_[2]) &&
           (garbled_table_[3] == other.garbled_table_[3]) &&
           (outputs_ == other.outputs_) && (global_key_ == other.global_key_)
        //
        ;
  };

  /**
   * Move assigment, needed by CircuitGenHelper::GenerateGarbledAndStripped
   * NOTE: the move ctor is not needed
   *
   * NOTE2: for some reason, can use classutils::noncopyable
   * b∕c "message_stripped_ = std::move(message_garbled);" fails with
   * "copy assignment operator is implicitly deleted because
   * 'ParallelGarbledCircuit' has a user-declared move constructor"
   */
  ParallelGarbledCircuit(ParallelGarbledCircuit const &) = delete;
  void operator=(ParallelGarbledCircuit const &x) = delete;
  ParallelGarbledCircuit &operator=(ParallelGarbledCircuit &&other) = default;
  ParallelGarbledCircuit(ParallelGarbledCircuit &&other) = default;

  std::vector<Block> ExtractLabels(
      const std::vector<uint8_t> &input_bits) const;

 private:
};

}  // namespace garble

}  // namespace interstellar