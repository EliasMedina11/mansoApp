package pro.manso.mansoapp

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import java.util.regex.Pattern

fun Activity.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) = Toast.makeText(this,message,duration).show()

fun Activity.longToast(message: CharSequence, duration: Int = Toast.LENGTH_LONG) = Toast.makeText(this,message,duration).show()

fun Activity.toast(resourceId: Int, duration: Int = Toast.LENGTH_SHORT) = Toast.makeText(this,resourceId,duration).show()

fun ViewGroup.inflate (layoutId: Int) = LayoutInflater.from(context).inflate(layoutId,this,false)!!

inline fun <reified T: Activity>Activity.goToActivity(noinline init: Intent.() -> Unit = {}){

    val intent = Intent(this , T:: class.java)
    intent.init()
    startActivity(intent)
}

fun Activity.goToActivityResult (action: String , requestCode: Int , init: Intent.() -> Unit = {} ) {

    val intent = Intent(action)
    intent.init()
    startActivityForResult(intent , requestCode)
}

fun EditText.validate (validation: (String) -> Unit){
    this.addTextChangedListener(object: TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
           validation(editable.toString())
        }
        override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }
        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }
    })
}

fun isValidEmail (email: String): Boolean{
    val pattern = Patterns.EMAIL_ADDRESS
    return pattern.matcher(email).matches()
}
 fun isValidPassword (password: String): Boolean{
    val passwordPattern = "(^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{4,}$)"
    val pattern = Pattern.compile(passwordPattern)
    return pattern.matcher(password).matches()
}
 fun isValidConfirmPassword (password: String, confirmPassword: String): Boolean{
    return password == confirmPassword
}

fun getBiggerImage (currentUser: FirebaseUser): String{
    return if(currentUser.providers?.get(0).toString() == "google.com"){
        "${currentUser.photoUrl}".replace("s96-c/photo.jpg", "s400-c/photo.jpg")
        //  "${currentUser.photoUrl.toString().substring(0, currentUser.photoUrl.toString().length - 15)}s400-c/photo.jpg"
    } else {
        "${currentUser.photoUrl}?type=large"
    }
}

/*fun getBiggerImageLeft (message: Message): String{
    val imagePath = if(message.providers?.get(0).toString() == "google.com"){
        "${currentUser.photoUrl}".replace("s96-c/photo.jpg", "s400-c/photo.jpg")
        //  "${currentUser.photoUrl.toString().substring(0, currentUser.photoUrl.toString().length - 15)}s400-c/photo.jpg"
    } else {
        "${currentUser.photoUrl}?type=large"
    }
    return imagePath
}

*/





