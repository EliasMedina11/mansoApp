package pro.manso.mansoapp.models

import java.util.*

data class Vote (var userId: String, var vote: String , val sentAt: Date = Date(),var userEmail: String)