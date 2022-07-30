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

#include "packmsg.pb.h"

namespace {

std::unique_ptr<interstellar::packmsg::Packmsg> ReadProtobufPackmsg(
    const interstellarpbcircuits::Packmsg &protobuf_packmsg) {
  // std::transform b/c we MUST convert interstellarpbcircuits::Block -> Block
  std::vector<Block> garbled_values;
  std::transform(protobuf_packmsg.garbled_values().cbegin(),
                 protobuf_packmsg.garbled_values().cend(),
                 std::back_inserter(garbled_values),
                 [](const interstellarpbcircuits::Block &pb_block) {
                   return Block(pb_block.high(), pb_block.low());
                 });

  std::vector<uint64_t> xormask(protobuf_packmsg.xormask().cbegin(),
                                protobuf_packmsg.xormask().cend());

  return std::make_unique<interstellar::packmsg::Packmsg>(garbled_values,
                                                          xormask);
}

}  // anonymous namespace

namespace interstellar::packmsg {

/**
 * Deserialize Packmsg from a buffer
 */
std::unique_ptr<interstellar::packmsg::Packmsg> DeserializePackmsgFromBuffer(
    const std::string &buffer) {
  interstellarpbcircuits::Packmsg protobuf_packmsg;
  auto ok = protobuf_packmsg.ParseFromString(buffer);
  if (!ok) {
    throw std::runtime_error("DeserializeFromBuffer: parsing failed");
  }

  return ReadProtobufPackmsg(protobuf_packmsg);
}

}  // namespace interstellar::packmsg
