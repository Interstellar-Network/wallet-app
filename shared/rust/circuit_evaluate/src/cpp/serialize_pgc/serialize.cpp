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

#include "serialize.h"

#include "circuit.pb.h"

// TODO?
#undef OUTPUTS_ENCODE_NEW

namespace {

#ifdef OUTPUTS_ENCODE_NEW

std::vector<std::pair<unsigned int, size_t>> VectorGetConsecutiveAscending(
    const std::vector<uint32_t> &vect) {
  std::vector<std::pair<unsigned int, size_t>> res;

  const size_t vect_size = vect.size();

  int last_value = vect[0];
  size_t count = 1;

  for (unsigned int i = 1; i < vect_size; ++i) {  // can skip the first one

    int current_value = vect[i];

    if (current_value != last_value + 1) {
      // plateau end
      res.emplace_back(last_value, count);

      // reset
      count = 1;
      last_value = current_value;
    } else {
      count++;
    }
  }

  // don't forget the last plateau
  res.emplace_back(last_value, count);

  return res;
}

#endif  // OUTPUTS_ENCODE_NEW

void ReadProtobuf(
    const interstellarpbcircuits::ParallelGarbledCircuit &protobuf_pgc,
    interstellar::garble::ParallelGarbledCircuit *parallel_garbled_circuit) {
  parallel_garbled_circuit->nb_inputs_ = protobuf_pgc.n();
  parallel_garbled_circuit->nb_outputs_ = protobuf_pgc.m();
  parallel_garbled_circuit->nb_gates_ = protobuf_pgc.q();
  parallel_garbled_circuit->nb_wires_ = protobuf_pgc.r();
  parallel_garbled_circuit->nb_layers_ = protobuf_pgc.nblayers();
  parallel_garbled_circuit->non_xor_count_ = protobuf_pgc.nonxorcount();

  parallel_garbled_circuit->layer_counts_.assign(
      protobuf_pgc.layercount().cbegin(), protobuf_pgc.layercount().cend());

  parallel_garbled_circuit->layer_nonxor_counts_.assign(
      protobuf_pgc.layernonxorcount().cbegin(),
      protobuf_pgc.layernonxorcount().cend());

  parallel_garbled_circuit->garbled_gates_.assign(
      protobuf_pgc.garbledgates().cbegin(), protobuf_pgc.garbledgates().cend());

  // std::transform b/c we MUST convert interstellarpbcircuits::Block -> Block

  std::transform(protobuf_pgc.input_labels().cbegin(),
                 protobuf_pgc.input_labels().cend(),
                 std::back_inserter(parallel_garbled_circuit->input_labels_),
                 [](const interstellarpbcircuits::Block &pb_block) {
                   return Block(pb_block.high(), pb_block.low());
                 });

  std::transform(protobuf_pgc.output_labels().cbegin(),
                 protobuf_pgc.output_labels().cend(),
                 std::back_inserter(parallel_garbled_circuit->output_labels_),
                 [](const interstellarpbcircuits::Block &pb_block) {
                   return Block(pb_block.high(), pb_block.low());
                 });

  std::transform(
      protobuf_pgc.garbletables().gt0().cbegin(),
      protobuf_pgc.garbletables().gt0().cend(),
      std::back_inserter(parallel_garbled_circuit->garbled_table_[0]),
      [](const interstellarpbcircuits::Block &pb_block) {
        return Block(pb_block.high(), pb_block.low());
      });
  std::transform(
      protobuf_pgc.garbletables().gt1().cbegin(),
      protobuf_pgc.garbletables().gt1().cend(),
      std::back_inserter(parallel_garbled_circuit->garbled_table_[1]),
      [](const interstellarpbcircuits::Block &pb_block) {
        return Block(pb_block.high(), pb_block.low());
      });
  std::transform(
      protobuf_pgc.garbletables().gt2().cbegin(),
      protobuf_pgc.garbletables().gt2().cend(),
      std::back_inserter(parallel_garbled_circuit->garbled_table_[2]),
      [](const interstellarpbcircuits::Block &pb_block) {
        return Block(pb_block.high(), pb_block.low());
      });
  std::transform(
      protobuf_pgc.garbletables().gt3().cbegin(),
      protobuf_pgc.garbletables().gt3().cend(),
      std::back_inserter(parallel_garbled_circuit->garbled_table_[3]),
      [](const interstellarpbcircuits::Block &pb_block) {
        return Block(pb_block.high(), pb_block.low());
      });

#ifdef OUTPUTS_ENCODE_NEW
#error "mutable_outputs not supported cf VectorGetConsecutiveAscending"
#else
  parallel_garbled_circuit->outputs_.assign(protobuf_pgc.outputs().cbegin(),
                                            protobuf_pgc.outputs().cend());
#endif  // OUTPUTS_ENCODE_NEW

  parallel_garbled_circuit->global_key_ =
      Block(protobuf_pgc.globalkey().high(), protobuf_pgc.globalkey().low());

  for (auto const &[key, val] : protobuf_pgc.config()) {
    parallel_garbled_circuit->config_.try_emplace(key, val);
  }
}

}  // anonymous namespace

namespace interstellar {

namespace garble {

/**
 * Deserialize from a file
 */
void DeserializeFromFile(ParallelGarbledCircuit *parallel_garbled_circuit,
                         std::filesystem::path pgarbled_input_path) {
  std::fstream input_stream(pgarbled_input_path.generic_string(),
                            std::ios::in | std::ios::binary);
  // if (!input_stream) {
  //   LOG(ERROR) << "GarbledCircuit: invalid file : " << skcd_input_path;
  //   throw std::runtime_error("GarbledCircuit: input_stream failed");
  // }

  interstellarpbcircuits::ParallelGarbledCircuit protobuf_pgc;
  auto ok = protobuf_pgc.ParseFromIstream(&input_stream);
  if (!ok) {
    throw std::runtime_error("DeserializeFromFile: parsing failed");
  }

  ReadProtobuf(protobuf_pgc, parallel_garbled_circuit);
}

/**
 * Deserialize from a buffer
 */
void DeserializeFromBuffer(ParallelGarbledCircuit *parallel_garbled_circuit,
                           const std::string &buffer) {
  interstellarpbcircuits::ParallelGarbledCircuit protobuf_pgc;
  auto ok = protobuf_pgc.ParseFromString(buffer);
  if (!ok) {
    throw std::runtime_error("DeserializeFromBuffer: parsing failed");
  }

  ReadProtobuf(protobuf_pgc, parallel_garbled_circuit);
}

}  // namespace garble

}  // namespace interstellar