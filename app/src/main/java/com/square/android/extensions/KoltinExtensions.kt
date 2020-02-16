package com.square.android.extensions

import android.content.res.ColorStateList
import android.text.TextUtils
import android.util.Patterns
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.square.android.App
import com.square.android.R
import java.text.SimpleDateFormat
import java.util.*

private val FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

class Country(var code: String, var name: String, var dialCode: String, var currency: String)

fun String.getCountryNameByDialCode(): String {
    val countries = arrayOf(Country("AD", "Andorra", "+376", "EUR"), Country("AE", "United Arab Emirates", "+971", "AED"), Country("AF", "Afghanistan", "+93", "AFN"), Country("AG", "Antigua and Barbuda", "+1", "XCD"), Country("AI", "Anguilla", "+1", "XCD"), Country("AL", "Albania", "+355", "ALL"), Country("AM", "Armenia", "+374", "AMD"), Country("AO", "Angola", "+244", "AOA"), Country("AQ", "Antarctica", "+672", "USD"), Country("AR", "Argentina", "+54", "ARS"), Country("AS", "American Samoa", "+1", "USD"), Country("AT", "Austria", "+43", "EUR"), Country("AU", "Australia", "+61", "AUD"), Country("AW", "Aruba", "+297", "AWG"), Country("AX", "Aland Islands", "+358", "EUR"), Country("AZ", "Azerbaijan", "+994", "AZN"), Country("BA", "Bosnia and Herzegovina", "+387", "BAM"), Country("BB", "Barbados", "+1", "BBD"), Country("BD", "Bangladesh", "+880", "BDT"), Country("BE", "Belgium", "+32", "EUR"), Country("BF", "Burkina Faso", "+226", "XOF"), Country("BG", "Bulgaria", "+359", "BGN"), Country("BH", "Bahrain", "+973", "BHD"), Country("BI", "Burundi", "+257", "BIF"), Country("BJ", "Benin", "+229", "XOF"), Country("BL", "Saint Barthelemy", "+590", "EUR"), Country("BM", "Bermuda", "+1", "BMD"), Country("BN", "Brunei Darussalam", "+673", "BND"), Country("BO", "Bolivia, Plurinational State of", "+591", "BOB"), Country("BQ", "Bonaire", "+599", "USD"), Country("BR", "Brazil", "+55", "BRL"), Country("BS", "Bahamas", "+1", "BSD"), Country("BT", "Bhutan", "+975", "BTN"), Country("BV", "Bouvet Island", "+47", "NOK"), Country("BW", "Botswana", "+267", "BWP"), Country("BY", "Belarus", "+375", "BYR"), Country("BZ", "Belize", "+501", "BZD"), Country("CA", "Canada", "+1", "CAD"), Country("CC", "Cocos (Keeling) Islands", "+61", "AUD"), Country("CD", "Congo, The Democratic Republic of the", "+243", "CDF"), Country("CF", "Central African Republic", "+236", "XAF"), Country("CG", "Congo", "+242", "XAF"), Country("CH", "Switzerland", "+41", "CHF"), Country("CI", "Ivory Coast", "+225", "XOF"), Country("CK", "Cook Islands", "+682", "NZD"), Country("CL", "Chile", "+56", "CLP"), Country("CM", "Cameroon", "+237", "XAF"), Country("CN", "China", "+86", "CNY"), Country("CO", "Colombia", "+57", "COP"), Country("CR", "Costa Rica", "+506", "CRC"), Country("CU", "Cuba", "+53", "CUP"), Country("CV", "Cape Verde", "+238", "CVE"), Country("CW", "Curacao", "+599", "ANG"), Country("CX", "Christmas Island", "+61", "AUD"), Country("CY", "Cyprus", "+357", "EUR"), Country("CZ", "Czech Republic", "+420", "CZK"), Country("DE", "Germany", "+49", "EUR"), Country("DJ", "Djibouti", "+253", "DJF"), Country("DK", "Denmark", "+45", "DKK"), Country("DM", "Dominica", "+1", "XCD"), Country("DO", "Dominican Republic", "+1", "DOP"), Country("DZ", "Algeria", "+213", "DZD"), Country("EC", "Ecuador", "+593", "USD"), Country("EE", "Estonia", "+372", "EUR"), Country("EG", "Egypt", "+20", "EGP"), Country("EH", "Western Sahara", "+212", "MAD"), Country("ER", "Eritrea", "+291", "ERN"), Country("ES", "Spain", "+34", "EUR"), Country("ET", "Ethiopia", "+251", "ETB"), Country("FI", "Finland", "+358", "EUR"), Country("FJ", "Fiji", "+679", "FJD"), Country("FK", "Falkland Islands (Malvinas)", "+500", "FKP"), Country("FM", "Micronesia, Federated States of", "+691", "USD"), Country("FO", "Faroe Islands", "+298", "DKK"), Country("FR", "France", "+33", "EUR"), Country("GA", "Gabon", "+241", "XAF"), Country("GB", "United Kingdom", "+44", "GBP"), Country("GD", "Grenada", "+1", "XCD"), Country("GE", "Georgia", "+995", "GEL"), Country("GF", "French Guiana", "+594", "EUR"), Country("GG", "Guernsey", "+44", "GGP"), Country("GH", "Ghana", "+233", "GHS"), Country("GI", "Gibraltar", "+350", "GIP"), Country("GL", "Greenland", "+299", "DKK"), Country("GM", "Gambia", "+220", "GMD"), Country("GN", "Guinea", "+224", "GNF"), Country("GP", "Guadeloupe", "+590", "EUR"), Country("GQ", "Equatorial Guinea", "+240", "XAF"), Country("GR", "Greece", "+30", "EUR"), Country("GS", "South Georgia and the South Sandwich Islands", "+500", "GBP"), Country("GT", "Guatemala", "+502", "GTQ"), Country("GU", "Guam", "+1", "USD"), Country("GW", "Guinea-Bissau", "+245", "XOF"), Country("GY", "Guyana", "+595", "GYD"), Country("HK", "Hong Kong", "+852", "HKD"), Country("HM", "Heard Island and McDonald Islands", "+000", "AUD"), Country("HN", "Honduras", "+504", "HNL"), Country("HR", "Croatia", "+385", "HRK"), Country("HT", "Haiti", "+509", "HTG"), Country("HU", "Hungary", "+36", "HUF"), Country("ID", "Indonesia", "+62", "IDR"), Country("IE", "Ireland", "+353", "EUR"), Country("IL", "Israel", "+972", "ILS"), Country("IM", "Isle of Man", "+44", "GBP"), Country("IN", "India", "+91", "INR"), Country("IO", "British Indian Ocean Territory", "+246", "USD"), Country("IQ", "Iraq", "+964", "IQD"), Country("IR", "Iran, Islamic Republic of", "+98", "IRR"), Country("IS", "Iceland", "+354", "ISK"), Country("IT", "Italy", "+39", "EUR"), Country("JE", "Jersey", "+44", "JEP"), Country("JM", "Jamaica", "+1", "JMD"), Country("JO", "Jordan", "+962", "JOD"), Country("JP", "Japan", "+81", "JPY"), Country("KE", "Kenya", "+254", "KES"), Country("KG", "Kyrgyzstan", "+996", "KGS"), Country("KH", "Cambodia", "+855", "KHR"), Country("KI", "Kiribati", "+686", "AUD"), Country("KM", "Comoros", "+269", "KMF"), Country("KN", "Saint Kitts and Nevis", "+1", "XCD"), Country("KP", "North Korea", "+850", "KPW"), Country("KR", "South Korea", "+82", "KRW"), Country("KW", "Kuwait", "+965", "KWD"), Country("KY", "Cayman Islands", "+345", "KYD"), Country("KZ", "Kazakhstan", "+7", "KZT"), Country("LA", "Lao People's Democratic Republic", "+856", "LAK"), Country("LB", "Lebanon", "+961", "LBP"), Country("LC", "Saint Lucia", "+1", "XCD"), Country("LI", "Liechtenstein", "+423", "CHF"), Country("LK", "Sri Lanka", "+94", "LKR"), Country("LR", "Liberia", "+231", "LRD"), Country("LS", "Lesotho", "+266", "LSL"), Country("LT", "Lithuania", "+370", "LTL"), Country("LU", "Luxembourg", "+352", "EUR"), Country("LV", "Latvia", "+371", "LVL"), Country("LY", "Libyan Arab Jamahiriya", "+218", "LYD"), Country("MA", "Morocco", "+212", "MAD"), Country("MC", "Monaco", "+377", "EUR"), Country("MD", "Moldova, Republic of", "+373", "MDL"), Country("ME", "Montenegro", "+382", "EUR"), Country("MF", "Saint Martin", "+590", "EUR"), Country("MG", "Madagascar", "+261", "MGA"), Country("MH", "Marshall Islands", "+692", "USD"), Country("MK", "Macedonia, The Former Yugoslav Republic of", "+389", "MKD"), Country("ML", "Mali", "+223", "XOF"), Country("MM", "Myanmar", "+95", "MMK"), Country("MN", "Mongolia", "+976", "MNT"), Country("MO", "Macao", "+853", "MOP"), Country("MP", "Northern Mariana Islands", "+1", "USD"), Country("MQ", "Martinique", "+596", "EUR"), Country("MR", "Mauritania", "+222", "MRO"), Country("MS", "Montserrat", "+1", "XCD"), Country("MT", "Malta", "+356", "EUR"), Country("MU", "Mauritius", "+230", "MUR"), Country("MV", "Maldives", "+960", "MVR"), Country("MW", "Malawi", "+265", "MWK"), Country("MX", "Mexico", "+52", "MXN"), Country("MY", "Malaysia", "+60", "MYR"), Country("MZ", "Mozambique", "+258", "MZN"), Country("NA", "Namibia", "+264", "NAD"), Country("NC", "New Caledonia", "+687", "XPF"), Country("NE", "Niger", "+227", "XOF"), Country("NF", "Norfolk Island", "+672", "AUD"), Country("NG", "Nigeria", "+234", "NGN"), Country("NI", "Nicaragua", "+505", "NIO"), Country("NL", "Netherlands", "+31", "EUR"), Country("NO", "Norway", "+47", "NOK"), Country("NP", "Nepal", "+977", "NPR"), Country("NR", "Nauru", "+674", "AUD"), Country("NU", "Niue", "+683", "NZD"), Country("NZ", "New Zealand", "+64", "NZD"), Country("OM", "Oman", "+968", "OMR"), Country("PA", "Panama", "+507", "PAB"), Country("PE", "Peru", "+51", "PEN"), Country("PF", "French Polynesia", "+689", "XPF"), Country("PG", "Papua New Guinea", "+675", "PGK"), Country("PH", "Philippines", "+63", "PHP"), Country("PK", "Pakistan", "+92", "PKR"), Country("PL", "Poland", "+48", "PLN"), Country("PM", "Saint Pierre and Miquelon", "+508", "EUR"), Country("PN", "Pitcairn", "+872", "NZD"), Country("PR", "Puerto Rico", "+1", "USD"), Country("PS", "Palestinian Territory, Occupied", "+970", "ILS"), Country("PT", "Portugal", "+351", "EUR"), Country("PW", "Palau", "+680", "USD"), Country("PY", "Paraguay", "+595", "PYG"), Country("QA", "Qatar", "+974", "QAR"), Country("RE", "Reunion", "+262", "EUR"), Country("RO", "Romania", "+40", "RON"), Country("RS", "Serbia", "+381", "RSD"), Country("RU", "Russia", "+7", "RUB"), Country("RW", "Rwanda", "+250", "RWF"), Country("SA", "Saudi Arabia", "+966", "SAR"), Country("SB", "Solomon Islands", "+677", "SBD"), Country("SC", "Seychelles", "+248", "SCR"), Country("SD", "Sudan", "+249", "SDG"), Country("SE", "Sweden", "+46", "SEK"), Country("SG", "Singapore", "+65", "SGD"), Country("SH", "Saint Helena, Ascension and Tristan Da Cunha", "+290", "SHP"), Country("SI", "Slovenia", "+386", "EUR"), Country("SJ", "Svalbard and Jan Mayen", "+47", "NOK"), Country("SK", "Slovakia", "+421", "EUR"), Country("SL", "Sierra Leone", "+232", "SLL"), Country("SM", "San Marino", "+378", "EUR"), Country("SN", "Senegal", "+221", "XOF"), Country("SO", "Somalia", "+252", "SOS"), Country("SR", "Suriname", "+597", "SRD"), Country("SS", "South Sudan", "+211", "SSP"), Country("ST", "Sao Tome and Principe", "+239", "STD"), Country("SV", "El Salvador", "+503", "SVC"), Country("SX", "Sint Maarten", "+1", "ANG"), Country("SY", "Syrian Arab Republic", "+963", "SYP"), Country("SZ", "Swaziland", "+268", "SZL"), Country("TC", "Turks and Caicos Islands", "+1", "USD"), Country("TD", "Chad", "+235", "XAF"), Country("TF", "French Southern Territories", "+262", "EUR"), Country("TG", "Togo", "+228", "XOF"), Country("TH", "Thailand", "+66", "THB"), Country("TJ", "Tajikistan", "+992", "TJS"), Country("TK", "Tokelau", "+690", "NZD"), Country("TL", "East Timor", "+670", "USD"), Country("TM", "Turkmenistan", "+993", "TMT"), Country("TN", "Tunisia", "+216", "TND"), Country("TO", "Tonga", "+676", "TOP"), Country("TR", "Turkey", "+90", "TRY"), Country("TT", "Trinidad and Tobago", "+1", "TTD"), Country("TV", "Tuvalu", "+688", "AUD"), Country("TW", "Taiwan", "+886", "TWD"), Country("TZ", "Tanzania, United Republic of", "+255", "TZS"), Country("UA", "Ukraine", "+380", "UAH"), Country("UG", "Uganda", "+256", "UGX"), Country("UM", "U.S. Minor Outlying Islands", "+1", "USD"), Country("US", "United States", "+1", "USD"), Country("UY", "Uruguay", "+598", "UYU"), Country("UZ", "Uzbekistan", "+998", "UZS"), Country("VA", "Holy See (Vatican City State)", "+379", "EUR"), Country("VC", "Saint Vincent and the Grenadines", "+1", "XCD"), Country("VE", "Venezuela, Bolivarian Republic of", "+58", "VEF"), Country("VG", "Virgin Islands, British", "+1", "USD"), Country("VI", "Virgin Islands, U.S.", "+1", "USD"), Country("VN", "Vietnam", "+84", "VND"), Country("VU", "Vanuatu", "+678", "VUV"), Country("WF", "Wallis and Futuna", "+681", "XPF"), Country("WS", "Samoa", "+685", "WST"), Country("XK", "Kosovo", "+383", "EUR"), Country("YE", "Yemen", "+967", "YER"), Country("YT", "Mayotte", "+262", "EUR"), Country("ZA", "South Africa", "+27", "ZAR"), Country("ZM", "Zambia", "+260", "ZMW"), Country("ZW", "Zimbabwe", "+263", "USD"))

    return if(this == "+1"){
        countries.first{country -> country.code == "US"}.name
    } else{
        countries.first{country -> country.dialCode == this}.name
    }
}

