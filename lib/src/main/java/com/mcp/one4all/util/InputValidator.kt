package com.mcp.one4all.util

import android.app.AlarmManager
import android.util.Patterns
import java.lang.System.currentTimeMillis
import java.util.*
import java.util.regex.Pattern

internal class InputValidator {
    companion object {
        val USERNAME_PATTERN =  "^[0-9a-zA-Z\\_]{3,18}$"

        /**
         * Check if text is valid email address
         *
         * @param email (String value)
         * @return true if mail is valid, otherwise false
         */
        fun isValidEmail(email: String?): Boolean {
            return if (email.isNullOrEmpty()) {
                false
            } else {
                Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        }

        fun isValidPhoneNumber(phoneNumber: String?): Boolean {
            return if (phoneNumber.isNullOrEmpty()) {
                false
            } else {
                Patterns.PHONE.matcher(phoneNumber).matches()
            }
        }

        fun isValidUrl(text: String?): Boolean {
            return if (text.isNullOrEmpty()) {
                false
            } else {
                Patterns.WEB_URL.matcher(text).matches()
            }
        }

        fun isValidUsername(text: String?): Boolean {
            return if (text.isNullOrEmpty()) {
                false
            } else {
                isValid(USERNAME_PATTERN, text)
            }
        }

        fun isValid(expression: String, text: String): Boolean {
            val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(text)
            return matcher.matches()
        }

        /**
         * Check if text consist only from letters
         *
         * @param value (String value)
         * @return true if only letters, false if symbols
         */
        fun isTextWithLetters(value: String): Boolean {
            val expression = "[A-Za-z]*"

            val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(value)
            return matcher.matches()
        }

        fun parseBoolean(string: String): Boolean {
            try {
                return Integer.parseInt(string) > 0
            } catch (nfe: NumberFormatException) {
                return java.lang.Boolean.parseBoolean(string)
            }

        }

        fun areSameDay(date1: Long, date2: Long): Boolean {
            val cal1 = calendarDate()
            val cal2 = calendarDate()
            cal1.timeInMillis = date1
            cal2.timeInMillis = date2

            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(
                Calendar.DAY_OF_YEAR
            )
        }

        fun areSameWeek(date1: Long, date2: Long): Boolean {
            val cal1 = calendarDate()
            val cal2 = calendarDate()
            cal1.timeInMillis = date1
            cal2.timeInMillis = date2

            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(
                Calendar.WEEK_OF_YEAR
            )
        }

        fun isYesterday(date1: Long): Boolean {
            val cal1 = calendarDate()
            val cal2 = calendarDate()
            cal1.timeInMillis = date1 + AlarmManager.INTERVAL_DAY
            cal2.timeInMillis = currentTimeMillis()

            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(
                Calendar.DAY_OF_YEAR
            )
        }


        fun isValidPassword(password: String): Boolean {
            return password.length >= 8
                    && isTextWithSpecialCharacters(password)
                    && isTextWithNumbers(password)
                    && isTextWithLetter(password)
        }

        fun isValidLength(text: String, minLength: Int, maxLength: Int) =
            text.length in minLength..maxLength

        fun isTextWithSpecialCharacters(text: String): Boolean {
            val specialChars = listOf("!", "@", "#", "$", "%", "^", "&", "*")
            var hasSpecialCharacter = false

            specialChars.forEach {
                if (text.contains(it)) {
                    hasSpecialCharacter = true
                }
            }

            return hasSpecialCharacter
        }

        fun isTextWithNumbers(text: String): Boolean {
            text.toCharArray().forEach {
                if (Character.isDigit(it)) {
                    return true
                }
            }
            return false
        }

        fun isTextWithLetter(text: String): Boolean {
            text.toCharArray().forEach {
                if (Character.isLetter(it)) {
                    return true
                }
            }
            return false
        }

        fun isTextWithUppercase(text: String): Boolean {
            text.toCharArray().forEach {
                if (Character.isLetter(it) && Character.isUpperCase(it)) {
                    return true
                }
            }
            return false
        }

        fun isTextWithLowercase(text: String): Boolean {
            text.toCharArray().forEach {
                if (Character.isLetter(it) && Character.isLowerCase(it)) {
                    return true
                }
            }
            return false
        }
    }
}