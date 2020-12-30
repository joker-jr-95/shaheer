package com.aait.shaheer.data_layer.remote

import com.aait.shaheer.domain.repository.base_repository.baseRepository
import java.util.*

object Helper {
    val language: String
        get() {
            var language = Locale.getDefault().displayLanguage
            if (language.equals("English", ignoreCase = true)) {
                language = "en"
            } else {
                language = "ar"
            }
            return baseRepository.lang

        }
}