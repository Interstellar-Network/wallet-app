/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gg.interstellar.wallet.android.data

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/* Hard-coded data for the wallet sample. */
@Immutable
data class Appearance(
    // default
    val largeRow:Boolean,
)


@Immutable
data class Currency(
    val name: String,
    val coin: String,
    val pubkey: String,
    val balance: Float,
    val balanceFiat: Float,
    val change: Float,
    val color: Color
)

@Immutable
data class Transaction(
    val name: String,
    val date: String,
    val amount: Float,
    val color: Color
)

@Immutable
data class Address(
    val name: String,
    val pubkey: String,
    // little trick to use Generic StatementBody with Address screen with no amount
    val no_amount: Float = 0f,
    val color: Color = Color(0xFF637DEA)
)


/**
 * Pretend repository for user's data.
 */
object UserData {

    val currencies: List<Currency> = listOf(
        Currency(
            "select", //trick to define a row for input
            "select",
            "0",
            0f,
            0f,
            0f,
            Color(0xFF637DEA)
        ),

        Currency(
            "Ethereum",
            "ETH",
            "0x54dbb737eac5007103e729e9ab7ce64a6850a310",
            2.13f,
            2181.06f,
            1.2f,
            Color(0xFF637DEA)
        ),
        Currency(
            "Bitcoin",
            "BTC",
            "17VZNX1SN5NtKa8UQFxwQbFeFc3iqRYhem",
            0.1343f,
            3627.84f,
            -1.7f,
            Color(0xFFFF9500)
        ),
        Currency(
            "Polkadot",
            "DOT",
            "1FRMM8PEiWXYax7rpS6X4XZX1aAAxSWx1CrKTyrVYhV24fg",
            221.13f,
            1019.27f,
            4.5f,
            Color(0xFFe60079)
        ),

        Currency(
            "Solana",
            "SOL",
            "83astBRguLMdt2h5U1Tpdq5tjFoJ6noeGwaY3mDLVcri",
            221.13f,
            900.15f,
            -2.3f,
            Color(0xFF47b7c1)
        )
    )

    val addresses: List<Address> = listOf(
        Address(
            "select",//trick to define a row for input
            "83astBRguLMdt2h5U1Tpdq5tjFoJ6noeGwaY3mDLVcri",
        ),

        Address(
            "alice",
            "83astBRguLMdt2h5U1Tpdq5tjFoJ6noeGwaY3mDLVcri",
        ),
        Address(
            "bob",
            "1FRMM8PEiWXYax7rpS6X4XZX1aAAxSWx1CrKTyrVYhV24fg",
        ),
        Address(
            "uniswap",
            "0x54dbb737eac5007103e729e9ab7ce64a6850a310",
        ),
        Address(
            "li",
            "17VZNX1SN5NtKa8UQFxwQbFeFc3iqRYhem",
        ),
        Address(
            "robert",
            "0x54d78737eac5007103e729e9ab7ce64a6850a310",
        ),
        Address(
            "siegfried",
            "1FRMM8PEiWXYax7rpS6X4XZX1aAAxSWx1CrKTyrVYhV24fg",
        ),
        Address(
            "oneinch",
            "0x54dbb737eac5007103e729e9ab7ce64a6850a310",
        ),
        Address(
            "li",
            "17VZNX1SN5NtKa8UQFxwQbFeFc3iqRYhem",
        ),
        Address(
            "tim",
            "0x54d78737eac5007103e729e9ab7ce64a6850a310",
        )

    )

    fun getAddress(addressName: String?): Address {
        return addresses.first { it.name == addressName }
    }

    fun getCurrency(currencyName: String?): Currency {
        return currencies.first { it.name == currencyName }
    }

}
