package com.aait.shaheer.data_layer.model.register_model

import com.aait.shaheer.data_layer.model.common.BaseResponse
import com.kareem.datalayer.model.common.UserModel

data class RegisterModel(
    val `data`: UserModel?
): BaseResponse()
