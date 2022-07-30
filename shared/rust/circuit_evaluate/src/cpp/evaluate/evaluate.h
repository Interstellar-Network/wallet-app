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

#include <vector>

#include "packmsg/packmsg.h"
#include "parallel_garbled_circuit/parallel_garbled_circuit.h"

namespace interstellar
{

    namespace garble
    {

        std::vector<uint8_t> EvaluateWithInputs(
            const ParallelGarbledCircuit &parallel_garbled_circuit,
            const std::vector<uint8_t> &inputs);

        std::vector<uint8_t> EvaluateWithPackmsg(
            const ParallelGarbledCircuit &parallel_garbled_circuit,
            const std::vector<uint8_t> &inputs, const packmsg::Packmsg &packmsg);

    } // namespace garble

} // namespace interstellar