fun EditText.isValid(): Boolean {
    return !TextUtils.isEmpty(this.text.toString().trim())
}



fun ImageView.drawableFromRes(@DrawableRes drawableRes: Int, @ColorRes tintColorRes: Int? = null){
    setImageDrawable(context.getDrawable(drawableRes))

    tintColorRes?.let {
        imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, tintColorRes!!))
    }
}

fun ImageView.tintFromRes(@ColorRes tintColorRes: Int){
    imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, tintColorRes!!))
}

fun Int.toOrdinalString() =
        this.toString() + when (this % 10) {
            in 11..13 -> "th"
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }

//TODO was dd-MM-yyyy
fun String.toDate(): Date {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    return format.parse(this)
}

//TODO was dd-MM-yyyy
fun String.toDateBooking(): Date {
    val format = SimpleDateFormat("yyyy-MM-dd HH.mm", Locale.getDefault())

    return format.parse(this)
}

//TODO was dd-MM-yyyy
fun String.toDateYMD(): Date {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    return format.parse(this)
}

fun Calendar.relativeTimeString(now: Calendar) : String {

    if (now.isToday(this)) return App.getString(R.string.today)

    val difference = timeInMillis - now.timeInMillis
    if (difference < 0) return App.getString(R.string.past)

    if (now.isTomorrow(this)) return App.getString(R.string.tomorrow)

    return getStringDate()
}

fun Calendar.isToday(other: Calendar): Boolean {
    return get(Calendar.DATE) == other.get(Calendar.DATE)
}

fun Calendar.isTomorrow(other: Calendar): Boolean {
    val futureCal: Calendar = Calendar.getInstance()
    futureCal.timeInMillis = timeInMillis
    futureCal.add(Calendar.DAY_OF_YEAR, 1)

    return futureCal.get(Calendar.YEAR) == other.get(Calendar.YEAR) && futureCal.get(Calendar.MONTH) == other.get(Calendar.MONTH) && futureCal.get(Calendar.DAY_OF_MONTH) == other.get(Calendar.DAY_OF_MONTH)
}

fun String?.isUrl() : Boolean {
    return this != null && Patterns.WEB_URL.matcher(this).matches()
}

fun Calendar.getStringDate(): String {
    return FORMAT.format(time)
}

