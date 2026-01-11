package com.ipca.socialstore.domain.services.reports

import android.content.Context
import com.ipca.socialstore.data.enums.ItemType
import com.ipca.socialstore.data.reports.exportDataToCsv
import com.ipca.socialstore.data.repository.DeliveryItemsRepository
import com.ipca.socialstore.data.repository.DeliveryRepository
import com.ipca.socialstore.data.repository.StockRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.item.GetItemsFromStockUseCase
import com.ipca.socialstore.presentation.models.ReportsHelperModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GenerateReportDocument @Inject constructor() {
    operator fun invoke(itemType: String, reportData: List<ReportsHelperModel>): ResultWrapper<String> {
        val sb = StringBuilder()

        sb.append("Produto,Total,Entregue,Em Stock\n")

        var total = 0
        var totalStock = 0

        reportData.forEach { item ->
            val cleanName = item.name.replace(",", " ")
            sb.append("$cleanName,${item.total},${item.delivered},${item.stock}\n")

            total += item.total
            totalStock += item.stock
        }
        sb.append("Total,$total,,$totalStock\n")

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val currentDate = LocalDate.now().format(formatter)
        val prefix = when (itemType) {
            ItemType.AlIMENTAÇÃO.type -> "RelatorioAlimentacao"
            ItemType.HIGIENE.type -> "RelatorioHigiene"
            else -> "RelatorioLimpeza"
        }
        val fileName = "${prefix}_${currentDate}.csv"

        return ResultWrapper.Success( sb.toString())
    }
}