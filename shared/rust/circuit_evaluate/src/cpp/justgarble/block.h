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

#include <ostream>

// generated
// needed only if shared structs
#include "circuit-evaluate/src/lib.rs.h"

/**
 * Historically a simple "__m128i Block"
 * But it's better to have a proper class(operators overload, etc)
 *
 * NOTE: the original impl was directly using a __m128i field.
 * This is SLOWER than using 2 uint64! Probably due to the fact that the m128i
 * field is copied everywhere when XORing etc
 * See bench/bench_xor_Block.cpp for comparison.
 *
 * WARNING: Those functions REALLY need to be inline:
 * circuit_display_gen_bench [1000], avg of 5 runs:
 * inline:                412k circuits/h
 * out of line(in .cpp):  385k circuits/h
 */
// TODO noncopyable
// TODO use absl int128.h
class Block {
 public:
  Block() {}

  Block(uint64_t high, uint64_t low) : low_(low), high_(high) {}

  /**
   * XorBlocks: XOR two elem & store in-place
   */
  void Xor(const Block &x, const Block &y) {
    high_ = x.high_ ^ y.high_;
    low_ = x.low_ ^ y.low_;
  }

  /**
   * XorBlocks: XOR self with x, and return; const so self not modified
   */
  Block Xor(const Block &other) const {
    return Block(high_ ^ other.high_, low_ ^ other.low_);
  }

  /**
   * Replaces getFromBlock(__m128i, 0/1)
   *
   * getHigh: return [127:64]
   * getLow: return [64:0]
   */
  uint64_t GetHigh() const { return high_; }

  uint64_t GetLow() const { return low_; }

  unsigned char GetLsb() const {
    // CHECK: we assume Block are [low, high]; we means &block == &block.low
    assert((GetLow() & 1) == (*((unsigned short *)this) & 1) &&
           "Block: wrong memory layout!");
    return GetLow() & 1;
  }

  void Aes(MyRustAes &aes) {
    // TODO rust
    encrypt_block(aes, low_, high_);

    // // convert ourself into a 128bits, to be used by AES-NI
    // __m128i val = _mm_set_epi64x(high_, low_);

    // // BEGIN standard AES
    // val = _mm_xor_si128(val, sched[0]);

    // for (unsigned int j = 1; j < rnds; j++) {
    //   val = _mm_aesenc_si128(val, sched[j]);
    // }

    // val = _mm_aesenclast_si128(val, sched[rnds]);
    // // END standard AES

    // // finally, copy the AES result back into ourself
    // high_ = _mm_extract_epi64(val, 1);
    // low_ = _mm_extract_epi64(val, 0);
  }

  /**
   * Double 'b' and store in ourself
   */
  void Double(const Block &b) {
    high_ = b.high_ * 2;
    low_ = b.low_ * 2;
  }

  /**
   * Quadruple 'b' and store in ourself
   * Same as calling double twice.
   */
  void Quadruple(const Block &b) {
    high_ = b.high_ * 4;
    low_ = b.low_ * 4;
  }

  /**
   * Zero ourself
   * Basically the same as: blk = Block(0, 0)
   */
  void Zero() {
    high_ = 0;
    low_ = 0;
  }

  // And also mapOutputs [dev only]
  bool operator==(const Block &other) const {
    return high_ == other.high_ && low_ == other.low_;
  }

  friend std::ostream &operator<<(std::ostream &os, const Block &other) {
    os << "(" << other.low_ << "," << other.high_ << ")";
    return os;
  }

 private:
  // the order is relevant for NewR() & GetLsb() !
  // And also for Aes() b/c of the intermediate m128i.
  uint64_t low_;
  uint64_t high_;
};
