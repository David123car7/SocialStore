package com.ipca.socialstore.data.pdfbox

import android.content.Context
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.models.AcademicModel
import com.ipca.socialstore.data.models.ApplicationModel
import com.ipca.socialstore.data.models.ScholarshipModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import org.bouncycastle.util.Bytes
import java.io.File
import javax.inject.Inject
import kotlin.io.path.readBytes

class PdfGenerator @Inject constructor(
    private val exceptionMapper: ExceptionMapper
) {
    fun generateAndUploadPdf(
        context: Context,
        fileName: String,
        application: ApplicationModel,
        academicData: AcademicModel?,
        scholarShip: ScholarshipModel?,
    ): ResultWrapper<ByteArray> {
        var tempFile: File? = null
        return try {
            val assetManager = context.assets
            val inputStream = assetManager.open("RequerimentoAutomatizado.pdf")
            val document = PDDocument.load(inputStream)
            val acroForm = document.documentCatalog.acroForm
            val on = "true"
            val off = "Off"

            if (acroForm != null) {
                acroForm.getField("checkbox_licenciatura")?.setValue(off)
                acroForm.getField("checkbox_mestrado")?.setValue(off)
                acroForm.getField("checkbox_ctesp")?.setValue(off)

                acroForm.getField("checkbox_food")?.setValue(off)
                acroForm.getField("checkbox_higiene")?.setValue(off)
                acroForm.getField("checkbox_clean")?.setValue(off)

                acroForm.getField("school_year")?.setValue(application.schoolYear.toString())
                acroForm.getField("name")?.setValue(application.name)
                acroForm.getField("birth_date")?.setValue(application.birthDate)
                acroForm.getField("cc")?.setValue(application.cc)
                acroForm.getField("phone_number")?.setValue(application.phoneNumber)
                acroForm.getField("email")?.setValue(application.email)

                if(academicData != null){
                    acroForm.getField("student_number")?.setValue(academicData.studenNumber)
                    acroForm.getField("course")?.setValue(academicData.course)
                    val typeCourse = academicData.typeCourse.lowercase()
                    when {
                        typeCourse.contains("licenciatura") -> acroForm.getField("checkbox_licenciatura")?.setValue(on)
                        typeCourse.contains("mestrado") -> acroForm.getField("checkbox_mestrado")?.setValue(on)
                        typeCourse.contains("ctesp") -> acroForm.getField("checkbox_ctesp")?.setValue(on)
                    }
                }

                when {
                    application.requestType.contains("food") ->
                        acroForm.getField("checkbox_food")?.setValue(on)

                    application.requestType.contains("hygiene") ->
                        acroForm.getField("checkbox_higiene")?.setValue(on)

                    application.requestType.contains("clean") ->
                        acroForm.getField("checkbox_clean")?.setValue(on)
                }

                if (application.faes) {
                    acroForm.getField("checkbox_true_faes")?.setValue(on)
                    acroForm.getField("checkbox_false_faes")?.setValue(off)
                } else {
                    acroForm.getField("checkbox_true_faes")?.setValue(off)
                    acroForm.getField("checkbox_false_faes")?.setValue(on)
                }

                if (scholarShip != null) {
                    acroForm.getField("checkbox_true_bolsa")?.setValue(on)
                    acroForm.getField("checkbox_false_bolsa")?.setValue(off)
                    acroForm.getField("bolsa_value")?.setValue(scholarShip.value.toString())
                } else {
                    acroForm.getField("checkbox_true_bolsa")?.setValue(off)
                    acroForm.getField("checkbox_false_bolsa")?.setValue(on)
                    acroForm.getField("bolsa_value")?.setValue("")
                }
            }
            tempFile = File(context.cacheDir, fileName)
            document.save(tempFile)
            document.close()
            val bytes = tempFile.readBytes()
            ResultWrapper.Success(bytes)
        } catch (e: Exception) {
            android.util.Log.e("App Debug", "Error generating/uploading PDF: ${e.message}")
            ResultWrapper.Error(exceptionMapper.map(e))
        } finally {
            try {
                tempFile?.delete()
            } catch (e: Exception) {
            }
        }
    }
}