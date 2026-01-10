package com.ipca.socialstore.presentation.utils.ui

import androidx.compose.ui.graphics.Color
import com.ipca.socialstore.data.enums.ApplicationDataStatus
import com.ipca.socialstore.data.enums.ApplicationDocumentTypeState
import com.ipca.socialstore.data.enums.ApplicationStates
import com.ipca.socialstore.presentation.models.StateViewDataModel
import com.ipca.socialstore.presentation.ui.theme.GreenIPCA

fun getApplicationDataStateViewData(state: String): StateViewDataModel{
    var text: String = ""
    var textColor: Color = Color.Black
    var bgColor: Color = Color.White

    if(state == ApplicationDataStatus.ACCEPTED.status){
        text = "Dados Aceites"
        textColor = GreenIPCA
        bgColor = Color(0x120FFC0B)
    }
    if(state == ApplicationDataStatus.DENIED.status){
        text = "Dados Negados"
        textColor = Color(0xFFCF1322)
        bgColor = Color(0x1BFF0000)
    }
    if(state == ApplicationDataStatus.TO_REVIEW.status){
        text = "Por Rever"
        textColor = Color(0xFFDAA210)
        bgColor = Color(0x43DAA210)
    }

    return StateViewDataModel(text = text, textColor = textColor, bgColor = bgColor)
}

fun getApplicationStateViewData(state: String): StateViewDataModel{
    var text: String = ""
    var textColor: Color = Color.Black
    var bgColor: Color = Color.White

    if(state == ApplicationStates.APPROVED.status){
        text = "Aceite"
        textColor = GreenIPCA
        bgColor = Color(0x120FFC0B)
    }
    if(state == ApplicationStates.REJECTED.status){
        text = "Rejeitado"
        textColor = Color(0xFFCF1322)
        bgColor = Color(0x1BFF0000)
    }
    if(state == ApplicationStates.PENDING.status){
        text = "Por Analisar"
        textColor = Color(0xFFDAA210)
        bgColor = Color(0x43DAA210)
    }
    if(state == ApplicationStates.CORRECTION.status){
        text = "Por Corrigir"
        textColor = Color(0xFF0057D9)
        bgColor = Color(0x3C0057D9)
    }
    if(state == ApplicationStates.ALMOST_APPROVED.status){
        text = "Fase Final"
        textColor = Color(0xFFFF9A00)
        bgColor = Color(0x27FF9A00)
    }

    return StateViewDataModel(text = text, textColor = textColor, bgColor = bgColor)
}

fun getApplicationDocumentTypeStateViewData(state: String): StateViewDataModel{
    var text: String = ""
    var textColor: Color = Color.Black
    var bgColor: Color = Color.White

    if(state == ApplicationDocumentTypeState.COMPLETED.state){
        text = "Completo"
        textColor = GreenIPCA
        bgColor = Color(0x120FFC0B)
    }
    if(state == ApplicationDocumentTypeState.SOMETHING_WRONG.state){
        text = "Incorreto"
        textColor = Color(0xFFCF1322)
        bgColor = Color(0x1BFF0000)
    }
    if(state == ApplicationDocumentTypeState.TO_REVIEW.state){
        text = "Por Rever"
        textColor = Color(0xFFDAA210)
        bgColor = Color(0x43DAA210)
    }

    return StateViewDataModel(text = text, textColor = textColor, bgColor = bgColor)
}