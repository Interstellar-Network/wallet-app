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
