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
import gg.interstellar.wallet.android.ui.theme.MagentaCustom
import gg.interstellar.wallet.android.ui.theme.PurpleCustom

/* Hard-coded data for the wallet sample. */
@Immutable
data class Appearance(
    // default
    val largeRow:Boolean,
)

@Immutable
data class StarButtonBox(
    val name: String,
    val weight: Float,
    val colorStart: Color,
    val colorEnd: Color,
)

@Immutable
data class Currency(
    val name: String,
    val coin: String,
    val pubKey: String,
    val balance: Float,
    val balanceFiat: Float,
    val change: Float,
    val usd: Float,
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
    val pubKey: String,
    val color: Color,
    // little trick to use Generic StatementBody with Address screen with no amount
    val no_amount: Float = 0f,

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
            0f,
            PurpleCustom
        ),

        Currency(
            "Ethereum",
            "ETH",
            "0x54dbb737eac5007103e729e9ab7ce64a6850a310",
            2.13f,
            2.13f*1208f,
            1.2f,
            1208.93f,
            Color(0xFF637DEA)
        ),
        Currency(
            "Bitcoin",
            "BTC",
            "17VZNX1SN5NtKa8UQFxwQbFeFc3iqRYhem",
            0.1343f,
            0.1343f*20912.09f,
            -1.7f,
            20912.09f,
            Color(0xFFFF9500)
        ),
        Currency(
            "Polkadot",
            "DOT",
            "1FRMM8PEiWXYax7rpS6X4XZX1aAAxSWx1CrKTyrVYhV24fg",
            221.13f,
            221.13f*7.74f,
            4.5f,
            7.73f,
            Color(0xFFe60079)
        ),

        Currency(
            "Solana",
            "SOL",
            "83astBRguLMdt2h5U1Tpdq5tjFoJ6noeGwaY3mDLVcri",
            104.13f,
            104.13f*38.61f,
            -2.3f,
            38.61f,
            Color(0xFF66f9a1)
        )
    )

    val addresses: List<Address> = listOf(
        Address(
            "select",//trick to define a row for input
            "83astBRguLMdt2h5U1Tpdq5tjFoJ6noeGwaY3mDLVcri",
            PurpleCustom
        ),
        Address(
            "bob",
            "1FRMM8PEiWXYax7rpS6X4XZX1aAAxSWx1CrKTyrVYhV24fg",
            Color(0xFF95554f)
        ),

        Address(
            "alice",
            "83astBRguLMdt2h5U1Tpdq5tjFoJ6noeGwaY3mDLVcri",
            Color(0xFF17e6b7)
        ),

        Address(
            "dave",
            "0x54dbb737eac5007103e729e9ab7ce64a6850a310",
            Color(0xFF8fcdf3)
        ),

        Address(
            "charlie",
            "17VZNX1SN5NtKa8UQFxwQbFeFc3iqRYhem",
            Color(0xFF5b0705)
        ),

    )

    val menu: List<StarButtonBox> = listOf(
        StarButtonBox(
            "Send",
            0.5f,
            MagentaCustom,
            Color(0xffcc33ff),

        ),
        StarButtonBox(
            "Receive",
            0.5f,
            Color(0xffde33ff),
            Color(0xffae33ff)
        ),
        StarButtonBox(
            "Portfolio",
            1f,
            Color(0xffd033ff),
            Color(0xff8133ff),
        ),
        StarButtonBox(
            "",   //bad trick to manage Portfolio to take all row
            0.5f,
            Color(0xffffffff),
            Color(0xffffffff),
        ),

        StarButtonBox(
            "Market",
            0.5f,
            Color(0xffa433ff),
            Color(0xff7133ff),
        ),

        StarButtonBox(
            "NFTs",
            0.5f,
            Color(0xff8833ff),
            Color(0xff6633ff),
        ),
        StarButtonBox(
            "Swap",
            0.5f,
            Color(0xff7933ff),
            Color(0xff6633ff),
        ),
        StarButtonBox(
            "Buy",
            0.5f,
            Color(0xff6633ff),
            Color(0xff6633ff),
        )
    )

    fun getAddress(addressName: String?): Address {
        return addresses.first { it.name == addressName }
    }

    fun getCurrency(currencyName: String?): Currency {
        return currencies.first { it.name == currencyName }
    }
    fun getBoxName(boxName: String?): StarButtonBox {
        return menu.first { it.name == boxName }
    }


}








