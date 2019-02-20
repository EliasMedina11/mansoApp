package pro.manso.mansoapp.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import pro.manso.mansoapp.*

class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val mGoogleApiClient: GoogleApiClient by  lazy {getGoogleApiClient()}
    private val RC_GOOGLE_SIGN_IN = 99
    private lateinit var callbackManager: CallbackManager

    // private var TAG = "FACEBOOK_ERROR"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)

        buttonFacebookLogIn.background = resources.getDrawable(R.drawable.manso_facebook)
        buttonFacebookLogIn.text = ""
        buttonFacebookLogIn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

/*        try {
            val info = packageManager.getPackageInfo(
                    "pro.manso.mansoapp",
                    PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {

        } catch (e: NoSuchAlgorithmException) {

        }*/

        buttonFacebookLogInTrue.setOnClickListener {
            buttonFacebookLogIn.performClick()
        }


        buttonLogIn.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            if (isValidEmail(email) && isValidPassword(password)) {
                signIn(email, password)
            } else {
                toast("Porfavor asegurese de que toda la informacion ingresada sea correcta")
            }

        }

        textViewForgotPassword.setOnClickListener {
            goToActivity<ForgotPasswordActivity>()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)

        }
        buttonCreateAccount.setOnClickListener {
            goToActivity<SignUpActivity>()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
        editTextEmail.validate {
            editTextEmail.error = if (isValidEmail(it)) null else "Email no valido"
        }
        editTextPassword.validate {
            editTextPassword.error = if (isValidPassword(it)) null else "Contraseña no valida"
        }
        buttonLogInGoogle.setOnClickListener {
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
            startActivityForResult(signInIntent,RC_GOOGLE_SIGN_IN)
        }

        buttonFacebookLogIn.setOnClickListener {
            logInByFacebookAccountIntoFirebase()
        }
    }

    private fun logInByFacebookAccountIntoFirebase (){
        callbackManager = CallbackManager.Factory.create()

        buttonFacebookLogIn.setReadPermissions("email", "public_profile")
        buttonFacebookLogIn.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
                // ...
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
                // ...
            }
        })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        goToActivity<MainActivity> {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        toast("Authentication failed.")
                    }

                }
    }

    private fun logInByGoogleAccountIntoFirebase (googleAccount: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(googleAccount.idToken, null)
        mAuth.signInWithCredential(credential).addOnCompleteListener(this){
            if(mGoogleApiClient.isConnected){
                Auth.GoogleSignInApi.signOut(mGoogleApiClient)
            }
            goToActivity<MainActivity> {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
    }

    private fun getGoogleApiClient(): GoogleApiClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        return GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build()
    }

    private fun signIn(email: String, password: String) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        if (mAuth.currentUser!!.isEmailVerified) {
                            goToActivity<MainActivity> {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                        } else {
                            toast("Debes confirmar tu correo primero")
                        }
                    } else {
                        longToast("Este usuario/contraseña no existe o es incorrecto, porfavor intenta de nuevo o crea una cuenta nueva")
                    }
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if(result.isSuccess){
                val account = result.signInAccount
                logInByGoogleAccountIntoFirebase(account!!)
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }

    }
    override fun onConnectionFailed(p0: ConnectionResult) {
        toast("Connection Failed")
    }

    companion object {
        private const val TAG = "FacebookLogin"
    }
}