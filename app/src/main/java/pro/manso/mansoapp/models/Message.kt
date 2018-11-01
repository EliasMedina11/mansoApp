package pro.manso.mansoapp.models

import java.util.*

data class Message (var authorId: String = "",
                    val message: String = "",
                    val profileImageURL: String = "",
                    val sentAt: Date = Date(),
                    val displayName: String = "",
                    val userEmail: String = "")