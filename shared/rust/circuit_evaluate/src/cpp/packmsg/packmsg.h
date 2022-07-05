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

#include <string>
#include <vector>

#include "justgarble/block.h"

namespace interstellar {

namespace packmsg {

class Packmsg {
 public:
  Packmsg(const std::vector<Block> &garbled_values,
          const std::vector<uint64_t> &xormask);

  // NO COPY
  // NO MOVE
  Packmsg(Packmsg const &) = delete;
  void operator=(Packmsg const &x) = delete;

  auto const &GetXormask() const { return xormask_; };
  auto const &GetGarbledValues() const { return garbled_values_; };

  // INTERNAL/TEST ONLY
  bool operator==(const Packmsg &other) const {
    return (garbled_values_ == other.garbled_values_) &&
           (xormask_ == other.xormask_);
  };

 private:
  std::vector<Block> garbled_values_;
  std::vector<uint64_t> xormask_;
};

}  // namespace packmsg

}  // namespace interstellar