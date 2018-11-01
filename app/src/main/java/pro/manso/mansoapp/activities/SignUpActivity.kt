package pro.manso.mansoapp.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*
import pro.manso.mansoapp.*


class SignUpActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        buttonGoLogin.setOnClickListener {

            goToActivity<LoginActivity> {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        buttonSignUp.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            val confirmPassword = editTextConfirmPassword.text.toString()
            if (isValidEmail(email) && isValidPassword(password) && isValidConfirmPassword(confirmPassword, password)) {
                signUpByEmail(email, password)
            } else {
                toast("Porfavor asegurate de que toda la data sea correcta")
            }
        }

        editTextEmail.validate {
            editTextEmail.error = if (isValidEmail(it)) null else "Correo no valido"
        }
        editTextPassword.validate {
            editTextPassword.error = if (isValidPassword(it)) null else "La contraseña debe contener al menos 1 mayuscula, 1 miniscula y 1 numero"
        }
        editTextConfirmPassword.validate {
            editTextConfirmPassword.error = if (isValidConfirmPassword(editTextPassword.text.toString(), it)) null else "Contraseñas no coinciden"
        }
    }

    private fun signUpByEmail(email: String, password: String) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        mAuth.currentUser!!.sendEmailVerification().addOnCompleteListener(this) {
                            toast("Un mail fue enviado a tu correo electronico porfavor confirma antes de volver a ingresar")

                            goToActivity<LoginActivity> {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        }

                    } else {
                        toast("An unexpected error has occurred")
                    }
                }
    }

}
