package uns.sakku.feature.pocket.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uns.sakku.ui.theme.ExpenseRed
import uns.sakku.ui.theme.IncomeGreen

// Data Classes
data class SavingGoal(val name: String, val target: Float, val currentAmount: Float)
data class PocketBudget(val category: String, val limit: Float, val spentAmount: Float)

@Composable
fun SavingCard(saving: SavingGoal) {
    val progressPercentage = if (saving.target > 0) (saving.currentAmount / saving.target) else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = saving.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(text = "${(progressPercentage * 100).toInt()}%", fontWeight = FontWeight.Bold, color = IncomeGreen)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Terkumpul: Rp ${saving.currentAmount.toInt()} / Rp ${saving.target.toInt()}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressPercentage.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(IncomeGreen)
                )
            }
        }
    }
}

@Composable
fun PocketCard(pocket: PocketBudget) {
    val progressPercentage = if (pocket.limit > 0) (pocket.spentAmount / pocket.limit) else 0f
    val isOverBudget = pocket.spentAmount > pocket.limit
    val barColor = if (isOverBudget) ExpenseRed else MaterialTheme.colorScheme.primary
    val textColor = if (isOverBudget) ExpenseRed else MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isOverBudget) ExpenseRed.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = pocket.category, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor)

                if (isOverBudget) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = "Over Budget", tint = ExpenseRed, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Terpakai: Rp ${pocket.spentAmount.toInt()}", fontSize = 12.sp, color = textColor, fontWeight = if(isOverBudget) FontWeight.Bold else FontWeight.Normal)
                Text(text = "Batas: Rp ${pocket.limit.toInt()}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressPercentage.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(barColor)
                )
            }

            if (isOverBudget) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Perhatian: Anda telah melewati batas anggaran kantong ini!",
                    color = ExpenseRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}